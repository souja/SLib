package com.souja.lib.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.tools.DensityUtil;

public class MSideBar extends View {
    private Paint paint;
    private TextView header;
    private float height;
    private ListView mListView;
    private RecyclerView mListViewR;
    private Context context;
    private boolean isRv = false;
    private SectionIndexer sectionIndex = null;

    public void setListView(ListView listView) {
        mListView = listView;
    }

    public void setListView(RecyclerView listView) {
        mListViewR = listView;
        isRv = true;
    }


    public MSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private String[] sections;

    private void init() {
        sections = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
                "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#5d5ff0"));
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(DensityUtil.sp2px(context, 10));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float center = getWidth() / 2;
        height = getHeight() / sections.length;
        for (int i = sections.length - 1; i > -1; i--) {
            canvas.drawText(sections[i], center, height * (i + 1), paint);
        }
    }

    private int sectionForPoint(float y) {
        int index = (int) (y / height);
        if (index < 0) {
            index = 0;
        }
        if (index > sections.length - 1) {
            index = sections.length - 1;
        }
        return index;
    }

    private void setHeaderTextAndscroll(MotionEvent event) {
        if (!isRv && mListView == null) return;
        if (isRv && mListViewR == null) return;

        String headerString = sections[sectionForPoint(event.getY())];
        if (headerString != null)
            header.setText(headerString);

        if (!isRv) {
            ListAdapter adapter = mListView.getAdapter();
            if (sectionIndex == null) {
                if (adapter instanceof HeaderViewListAdapter) {
                    sectionIndex = (SectionIndexer) ((HeaderViewListAdapter) adapter).getWrappedAdapter();
                } else if (adapter instanceof SectionIndexer) {
                    sectionIndex = (SectionIndexer) adapter;
                } else {
                    throw new RuntimeException("listview sets mAdapter does not implement SectionIndexer interface");
                }
            }
            String[] adapterSections = (String[]) sectionIndex.getSections();
            try {
                for (int i = adapterSections.length - 1; i > -1; i--) {
                    if (adapterSections[i].equals(headerString)) {
                        mListView.setSelection(sectionIndex.getPositionForSection(i));
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("setHeaderTextAndScroll", e.getMessage());
            }
        } else {
            RecyclerView.Adapter adapter = mListViewR.getAdapter();
            if (sectionIndex == null) {
                if (adapter instanceof SectionIndexer) {
                    sectionIndex = (SectionIndexer) adapter;
                } else {
                    throw new RuntimeException("listview sets mAdapter does not implement SectionIndexer interface");
                }
            }
            String[] adapterSections = (String[]) sectionIndex.getSections();
            try {
                for (int i = adapterSections.length - 1; i > -1; i--) {
                    if (adapterSections[i].equals(headerString)) {
                        mListViewR.scrollToPosition(sectionIndex.getPositionForSection(i) + 1);
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("setHeaderTextAndScroll", e.getMessage());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (header == null) {
                    header = ((View) getParent()).findViewById(R.id.floating_header);
                }
                setHeaderTextAndscroll(event);
                header.setVisibility(View.VISIBLE);
                if (mListener != null) mListener.onChange(true);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                setHeaderTextAndscroll(event);
                if (mListener != null) mListener.onChange(true);
                return true;
            }
            case MotionEvent.ACTION_UP:
                header.setVisibility(View.INVISIBLE);
                if (mListener != null) mListener.onChange(false);
                return true;
            case MotionEvent.ACTION_CANCEL:
                header.setVisibility(View.INVISIBLE);
                if (mListener != null) mListener.onChange(false);
                return true;
        }
        return super.onTouchEvent(event);
    }

    public interface OnSidebarFocusChangeListener {
        void onChange(boolean focus);
    }

    private OnSidebarFocusChangeListener mListener;

    public void setFocusChangeListener(OnSidebarFocusChangeListener listener) {
        mListener = listener;
    }
}
