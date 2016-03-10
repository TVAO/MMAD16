package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import tvao.mmad.itu.tingle.Controller.Helpers.RecyclerItemClickListener;
import tvao.mmad.itu.tingle.Controller.Helpers.ThingAdapter;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;


/**
 * This class represents a fragment used to display all items in list.
 * The fragment is hosted by the activity TingleActivity.
 */
public class ThingListFragment extends Fragment {

    private View mView;
    private Button mBackButton, mDeleteButton;
    private ThingRepository mThingRepository;
    private RecyclerView mThingRecyclerView;
    private ThingAdapter mAdapter;
    private int selectedItemPosition; // Position of selected item used to removeAt
    private onBackPressedListener mCallBackToActivity; // Used to call host activity TingleActivity

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
     * The fragment captures the interface implementation in the activity TingleActivity during onAttach() lifecycle method.
     * This method calls the interface methods in order to communicate with the activity TingleActivity.
     * The method checks if the container activity has implemented the callback interface, otherwise throws an exception.
     * @param context - context of host activity
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Activity activity = null;

        try
        {
            activity = (Activity) context;
            mCallBackToActivity = (onBackPressedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement onBackPressedListener");
        }
    }

    /**
     * Call to do initial creation of fragment
     * @param savedInstanceState - fragment rebuilt from saved state if not null
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mThingRepository = ThingRepository.get(this.getContext());

        selectedItemPosition = -1;
    }

    /**
     *  Creates and returns the view hierarchy associated with the fragment
     * @param inflater - used to inflate view in fragment
     * @param container - parent view that fragment is attached to
     * @param savedInstanceState - fragment rebuilt from saved state if not null
     * @return - fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_list, container, false);

        setButtons();

        // setItemListView();
        mThingRecyclerView = (RecyclerView) mView.findViewById(R.id.thing_recycler_view);

        mThingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // RecyclerView requires a LayoutManager

        mThingRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        mThingRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mThingRecyclerView.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        int itemPosition = mThingRecyclerView.getChildPosition(view);
                        Thing item = mThingRepository.get(itemPosition);
                        Toast.makeText(mThingRecyclerView.getContext(), item.getWhat(), Toast.LENGTH_LONG).show();
                        mThingRepository.getThings().remove(position);
                        mAdapter.notifyItemRemoved(position); // Refresh items
                        mAdapter.notifyItemRangeChanged(position, mThingRepository.getThings().size()); // Adjust all views below deleted item

                    }
                })
        );

        updateUI();

        return mView;
    }

    // Update recycler view with items in list
    private void updateUI()
    {
        ThingRepository repository = ThingRepository.get(getActivity());
        List<Thing> things = repository.getThings();

        mAdapter = new ThingAdapter(things, getActivity());
        mThingRecyclerView.setAdapter(mAdapter);
    }

    // Redirect back to main page (TingleActivity)
    private void setButtons()
    {
        // Only have back button in portrait mode (removed in landscape mode)
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mBackButton = (Button) mView.findViewById(R.id.back_button);
            mBackButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mCallBackToActivity.onBackPressed(); // Call host activity
                }
            });
        }

        // Delete button to remove item
        mDeleteButton = (Button) mView.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (selectedItemPosition == -1)
                {
                    makeToast(getString(R.string.item_notfound_toast));
                }
                else
                {
                    String itemName = mThingRepository.get(selectedItemPosition).getWhat();
                    mThingRepository.removeThing(selectedItemPosition);
                    makeToast(getString(R.string.item_deleted_toast) + " " + itemName);
                    // setItemListView();
                    selectedItemPosition = -1;
                }
            }
        });

    }

    private void makeToast(String string)
    {
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    //    // Bind list and items in
//    private void setItemListView()
//    {
//        mListView = (ListView) mView.findViewById(R.id.list_view);
//
//        // Adapter used to bind together list and its items in the ListActivity
//        // final ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.rowitem, R.id.row_item, mThingRepository.getThings().toArray()); // Context, layout and how items should be visualised
//        adapter = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.rowitem, R.id.row_item, mThingRepository.getThings().toArray()); // Context, layout and how items should be visualised
//        if (mListView != null)
//        mListView.setAdapter(adapter);
//
//        // Used to select item for deletion
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectedItemPosition = position;
//                // mTextView.getChildAt((int)id).setBackgroundColor(Color.CYAN); // Mark selected item
//                Thing selectedItem = (Thing) adapter.getItem((int)id);
//                makeToast(getString(R.string.item_found_toast) + " " + selectedItem.getWhat());
//            }
//        });
//    }

}