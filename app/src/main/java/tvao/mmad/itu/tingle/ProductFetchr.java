package tvao.mmad.itu.tingle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handle networking in Tingle
 */
public class ProductFetchr {

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
     * converts the result from getUrlBytes(String) to a String
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException
    {
        return new String(getUrlBytes(urlSpec));
    }

}
