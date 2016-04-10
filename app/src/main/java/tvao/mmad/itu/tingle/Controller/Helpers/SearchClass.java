package tvao.mmad.itu.tingle.Controller.Helpers;

import android.os.AsyncTask;

import java.util.List;

import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;

/**
 * This class is used to search for items in the database asynchronously using AsyncTask.
 * The Async task takes a search string item as input and returns a string of the name of item if it exists.
 */
public class SearchClass extends AsyncTask<String, Void, String> {

    private String response = "????";
    private Boolean found = false;
    private String mWhat;
    private List<Thing> mThings;

    public AsyncResponse delegate = null;

    // Interface used to get result of OnPostExecute() in main fragment 'TingleMainFragment'
    // This has been done to avoid this class being a private inner class since it may be used in both 'TingleMainFragment' and 'ThingDetailFragment'
    public interface AsyncResponse
    {
        void processFinish(String searchResult);
    }

    public SearchClass(List<Thing> things, AsyncResponse delegate)
    {
        this.delegate = delegate;
        mThings = things;
    }

    @Override
    protected String doInBackground(String... param)
    {
        int i = 0;
        Boolean found = false;
        mWhat = param[0].trim();
        int size = mThings.size();
        while (!found && i < size)
        {
            found = mThings.get(i).getWhat().equals(mWhat);
            i++;
        }
        // Return where item is located if found
        return (found) ? mThings.get(i - 1).getWhere() : "????";

    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinish(result);
    }
}
