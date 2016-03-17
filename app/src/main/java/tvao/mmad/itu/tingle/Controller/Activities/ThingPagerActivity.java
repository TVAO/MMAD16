package tvao.mmad.itu.tingle.Controller.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;
import java.util.UUID;

import tvao.mmad.itu.tingle.Controller.Fragments.ThingFragment;
import tvao.mmad.itu.tingle.Controller.Fragments.TingleFragment;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;

import static tvao.mmad.itu.tingle.Controller.Fragments.TingleFragment.*;

/**
 * ViewPager allows users to navigate between list items by swiping across the screen,
 * to “page” forward or backward through the things.
 */
public class ThingPagerActivity extends AppCompatActivity { //FragmentActivity implements eventListener {

    private static final String EXTRA_THING_ID = "thing_id";
    //private static final String EXTRA_THING_ID = "thingintent.thing_id";
    private ViewPager mViewPager;
    private List<Thing> mThings;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, ThingPagerActivity.class);
        intent.putExtra(EXTRA_THING_ID, crimeId);
        return intent;
    }

    /**
     * Helper function used to create intent carrying id of thing to be displayed on the screen when swiping between activities.
     * @param savedInstanceState - a bundle map with e.g. serializable objects from saved state of application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_pager);

        UUID thingId = (UUID) getIntent().getSerializableExtra(EXTRA_THING_ID);

        // Find View Pager
        mViewPager = (ViewPager) findViewById(R.id.activity_thing_pager_view_pager);

        // Get data
        mThings = ThingRepository.get(this).getThings();

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Setup agent managing conversation with ViewPager
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager)
        {

            @Override
            public Fragment getItem(int position)
            {
                Thing thing = mThings.get(position);
                return ThingFragment.newInstance(thing.getId());
            }

            @Override
            public int getCount()
            {
                return mThings.size();
            }

        });

        // Set current item in ViewPager to item clicked on in list
        for (int i = 0; i < mThings.size(); i++)
        {
            if (mThings.get(i).getId().equals(thingId))
            {
                mViewPager.setCurrentItem(i); // Todo bug always shows detailed screen for first item in list
                break;
            }
        }

    }

    public static ActivityOptions getTransition(Activity activity, View thingView)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            thingView.setTransitionName("thing");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    thingView, "thing");

            return options;
        }
        else
        {
            return null;
        }
    }

}
