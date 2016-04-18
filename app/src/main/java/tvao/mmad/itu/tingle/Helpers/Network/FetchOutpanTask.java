package tvao.mmad.itu.tingle.Helpers.Network;

import android.os.AsyncTask;
import android.util.Log;

import tvao.mmad.itu.tingle.Model.Thing;

/**
 * Used to work with background threads using AsyncTask.AsyncTask class that creates a background thread and runs code in doInBackground on that thread.
 * This inner class override AsyncTask.doInBackground to get product data from Outpan website and log it.
 * First parameter is input parameters (e.g. barcode),
 * Second parameter is a specific type for sending progress updates (not used),
 * Third parameter is the result from fetching data (e.g. Thing object with product info fetched from JSON object).
 */
public class FetchOutpanTask extends AsyncTask<String, Void, Thing> {

    private static final String TAG = "FetchOutpanTask";
    public AsyncResponse delegate = null;

    /**
     * Interface used to get result of OnPostExecute() in main fragment 'TingleMainFragment'
     * This has been done to avoid this class being a private inner class since it is used in both 'TingleMainFragment' and 'ThingDetailFragment'
     */
    public interface AsyncResponse
    {
        void processFinish(Thing output);
    }

    public FetchOutpanTask(AsyncResponse delegate)
    {
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(Thing result)
    {
        delegate.processFinish(result);
    }

    @Override
    protected Thing doInBackground(String... params)
    {
;       Thing result = new ThingFetcher().fetchThing(params[0]); // Barcode param
        Log.i(TAG, "Fetched contents of URL with resulted Thing: " + result);

        return result;
    }

}
