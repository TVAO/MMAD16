package tvao.mmad.itu.tingle.Helpers.Location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tvao.mmad.itu.tingle.R;

/**
 * Lookup service used to get the address of the user's location to know where an item was registered.
 * A street address is fetched by using the Geocoder class in the Android framework location API
 * to convert geographic coordinates (latitude/longitude) to corresponding addresses , vice versa (reverse geocoding).
 *
 * The intent service handles an intent asynchronously on a worker thread, and stops itself when it runs out of work.
 * The intent extras provide the data needed by the service, including a Location object for conversion to an address,
 * and a ResultReceiver object to handle the results of the address lookup.
 * The service uses a Geocoder to fetch the address for the location, and sends the results to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {


    private static final String TAG = "AddressIntentService";
    private ResultReceiver mReceiver;

    /**
     * Creates the IntentService invoked by subclass constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    /**
     * Invoked on worker thread with request to process, only one Intent at the time.
     * Creates a Geocoder object to handle reverse geocoding and fetch the current address of the user.
     * Shuts down automatically when all intents have been processed.
     * @param intent - operation to be performed.
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault()); // A locale represents specific geographical or linguistic region of user

        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        List<Address> addresses = null;

        try
        {
            // Get single address
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        }
        catch (IOException ioException)
        {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0)
        {
            if (errorMessage.isEmpty())
            {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }
        else
        {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
            {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }

    }

    // Sends a result code and message bundle to result receiver
    // Address is sent back to ResultReceiver in activity that started the service
    // Numeric code is used to report success or failure of geocoding request
    // Message contains result data with address
    private void deliverResultToReceiver(int resultCode, String message)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

}
