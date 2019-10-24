package com.brucej;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class WaterFallLayout extends ViewGroup {
    public WaterFallLayout(Context context) {
        this(context, null);
    }

    public WaterFallLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterFallLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        //return super.generateLayoutParams(attrs);
        return new MarginLayoutParams(getContext(), attrs);
    }

    private List<List<View>> viewList = new ArrayList<>();
    private List<Integer> heightList = new ArrayList<>();
    private boolean isMeasure = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //第二次绘制 直接退出
        if (isMeasure) {
            return;
        }
        isMeasure = true;
        //获取 父布局的尺寸规格参数
        int pWithMode = MeasureSpec.getMode(widthMeasureSpec);
        int pHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int pWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int pHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        //当前控件宽高
        int measureWidth = 0;
        int measureHeight = 0;

        //进行二级测量优化
        if (pWithMode == MeasureSpec.EXACTLY && pHeightMode == MeasureSpec.EXACTLY) {
            measureWidth = pWidthSize;
            measureHeight = pHeightSize;
            //setMeasuredDimension(measureWidth, measureHeight);
        } else {
            int childCount = getChildCount();
            int childWidth = 0;
            int childHeight = 0;
            int currentLineWidth = 0;
            int currentLineHeight = 0;
            ArrayList<View> currentLineViews = new ArrayList<>();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                //测量子View
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                childWidth = child.getMeasuredWidth() + layoutParams.leftMargin
                        + layoutParams.rightMargin;
                childHeight = child.getMeasuredHeight() + layoutParams.topMargin
                        + layoutParams.bottomMargin;
                //如果换行
                if (currentLineWidth + childWidth > pWidthSize) {
                    //记录当前行 的数据
                    viewList.add(currentLineViews);
                    heightList.add(currentLineHeight);
                    //开始保存 新的一行的数据
                    currentLineWidth = childWidth;
                    currentLineHeight = childHeight;

                    currentLineViews = new ArrayList<>();
                    currentLineViews.add(child);

                    measureWidth = Math.max(measureWidth, currentLineWidth);
                    measureHeight = Math.max(measureHeight, currentLineHeight);
                } else {//没有换行
                    currentLineWidth += childWidth;
                    currentLineHeight = Math.max(currentLineHeight, childHeight);
                    //添加到当前行的容器
                    currentLineViews.add(child);

                    measureWidth = Math.max(measureWidth, currentLineWidth);
                    measureHeight = Math.max(measureHeight, currentLineHeight);
                }
                //处理 最后一个child
                if (i == childCount - 1) {
                    heightList.add(currentLineHeight);
                    viewList.add(currentLineViews);
                }
            }
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int l = 0, t = 0, r = 0, b = 0;
        int lineWidth = 0, lineHeight = 0;
        for (int i = 0; i < viewList.size(); i++) {
            //每一行
            for (int j = 0; j < viewList.get(i).size(); j++) {
                View child = viewList.get(i).get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                l = lineWidth + lp.leftMargin;
                t = lineHeight + lp.topMargin;
                r = l + child.getMeasuredWidth();
                b = t + child.getMeasuredHeight();
                //进行布局
                child.layout(l, t, r, b);
                //
                lineWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            lineWidth = 0;
            lineHeight += heightList.get(i);
        }
    }
}
