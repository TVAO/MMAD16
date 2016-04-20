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

    /**
     * Runs on the UI thread after doInBackground().
     * The specified result is the value returned by doInBackground().
     * Not invoked if task is cancelled.
     * @param result - result of the operation computed by doInBackground().
     */
    @Override
    protected void onPostExecute(Thing result)
    {
        delegate.processFinish(result);
    }

    /**
     * Overwritten to perform a computation on a background thread.
     * The specified parameters are the parameters passed to execute() by the caller of this task.
     * Method may call {@link #publishProgress} to publish updates on the UI thread.
     *
     * @param params - task parameters.
     *
     * @return result defined by subclass of this task used in main and detail fragment to fetch barcode data.
     */
    @Override
    protected Thing doInBackground(String... params)
    {
;       Thing result = new ThingFetcher().fetchThing(params[0]); // Barcode param
        Log.i(TAG, "Fetched contents of URL with resulted Thing: " + result);

        return result;
    }

}
