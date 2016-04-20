package tvao.mmad.itu.tingle.Controller.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

import tvao.mmad.itu.tingle.Controller.Fragments.ThingDetailFragment;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;

/**
 * ViewPager allows users to navigate between list items by swiping across the screen,
 * to “page” forward or backward through the things.
 */
public class ThingPagerActivity extends AppCompatActivity {

    private static final String EXTRA_THING_ID = "tvao.mmad.itu.tingle.thing_id";
    private ViewPager mViewPager;
    private List<Thing> mThings;

    /**
     * This method is used to instantiate a new Fragment used to display a detailed screen.
     * Encapsulates and abstracts the steps required to setup the object from the client.
     *
     * Rather than having the client call the default constructor and manually set the fragment's arguments themselves,
     * we provide a static factory method that does this for them making fragment instantiation convenient and enforcing well-defined behavior.
     *
     * @param thingId - id related to thing to be shown in activity.
     * @return - new activity to be used with fragments containing thing details.
     */
    public static Intent newIntent(Context packageContext, UUID thingId)
    {
        Intent intent = new Intent(packageContext, ThingPagerActivity.class);
        intent.putExtra(EXTRA_THING_ID, thingId);
        return intent;
    }

    /**
     * Helper function used to create intent carrying id of thing to be displayed on the screen when swiping between activities.
     *
     * This method is called after the Activity onAttachFragment() but before that Fragment onCreateView().
     * The method is used to assign variables, get Intent extras, and anything else that does not involve the View hierarchy/
     *
     * @param savedInstanceState - a bundle map with e.g. serializable objects from saved state of application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_pager);

        final UUID thingId = (UUID) getIntent().getSerializableExtra(EXTRA_THING_ID);

        // Find View Pager
        mViewPager = (ViewPager) findViewById(R.id.activity_thing_pager_view_pager);

        // Get data
        mThings = ThingRepository.get(this).getThings();
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Setup agent managing conversation with ViewPager
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager)
        {
            /**
             * Return the Fragment associated with a specified position.
             */
            @Override
            public Fragment getItem(int position)
            {
                Thing thing = mThings.get(position);
                return ThingDetailFragment.newInstance(thing.getId());
            }

            /**
             * Get amount of items.
             * @return size of item list.
             */
            @Override
            public int getCount() {
                return mThings.size();
            }

        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            /**
             * Invoked when the current page is scrolled.
             * @param position position index of first page being displayed
             * @param positionOffset value from [0, 1) indicating offset from the page at position.
             * @param positionOffsetPixels value in pixels indicating the offset from position.
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            /**
             * Invoked when a new page becomes selected
             * Changes title to name of new thing.
             * @param position - position index of new selected page.
             */
            @Override
            public void onPageSelected(int position)
            {
                Thing thing = mThings.get(position);
                if (thing.getWhat() != null)
                {
                    setTitle(thing.getWhat());
                }
            }

            /**
             * Called when scroll state changes. Useful for discovering when the user
             * begins dragging, when the pager is automatically settling to the current page,
             * or when it is fully stopped/idle.
             *
             * @param state The new scroll state.
             */
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Set current item in ViewPager to item clicked on in list
        for (int i = 0; i < mThings.size(); i++)
        {
            if (mThings.get(i).getId().equals(thingId))
            {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

}
