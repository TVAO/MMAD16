package tvao.mmad.itu.tingle.Controller.Activities;
import android.support.v4.app.Fragment;

import tvao.mmad.itu.tingle.Controller.Fragments.ThingListFragment;
import tvao.mmad.itu.tingle.Helpers.SingleFragmentActivity;

/**
 * This activity is used to wrap a ThingListFragment used to display things.
 */
@Deprecated
public class ThingListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment()
    {
        return new ThingListFragment();
    }
}

