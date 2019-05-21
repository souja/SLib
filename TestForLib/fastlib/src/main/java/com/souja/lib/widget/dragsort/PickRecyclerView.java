package com.souja.lib.widget.dragsort;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;

import com.myzt.doctor.R;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by Stefan on 2016/10/17.
 * 用于照片展示的RecyclerView，包含了一切可控性的逻辑，用于降低与Activity或者Fragment的耦合度
 */
public class PickRecyclerView extends RecyclerView implements OnStartDragListener {

    private ArrayList<String> imageList = new ArrayList<>();
    private DisplayRecyclerAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;


    public PickRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int itemRes;
        int spanCount;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PickRecyclerView);
        try {
            spanCount = a.getInteger(R.styleable.PickRecyclerView_mSpanCount, 3);
            itemRes = a.getResourceId(R.styleable.PickRecyclerView_mItemRes, R.layout.item_pick_recycle);
        } finally {
            a.recycle();
        }

        int maxCount = 6;
        mAdapter = new DisplayRecyclerAdapter(context, imageList, itemRes, maxCount, this);
        setLayoutManager(new GridLayoutManager(context, spanCount));
        setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this);
    }


    public void setOnOperateListener(OnOperateListener mOnOperateListener) {
        mAdapter.setOnOperateListener(mOnOperateListener);
    }

    /**
     * 绑定数据，请在onActivityResult() 方法中调用此方法
     *
     * @param newList 新的列表
     */
    public void bind(ArrayList<String> newList) {
        if (newList == null || newList.size() == 0) {
            LogUtil.e("图片路径无效,return");
            return;
        }
        if (equals(newList)) {
            LogUtil.e("图片路径相同,return");
            return;
        }

        imageList.clear();
        imageList.addAll(newList);
        mAdapter.notifyChange();
    }

    /**
     * 判断是否为相同数据
     */
    public boolean equals(ArrayList<String> newList) {
        if (imageList.size() == newList.size()) {
            for (int i = 0; i < imageList.size(); i++) {
                if (!imageList.get(i).equals(newList.get(i))) {
                    return false;
                }
            }
        } else return false;
        return true;
    }

    /**
     * 获取当前选中的照片数据
     */
    public ArrayList<String> getImageList() {
        return imageList;
    }

    //根据图片路径，获取对应描述信息
    public String getImgRemark(String path) {
        String remark = mAdapter.getTextMap().get(path);
        LogUtil.e("path=" + path + ",remark=" + remark);
        return remark;
    }

    public int getSize() {
        return imageList == null ? 0 : imageList.size();
    }

    @Override
    public void startDrag(ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

//    public ArrayMap<String, Bitmap> getBitmapMap() {
//        return mAdapter.getBitmapMap();
//    }

    public void setRemarkMap(ArrayMap<String, String> remarkMap) {
        mAdapter.setRemarkMap(remarkMap);
    }
}
