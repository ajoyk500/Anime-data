
package io.github.rosemoe.sora.widget;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;

public class SymbolPairMatch {
    private final Map<Character, SymbolPair> singleCharPairMaps = new HashMap<>();
    private final Map<Character, List<SymbolPair>> multipleCharByEndPairMaps = new HashMap<>();
    private SymbolPairMatch parent;
    public SymbolPairMatch() {
        this(null);
    }
    public SymbolPairMatch(SymbolPairMatch parent) {
        setParent(parent);
    }
    protected void setParent(SymbolPairMatch parent) {
        this.parent = parent;
    }
    public void putPair(char singleCharacter, SymbolPair symbolPair) {
        singleCharPairMaps.put(singleCharacter, symbolPair);
    }
    public void putPair(char[] charArray, SymbolPair symbolPair) {
        char endChar = charArray[charArray.length - 1];
        var list = multipleCharByEndPairMaps.get(endChar);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(symbolPair);
        multipleCharByEndPairMaps.put(endChar, list);
    }
    public void putPair(String openString, SymbolPair symbolPair) {
        putPair(openString.toCharArray(), symbolPair);
    }
    @Nullable
    public final SymbolPair matchBestPairBySingleChar(char editChar) {
        var pair = singleCharPairMaps.get(editChar);
        if (pair == null && parent != null) {
            return parent.matchBestPairBySingleChar(editChar);
        }
        return pair;
    }
    public final List<SymbolPair> matchBestPairList(char editChar) {
        var result = multipleCharByEndPairMaps.get(editChar);
        if (result == null && parent != null) {
            var parentResult = parent.matchBestPairList(editChar);
            result = new ArrayList<>(parentResult);
        }
        return result == null ? Collections.emptyList() : result;
    }
    public void removeAllPairs() {
        singleCharPairMaps.clear();
        multipleCharByEndPairMaps.clear();
    }
    public static class SymbolPair {
        public final static SymbolPair EMPTY_SYMBOL_PAIR = new SymbolPair("", "");
        public final String open;
        public final String close;
        private SymbolPairEx symbolPairEx;
        private int cursorOffset;
        private int insertOffset;
        public SymbolPair(String open, String close) {
            this.open = open;
            this.close = close;
        }
        public SymbolPair(String open, String close, SymbolPairEx symbolPairEx) {
            this(open, close);
            this.symbolPairEx = symbolPairEx;
        }
        protected boolean shouldDoAutoSurround(Content content) {
            if (symbolPairEx == null) {
                return false;
            }
            return symbolPairEx.shouldDoAutoSurround(content);
        }
        protected void measureCursorPosition(int offsetIndex) {
            cursorOffset = offsetIndex + open.length();
            insertOffset = offsetIndex;
        }
        protected int getCursorOffset() {
            return cursorOffset;
        }
        public int getInsertOffset() {
            return insertOffset;
        }
        public interface SymbolPairEx {
            default boolean shouldDoAutoSurround(Content content) {
                return false;
            }
        }
    }
    public final static class DefaultSymbolPairs extends SymbolPairMatch {
        public DefaultSymbolPairs() {
            super.putPair('{', new SymbolPair("{", "}"));
            super.putPair('(', new SymbolPair("(", ")"));
            super.putPair('[', new SymbolPair("[", "]"));
            super.putPair('"', new SymbolPair("\"", "\"", new SymbolPair.SymbolPairEx() {
                @Override
                public boolean shouldDoAutoSurround(Content content) {
                    return content.getCursor().isSelected();
                }
            }));
            super.putPair('\'', new SymbolPair("'", "'", new SymbolPair.SymbolPairEx() {
                @Override
                public boolean shouldDoAutoSurround(Content content) {
                    return content.getCursor().isSelected();
                }
            }));
        }
    }
}
