package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import tvao.mmad.itu.tingle.Helpers.BaseFragment;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.Helpers.Network.FetchOutpanTask;
import tvao.mmad.itu.tingle.Helpers.Network.NetworkUtils;
import tvao.mmad.itu.tingle.R;

/**
 * This class represents the fragment of main page.
 * The TingleMainFragment is hosted by the activity TingleActivity.
 */
public class TingleMainFragment extends BaseFragment {

    private static final String ARG_THING_ID = "thing_id"; // Fragment argument used by host activity
    private static final int REQUEST_SCAN = 3;

    private static ThingRepository sThingRepository; // Database
    private TingleMainFragmentEventListener mCallBackToActivity; // Used to call host activity TingleActivity

    private Button mAddButton, mListButton, mScanButton; // GUI variables
    private TextView mLastAdded, mWhatField, mWhereField;
    private EditText mBarcodeField;

    /**
     * This interface allows TingleMainFragment to communicate to host TingleActivity.
     * Interface is encapsulated in fragment to avoid use in other activities.
     * Interface is implemented by host activity determining what happens upon triggering the listener.
     */
    public interface TingleMainFragmentEventListener
    {
        void onShowItemsPressed(); // See list of items
        void onAddItemPressed(); // Update list in landscape after adding item
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
            mCallBackToActivity = (TingleMainFragmentEventListener) activity;
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

        sThingRepository = ThingRepository.get(this.getContext());

    }

    /**
     *  Creates and returns the view hierarchy associated with the fragment.
     * @param inflater - used to inflate view in fragment.
     * @param container - parent view that fragment is attached to.
     * @param savedInstanceState - fragment rebuilt from saved state if not null.
     * @return - fragment view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_tingle_main, container, false);

        setButtons(v);
        setTextFields(v);
        updateUI();

        return v;
    }

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
        switch (requestCode)
        {
            case Activity.RESULT_OK :
                makeToast(getString(R.string.ok));

            case Activity.RESULT_CANCELED :
                makeToast("Scan was cancelled!");
                Log.d("onActivityResult", "RESULT_CANCELED");

            case REQUEST_SCAN :
                handleScanData(data);
        }
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
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
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
                    mCallBackToActivity.onAddItemPressed(); // Used to update list in landscape after adding item
                }
            }
        });

        // Only show item list button when in portrait mode (removed in landscape)
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mListButton = (Button) view.findViewById(R.id.item_list_button);
            mListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBackToActivity.onShowItemsPressed(); // Callback to activity
                }
            });
        }

        mScanButton = (Button) view.findViewById(R.id.barcode_scanner);
        mScanButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Fire an intent to open barcode scanning activity
             * @param v
             */
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intent, 0);
            }
        });

        mBarcodeField = (EditText) view.findViewById(R.id.barcode_text);


    }

    // Lookup item from barcode and save information
    private void handleScanData(Intent data)
    {
        if (data != null && data.getExtras() != null) // Scan data received
        {
            // Handle successful scan
            String contents = data.getStringExtra("SCAN_RESULT");
            makeToast("Content:" + contents);
            Log.d("onActivityResult", "contents: " + contents);

            // Lookup item from barcode if user has connection
            if (new NetworkUtils(getContext()).isOnline())
            {
                FetchOutpanTask lookupBarcodeTask = new FetchOutpanTask(new FetchOutpanTask.AsyncResponse()
                {
                    @Override
                    public void processFinish(Thing output)
                    {
                        // Set barcode info based on lookup result from OnPostExecute() in AsyncTask
                        mBarcodeField.setText(output.getBarcode());
                        mWhatField.setText(output.getWhat());
                        Log.d("Lookup", "barcode: " + output.getBarcode());
                        Log.d("Lookup", "what: " + output.getWhat());
                    }
                });

                lookupBarcodeTask.execute(contents); // Execute barcode info task async
            }
            else
            {
                makeToast("You are not connected to a network... Please try again.");
            }
        }
        else
        {
            // Cancel scan
            makeToast("Scan was cancelled!");
            Log.d("onActivityResult", "RESULT_CANCELED");
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

}
