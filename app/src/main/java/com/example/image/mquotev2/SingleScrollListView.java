package com.example.image.mquotev2;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ListView;

/**
 * Created by bradj on 11/12/13.
 */
public class SingleScrollListView extends ListView
{
    private boolean mSingleScroll = false;
    private VelocityTracker mVelocity = null;
    final private float mEscapeVelocity = 2000.0f;
    final private int mMinDistanceMoved = 20;
    private float mStartY = 0;

    public SingleScrollListView(Context context)
    {
        super(context);
    }

    public SingleScrollListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SingleScrollListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    int fix_position = 0;
    public void setSingleScroll(boolean aSingleScroll) { mSingleScroll = aSingleScroll; }
    public int getVerticalScrollOffset() { return getFirstVisiblePosition(); }
    public void set_fixed(int screen_width, Context ct){
        fix_position = - (int)(screen_width/2 - convertDpToPixel(190,ct));
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent aMotionEvent)
    {
        if (aMotionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (mSingleScroll && mVelocity == null)
                mVelocity = VelocityTracker.obtain();
            mStartY = aMotionEvent.getY();
            return super.dispatchTouchEvent(aMotionEvent);
        }

        if (aMotionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            if (mVelocity != null)
            {
                if (Math.abs(aMotionEvent.getY() - mStartY) > mMinDistanceMoved)
                {
                    mVelocity.computeCurrentVelocity(1000);
                    float velocity = mVelocity.getYVelocity();

                    if (aMotionEvent.getY() > mStartY)
                    {
                        // always lock
                        if (velocity > mEscapeVelocity)
                            smoothScrollToPositionFromTop(getFirstVisiblePosition(),fix_position,300);
                        else
                        {
                            // lock if over half way there
                            View view = getChildAt(0);
                            if (view != null)
                            {
                                if (view.getBottom() >= getHeight() / 2)
                                    smoothScrollToPositionFromTop(getFirstVisiblePosition(),fix_position,300);
                                else
                                    smoothScrollToPositionFromTop(getFirstVisiblePosition(),0,300);
                            }
                        }
                    }
                    else
                    {
                        if (Math.abs(aMotionEvent.getY()-mStartY)>mMinDistanceMoved)
                            smoothScrollToPositionFromTop(getLastVisiblePosition(),fix_position,300);
                        else
                        {
                            // lock if over half way there
                            View view = getChildAt(1);
                            if (view != null)
                            {
                                if (view.getTop() <= getHeight() / 2)
                                    smoothScrollToPositionFromTop(getLastVisiblePosition(),fix_position,300);
                                else
                                    smoothScrollToPositionFromTop(getLastVisiblePosition(),fix_position,300);
                            }
                        }
                    }
                }
                mVelocity.recycle();
            }
            mVelocity = null;

            if (mSingleScroll)
            {
                if (Math.abs(aMotionEvent.getY() - mStartY) > mMinDistanceMoved)
                    return super.dispatchTouchEvent(aMotionEvent);
            }
            else
                return super.dispatchTouchEvent(aMotionEvent);
        }

        if (mSingleScroll)
        {
            if (mVelocity == null)
            {
                mVelocity = VelocityTracker.obtain();
                mStartY = aMotionEvent.getY();
            }
            mVelocity.addMovement(aMotionEvent);
        }

        return super.dispatchTouchEvent(aMotionEvent);
    }
}
