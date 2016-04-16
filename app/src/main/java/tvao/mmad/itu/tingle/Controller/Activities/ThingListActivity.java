package tvao.mmad.itu.tingle.Controller.Activities;
import android.support.v4.app.Fragment;

import tvao.mmad.itu.tingle.Controller.Fragments.ThingListFragment;
import tvao.mmad.itu.tingle.Helpers.SingleFragmentActivity;

/**
 * This activity is used to wrap a ThingListFragment used to display things.
 */
@Deprecated
public class ThingListActivity extends SingleFragmentActivity {

    // Todo add fragment methods common from abstract class in base activity and use instead of TingDetailFragment directly

    @Override
    protected Fragment createFragment()
    {
        return new ThingListFragment();
    }
}

