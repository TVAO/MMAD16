package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.content.Context;
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
import tvao.mmad.itu.tingle.Helpers.Search.ISort;
import tvao.mmad.itu.tingle.Helpers.Search.SearchHandler;
import tvao.mmad.itu.tingle.Helpers.Search.SelectionSort;


/**
 * This class represents a fragment used to display all items in list.
 * The fragment is hosted by the activity TingleActivity.
 */
public class ThingListFragment extends BaseFragment {

    // Tags used to save pager and count of things upon change of configuration (rotation)
    private static final String TAG = "thingListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private ThingListFragmentEventListener mCallback; // Used to go back to main activity
    private RecyclerView mThingRecyclerView;
    private ThingAdapter mAdapter;
    private boolean mSubtitleVisible; // Keep track of subtitle visibility
    private SearchHandler mSearchHandler; // Search and sort content of items

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
         * This method is used to implement multi selection with a contextual action mode.
         * Selecting an item will turn multi choice on and activate an Action mode representing the multi select interaction.
         * @param actionMode - set of option mode callbacks that are only called for multi select action mode.
         * @param menuItem - menu item that was clicked.
         * @return true if item was deleted.
         */
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
        {
            if (menuItem.getItemId() == R.id.menu_item_delete_thing)
            {
                // Need to finish the action mode before selecting items or app crashes
                actionMode.finish();

                for (int i = ThingRepository.get(getActivity()).size(); i >= 0; i--)
                {
                    if (mMultiSelector.isSelected(i, 0))
                    {
                        Thing thing = mAdapter.getThings().get(i);
                        ThingRepository.get(getActivity()).removeThing(thing.getId());
                    }
                }
                mMultiSelector.clearSelections();
                updateList();

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
    public interface ThingListFragmentEventListener
    {
        void onBackPressed(); // Used to go back to main page from list
    }

    /**
     * The fragment captures the interface implementation in the activity TingleActivity during onAttach() lifecycle method.
     * This method calls the interface methods in order to communicate with the activity TingleActivity.
     * The method checks if the container activity has implemented the callback interface, otherwise throws an exception.
     * @param context - context of host activity.
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Activity activity = new Activity();

        try
        {
            activity = (Activity) context;
            mCallback = (ThingListFragmentEventListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement TingleMainFragmentEventListener");
        }
    }

    /**
     * Call to do initial creation of fragment.
     * @param savedInstanceState - fragment rebuilt from saved state if not null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Tell FM that fragment receives menu callbacks
        getActivity().setTitle(R.string.things_title);
        mSubtitleVisible = false;
        mSearchHandler = new SearchHandler(new SelectionSort());
    }

    /**
     * Note: since the fragment is retained. the bundle passed in after state is restored is null.
     * The only way to pass parcelable objects is through the activities onSavedInstanceState and appropriate startup lifecycle
     * However after having second thoughts, since the fragment is retained then all the states and instance variables are
     * retained as well. No need to make the selection states parcelable, therefore just check for the selectionState
     * from the MultiSelector.
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
     * @param inflater           - used to inflate view in fragment
     * @param container          - parent view that fragment is attached to
     * @param savedInstanceState - fragment rebuilt from saved state if not null
     * @return - fragment view displaying items in a list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_thing_list, container, false);
        mThingRecyclerView = (RecyclerView) view.findViewById(R.id.thing_recycler_view);  // Set items list
        mThingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // RecyclerView requires a LayoutManager

        mThingRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null)
        {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateList();

        return view;
    }

    /**
     * Used to refresh the list of items upon resuming the list fragment.
     * Otherwise called when launched activity exits returning request code, result code that the activity was started with and additional data.
     * The resultCode will be RESULT_CANCELED if the activity explicitly returned that, didn't return any result, or crashed during its operation.
     * @param requestCode - integer request code supplied to startActivityForResult(), allowing to identify where result came from.
     * @param resultCode - integer result code returned by the child activity through its setResult().
     * @param data - intent which can return data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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

            case R.id.back_button:
                goBack();
                return true;

            case R.id.search_what:
                item.setChecked(true);
                mSearchHandler.setSearchType(SearchHandler.SearchType.SEARCH_WHAT);
                return true;

            case R.id.search_where:
                item.setChecked(true);
                mSearchHandler.setSearchType(SearchHandler.SearchType.SEARCH_WHERE);
                return true;

            case R.id.sortWhat:
                setSortedList(ISort.SortingOrder.WHAT);
                return true;

            case R.id.sortWhere:
                setSortedList(ISort.SortingOrder.WHERE);
                return true;

            case R.id.sortDate:
                setSortedList(ISort.SortingOrder.DATE);
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

    // Go back to list if in portrait mode
    private void goBack()
    {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mCallback.onBackPressed();
        }
    }

    private void setSearchView(MenuItem searchItem)
    {
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            /**
             * Called when the user submits the query.
             * This could be due to a key press on the keyboard or due to pressing a submit button.
             * @param query - search string.
             * @return - true if item was found.
             */
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                List<Thing> result = mSearchHandler.search(query.toLowerCase().trim(), mAdapter.getThings());
                if (result == null)
                {
                    makeToast(getString(R.string.item_notFound_toast));
                    return false;
                }
                mAdapter.setThings(result);
                mAdapter.notifyDataSetChanged(); // Could use notifyItemChanged() for performance
                return true;
            }

            /**
             * Called when the query text is changed by the user.
             * @param newText - new content of the query
             * @return true if action was handled and items were found.
             */
            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (newText.length() == 0)
                {
                    updateList(); // Reset list
                }
                else
                {
                    List<Thing> result = mSearchHandler.search(newText.toLowerCase().trim(), mAdapter.getThings());
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

        if (!things.isEmpty()) // Only sort if database contains items
        {
            mSearchHandler.sortDefault(things);
        }

        if (mAdapter == null)
        {
            mAdapter = new ThingAdapter(things);
            mThingRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setThings(things);
            mAdapter.notifyDataSetChanged(); // Only reload items when going back from detailed screen so no real overhead
        }

        updateSubtitle();
    }

    /**
     * Sorts list of items and update view.
     * @param SortingOrder - parameter used to sort (name or location).
     */
    private void setSortedList(ISort.SortingOrder SortingOrder)
    {
        if (mAdapter.mThings.size() == 0) return;
        List<Thing> result = mSearchHandler.sort(mAdapter.mThings, SortingOrder);
        mAdapter.setThings(result);
        mAdapter.notifyDataSetChanged(); // Do not use notifyItemChanged() for performance due to sorting feature
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

            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);

            mTextView = (TextView) itemView;
        }

        /**
         * When given a Thing, ThingHolder will update content of TextView to reflect state of Thing
         * @param thing - thing to bind.
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
                selectThing(mThing); // Toggle selection of item and navigate to detailed screen
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
            ((AppCompatActivity) getActivity()).startSupportActionMode(mDeleteMode); // Turn on delete action mode
            mMultiSelector.setSelected(this, true);
            return true;
        }

    }

    /**
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
         * Bind ViewHolder view to model object (thing data to view holder with text view on screen)
         * @param holder - view holder describing item view.
         * @param position - position of view holder.
         */
        @Override
        public void onBindViewHolder(ThingHolder holder, int position)
        {
            Thing thing = mThings.get(position);
            holder.bindThing(thing);
            Log.d(TAG, "binding things" + thing + " at position" + position);
        }

        /**
         * Get total amount of items to display.
         * @return amount of items.
         */
        @Override
        public int getItemCount()
        {
            return mThings.size();
        }

        /**
         * Previously used to remove items on specific positions.
         * No longer used due to sorting functionality that moves around item orders and requires complete refresh of item views.
         * @param position - position if item.
         */
        @Deprecated
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

        public List<Thing> getThings() { return mThings; }
    }

}
