package com.souja.lib.widget.dragsort;

import android.view.View;

public abstract class OperateListenerAdapter implements OnOperateListener {



    /**
     * {@inheritDoc}
     */
    @Override
    public void onChanged() {

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClicked(View v, String picPath, int position) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemLongClicked(String picPath, int position) {

    }

    /**
     * {@inheritDoc}
     * @param position
     */
    @Override
    public void onRemoved(String position) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClickAdd() {

    }
}
