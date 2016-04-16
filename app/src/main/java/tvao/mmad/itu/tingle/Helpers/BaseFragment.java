package tvao.mmad.itu.tingle.Helpers;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * This class is used by all fragments.
 */
public class BaseFragment extends Fragment
{

    protected void makeToast(String string)
    {
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    // Todo Make abstract methods for createButtons, createTextFields and all features common to fragments

}
