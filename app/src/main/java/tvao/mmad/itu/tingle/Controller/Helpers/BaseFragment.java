package tvao.mmad.itu.tingle.Controller.Helpers;

import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * This class is used by all fragments.
 */
public class BaseFragment extends Fragment
{

    protected ActionBar getActionBar()
    {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

}
