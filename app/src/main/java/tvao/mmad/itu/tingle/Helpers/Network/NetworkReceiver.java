package tvao.mmad.itu.tingle.Helpers.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import tvao.mmad.itu.tingle.R;

/**
 * This class is used to register and unregister the NetworkReceiver within the main activity,
 * you ensure that the app won't be woken up after the user leaves the app to avoid draining system resources.
 * When the device's network connection changes, NetworkReceiver intercepts the action CONNECTIVITY_ACTION,
 * determines what the network connection status is, and sets the flags wifiConnected and mobileConnected to true/false accordingly.
 * The upshot is that the next time the user returns to the app,
 * the app will only download the latest feed and update the display if NetworkActivity.refreshDisplay is set to true.
 */
@Deprecated
public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        // Checks the user prefs and the network connection. Based on the result, decides whether
        // to refresh the display or keep the current display.
        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
        if (NetworkActivity.WIFI.equals(NetworkActivity.sPref)
                && networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
        {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            NetworkActivity.refreshDisplay = true;
            Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();

            // If the setting is ANY network and there is a network connection
            // (which by process of elimination would be mobile), sets refreshDisplay to true.
        }
        else if (NetworkActivity.ANY.equals(NetworkActivity.sPref) && networkInfo != null)
        {
            NetworkActivity.refreshDisplay = true;

            // Otherwise, the app can't download content--either because there is no network
            // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
            // is no Wi-Fi connection.
            // Sets refreshDisplay to false.
        }
        else
        {
            NetworkActivity.refreshDisplay = false;
            Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
        }
    }
}
