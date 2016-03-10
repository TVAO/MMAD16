package tvao.mmad.itu.tingle.Controller.Helpers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import tvao.mmad.itu.tingle.R;

/**
 * This class is used to reuse code that is written for all activities.
 * The class is a subclass of FragmentActivity and implemented by the activities to reduce code duplication.
 */
public abstract class SingleFragmentActivity extends FragmentActivity {

    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle); // Proper layout?

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container_left);

        if (fragment == null)
        {
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_left, fragment)
                    .commit();
        }
    }



}
