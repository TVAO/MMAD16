package tvao.mmad.itu.tingle.Controller.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.List;

import tvao.mmad.itu.tingle.Controller.Activities.ThingPagerActivity;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;


/**
 * This class represents a fragment used to display all items in list.
 * The fragment is hosted by the activity TingleActivity.
 */
public class ThingListFragment extends Fragment {

    // Used to safe pager and count of things upon change of configuration (rotation)
    private static final String TAG = "thingListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mThingRecyclerView;
    private ThingAdapter mAdapter;
    private boolean mSubtitleVisible; // Keep track of subtitle visibility

    // Used to allow multi selection and deletion of selected items
    private MultiSelector mMultiSelector = new MultiSelector();
    private ModalMultiSelectorCallback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector)
    {

        /**
         * Called when action mode is first created. The menu supplied will be used to generate action buttons for the action mode.
         * @param actionMode - set of option mode callbacks that are only called for multi select action mode.
         * @param menu - menu used to populate action items.
         * @return true if action mode should be crated, false is entering mode should be aborted.
         */
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
        {
            super.onCreateActionMode(actionMode, menu);
            getActivity().getMenuInflater().inflate(R.menu.thing_list_item_context, menu);
            return true;
        }

        /**
         * This method is used to implement multi select with a contextual action mode.
         * Selecting an item will turn multi choice on and activate an Action mode representing the multi select interaction.
         * @param actionMode - set of option mode callbacks that are only called for multi select action mode.
         * @param menuItem - menu item that was clicked.
         * @return
         */
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
        {
            if (menuItem.getItemId() == R.id.menu_item_delete_thing)
            {
                // Need to finish the action mode before doing the following,
                // not after. No idea why, but it crashes.
                actionMode.finish();

                for (int i = ThingRepository.get(getActivity()).size(); i >= 0; i--)
                {
                    if (mMultiSelector.isSelected(i, 0))
                    {


                        Thing thing = ThingRepository.get(getActivity()).getThings().get(i);
                        //Thing thing = mThings.get(i);
                        ThingRepository.get(getActivity()).removeThing(thing.getId());

                        mAdapter.removeAt(i);

                        //mAdapter.notifyItemRemoved(i);
                        //mThingRecyclerView.getAdapter().notifyItemRemoved(i);
                    }
                }

                mMultiSelector.clearSelections();
                return true;

            }
            return false;
        }
    };

    // Todo consider removing this and implemented code in TingleActivity
    /**
     * This interface allows TingleFragment to communicate to host TingleActivity.
     * Interface is encapsulated in fragment to avoid use in other activities.
     * Interface is implemented by host activity determining what happens upon triggering the listener.
     */
    public interface onBackPressedListener
    {
        void onBackPressed();
    }

    /**
     * Call to do initial creation of fragment
     *
     * @param savedInstanceState - fragment rebuilt from saved state if not null
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Tell FM that fragment receives menu callbacks
        getActivity().setTitle(R.string.things_title);
        mSubtitleVisible = false;
    }

    /**
     * Note: since the fragment is retained. the bundle passed in after state is restored is null.
     * THe only way to pass parcelable objects is through the activities onSavedInstanceState and appropriate startup lifecycle
     * However after having second thoughts, since the fragment is retained then all the states and instance variables are
     * retained as well. no need to make the selection states percelable therefore just check for the selectionState
     * from the MultiSelector
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {

        if (mMultiSelector != null)
        {
            Bundle bundle = savedInstanceState;
            if (bundle != null)
            {
                mMultiSelector.restoreSelectionStates(bundle.getBundle(TAG));
            }

            if (mMultiSelector.isSelectable())
            {
                if (mDeleteMode != null)
                {
                    mDeleteMode.setClearOnPrepare(false);
                    ((AppCompatActivity) getActivity()).startSupportActionMode(mDeleteMode);
                }

            }
        }

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment
     *
     * @param inflater           - used to inflate view in fragment
     * @param container          - parent view that fragment is attached to
     * @param savedInstanceState - fragment rebuilt from saved state if not null
     * @return - fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_thing_list, container, false);

        mThingRecyclerView = (RecyclerView) view.findViewById(R.id.thing_recycler_view);
        mThingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // RecyclerView requires a LayoutManager
        //mThingRecyclerView.setAdapter(new ThingAdapter(ThingRepository.get(getActivity()).getThings()));
        mThingRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null)
        {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    // Toggle item and navigate to detailed screen
    private void selectThing(Thing thing)
    {
        Intent i = new Intent(getActivity(), ThingPagerActivity.class);
        i.putExtra(ThingFragment.EXTRA_THING_ID, thing.getId());
        startActivity(i);
    }

//    private void selectThing(Thing thing)
//    {
//        // start an instance of CrimePagerActivity
//        Intent i = new Intent(getActivity(), ThingPagerActivity.class);
//        i.putExtra(ThingFragment.EXTRA_THING_ID, thing.getId());
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
//            // NOTE: shared element transition here.
//            // Support library fragments do not support the three parameter
//            // startActivityForResult call. So to get this to work, the entire
//            // project had to be shifted over to use stdlib fragments,
//            // and the v13 ViewPager.
//
//            int index = ThingRepository.get(getActivity()).getThings().indexOf(thing);
//            //int index = mThings.indexOf(thing);
//            ThingHolder holder = (ThingHolder) mThingRecyclerView.findViewHolderForAdapterPosition(index);  // Take into account data changes
//
//            ActivityOptions options = ThingPagerActivity.getTransition(
//                    getActivity(), holder.itemView);
//
//            startActivityForResult(i, 0, options.toBundle());
//        }
//        else
//        {
//            startActivityForResult(i, 0);
//        }
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        //mThingAdapter.notifyDataSetChanged();
//        mThingRecyclerView.getAdapter().notifyDataSetChanged();
//    }

    /**
     * This method is called whenever Fragment with list of items is shown to user.
     * The content of the list is updated each time the user displays the things in the list.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        updateUI();
    }

    /**
     * This method is used to pass data from saved bundle upon change of device configuration (rotation).
     * By way of example, the total number of things shown in menu bar subtitle should be transferred in landscape mode.
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBundle(TAG, mMultiSelector.saveSelectionStates()); // Save selected items
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible); // Save count of total things
    }

    /**
     * This method is used to inflate a custom menu with actions bars used to add and delete things.
     *
     * @param menu     - toolbar menu in top right corner
     * @param inflater - instantiate menu layout items into menu objects
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_thing_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) //&& subtitleItem != null)
        {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    /**
     * This method responds to the selection of the menu item.
     * It creates a new Thing and adds it to the database and then starts an instance of the ThingPagerActivity to edit the new Thing.
     * @param item - menu item
     * @return true if menu item has been handled and require no further processing
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_item_new_thing: // Add new thing
                Thing thing = new Thing();
                ThingRepository.get(getActivity()).addThing(thing);
                //mThingRecyclerView.getAdapter().notifyItemInserted(mThings.indexOf(thing));
                int indexPosition = ThingRepository.get(getActivity()).getThings().indexOf(thing);
                mAdapter.notifyItemInserted(indexPosition);
                return true;

            case R.id.menu_item_show_subtitle: // Show total items
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu(); // Option menu showing amount of things changed
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // Set subtitle in toolbar showing number of things in total
    private void updateSubtitle()
    {
        ThingRepository thingRepository = ThingRepository.get(getActivity());
        int thingCount = thingRepository.size();
        String subtitle = getString(R.string.subtitle_format, thingCount);

        if (!mSubtitleVisible)
        {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    // Get data from singleton repository and setup adapter with reloaded items
    private void updateUI()
    {
        ThingRepository thingRepository = ThingRepository.get(getActivity());
        List<Thing> things = thingRepository.getThings();

        if (mAdapter == null)
        {
            mAdapter = new ThingAdapter(things);
            mThingRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setThings(things);
            //mAdapter.notifyDataSetChanged(); // Todo use specific notify option to avoid overhead of reloading all items
        }

        updateSubtitle();
    }

    /**
     * Callback to be invoked when the context menu for this view is being built.
     * Shows delete action item in menu bar.
     * @param menu - context menu to call that is built.
     * @param view - the view for which menu is built.
     * @param menuInfo - extra information about menu.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.thing_list_item_context, menu);
    }

    /**
     * This class is a ViewHolder used to maintain a view for each Thing row in the RecyclerView.
     * findViewById() is called frequently during the scrolling of list, which can slow down performance.
     * Even when the Adapter returns an inflated view for recycling, you still need to look up the elements and update them.
     *
     * A way around repeated use of findViewById() is to use the "view holder" design pattern.
     * A ViewHolder object stores each of the component views inside the tag field of the Layout,
     * so you can immediately access them without the need to look them up repeatedly.
     *
     * ViewHolder also becomes the most natural place to handle any click events for a specific item.
     * ViewHolder is in a clear position to act as a row-level controller object that handles those kinds of details.
     * ViewHolder is the last piece: it’s responsible for handling any events that occur on a specific item that RecyclerView displays.
     *
     * Note that ViewHolder does not support any tools for selection and thus use MultiSelection library instead.
     * SwappingHolder is an extension of ViewHolder class with additional properties used to elevate items upon selection.
     */
    private class ThingHolder extends SwappingHolder implements OnClickListener, OnLongClickListener {

        private TextView mTextView;
        private Thing mThing;

        public ThingHolder(View itemView)
        {
            super(itemView, mMultiSelector); // multi selector communicates with ViewHolder
            //mTextView = (TextView) itemView; // findViewById(R.id.list_item_thing_title_text_view)

            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);

            mTextView = (TextView) itemView;
        }

        /**
         * When given a Thing, ThingHolder will update content of TextView to reflect state of Thing
         *
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
         *
         * @param view - view of item.
         */
        @Override
        public void onClick(View view)
        {
            if (mThing == null)
            {
                return;
            }
            if (!mMultiSelector.tapSelection(ThingHolder.this)) // Simulate tapping an item
            {
                // Toggle selection of item and navigate to detailed screen
                selectThing(mThing);
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
            ((AppCompatActivity) getActivity()).startSupportActionMode(mDeleteMode); // Turn on delete action mode.
            mMultiSelector.setSelected(this, true);
            return true;
        }

    }

    /**
     *
     * This Adapter is used to communicate with the RecyclerView,
     * when a ViewHolder needs to be created or connected with a Thing object.
     * RecyclerView does not know about Thing object but Thing Adapter knows about Thing model.
     * The adapter is used to create and bind ViewHolders.
     */
    private class ThingAdapter extends RecyclerView.Adapter<ThingHolder> {

        private List<Thing> mThings;

        public ThingAdapter(List<Thing> things)
        {
            mThings = things;
        }

        /**
         * Create view and wrap it in a view holder (for single list item).
         * Inflates the row layout and initializes the View Holder.
         * Once the View Holder is initialized it manages the findViewById() methods,
         * finding the views once and recycling them to avoid repeated calls
         * @param parent - host activity
         * @param pos - position of item view
         * @return view holder for item
         */
        @Override
        public ThingHolder onCreateViewHolder(ViewGroup parent, int pos)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ThingHolder(view);
        }

        /**
         * Bind ViewHolder view to model object (Thing data to view holder with text view on screen)
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(ThingHolder holder, int position)
        {
            Thing thing = mThings.get(position);
            holder.bindThing(thing);
            Log.d(TAG, "binding things" + thing + " at position" + position);
        }

        /**
         * Get total amount of items to display
         * @return amount of items
         */
        @Override
        public int getItemCount()
        {
            return mThings.size();
        }

        public void removeAt(int position)
        {
            mThings.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mThings.size());
        }

        public void setThings(List<Thing> things)
        {
            mThings = things;
        }
    }

}
