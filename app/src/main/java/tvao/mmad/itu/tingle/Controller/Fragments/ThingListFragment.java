package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import tvao.mmad.itu.tingle.Controller.Activities.ThingPagerActivity;
import tvao.mmad.itu.tingle.Controller.Helpers.RecyclerItemClickListener;
import tvao.mmad.itu.tingle.Controller.Helpers.ThingAdapter;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;

import static tvao.mmad.itu.tingle.Controller.Helpers.RecyclerItemClickListener.OnItemClickListener;


/**
 * This class represents a fragment used to display all items in list.
 * The fragment is hosted by the activity TingleActivity.
 */
public class ThingListFragment extends Fragment {

    private View mView;
    private Button mBackButton;
    private ThingRepository mThingRepository;
    private RecyclerView mThingRecyclerView;
    private ThingAdapter mAdapter;
    private onBackPressedListener mCallBackToActivity; // Used to call host activity TingleActivity
    private boolean mSubtitleVisible; // Keep track of subtitle visibility
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle"; // Used to save subtitle visibility upon rotation

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
     *
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
     *
     * @param savedInstanceState - fragment rebuilt from saved state if not null
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Tell FM that fragment receives menu callbacks
        mThingRepository = ThingRepository.get(getContext());
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
        mView = inflater.inflate(R.layout.fragment_thing_list, container, false);

        setButtons();

        mThingRecyclerView = (RecyclerView) mView.findViewById(R.id.thing_recycler_view);

        mThingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // RecyclerView requires a LayoutManager

//        mThingRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getContext(), new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Thing item = mThingRepository.getThings().get(position);
//
//                        boolean isDeleted = mThingRepository.removeThing(item.getId());
//                        mAdapter.removeAt(position);
//
//                        if (isDeleted = true) {
//                            Toast.makeText(mThingRecyclerView.getContext(), item.getWhat(), Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(mThingRecyclerView.getContext(), item.getWhat() + "was not deleted", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                })
//        );

        if (savedInstanceState != null)
        {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return mView;
    }

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
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
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
        if (mSubtitleVisible)
        {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else {
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

                Intent intent = ThingPagerActivity
                        .newIntent(getActivity(), thing.getId());

                startActivity(intent);

                return true;

            case R.id.menu_item_show_subtitle: // Show total items
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
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

    // Update recycler view with items in list
    private void updateUI()
    {
        if (mAdapter == null)
        {
            mAdapter = new ThingAdapter(mThingRepository.getThings(), getContext());
            mThingRecyclerView.setAdapter(mAdapter);
            //mAdapter.notifyDataSetChanged();
        }
        else
        {
            mAdapter.setThings(mThingRepository.getThings());
            // mAdapter.notifyDataSetChanged(); // Todo expensive use specific notify already
        }

        updateSubtitle(); // Update number of things after going back to main page
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

    }

    private void makeToast(String string)
    {
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
