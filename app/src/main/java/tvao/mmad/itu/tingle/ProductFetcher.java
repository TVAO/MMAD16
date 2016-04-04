package tvao.mmad.itu.tingle;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tvao.mmad.itu.tingle.Model.Thing;

/**
 * Handle networking in Tingle and is used to get product info from outpan.com.
 */
public class ProductFetcher {

    private static final String TAG = "ProductFetcher";
    protected static final String API_KEY = "dfcb9acf8be3478bd404abec2c193791";

    /**
     * Fetches raw data from a URL and returns it as an array of bytes.
     * Creates a URL object from a string – like, say, https://www.bignerdranch.com.
     * Then it calls openConnection() to create a connection object pointed at the URL.
     * URL.openConnection() returns a URLConnection, but because you are connecting to an http URL, you can cast it to HttpURLConnection.
     * This gives you HTTP-specific interfaces for working with request methods, response codes, streaming methods, and more.
     * @param urlSpec, URL string e.g. www.tingle.com
     * @return byte array of data
     * @throws IOException
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException
    {

        // Create a URL from string
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Setup connection

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            // Error handling
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            // Fetch data continuously
            while ((bytesRead = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, bytesRead);
            }

            // Close connection and return byte array
            out.close();
            return out.toByteArray();
        }
        finally
        {
            connection.disconnect();
        }
    }

    /**
     * Converts the result from getUrlBytes(String) to a String
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException
    {
        try
        {
            return new String(getUrlBytes(urlSpec));
        }
        catch(IOException ioe)
        {
            return "Unable to retrieve web page. URL may be invalid";
        }
    }

    public List<Thing> fetchProducts()
    {
        List<Thing> items = new ArrayList<>();

        try
        {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseProducts(items, jsonBody);
        }
        catch (JSONException je)
        {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        catch(IOException ioe)
        {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    private void parseProducts(List<Thing> things, JSONObject jsonBody)
            throws IOException, JSONException
    {
        JSONObject photosJsonObject = jsonBody.getJSONObject("images");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("images");

        for (int i = 0; i < photoJsonArray.length(); i++)
        {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            Thing thing = new Thing();

            thing.setBarcode(photoJsonObject.getString("barcode"));
            thing.setWhat(photoJsonObject.getString("name"));

            if (!photoJsonObject.has("url_s"))
            {
                continue;
            }

            //thing.setUrl(photoJsonObject.getString("url_s"));
            things.add(thing);
        }
    }



}
