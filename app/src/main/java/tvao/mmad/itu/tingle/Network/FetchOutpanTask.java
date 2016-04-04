package tvao.mmad.itu.tingle.Network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Used to work with background threads using AsyncTask.AsyncTask class that creates a background thread and runs code in doInBackground on that thread.
 * This inner class override AsyncTask.doInBackground to get product data from Outpan website and log it.
 * First parameter is input parameters (e.g. barcode),
 * Second parameter is a specific type for sending progress updates (not used),
 * Third parameter is the result from fetching data (e.g. JSON object with product info)
 */
public class FetchOutpanTask extends AsyncTask<String, Void, byte[]> {

    private static final String TAG = "FetchOutpanTask";

    @Override
    protected byte[] doInBackground(String... params)
    {
        byte[] result = null;

        try
        {
            // Example: https://www.outpan.com/view_product.php?barcode=0076808501063
            result = new ThingFetcher().getUrlBytes("https://www.outpan.com/");

            // https://api.outpan.com/v2/products/[barcode]/?apikey=[key]
            // result = new ThingFetcher().
            // getUrlBytes("https://api.outpan.com/v2/products/" + params[0] + "/?apikey=[KEY]");
            Log.i(TAG, "Fetched contents of URL: " + result);
        }
        catch (IOException ioe)
        {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        }

        new ThingFetcher().fetchProducts();

        return result;
    }

    @Override
    protected void onPostExecute(byte[] products)
    {
        //updateUI(); // Todo post execute something ?
    }

//        @Override
//        protected void onPostExecute(List<Thing> products)
//        {
//            mAdapter.setThings(products);
//            updateUI();
//        }
}
