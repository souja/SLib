package com.souja.lib.utils.pageTransformer;

import android.view.View;

public class CubeOutTransformer  extends ABaseTransformer {

    @Override
    public void onTransform(View view, float position) {
        view.setPivotX(position < 0f ? view.getWidth() : 0f);
        view.setPivotY(view.getHeight() * 0.5f);
        view.setRotationY(90f * position);
    }

    @Override
    public boolean isPagingEnabled() {
        return true;
    }

}
