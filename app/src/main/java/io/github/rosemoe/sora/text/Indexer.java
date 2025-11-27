
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;

public interface Indexer {
    int getCharIndex(int line, int column);
    int getCharLine(int index);
    int getCharColumn(int index);
    @NonNull
    CharPosition getCharPosition(int index);
    @NonNull
    CharPosition getCharPosition(int line, int column);
    void getCharPosition(int index, @NonNull CharPosition dest);
    void getCharPosition(int line, int column, @NonNull CharPosition dest);
}
