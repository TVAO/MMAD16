package tvao.mmad.itu.tingle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * This class is used to create a robust network connection (connection detector).
 * The utility class is used to check if a connection exists to a given service, e.g. outpan.com.
 */
public class NetworkUtils {

    private static String DEBUG_TAG = "";

    private Context mContext;

    public NetworkUtils(Context context)
    {
        mContext = context;
    }

    /**
     * Help function used to check if network is "available".
     * Should be used before any network operations used to fetch product data.
     * Handles cases like flaky mobile networks, airplane mode, and restricted background data
     * @return true if connection is available.
     */
    public boolean isOnline()
    {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // Get first available network or null
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet()
    {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks)
            {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                {
                    return true;
                }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network",
                                    "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        Toast.makeText(mContext, mContext.getString(R.string.please_connect_to_internet), Toast.LENGTH_SHORT).show();
        return false;
    }
}


