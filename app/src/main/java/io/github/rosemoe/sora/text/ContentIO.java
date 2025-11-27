
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public class ContentIO {
    private final static int BUFFER_SIZE = 16384;
    @NonNull
    public static Content createFrom(@NonNull InputStream stream) throws IOException {
        return createFrom(stream, Charset.defaultCharset());
    }
    @NonNull
    public static Content createFrom(@NonNull InputStream stream, @NonNull Charset charset) throws IOException {
        return createFrom(new InputStreamReader(stream, charset));
    }
    @NonNull
    public static Content createFrom(@NonNull Reader reader) throws IOException {
        var content = new Content();
        content.setUndoEnabled(false);
        var buffer = new char[BUFFER_SIZE];
        var wrapper = new CharArrayWrapper(buffer, 0);
        int count;
        while ((count = reader.read(buffer)) != -1) {
            if (count > 0) {
                if (buffer[count - 1] == '\r') {
                    var peek = reader.read();
                    if (peek == '\n') {
                        wrapper.setDataCount(count - 1);
                        var line = content.getLineCount() - 1;
                        content.insert(line, content.getColumnCount(line), wrapper);
                        line = content.getLineCount() - 1;
                        content.insert(line, content.getColumnCount(line), "\r\n");
                        continue;
                    } else if (peek != -1) {
                        wrapper.setDataCount(count);
                        var line = content.getLineCount() - 1;
                        content.insert(line, content.getColumnCount(line), wrapper);
                        line = content.getLineCount() - 1;
                        content.insert(line, content.getColumnCount(line), String.valueOf((char) peek));
                        continue;
                    }
                }
                wrapper.setDataCount(count);
                var line = content.getLineCount() - 1;
                content.insert(line, content.getColumnCount(line), wrapper);
            }
        }
        reader.close();
        content.setUndoEnabled(true);
        return content;
    }
    public static void writeTo(@NonNull Content text, @NonNull OutputStream stream, boolean closeOnSucceed) throws IOException {
        writeTo(text, stream, Charset.defaultCharset(), closeOnSucceed);
    }
    public static void writeTo(@NonNull Content text, @NonNull OutputStream stream, @NonNull Charset charset, boolean closeOnSucceed) throws IOException {
        writeTo(text, new OutputStreamWriter(stream, charset), closeOnSucceed);
    }
    public static void writeTo(@NonNull Content text, @NonNull Writer writer, boolean closeOnSucceed) throws IOException {
        final var buffered = (writer instanceof BufferedWriter) ? (BufferedWriter)writer : new BufferedWriter(writer, BUFFER_SIZE);
        try {
            text.runReadActionsOnLines(0, text.getLineCount() - 1, (Content.ContentLineConsumer2) (index, line, flag) -> {
                try {
                    buffered.write(line.getBackingCharArray(), 0, line.length());
                    buffered.write(line.getLineSeparator().getChars());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            var cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else {
                throw e;
            }
        }
        buffered.flush();
        if (closeOnSucceed) {
            buffered.close();
        }
    }
}
