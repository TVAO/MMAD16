package tvao.mmad.itu.tingle.Helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * This class is used to implement an OnClickListener for a RecyclerView.
 * This is used to register click events on items used for e.g. deletion.
 */
@Deprecated
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener mListener;

    public interface OnItemClickListener
    {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener)
    {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
        {
            @Override public boolean onSingleTapUp(MotionEvent e)
            {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e)
    {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e))
        {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView)); // getChildPosition
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}