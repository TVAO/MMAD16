package tvao.mmad.itu.tingle.Controller.Activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import tvao.mmad.itu.tingle.Controller.Fragments.ThingListFragment;
import tvao.mmad.itu.tingle.Controller.Fragments.ThingListFragment.ThingListFragmentEventListener;
import tvao.mmad.itu.tingle.Controller.Fragments.TingleMainFragment;
import tvao.mmad.itu.tingle.Controller.Fragments.TingleMainFragment.TingleMainFragmentEventListener;
import tvao.mmad.itu.tingle.Helpers.SingleFragmentActivity;
import tvao.mmad.itu.tingle.R;


/**
 * This class represents the main application screen that the user can interact with.
 * The class uses the TingleMainFragment and ThingListFragment to add, removeAt and show items.
 * The activity communicates with fragments by implementing listeners on them.
 */
public class TingleActivity extends SingleFragmentActivity implements ThingListFragmentEventListener, TingleMainFragmentEventListener {

    /**
     * Used to call fragment displaying main page and go back.
     */
    @Override
    public void onBackPressed()
    {
        changeFragment(new TingleMainFragment());
    }

    /**
     * Used to call fragment displaying list.
     */
    @Override
    public void onShowItemsPressed()
    {
        changeFragment(new ThingListFragment());
    }

    /**
     *  Refresh fragments to update list in landscape after adding item.
     */
    @Override
    public void onAddItemPressed()
    {
        setFragment();
    }

    /**
     * Used to create a fragment displaying UI content of main page.
     * @return fragment with main page content.
     */
    @Override
    protected Fragment createFragment()
    {
        return new TingleMainFragment();
    }

    /**
     * Called when the activity is starting where the activity UI is inflated and setup.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);
        setFragment();
    }

    private void changeFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_left, fragment)
                .commit();
    }

    private void setFragment()
    {
        // If orientation is portrait
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
        FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = new TingleMainFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_left, fragment) // Replace current fragment instead of new fragment each time
                    .commit();
        }

        // If orientation is landscape
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment tingleFragment = new TingleMainFragment();
            Fragment listFragment = new ThingListFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_left, tingleFragment) // Left side showing main page
                        .replace(R.id.fragment_container_right, listFragment) // Right side showing itemClC list
                        .commit();
        }

    }

}
