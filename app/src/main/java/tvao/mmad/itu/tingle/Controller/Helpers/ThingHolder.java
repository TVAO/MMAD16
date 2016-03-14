package tvao.mmad.itu.tingle.Controller.Helpers;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.List;

import tvao.mmad.itu.tingle.Model.Thing;

import static android.view.View.*;

/**
 * This class is a ViewHolder used to maintain a view for each Thing in the list.
 * findViewById() is called frequently during the scrolling of ListView, which can slow down performance.
 * Even when the Adapter returns an inflated view for recycling, you still need to look up the elements and update them.
 * A way around repeated use of findViewById() is to use the "view holder" design pattern.
 * A ViewHolder object stores each of the component views inside the tag field of the Layout,
 * so you can immediately access them without the need to look them up repeatedly.
 */
public class ThingHolder extends SwappingHolder implements OnClickListener, OnLongClickListener {

    private TextView mTextView;
    private Thing mThing;
    private static MultiSelector mMultiSelector = new MultiSelector(); // Used to select multiple items

    // Enter ActionMode as part of selection mode
//    private ModalMultiSelectorCallback mActionModeCallback = new ModalMultiSelectorCallback() {
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
//        {
//            return false;
//        }
//    };

    public ThingHolder(View itemView)
    {
        super(itemView, mMultiSelector); // multi selector communicates with ViewHolder
        mTextView = (TextView) itemView; // findViewById(R.id.list_item_thing_title_text_view)

        itemView.setClickable(true);
        itemView.setLongClickable(true);
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

    /**
     * Navigate to detailed screen about thing on short click.
     * Called when not in selection mode.
     * @param view - view of item.
     */
    @Override
    public void onClick(View view) {
        if (!mMultiSelector.tapSelection(ThingHolder.this))
        {
            // Navigate to detail screen
        }
    }

    /**
     * Enter selection mode of items on long press.
     * @param view - view of item.
     * @return true if item is selected, false if already selected.
     */
    @Override
    public boolean onLongClick(View view)
    {
        if (!mMultiSelector.isSelectable()) // Check if multi selector is already in selection mode
        {
            //((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
            mMultiSelector.setSelectable(true); // Enter selection mode
            mMultiSelector.setSelected(ThingHolder.this, true); // Set selected item
            return true;
        }
        return false;
    }
}
