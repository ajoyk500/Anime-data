
package io.github.rosemoe.sora.text;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class UndoManager implements ContentListener, Parcelable {
    public final static Creator<UndoManager> CREATOR = new Creator<>() {
        @Override
        public UndoManager createFromParcel(Parcel parcel) {
            var o = new UndoManager();
            o.maxStackSize = parcel.readInt();
            o.stackPointer = parcel.readInt();
            o.undoEnabled = parcel.readInt() > 0;
            var count = parcel.readInt();
            while (count > 0) {
                o.actionStack.add(parcel.readParcelable(UndoManager.class.getClassLoader()));
                count--;
            }
            return o;
        }
        @Override
        public UndoManager[] newArray(int flags) {
            return new UndoManager[flags];
        }
    };
    private static long sMergeTimeLimit = 8000L;
    private final List<ContentAction> actionStack;
    private boolean undoEnabled;
    private int maxStackSize;
    private InsertAction insertAction;
    private DeleteAction deleteAction;
    private Content targetContent;
    private boolean replaceMark;
    private int stackPointer;
    private boolean ignoreModification;
    private boolean forceNewMultiAction;
    private TextRange memorizedCursorRange;
    UndoManager() {
        actionStack = new ArrayList<>();
        replaceMark = false;
        insertAction = null;
        deleteAction = null;
        stackPointer = 0;
        ignoreModification = false;
    }
    public static long getMergeTimeLimit() {
        return sMergeTimeLimit;
    }
    public static void setMergeTimeLimit(long mergeTimeLimit) {
        UndoManager.sMergeTimeLimit = mergeTimeLimit;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(maxStackSize);
        parcel.writeInt(stackPointer);
        parcel.writeInt(undoEnabled ? 1 : 0);
        parcel.writeInt(actionStack.size());
        for (ContentAction contentAction : actionStack) {
            parcel.writeParcelable(contentAction, flags);
        }
    }
    public boolean isModifyingContent() {
        return ignoreModification;
    }
    @Nullable
    public TextRange undo(Content content) {
        if (canUndo() && !isModifyingContent()) {
            ignoreModification = true;
            var action = actionStack.get(stackPointer - 1);
            action.undo(content);
            stackPointer--;
            ignoreModification = false;
            return action.cursor;
        }
        return null;
    }
    public void redo(Content content) {
        if (canRedo() && !isModifyingContent()) {
            ignoreModification = true;
            actionStack.get(stackPointer).redo(content);
            stackPointer++;
            ignoreModification = false;
        }
    }
    void onExitBatchEdit() {
        forceNewMultiAction = true;
        if (!actionStack.isEmpty() && actionStack.get(actionStack.size() - 1) instanceof MultiAction) {
            var action = ((MultiAction) actionStack.get(actionStack.size() - 1));
            if (action._actions.size() == 1) {
                actionStack.set(actionStack.size() - 1, action._actions.get(0));
            }
        }
    }
    public boolean canUndo() {
        return isUndoEnabled() && (stackPointer > 0);
    }
    public boolean canRedo() {
        return isUndoEnabled() && (stackPointer < actionStack.size());
    }
    public boolean isUndoEnabled() {
        return undoEnabled;
    }
    public void setUndoEnabled(boolean enabled) {
        undoEnabled = enabled;
        if (!enabled) {
            cleanStack();
        }
    }
    public int getMaxUndoStackSize() {
        return maxStackSize;
    }
    public void setMaxUndoStackSize(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException(
                    "max size can not be zero or smaller.Did you want to disable undo module by calling setUndoEnabled()?");
        }
        maxStackSize = maxSize;
        cleanStack();
    }
    private void cleanStack() {
        if (!undoEnabled) {
            actionStack.clear();
            stackPointer = 0;
        } else {
            while (stackPointer > 1 && actionStack.size() > maxStackSize) {
                actionStack.remove(0);
                stackPointer--;
            }
        }
    }
    private void cleanBeforePush() {
        while (stackPointer < actionStack.size()) {
            actionStack.remove(actionStack.size() - 1);
        }
    }
    private void pushAction(Content content, ContentAction action) {
        if (!isUndoEnabled()) {
            return;
        }
        cleanBeforePush();
        if (content.isInBatchEdit()) {
            if (actionStack.isEmpty()) {
                MultiAction a = new MultiAction();
                a.addAction(action);
                a.cursor = action.cursor;
                actionStack.add(a);
                stackPointer++;
            } else {
                ContentAction a = actionStack.get(actionStack.size() - 1);
                if (a instanceof MultiAction && !forceNewMultiAction) {
                    MultiAction ac = (MultiAction) a;
                    ac.addAction(action);
                } else {
                    MultiAction ac = new MultiAction();
                    ac.addAction(action);
                    ac.cursor = action.cursor;
                    actionStack.add(ac);
                    stackPointer++;
                }
            }
        } else {
            if (actionStack.isEmpty()) {
                actionStack.add(action);
                stackPointer++;
            } else {
                ContentAction last = actionStack.get(actionStack.size() - 1);
                if (last.canMerge(action)) {
                    last.merge(action);
                } else {
                    actionStack.add(action);
                    stackPointer++;
                }
            }
        }
        forceNewMultiAction = false;
        cleanStack();
    }
    public void exitReplaceMode() {
        if (replaceMark && deleteAction != null) {
            pushAction(targetContent, deleteAction);
        }
        replaceMark = false;
        targetContent = null;
    }
    @Override
    public void beforeReplace(@NonNull Content content) {
        if (ignoreModification) {
            return;
        }
        replaceMark = true;
        targetContent = content;
    }
    @Override
    public void afterInsert(@NonNull Content content, int startLine, int startColumn, int endLine, int endColumn,
                            @NonNull CharSequence insertedContent) {
        if (ignoreModification) {
            return;
        }
        insertAction = new InsertAction();
        insertAction.startLine = startLine;
        insertAction.startColumn = startColumn;
        insertAction.endLine = endLine;
        insertAction.endColumn = endColumn;
        insertAction.text = insertedContent;
        if (replaceMark && deleteAction != null) {
            ReplaceAction rep = new ReplaceAction();
            rep.delete = deleteAction;
            rep.insert = insertAction;
            rep.cursor = memorizedCursorRange;
            pushAction(content, rep);
        } else {
            insertAction.cursor = memorizedCursorRange;
            pushAction(content, insertAction);
        }
        deleteAction = null;
        insertAction = null;
        replaceMark = false;
    }
    @Override
    public void afterDelete(@NonNull Content content, int startLine, int startColumn, int endLine, int endColumn,
                            @NonNull CharSequence deletedContent) {
        if (ignoreModification) {
            return;
        }
        deleteAction = new DeleteAction();
        deleteAction.endColumn = endColumn;
        deleteAction.startColumn = startColumn;
        deleteAction.endLine = endLine;
        deleteAction.startLine = startLine;
        deleteAction.text = deletedContent;
        deleteAction.cursor = memorizedCursorRange;
        if (!replaceMark) {
            pushAction(content, deleteAction);
        }
    }
    @Override
    public void beforeModification(@NonNull Content content) {
        if (!undoEnabled || !content.isCursorCreated() || replaceMark && deleteAction != null) {
            return;
        }
        var cursor = content.getCursor();
        memorizedCursorRange = cursor.getRange();
    }
    public static abstract class ContentAction implements Parcelable {
        public transient TextRange cursor;
        public abstract void undo(Content content);
        public abstract void redo(Content content);
        public abstract boolean canMerge(ContentAction action);
        public abstract void merge(ContentAction action);
    }
    public static final class InsertAction extends ContentAction {
        public static final Creator<InsertAction> CREATOR = new Creator<>() {
            @Override
            public InsertAction createFromParcel(Parcel parcel) {
                var o = new InsertAction();
                o.startLine = parcel.readInt();
                o.startColumn = parcel.readInt();
                o.endLine = parcel.readInt();
                o.endColumn = parcel.readInt();
                o.text = parcel.readString();
                return o;
            }
            @Override
            public InsertAction[] newArray(int size) {
                return new InsertAction[size];
            }
        };
        public int startLine, endLine, startColumn, endColumn;
        public transient long createTime = System.currentTimeMillis();
        public CharSequence text;
        @Override
        public void undo(Content content) {
            content.delete(startLine, startColumn, endLine, endColumn);
        }
        @Override
        public void redo(Content content) {
            content.insert(startLine, startColumn, text);
        }
        @Override
        public boolean canMerge(ContentAction action) {
            if (action instanceof InsertAction) {
                InsertAction ac = (InsertAction) action;
                return (ac.startColumn == endColumn && ac.startLine == endLine
                        && ac.text.length() + text.length() < 10000
                        && Math.abs(ac.createTime - createTime) < sMergeTimeLimit);
            }
            return false;
        }
        @Override
        public void merge(ContentAction action) {
            if (!canMerge(action)) {
                throw new IllegalArgumentException();
            }
            InsertAction ac = (InsertAction) action;
            this.endColumn = ac.endColumn;
            this.endLine = ac.endLine;
            StringBuilder sb;
            if (text instanceof StringBuilder) {
                sb = (StringBuilder) text;
            } else {
                sb = new StringBuilder(text);
                text = sb;
            }
            sb.append(ac.text);
        }
        @NonNull
        @Override
        public String toString() {
            return "InsertAction{" +
                    "startLine=" + startLine +
                    ", endLine=" + endLine +
                    ", startColumn=" + startColumn +
                    ", endColumn=" + endColumn +
                    ", createTime=" + createTime +
                    ", text=" + text +
                    '}';
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(startLine);
            parcel.writeInt(startColumn);
            parcel.writeInt(endLine);
            parcel.writeInt(endColumn);
            parcel.writeString(text.toString());
        }
    }
    public static final class MultiAction extends ContentAction {
        public final static Creator<MultiAction> CREATOR = new Creator<>() {
            @Override
            public MultiAction createFromParcel(Parcel parcel) {
                var o = new MultiAction();
                var count = parcel.readInt();
                while (count > 0) {
                    o._actions.add(parcel.readParcelable(MultiAction.class.getClassLoader()));
                    count--;
                }
                return o;
            }
            @Override
            public MultiAction[] newArray(int size) {
                return new MultiAction[size];
            }
        };
        private final List<ContentAction> _actions = new ArrayList<>();
        public void addAction(ContentAction action) {
            if (_actions.isEmpty()) {
                _actions.add(action);
            } else {
                ContentAction last = _actions.get(_actions.size() - 1);
                if (last.canMerge(action)) {
                    last.merge(action);
                } else {
                    _actions.add(action);
                }
            }
        }
        @Override
        public void undo(Content content) {
            for (int i = _actions.size() - 1; i >= 0; i--) {
                _actions.get(i).undo(content);
            }
        }
        @Override
        public void redo(Content content) {
            for (int i = 0; i < _actions.size(); i++) {
                _actions.get(i).redo(content);
            }
        }
        @Override
        public boolean canMerge(ContentAction action) {
            return false;
        }
        @Override
        public void merge(ContentAction action) {
            throw new UnsupportedOperationException();
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(_actions.size());
            for (ContentAction action : _actions) {
                parcel.writeParcelable(action, flags);
            }
        }
    }
    public static final class DeleteAction extends ContentAction {
        public final static Creator<DeleteAction> CREATOR = new Creator<>() {
            @Override
            public DeleteAction createFromParcel(Parcel parcel) {
                var o = new DeleteAction();
                o.startLine = parcel.readInt();
                o.startColumn = parcel.readInt();
                o.endLine = parcel.readInt();
                o.endColumn = parcel.readInt();
                o.text = parcel.readString();
                return o;
            }
            @Override
            public DeleteAction[] newArray(int size) {
                return new DeleteAction[size];
            }
        };
        public int startLine, endLine, startColumn, endColumn;
        public transient long createTime = System.currentTimeMillis();
        public CharSequence text;
        @Override
        public void undo(Content content) {
            content.insert(startLine, startColumn, text);
        }
        @Override
        public void redo(Content content) {
            content.delete(startLine, startColumn, endLine, endColumn);
        }
        @Override
        public boolean canMerge(ContentAction action) {
            if (action instanceof DeleteAction) {
                DeleteAction ac = (DeleteAction) action;
                return (ac.endColumn == startColumn && ac.endLine == startLine
                        && ac.text.length() + text.length() < 10000
                        && Math.abs(ac.createTime - createTime) < sMergeTimeLimit);
            }
            return false;
        }
        @Override
        public void merge(ContentAction action) {
            if (!canMerge(action)) {
                throw new IllegalArgumentException();
            }
            DeleteAction ac = (DeleteAction) action;
            this.startColumn = ac.startColumn;
            this.startLine = ac.startLine;
            StringBuilder sb;
            if (text instanceof StringBuilder) {
                sb = (StringBuilder) text;
            } else {
                sb = new StringBuilder(text);
                text = sb;
            }
            sb.insert(0, ac.text);
        }
        @NonNull
        @Override
        public String toString() {
            return "DeleteAction{" +
                    "startLine=" + startLine +
                    ", endLine=" + endLine +
                    ", startColumn=" + startColumn +
                    ", endColumn=" + endColumn +
                    ", createTime=" + createTime +
                    ", text=" + text +
                    '}';
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(startLine);
            parcel.writeInt(startColumn);
            parcel.writeInt(endLine);
            parcel.writeInt(endColumn);
            parcel.writeString(text.toString());
        }
    }
    public static final class ReplaceAction extends ContentAction {
        public final static Creator<ReplaceAction> CREATOR = new Creator<>() {
            @Override
            public ReplaceAction createFromParcel(Parcel parcel) {
                var o = new ReplaceAction();
                o.insert = parcel.readParcelable(ReplaceAction.class.getClassLoader());
                o.delete = parcel.readParcelable(ReplaceAction.class.getClassLoader());
                return o;
            }
            @Override
            public ReplaceAction[] newArray(int size) {
                return new ReplaceAction[size];
            }
        };
        public InsertAction insert;
        public DeleteAction delete;
        @Override
        public void undo(Content content) {
            insert.undo(content);
            delete.undo(content);
        }
        @Override
        public void redo(Content content) {
            delete.redo(content);
            insert.redo(content);
        }
        @Override
        public boolean canMerge(ContentAction action) {
            return false;
        }
        @Override
        public void merge(ContentAction action) {
            throw new UnsupportedOperationException();
        }
        @NonNull
        @Override
        public String toString() {
            return "ReplaceAction{" +
                    "insert=" + insert +
                    ", delete=" + delete +
                    '}';
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeParcelable(insert, flags);
            parcel.writeParcelable(delete, flags);
        }
    }
}
