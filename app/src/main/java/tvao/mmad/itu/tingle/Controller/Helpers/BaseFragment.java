package tvao.mmad.itu.tingle.Controller.Helpers;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * This class is used by all fragments.
 */
public class BaseFragment extends Fragment
{

//    protected ActionBar getActionBar()
//    {
//        return ((AppCompatActivity) getActivity()).getSupportActionBar();
//    }

    protected void makeToast(String string)
    {
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    // Todo Make abstract methods for createButtons, createTextFields and all features common to fragments

}
