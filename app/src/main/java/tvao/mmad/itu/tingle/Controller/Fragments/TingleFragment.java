package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.UUID;

import tvao.mmad.itu.tingle.Controller.Activities.BarcodeActivity;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;

/**
 * This class represents the fragment of main page.
 * The TingleFragment is hosted by the activity TingleActivity.
 */
public class TingleFragment extends Fragment {

    private static final String ARG_THING_ID = "thing_id"; // Fragment argument used by host activity

    private Button mAddButton, mListButton, mSearchButton, mScanButton; // GUI variables
    private TextView mLastAdded, mWhatField, mWhereField;
    private EditText mBarcodeField;
    private static ThingRepository sThingRepository; // Database
    private eventListener mCallBackToActivity; // Used to call host activity TingleActivity

    /**
     * This interface allows TingleFragment to communicate to host TingleActivity.
     * Interface is encapsulated in fragment to avoid use in other activities.
     * Interface is implemented by host activity determining what happens upon triggering the listener.
     */
    public interface eventListener // Todo remove ?
    {
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
    public void onAttach(Context context) // Todo remove ?
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
    public static TingleFragment newInstance(UUID thingId) // Todo use in TingleActivity
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
        mAddButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if ((mWhatField.getText().length() > 0) && (mWhereField.getText().length() > 0))
                {
                    if(mBarcodeField.getText().toString().isEmpty())
                    {
                        // Add item without barcode
                        sThingRepository.addThing(
                                new Thing(mWhatField.getText().toString(),
                                         mWhereField.getText().toString()));
                    }
                    else
                    {
                        // Add item with barcode
                        sThingRepository.addThing(
                                new Thing(mWhatField.getText().toString(),
                                          mWhereField.getText().toString(),
                                          mBarcodeField.getText().toString())
                        );
                    }
                    mWhatField.setText("");
                    mWhereField.setText("");
                    updateUI();
                    mCallBackToActivity.onAddItems(); // Used to update list in landscape after adding item
                }
            }
        });

        // Setup search button used to search for item denoted in "What" field
        mSearchButton = (Button) view.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mWhatField.getText().length() > 0)
                {
                    String searchResult = searchItems(mWhatField.getText().toString());
                    if (searchResult != null) makeToast(getString(R.string.item_locationIs_toast) + " " + searchResult); // Item found
                    else makeToast(getString(R.string.item_notFound_toast)); // Item not found
                }
                // makeToast(getString(R.string.item_notSpecified_toast));
            }
        });

        // Only show item list button when in portrait mode (removed in landscape)
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mListButton = (Button) view.findViewById(R.id.item_list_button);
            mListButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mCallBackToActivity.onShowItems(); // Callback to activity
                }
            });

            mScanButton = (Button) view.findViewById(R.id.barcode_scanner);
            mScanButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    startActivityForResult(intent, 0);
                }
            });

            mBarcodeField = (EditText) view.findViewById(R.id.barcode_text);
        }

    }

    // Todo code is duplicated in TingleFragment and ThingFragment, use BarcodeActivity instead and remove code duplication
    /**
     * This method is used to get the result back from scanning a barcode and save it in the barcode field.
     * Called whenever Scanner exits, giving requestCode you started it with, the resultCode it returned, and any additional data from it.
     * @param requestCode - integer request code to identify where result came from in startActivityForResult called when clicking Scan button.
     * @param resultCode - integer result code returned by the child activity through its setResult()/
     * @param data - intent with result data to the caller.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0)
        {
            if (resultCode == getActivity().RESULT_OK)
            {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                mBarcodeField.setText(contents);

                // Handle successful scan
                Toast toast = Toast.makeText(getContext(), "Content:" + contents + " Format:" + format , Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();

                Log.d("onActivityResult", "contents: " + contents);
            }
            else if (resultCode == getActivity().RESULT_CANCELED)
            {   // Handle cancel
                Toast toast = Toast.makeText(getContext(), "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
                Log.d("onActivityResult", "RESULT_CANCELED");
            }
        }
    }

    // Update content of UI after adding a new item, e.g. last added item.
    private void updateUI()
    {
        int size = sThingRepository.size();
        if (size > 0 )
        {
            mLastAdded.setText(sThingRepository.getThings().get(size - 1).toString()); // Set text to last item added
            getActivity().invalidateOptionsMenu(); // Recreate menu due to new count of total items
        }
        else this.mLastAdded.setText(getString(R.string.item_notFound_toast));
    }

    private void makeToast(String string)
    {
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
