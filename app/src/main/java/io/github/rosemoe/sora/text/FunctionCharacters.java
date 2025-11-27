
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;

public class FunctionCharacters {
    private final static String[] names = {
            "NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK",
            "BEL", "BS", "HT", "LF", "VT", "FF", "CR", "SO",
            "SI", "DLE", "DC1", "DC2", "DC3", "DC4", "NAK",
            "SYN", "ETB", "CAN", "EM", "SUB", "ESC", "FS",
            "GS", "RS", "US", "SP"
    };
    public static boolean isFunctionCharacter(char letter) {
        return letter < 32 || letter == 127;
    }
    public static boolean isEditorFunctionChar(char letter) {
        return letter != '\t' && isFunctionCharacter(letter);
    }
    @NonNull
    public static String getNameForFunctionCharacter(char letter) {
        if (letter < 32) {
            return names[letter];
        } else if (letter == 127) {
            return "DEL";
        }
        return "UNK";
    }
}
