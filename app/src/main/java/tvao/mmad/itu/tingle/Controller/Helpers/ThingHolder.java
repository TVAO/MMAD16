package tvao.mmad.itu.tingle.Controller.Helpers;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import tvao.mmad.itu.tingle.Model.Thing;

/**
 * This class is a ViewHolder used to maintain a view for each Thing in the list.
 * findViewById() is called frequently during the scrolling of ListView, which can slow down performance.
 * Even when the Adapter returns an inflated view for recycling, you still need to look up the elements and update them.
 * A way around repeated use of findViewById() is to use the "view holder" design pattern.
 * A ViewHolder object stores each of the component views inside the tag field of the Layout,
 * so you can immediately access them without the need to look them up repeatedly.
 */
public class ThingHolder extends ViewHolder {

    private TextView mTextView;
    private Thing mThing;
//    private List<Thing> mThings;
//    private View mItemView;

    public ThingHolder(View itemView)
    {
        super(itemView);
        mTextView = (TextView) itemView; // findViewById(R.id.list_item_thing_title_text_view)
//        mThings = things;
//        mItemView = itemView;
    }

    public Thing getThing(){
        return mThing;
    }

    /**
     * When given a Thing, ThingHolder will update content of TextView to reflect state of Thing
     * @param thing
     */
    public void bindThing(Thing thing)
    {
        mThing = thing;
        mTextView.setText(mThing.toString());
    }

}
