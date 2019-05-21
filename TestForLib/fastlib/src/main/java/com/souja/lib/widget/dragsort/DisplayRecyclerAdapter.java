package com.souja.lib.widget.dragsort;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.souja.lib.R;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.ScreenUtil;
import com.souja.lib.widget.PopZoomGallery;
import com.souja.lib.widget.ZoomImageModel;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stefan on 2016/10/17.
 * 用于展示选择的照片Recycler的Adapter
 */
public class DisplayRecyclerAdapter extends RecyclerView.Adapter<DisplayRecyclerAdapter.HolderImgItem>
        implements OnMoveAndSwipedListener {
    private final RequestManager mRequestManager;
    private ArrayList<String> imagePathList;
    private OnOperateListener mOnOperateListener;
    private final int max;
    private PickRecyclerView mPickRecyclerView;
    private ArrayMap<String, String> mRemarkMap;

    public ArrayMap<String, String> getTextMap() {
        return mRemarkMap;
    }

    private Context mContext;
    private int itemRes;
    private Handler mHandler;

    public DisplayRecyclerAdapter(Context context,
                                  ArrayList<String> imagePathList,
                                  int itemRes,
                                  int max,
                                  PickRecyclerView pickRecyclerView) {
        mContext = context;
        mPickRecyclerView = pickRecyclerView;
        this.itemRes = itemRes;
        this.mRequestManager = Glide.with(mContext);
        this.imagePathList = imagePathList;
        this.max = max;
        mRemarkMap = new ArrayMap<>();
        mHandler = new Handler();
    }

    @Override
    public HolderImgItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HolderImgItem(LayoutInflater.from(parent.getContext())
                .inflate(itemRes, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(HolderImgItem holder, int position) {
        if (hasAddDrawable() && position == imagePathList.size()) {//最后一张'添加'的图标
//            holder.mEdImgRemark.setVisibility(View.INVISIBLE);
            holder.mDeleteBtn.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(v -> {
                if (mOnOperateListener != null)
                    mOnOperateListener.onClickAdd();
            });
            holder.itemView.setOnTouchListener(null);
            mRequestManager.load(R.drawable.ic_lib_add)
                    .into(holder.mPicIv);
            holder.mEdImgRemark.setEnabled(false);
        } else {
//            holder.mEdImgRemark.setVisibility(View.VISIBLE);
            holder.mEdImgRemark.setEnabled(true);
            holder.mDeleteBtn.setVisibility(View.VISIBLE);
            String imgPath = getItem(position);

          /*  if (imgPath.contains("http:") || imgPath.contains("https:")) {
                LogUtil.e("加载网络图片:" + imgPath);
                mRequestManager.load(GlideUtil.getValidUrl(imgPath))
                        .placeholder(R.drawable.ic_loading)
                        .into(holder.mPicIv);
            } else {
                LogUtil.e("加载本地图片:" + imgPath);
                mRequestManager.load(imgPath)
                        .placeholder(R.drawable.ic_loading)
                        .into(holder.mPicIv);
            }*/
            //图片备注
            holder.mEdImgRemark.setCursorVisible(false);
            holder.mEdImgRemark.setOnTouchListener((v, event) -> {
                LogUtil.e("cur position:" + position);
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    holder.mEdImgRemark.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            });
            //填入备注
            if (mRemarkMap != null && mRemarkMap.containsKey(imgPath)) {
                String remark = mRemarkMap.get(imgPath);
                LogUtil.e(imgPath + ":::" + remark);
                if (remark != null && !remark.isEmpty() && !remark.equals("null"))
                    holder.mEdImgRemark.setText(remark);
                else
                    holder.mEdImgRemark.setText("");
            }
            holder.mEdImgRemark.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!bDelete) {
                        mOnOperateListener.onChanged();
                        syncRemark(imgPath, editable.toString());
                    }
                }
            });

            //删除图片
            holder.mDeleteBtn.setOnClickListener(v -> onDelete(position));
            //预览图片
            holder.itemView.setOnClickListener(v -> MTool.showPopImages(mContext, v, imagePathList, imgPath));
            //图片排序
            holder.itemView.setOnLongClickListener(v -> {
                if (mOnOperateListener != null && (!hasAddDrawable() || hasAddDrawable() && position != imagePathList.size()))
                    mOnOperateListener.onItemLongClicked(imgPath, position);
                LogUtil.e("longclick................");
                //如果判断为最后一张添加的图，不做拖动响应
                if (hasAddDrawable() && holder.getLayoutPosition() == imagePathList.size()) {
                    LogUtil.e("判断为最后一张添加的图，不做拖动响应");
                    return false;
                }
                //回调RecyclerListFragment中的startDrag方法
                //让mItemTouchHelper执行拖拽操作
                LogUtil.e("让mItemTouchHelper执行拖拽操作");
                mPickRecyclerView.startDrag(holder);
                return true;
            });
        }
    }

    public String getItem(int pos) {
        return imagePathList.get(pos);
    }

    private void syncRemark(String path, String s) {
        LogUtil.e("保存备注：path= " + path + " remark= " + s);
        mRemarkMap.put(path, s);
    }

    public boolean bDelete = false;

    private void onDelete(int position) {
        bDelete = true;
        String curPath = getItem(position);
        imagePathList.remove(curPath);
        LogUtil.e("删除图片：path= " + curPath);

        if (mRemarkMap.containsKey(curPath)) {
            LogUtil.e("删除备注：" + mRemarkMap.get(curPath));
            mRemarkMap.remove(curPath);
        }
        notifyDataSetChanged();

        mHandler.postDelayed(() -> bDelete = false, 100);

        if (mOnOperateListener != null) {
            mOnOperateListener.onRemoved(curPath);//同步图片数量
        }
    }

    public void notifyChange() {
        bDelete = true;
        notifyDataSetChanged();

        mHandler.postDelayed(() -> bDelete = false, 100);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (toPosition >= imagePathList.size() || fromPosition >= imagePathList.size())
            return false;

        //数据机构上实现from到to的插入
        if (fromPosition < toPosition) {
            //分别把中间所有的item的位置重新交换
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(imagePathList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(imagePathList, i, i - 1);
            }
        }
        mOnOperateListener.onChanged();
        //交换RecyclerView列表中item的位置
        notifyItemMoved(fromPosition, toPosition);
        LogUtil.e("from:" + fromPosition + "--->" + "to:" + toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int pos) {
//        mPickRecyclerView.remove(pos);
    }

    /**
     * holder被回收时，清除相应缓存
     */
    @Override
    public void onViewRecycled(HolderImgItem holder) {
//        Glide.clear(holder.mPicIv);
        super.onViewRecycled(holder);
    }

    @Override
    public void onViewDetachedFromWindow(HolderImgItem holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


    /**
     * 判断是否显示最后一张的添加图
     */
    private boolean hasAddDrawable() {
        boolean has = false;
        if (max > imagePathList.size()) has = true;
        return has;
    }

    @Override
    public int getItemCount() {
        int itemCount = imagePathList == null ? 0 : hasAddDrawable() ? (imagePathList.size() + 1) : imagePathList.size();
        return itemCount;
    }

    public void setRemarkMap(ArrayMap<String, String> remarkMap) {
        this.mRemarkMap = remarkMap;
    }

    public class HolderImgItem extends RecyclerView.ViewHolder {
        private ImageView mPicIv;
        private ImageButton mDeleteBtn;
        private EditText mEdImgRemark;

        HolderImgItem(View itemView) {
            super(itemView);
            ScreenUtil.initScale(itemView);
            mPicIv = itemView.findViewById(R.id.__display_iv);
            mDeleteBtn = itemView.findViewById(R.id.ib_del);
            mEdImgRemark = itemView.findViewById(R.id.ed_photoMark);
        }
    }

    public void setOnOperateListener(OnOperateListener mOnOperateListener) {
        this.mOnOperateListener = mOnOperateListener;
    }

}
