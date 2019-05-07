package com.souja.lib.utils.pageTransformer;

import android.view.View;

//抽屉
public class DrawerTransformer  extends ABaseTransformer {
    @Override
    public void onTransform(View view, float position) {
        if (position <= 0) {
            view.setTranslationX(0);
        } else if (position > 0 && position <= 1) {
            view.setTranslationX(-view.getWidth() / 2 * position);
        }
    }
}