package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
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

import java.io.File;
import java.util.UUID;

import tvao.mmad.itu.tingle.Controller.Helpers.BaseFragment;
import tvao.mmad.itu.tingle.Controller.Helpers.PictureUtils;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.Network.FetchOutpanTask;
import tvao.mmad.itu.tingle.Network.NetworkUtils;
import tvao.mmad.itu.tingle.R;

/**
 * This class represents the fragment of a detailed page for a given item.
 * The TingleMainFragment is hosted by the activity TinglePagerActivity.
 */
public class ThingDetailFragment extends BaseFragment {

    public static final String EXTRA_THING_ID = "thingintent.THING_ID";
    public static final String TAG = "ThingDetailFragment";

    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_SCAN = 3;

    private Thing mThing;
    private Button mAddButton, mScanButton;
    private EditText mWhatField, mWhereField, mBarcodeField;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

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

        setTextFields(v);
        setAddButton(v);

        mScanButton = (Button) v.findViewById(R.id.barcode_scanner);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intent, REQUEST_SCAN);
            }
        });

        setupCameraButton(v);

        return v;
    }

    private void setupCameraButton(View v) {
        // Only show camera functionality in portrait mode (removed in landscape)
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
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

            mPhotoView = (ImageView) v.findViewById(R.id.thing_photo);

            updatePhotoView(); // Load image into image view
        }
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
            public void onClick(View v)
            {
                if((mWhatField.getText().length() > 0) && (mWhereField.getText().length() > 0))
                {
                    mThing.setWhat(mWhatField.getText().toString().trim());
                    mThing.setWhere(mWhereField.getText().toString().trim());
                    mThing.setBarcode(mBarcodeField.getText().toString().trim());

                    if (ThingRepository.get(getActivity()).getThing(mThing.getId()) == null)
                    {
                        // Add new item from menu bar
                        ThingRepository.get(getActivity()).addThing(mThing);
                    }
                    else
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

    private void setTextFields(View v)
    {
        mWhatField = (EditText) v.findViewById(R.id.thing_details_what);
        mWhatField.setText(mThing.getWhat());

        mWhereField = (EditText) v.findViewById(R.id.thing_details_where);
        mWhereField.setText(mThing.getWhere());

        mBarcodeField = (EditText) v.findViewById(R.id.barcode_text);
        mBarcodeField.setText(mThing.getBarcode());
    }

    // Todo onActivityResult is duplicated in ThingDetailFragment and TingleMainFragment

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
            case Activity.RESULT_OK :
                makeToast(getString(R.string.ok));

            case Activity.RESULT_CANCELED :
                makeToast("Scan was cancelled!");
                Log.d("onActivityResult", "RESULT_CANCELED");

            case REQUEST_SCAN :
                handleScanData(data);

            case REQUEST_PHOTO :
                updatePhotoView();
        }
    }

    // Lookup item from barcode and save information
    private void handleScanData(Intent data)
    {
        String contents = data.getStringExtra("SCAN_RESULT");
        String format = data.getStringExtra("SCAN_RESULT_FORMAT");

        // Handle successful scan
        Toast toast = Toast.makeText(getContext(), "Content:" + contents + " Format:" + format , Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();

        Log.d("onActivityResult", "contents: " + contents);

        // Lookup item from barcode if user has connection
        if(new NetworkUtils(getContext()).isOnline())
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

    // Method used to load bitmap into ImageView showing picture
    private void updatePhotoView()
    {
        if (mPhotoFile == null || !mPhotoFile.exists())
        {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }


}
