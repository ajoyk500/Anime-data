
package io.github.rosemoe.sora;

import android.content.Context;
import android.util.SparseIntArray;
import androidx.annotation.NonNull;

public class I18nConfig {
    private static final SparseIntArray mapping = new SparseIntArray();
    public static void mapTo(int originalResId, int newResId) {
        mapping.put(originalResId, newResId);
    }
    public static int getResourceId(int resId) {
        int newResource = mapping.get(resId);
        if (newResource == 0) {
            return resId;
        }
        return newResource;
    }
    public static String getString(@NonNull Context context, int resId) {
        return context.getString(getResourceId(resId));
    }
}
