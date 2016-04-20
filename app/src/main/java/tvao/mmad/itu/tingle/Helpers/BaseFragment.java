package tvao.mmad.itu.tingle.Helpers;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * This class is used by all fragments and outlines functionalities and attributes they have in common.
 * The class should have been abstract and contain methods for creating widgets (buttons, text fields ...) common for all fragments.
 */
public class BaseFragment extends Fragment
{

    protected void makeToast(String string)
    {
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
