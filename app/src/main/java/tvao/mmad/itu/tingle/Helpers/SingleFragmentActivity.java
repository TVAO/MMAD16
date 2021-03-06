package tvao.mmad.itu.tingle.Helpers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import tvao.mmad.itu.tingle.R;

/**
 * This class is used to reuse code that is written for all activities.
 * The class is a subclass of AppCompatActivity and implemented by the activities to reduce code duplication.
 * The AppCompatActivity class is extended to use the toolbar and support FragmentActivities.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Method used by all fragments for setup.
     * @return - new fragment.
     */
    protected abstract Fragment createFragment();

    /**
     * Initialize fragment component and inflate UI with defined layout resource programmed widgets to interact with.
     * @param savedInstanceState - contains data supplied from onSaveInstanceState() if reinitialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);

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
