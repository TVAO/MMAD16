package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import tvao.mmad.itu.tingle.Controller.Activities.ThingListActivity;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;

/**
 * This class represents the fragment of a detailed page for a given item.
 * The TingleFragment is hosted by the activity TinglePagerActivity.
 */
public class ThingFragment extends Fragment {

    public static final String EXTRA_THING_ID = "thingintent.THING_ID";
    private static final String WHAT = "what";
    private static final String WHERE = "where";
    private static final String DESCRIPTION = "description";

    private Thing mThing;
    private Button mAddButton;
    private EditText mTitleField;
    private TextView mWhatField, mWhereField;

    /**
     * This method is used to instantiate a new Fragment used to display a detailed screen.
     * Encapsulates and abstracts the steps required to setup the object from the client.
     *
     * Rather than having the client call the default constructor and manually set the fragment's arguments themselves,
     * we provide a static factory method that does this for them making fragment instantiation convenient and enforcing well-defined behavior.
     *
     * @param thingId - id related to thing to be shown in activity.
     * @return - new fragment with thing details.
     */
    public static ThingFragment newInstance(UUID thingId)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_THING_ID, thingId); // Fragment argument

        ThingFragment fragment = new ThingFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Called when activity is starting and used to setup detailed screen for a given thing.
     * @param savedInstanceState - possible data from previous activity shutdown.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UUID thingId = (UUID) getArguments().getSerializable(EXTRA_THING_ID);
        mThing = ThingRepository.get(getActivity()).getThing(thingId);
        setHasOptionsMenu(true);
    }

    /**
     * This method is called after onCreate() and is used to assign View variables and do graphical initializations in view.
     * @param inflater - instantiates a XML layout file into corresponding objects.
     * @param parent - base class for layout parameters.
     * @param savedInstanceState - possible data from previous activity shutdown.
     * @return - new main UI view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_thing, parent, false);

        // Find add button
        mAddButton = (Button) v.findViewById(R.id.thing_details_add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThing.setWhat(mWhatField.getText().toString().trim());
                mThing.setWhere(mWhereField.getText().toString().trim());
                ThingRepository.get(getActivity()).updateThing(mThing);
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        });

        mWhatField = (EditText) v.findViewById(R.id.thing_details_what);
        mWhatField.setText(mThing.getWhat());

        mWhereField = (EditText) v.findViewById(R.id.thing_details_where);
        mWhereField.setText(mThing.getWhere());

        mTitleField = (EditText) v.findViewById(R.id.thing_title);
        mTitleField.setText(mThing.getWhat());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mThing.setWhat(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // Space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // This one too
            }
        });

        return v;

    }

    /**
     * This method will be used to get a result from an activity in the future.
     * @param requestCode - integer request code supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode -  integer result code returned by the child activity through its setResult()
     * @param data -  intent which can return result data to caller attached to Intent "extra"
     */
    @Override
    @Deprecated
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK) return;
    }

    /**
     * This method is used to implement the Android ActionBar back button.
     * This allows the user to navigate back to the list of items from a detailed screen for a given thing.
     * @param item - menu item that is used.
     * @return - true if navigating to home activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity()); // Navigate to parent activity (ThingListFragment)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
