package me.kareluo.imaging.core.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;

import me.kareluo.imaging.IMG;

/**
 * Created by felix on 2017/12/26 下午3:07.
 */

public class IMGFileDecoder extends IMGDecoder {

    public IMGFileDecoder(Uri uri) {
        super(uri);
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        Uri uri = getUri();
        if (uri == null) {
            return null;
        }

        String path = uri.getPath();
        if (IMG.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (file.exists()) {
            return BitmapFactory.decodeFile(path, options);
        }

        return null;
    }
}
