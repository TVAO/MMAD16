package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.UUID;

import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;

/**
 * This class represents the fragment of main page.
 * The TingleFragment is hosted by the activity TingleActivity.
 */
public class TingleFragment extends Fragment {

    private static final String ARG_THING_ID = "thing_id"; // Fragment argument used by host activity

    // GUI variables
    private Button mAddButton, mListButton, mSearchButton;
    private TextView mLastAdded, mWhatField, mWhereField;

    // Database
    private static ThingRepository sThingRepository;

    // Used to call host activity TingleActivity
    private eventListener mCallBackToActivity;

    /**
     * This interface allows TingleFragment to communicate to host TingleActivity.
     * Interface is encapsulated in fragment to avoid use in other activities.
     * Interface is implemented by host activity determining what happens upon triggering the listener.
     */
    public interface eventListener
    {
        // void onClickItem(); // See details of item
        void onShowItems(); // See list of items
        void onAddItems(); // Update list in landscape after adding item
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
        Activity activity = new Activity();

        try
        {
            activity = (Activity) context;
            mCallBackToActivity = (eventListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement eventListener");
        }
    }

    /**
     * Attach fragment argument with thing id used by host activity to get TingleFragment with specific Thing id
     * Creates an arguments bundle, creates a fragment instance, and then attaches the arguments to the fragment.
     * @param thingId - id of Thing to be displayed in fragment
     * @return - fragment displaying thing
     */
    public static TingleFragment newInstance(UUID thingId)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_THING_ID, thingId);
        TingleFragment fragment = new TingleFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Call to do initial creation of fragment
     * @param savedInstanceState - fragment rebuilt from saved state if not null
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sThingRepository = ThingRepository.get(this.getContext());

    }

//    // Update content of repository upon updates
//    @Override
//    public void onPause() {
//        super.onPause();
//        ThingRepository.getThing(getActivity())
//                .updateThing(mThing);
//    }

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
        View v = inflater.inflate(R.layout.fragment_tingle, container, false);

        setButtons(v);
        setTextFields(v);
        updateUI();

        return v;
    }

    /**
     * Used to search for an item in database.
     * @param item - item to search for
     * @return - where item is located
     */
    public String searchItems(String item)
    {
        String searchItem = item.toLowerCase().trim();
        String result = null;

        for (Thing i : sThingRepository.getThings())
        {
            if(i.getWhat().toLowerCase().trim().equals(searchItem))
            {
                result = i.getWhere(); // Return specific item per default
            }
            else if (i.getWhat().toLowerCase().trim().contains(searchItem))
            {
                result = i.getWhere(); // Return item containing
            }
        }
        return result;
    }

    // Setup text fields
    private void setTextFields(View view)
    {
        //Text field used so show last item added
        mLastAdded = (TextView) view.findViewById(R.id.last_thing);

        // Text fields for describing a thing by what it is and where it is located
        mWhatField = (TextView) view.findViewById(R.id.what_text);
        mWhereField = (TextView) view.findViewById(R.id.where_text);
    }

    // Setup buttons and listeners on them
    private void setButtons(View view)
    {
        // Button used to add items
        mAddButton = (Button) view.findViewById(R.id.add_button);

        // Add item click event
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mWhatField.getText().length() > 0) && (mWhereField.getText().length() > 0))
                {
                    sThingRepository.addThing(
                            new Thing(mWhatField.getText().toString(),
                                    mWhereField.getText().toString()));
                    mWhatField.setText("");
                    mWhereField.setText("");
                    updateUI();
                    mCallBackToActivity.onAddItems(); // Used to update list in landscape after adding item
                }
            }
        });

        // Setup search button used to search for item denoted in "What" field
        mSearchButton = (Button) view.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWhatField.getText().length() > 0)
                {
                    String searchResult = searchItems(mWhatField.getText().toString());
                    if (searchResult != null) makeToast(getString(R.string.item_locationIs_toast) + " " + searchResult); // Item found
                    else makeToast(getString(R.string.item_notfound_toast)); // Item not found
                }
                // makeToast(getString(R.string.item_notSpecified_toast));
            }
        });

        // Only show item list button when in portrait mode (removed in landscape)
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mListButton = (Button) view.findViewById(R.id.item_list_button);
            mListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBackToActivity.onShowItems(); // Callback to activity
                }
            });
        }

    }

    // Update content of UI after adding a new item, e.g. last added item.
    private void updateUI()
    {
        int size = sThingRepository.size();
        if (size > 0 )
        {
            mLastAdded.setText(sThingRepository.getThing(size - 1).toString()); // Set text to last item added
        }
        else this.mLastAdded.setText(getString(R.string.item_notfound_toast));
    }

    private void makeToast(String string)
    {
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
