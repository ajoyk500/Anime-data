
package io.github.rosemoe.sora.lang.styling;

import java.util.List;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public interface Spans {
    Spans copy();
    void adjustOnInsert(CharPosition start, CharPosition end);
    void adjustOnDelete(CharPosition start, CharPosition end);
    Reader read();
    boolean supportsModify();
    Modifier modify();
    int getLineCount();
    interface Reader {
        void moveToLine(int line);
        int getSpanCount();
        Span getSpanAt(int index);
        List<Span> getSpansOnLine(int line);
    }
    interface Modifier {
        void setSpansOnLine(int line, List<Span> spans);
        void addLineAt(int line, List<Span> spans);
        void deleteLineAt(int line);
    }
}
