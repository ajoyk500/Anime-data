
package io.github.rosemoe.sora.langs.textmate.registry.provider;

import android.content.res.AssetManager;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class AssetsFileResolver implements FileResolver {
    private AssetManager assetManager;
    public AssetsFileResolver(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    @Nullable
    @Override
    public InputStream resolveStreamByPath(String path) {
        try {
            return assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void dispose() {
        assetManager = null;
    }
}
