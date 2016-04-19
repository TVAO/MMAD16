package tvao.mmad.itu.tingle.Controller.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import tvao.mmad.itu.tingle.Helpers.BaseFragment;
import tvao.mmad.itu.tingle.Helpers.Location.Constants;
import tvao.mmad.itu.tingle.Helpers.Location.FetchAddressIntentService;
import tvao.mmad.itu.tingle.Helpers.PictureUtils;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.Helpers.Network.FetchOutpanTask;
import tvao.mmad.itu.tingle.Helpers.Network.NetworkUtils;
import tvao.mmad.itu.tingle.R;

/**
 * This class represents the fragment of a detailed page for a given item.
 * The TingleMainFragment is hosted by the activity TinglePagerActivity.
 */
public class ThingDetailFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String EXTRA_THING_ID = "thingintent.THING_ID";
    public static final String TAG = "ThingDetailFragment";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = -1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_SCAN = 3;
    private static final int LOCATION_INTERVAL = 30000; // 30 seconds interval for fetching user location

    private Thing mThing;
    private Button mAddButton, mScanButton, mDateButton;
    private EditText mWhatField, mWhereField, mBarcodeField;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    //private ResultReceiver mResultReceiver;
    private String mAddressOutput;
    private boolean mAddressRequested;
    private GoogleApiClient mGoogleApiClient;
    private Button mLocationButton;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi mFusedLocationProviderAPI;

    // Start intent service used to fetch user address location
    protected void startIntentService()
    {
        Intent intent = new Intent(this.getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    /**
     * Initialize GoogleApiClient Builder to fetch required Google Services
     */
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void getLocation()
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_INTERVAL);
        mFusedLocationProviderAPI = LocationServices.FusedLocationApi;

        //buildGoogleApiClient();
//        googleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
        //if (mGoogleApiClient != null)
        //{
        //   mGoogleApiClient.connect();
        //}
    }

    /**
     * Calls the startIntentService() method when user takes an action that requires a geocoding address lookup.
     * Checks that the connection to Google Play services is present before starting the intent service.
     * @param view - view of activity
     */
    public void fetchAddressButtonHandler(View view)
    {
        //mGoogleApiClient.connect();
        // Only start service to fetch address if GoogleApiClient is connected
        //if (mGoogleApiClient.isConnected() && mLastLocation != null)
        if(mGoogleApiClient.isConnected())
        {
            startIntentService();
        }
        // Else process user request by setting mAddressRequested to true
        // Later, launch service to fetch address when Google API Client connects

        mAddressRequested = true;
        //updateUIWidgets();
    }

    /**
     * Called when the client is temporarily in a disconnected state.
     * This can happen if there is a problem with the remote service
     * (e.g. a crash or resource problem causes it to be killed by the system).
     * @param i - numeric describing success or failure of service
     */
    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        makeToast("onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    /**
     * After calling connect(), this method will be invoked asynchronously when the connect request has successfully completed.
     * You must also start the intent service when the connection to Google Play services is established,
     * if the user has already clicked the button on your app's UI.
     * The following code snippet shows the call to the startIntentService() method in the onConnected() callback
     * provided by the Google API Client
     * @param connectionHint - content defined by FetchAddressIntentService
     */
    @Override
    public void onConnected(Bundle connectionHint)
    {
        // Gets the best and most recent location currently available,
        // which may be null in rare cases when a location is not available.
        try
        {
            getLocation(); // Create location request
            //mFusedLocationProviderAPI.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this.getActivity()); // Await result of latest location

            mLastLocation = mFusedLocationProviderAPI.getLastLocation(mGoogleApiClient);
            //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            //        mGoogleApiClient);
        }

        catch (SecurityException ex)
        {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }

        if (mLastLocation != null)
        {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent())
            {
                Toast.makeText(this.getActivity(), R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (mAddressRequested)
            {
                startIntentService();
            }
        }
    }

    /**
     * Called when there was an error connecting the client to the service.
     * @param connectionResult - used for resolving the error, and deciding what sort of error occurred.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        makeToast("Error occurred during connection to Google API");
    }

    // Class used to handle response from FetchAddressIntentService
    @SuppressLint("ParcelCreator")
    private class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {

            // Set the address or display an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            mWhereField.setText(mAddressOutput); //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT)
            {
                makeToast(getString(R.string.address_found));
            }
            else makeToast(getString(R.string.no_address_found));

        }
    }

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
    public static ThingDetailFragment newInstance(UUID thingId)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_THING_ID, thingId); // Fragment argument

        ThingDetailFragment fragment = new ThingDetailFragment();
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
        mPhotoFile = ThingRepository.get(getContext()).getPhotoFile(mThing);
        setHasOptionsMenu(true);
        buildGoogleApiClient();
    }

    /**
     * Called when you are no longer visible to the user.
     * You will next receive either onRestart(), onDestroy(), or nothing, depending on later user activity.
     * Note that this method may never be called, in low memory situations
     * ,where the system does not have enough memory to keep your activity's process running after its onPause() method is called.
     * Disconnects from the Google API Client used to fetch user location.
     */
    @Override
    public void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Called after onCreate(Bundle) — or after onRestart() when the activity had been stopped,
     * but is now again being displayed to the user. It will be followed by onResume().
     * Connects to the Google API Client used to fetch the latest location of the user.
     */
    @Override
    public void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
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
        View v = inflater.inflate(R.layout.fragment_thing_detail, parent, false);

        setTextFields(v);
        setAddButton(v);
        setDateButton(v);

        mScanButton = (Button) v.findViewById(R.id.barcode_scanner);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intent, REQUEST_SCAN);
            }
        });

        mLocationButton = (Button) v.findViewById(R.id.location);
        mLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //buildGoogleApiClient();
                fetchAddressButtonHandler(v);
                Log.d("my", " Initialized google plus api client");
            }
        });

        setupCameraButton(v);

        return v;
    }

    /**
     * This method is used to get the result back from scanning a barcode and save it in the barcode field.
     * Called whenever Scanner exits, giving requestCode you started it with, the resultCode it returned, and any additional data from it.
     * @param requestCode - integer request code supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode -  integer result code returned by the child activity through its setResult()
     * @param data -  intent which can return result data to caller attached to Intent "extra"
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            //case Activity.RESULT_OK :

            case Activity.RESULT_CANCELED :

            case REQUEST_SCAN :
                handleScanData(data);

            case REQUEST_PHOTO :
                updatePhotoView();

            case REQUEST_DATE :
                handleDate(data);
        }
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

    private void setupCameraButton(View v)
    {
            mPhotoView = (ImageView) v.findViewById(R.id.thing_photo);

            mPhotoButton = (ImageButton) v.findViewById(R.id.thing_camera);

            // Intent used to fire up camera application using action "ACTION_IMAGE_CAPTURE"
            final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            boolean isCanTakePhoto = isCanTakePhoto(captureImage);

            mPhotoButton.setEnabled(isCanTakePhoto(captureImage));

            if (isCanTakePhoto)
            {
                Uri uri = Uri.fromFile(mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }

            mPhotoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                  startActivityForResult(captureImage, REQUEST_PHOTO); // Todo get SecurityException due to permission issue ???
                }
            });

            updatePhotoView(); // Load image into image view
    }

    // Check if camera is available
    private boolean isCanTakePhoto(Intent captureImage)
    {
        // Check if camera is available, else disable camera button
        PackageManager packageManager = getActivity().getPackageManager();

        return mPhotoFile != null &&
                        captureImage.resolveActivity(packageManager) != null;
    }

    private void setAddButton(View v)
    {
        // Find add button
        mAddButton = (Button) v.findViewById(R.id.thing_details_add_button);
        mAddButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if ((mWhatField.getText().length() > 0) && (mWhereField.getText().length() > 0))
                {
                    mThing.setWhat(mWhatField.getText().toString().trim());
                    mThing.setWhere(mWhereField.getText().toString().trim());
                    mThing.setBarcode(mBarcodeField.getText().toString().trim());

                    if (ThingRepository.get(getActivity()).getThing(mThing.getId()) == null)
                    {
                        // Add new item from menu bar
                        ThingRepository.get(getActivity()).addThing(mThing);
                    } else
                    {
                        // Update existing item
                        ThingRepository.get(getActivity()).updateThing(mThing);
                    }
                    getActivity().finish(); // Done and close activity
                    //NavUtils.navigateUpFromSameTask(getActivity()); // Navigate to parent activity (ThingListFragment)
                }
            }
        });
    }

    private void setDateButton(View v)
    {
        mDateButton = (Button) v.findViewById(R.id.thing_details_date_button);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mThing.getDate());
                dialog.setTargetFragment(ThingDetailFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
    }

    private void setTextFields(View v)
    {
        mWhatField = (EditText) v.findViewById(R.id.thing_details_what);
        mWhatField.setText(mThing.getWhat());

        mWhereField = (EditText) v.findViewById(R.id.thing_details_where);
        mWhereField.setText(mThing.getWhere());

        mBarcodeField = (EditText) v.findViewById(R.id.barcode_text);
        mBarcodeField.setText(mThing.getBarcode());
    }

    private void handleDate(Intent data)
    {
        if (data != null && data.getExtras() != null)
        {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mThing.setDate(date);
            updateDate();
        }
    }

    // Lookup item from barcode and save information
    private void handleScanData(Intent data)
    {
        if (data != null && data.getExtras() != null) // Scan data received
        {
            String contents = data.getStringExtra("SCAN_RESULT");
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");

            // Handle successful scan
            Toast toast = Toast.makeText(getContext(), "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

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
                        // Todo could just add Thing directly to items with name, barcode and optionally attributed in new field
                    }
                });

                lookupBarcodeTask.execute(contents);
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

    // Method used to load bitmap into ImageView showing picture
    private void updatePhotoView()
    {
        if (mPhotoFile == null || !mPhotoFile.exists())
        {
            mPhotoView.setImageDrawable(null);
        } else
        {
            // Scale image before insert
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updateDate()
    {
        if(mThing.getDate() != null)
        {
            String simpleDate = simplifyDateFormatDisplay(mThing.getDate());
            mDateButton.setText(simpleDate);
        }
    }

    private String simplifyDateFormatDisplay(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * This method is used to check if a permission exists in the manifest file.
     * Specifically, this is used for the camera permission that needs to be requested during runtime.
     * See link: http://stackoverflow.com/questions/32789027/android-m-camera-intent-permission-bug
     * @param context - context of fragment.
     * @param permissionName - name of permission, e.g. Manifest.permission.CAMERA
     * @return
     */
    public boolean hasPermissionInManifest(Context context, String permissionName)
    {
        final String packageName = context.getPackageName();
        try
        {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0)
            {
                for (String p : declaredPermisisons)
                {
                    if (p.equals(permissionName))
                    {
                        return true;
                    }
                }
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
