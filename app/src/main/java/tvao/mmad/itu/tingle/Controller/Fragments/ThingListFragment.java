package tvao.mmad.itu.tingle.Controller.Fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import tvao.mmad.itu.tingle.Helpers.BaseFragment;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;
import tvao.mmad.itu.tingle.Search.ISort;
import tvao.mmad.itu.tingle.Search.SearchHandler;


/**
 * This class represents a fragment used to display all items in list.
 * The fragment is hosted by the activity TingleActivity.
 */
public class ThingListFragment extends BaseFragment {

    // Used to safe pager and count of things upon change of configuration (rotation)
    private static final String TAG = "thingListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    onBackPressedListener mCallback; // Used to go back

    private RecyclerView mThingRecyclerView;
    private ThingAdapter mAdapter;
    private boolean mSubtitleVisible; // Keep track of subtitle visibility
    private SearchHandler mSearchHandler; // Search and sort content of items
    ISort sortingParameter;

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
                        ThingRepository.get(getActivity()).removeThing(thing.getId());
                        mAdapter.removeAt(i);
                    }
                }

                mMultiSelector.clearSelections();
                return true;

            }
            return false;
        }
    };

    /**
     * This interface allows TingleMainFragment to communicate to host TingleActivity.
     * Interface is encapsulated in fragment to avoid use in other activities.
     * Interface is implemented by host activity determining what happens upon triggering the listener.
     */
    public interface onBackPressedListener
    {
        void onBackPressed(); // Used to go back to main page from list
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
        mSearchHandler = new SearchHandler(ThingRepository.get(getContext()).getThings(), sortingParameter,
                new SearchHandler.AsyncResponse()
                {
                    @Override
                    public void processFinish(String searchResult)
                    {
                        // Todo get result of search and do something
                    }
                });
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

        // Set items list
        mThingRecyclerView = (RecyclerView) view.findViewById(R.id.thing_recycler_view);
        mThingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // RecyclerView requires a LayoutManager

        mThingRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null)
        {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateList();

        return view;
    }

    // Toggle item and navigate to detailed screen
    private void selectThing(Thing thing)
    {
        Intent intent = ThingPagerActivity
                .newIntent(getActivity(), thing.getId());
        startActivity(intent);
    }

    // Create new thing and navigate to detailed screen activity
    private void addNewThing(Thing thing)
    {
        ThingRepository.get(getActivity()).addThing(thing);
        Intent intent = ThingPagerActivity
                .newIntent(getActivity(), thing.getId());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //mThingAdapter.notifyDataSetChanged();
        mThingRecyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * This method is called whenever Fragment with list of items is shown to user.
     * The content of the list is updated each time the user displays the things in the list.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        updateList();
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
        menu.clear(); // clear menu object before adding items to avoid duplicate menu items upon rotation

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_thing_list, menu);

        //Set groups
        MenuItem defaultSearchItem = menu.findItem(R.id.search_what);
        defaultSearchItem.setChecked(true);

        // Set search view used to search for items
        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        setSearchView(searchItem);

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

    private void setSearchView(MenuItem searchItem)
    {
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                List<Thing> result = mSearchHandler.search(query.toLowerCase().trim(),
                                                           ThingRepository.get(getContext()).getThings());
                if (result == null)
                {
                    makeToast(getString(R.string.item_notFound_toast));
                    return false;
                }
                mAdapter.setThings(result);
                mAdapter.notifyDataSetChanged(); // Todo consider more specific refresh
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (newText.length() == 0)
                {
                    updateList(); // Reset list
                } else {
                    List<Thing> result = mSearchHandler.search(newText.toLowerCase().trim(),
                                                               ThingRepository.get(getContext()).getThings());
                    if (result == null)
                    {
                        return false;
                    }
                    mAdapter.setThings(result);
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
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
                addNewThing(thing); // Go to detailed screen with new thing
                return true;

            case R.id.menu_item_show_subtitle: // Show total items
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu(); // Option menu showing amount of things changed
                updateSubtitle();
                return true;

//            case R.id.delete_Button:
//                deleteItem();
//                return true;
//
//            case R.id.back_button:
//                goBack();
//                return true;

            case R.id.search_what:
                item.setChecked(true);
                mSearchHandler.setSearchType(SearchHandler.Type.WHAT);
                return true;

            case R.id.search_where:
                item.setChecked(true);
                mSearchHandler.setSearchType(SearchHandler.Type.WHERE);
                return true;

            case R.id.sortWhat:
                setSortedList(ISort.sortingParameter.WHAT);
                return true;

            case R.id.sortWhere:
                setSortedList(ISort.sortingParameter.WHERE);
                return true;

            case R.id.sortDate:
                setSortedList(ISort.sortingParameter.DATE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Callback to be invoked when the context menu for this view is being built.
     * Shows delete action item in menu bar.
     * @param menu - context menu to call that is built.
     * @param view - the view for which menu is built.
     * @param menuInfo - extra information about menu.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        getActivity().getMenuInflater().inflate(R.menu.thing_list_item_context, menu);
    }

    // Go back to list if in portrait mode
    private void goBack()
    {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mCallback.onBackPressed();
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
    private void updateList()
    {
        ThingRepository thingRepository = ThingRepository.get(getActivity());
        List<Thing> things = thingRepository.getThings();
        mSearchHandler.sortDefault(things);

        if (mAdapter == null)
        {
            mAdapter = new ThingAdapter(things);
            mThingRecyclerView.setAdapter(mAdapter);
        } else
        {
            mAdapter.setThings(things);
            mAdapter.notifyDataSetChanged(); // Only reload items when going back from detailed screen so no real overhead
        }

        updateSubtitle();
    }

    /**
     * Sorts list of items and update view.
     * @param sortingParameter - parameter used to sort (name or location).
     */
    private void setSortedList(ISort.sortingParameter sortingParameter)
    {
        if (mAdapter.mThings.size() == 0) return;
        List<Thing> result = mSearchHandler.sort(mAdapter.mThings, sortingParameter);
        mAdapter.setThings(result);
        mAdapter.notifyDataSetChanged(); // Todo consider more specific notify due to performance
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
