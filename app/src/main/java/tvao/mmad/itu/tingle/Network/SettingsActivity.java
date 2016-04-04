package tvao.mmad.itu.tingle.Network;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import static android.content.SharedPreferences.*;

/**
 * SettingsActivity is a subclass of PreferenceActivity that displays a preferences screen.
 * The preference screen should let users specify whether to display attributes for an item
 * or whether to download/fetch product information for items using ThingFetcher.
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Fetch product data based on saved items to check for updated info

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Registers a listener whenever a key changes
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Unregisters the listener set in onResume().
        // It's best practice to unregister listeners when your app isn't using them to cut down on
        // unnecessary system overhead. You do this in onPause().
        //getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this); // Not relevant for fragment based app
    }

    // When the user changes the preferences selection,
    // onSharedPreferenceChanged() restarts the main activity as a new
    // task. Sets the refreshDisplay flag to "true" to indicate that
    // the main activity should update its display.
    // The main activity queries the PreferenceManager to get the latest settings.

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // Sets refreshDisplay to true so that when the user returns to the main
        // activity, the display refreshes to reflect the new settings.
        NetworkActivity.refreshDisplay = true;
    }
}