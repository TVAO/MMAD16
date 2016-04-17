package tvao.mmad.itu.tingle.Search;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tvao.mmad.itu.tingle.Model.Thing;

/**
 * This class is used to search for items in the database asynchronously using AsyncTask.
 * The Async task takes a search string item as input and returns a string of the name of item if it exists.
 */
public class SearchHandler extends AsyncTask<String, Void, String> {

    private String response = "????";
    private Boolean found = false;
    private String mWhat;
    private List<Thing> mThings;

    // Determine how to sort and on what parameter to sort content
    ISort mSortHandler;
    Type mSearchType;

    /**
     * Set the current parameter to search on.
     * Can either be name of item, location of item or date of when item was registered.
     * @param searchType - parameter used to search items.
     */
    public void setSearchType(Type searchType)
    {
        mSearchType = searchType;
    }

    // Type of search either based on name or location
    public enum Type
    {
        WHAT,
        WHERE
    }

    public AsyncResponse delegate = null;

    // Interface used to get result of OnPostExecute() in main fragment 'TingleMainFragment'
    // This has been done to avoid this class being a private inner class since it may be used in both 'TingleMainFragment' and 'ThingDetailFragment'
    public interface AsyncResponse
    {
        void processFinish(String searchResult);
    }

    public SearchHandler(ISort sortHandler)
    {
        this.mSortHandler = sortHandler;
        setSearchType(Type.WHAT);//set what as default

    }

    public SearchHandler(List<Thing> things, AsyncResponse delegate)
    {
        this.delegate = delegate;
        mThings = things;
    }

    public SearchHandler(List<Thing> things, ISort sortHandler, AsyncResponse delegate)
    {
        this.delegate = delegate;
        mThings = things;
        mSortHandler = sortHandler;
        setSearchType(Type.WHAT); // Name of item is default search parameter
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

    /**
     * Sorts a given list of items based on a sorting parameter.
     * @param things - list of items.
     * @param type - sorting parameter (name, location or date)
     * @return sorted list - new list in sorted order.
     */
    public List sort(List<Thing> things, ISort.sortingParameter type)
    {
        Thing[] thingArr = things.toArray(new Thing[things.size()]); // convert list to array
        mSortHandler.sort(thingArr, type);
        return Arrays.asList(thingArr);
    }

    /**
     * Default sorting based on name of item.
     * @param things - items to be sorted.
     * @return sorted list - new list in sorted order based on name.
     */
    public List sortDefault(List<Thing> things)
    {
        return sort(things, ISort.sortingParameter.WHAT);
    }

    /**
     * This method is used to find the first item that starts with the same letter as the search string (based on name or location).
     * It uses the first char of search string and the Thing from list as comparison elements.
     *
     * 1) Initially, the method starts a binary search to find an element with a char
     * that is less by one than the char to find.
     *
     * 2) When an element is found, the method will start to fine tune by iterating from the located element until the first element that
     * starts with the char we want to find is located.
     *
     * The binary search implementation has been taken from Peter Sestoft in his paper "Searching and Sorting with Java".
     * @param charInput - first char from input
     * @return index of located element
     */
    private int searchFirstElement(char charInput, List<Thing> things)
    {
        int start = 0;
        int end = things.size(); //- 1; // Todo check ???
        boolean found = false;
        Thing thing;
        int locatedChar = 0;
        int charToCompare = charInput;

        // Binary search until char is lower by one than charToCompare
        int i = 0;
        while (!found && start <= end)
        {
            i = (start + end) / 2;
            thing = things.get(i);

            switch (mSearchType)
            {
                case WHAT:
                    if(thing.getWhat() != null && !thing.getWhat().isEmpty())
                    {
                        locatedChar = thing.getWhat().charAt(0);
                    }
                    break;
                case WHERE:
                    if (thing.getWhere() != null && !thing.getWhat().isEmpty())
                    {
                        locatedChar = thing.getWhere().charAt(0);
                    }
                    break;
            }

            if (charToCompare < locatedChar)
                end = i - 1;
            else if (charToCompare > locatedChar)
                start = i + 1;
            else found = true;
        }

        // Fine tune until the first element is found that starts with charToCompare (linear search)
        if (found)
        {
            while (locatedChar == charToCompare)
            {
                thing = things.get(i);

                switch (mSearchType)
                {
                    case WHAT :
                        if(thing.getWhat() != null && !thing.getWhat().isEmpty())
                        {
                            locatedChar = thing.getWhat().charAt(0);
                        }
                        break;
                    case WHERE :
                        if(thing.getWhere() != null && !thing.getWhere().isEmpty())
                        {
                            locatedChar = thing.getWhere().charAt(0);
                        }
                        break;
                }

                // Found an a!
                if (i == 0 && locatedChar == charToCompare) return 0;

                // Found something smaller than charInput, so return the next element.
                if (locatedChar < charToCompare) return i + 1;

                i--;
            }
        }

        return -1; // Nothing found
    }

    /**
     * Compare search string with stored items.
     * @param searchString - String eg. address.
     * @param things - list of items.
     * @return list of items that match search (e.g. on name or location).
     */
    public List<Thing> search(String searchString, List<Thing> things)
    {
        if (searchString.length() == 0) return new ArrayList<>(); // No input

        searchString = searchString.toLowerCase().trim();

        List<Thing> auxThings = new ArrayList<>();

        // Add items in auxiliary list to avoid modifying original
        for (Thing t : things)
        {
            if (t.getWhat() != null && !t.getWhat().isEmpty() && t.getWhere() != null && !t.getWhere().isEmpty())
            {
                auxThings.add(t);
            }
        }

        // Sort list based on search parameter (name or location)
        switch (mSearchType)
        {
            case WHAT :
                auxThings = sort(auxThings, ISort.sortingParameter.WHAT);
                break;
            case WHERE :
                auxThings = sort(auxThings, ISort.sortingParameter.WHERE);
                break;
        }

        int i = searchFirstElement(searchString.charAt(0), auxThings); // Search for first element that starts with the same char as input
        if (i == -1) return null; // No start element found

        // Define search result as strings
        Thing thing = auxThings.get(i);
        String toCompare = getCurrentSearchResultString(thing);

        //Define resulting list of matches
        List<Thing> result = new ArrayList<>();

        // Compare and insert
        while (toCompare.charAt(0) <= searchString.charAt(0) && i < auxThings.size()) //Do not compare Strings greater than input
        {
            thing = auxThings.get(i);
            toCompare = getCurrentSearchResultString(thing);

            if (toCompare.startsWith(searchString))
            {
                //Add to suggestion list
                result.add(thing);
            }

            i++;
        }
        return result;
    }

    // Returns string based on sorting parameter (name or location).
    private String getCurrentSearchResultString(Thing thing)
    {
        switch (mSearchType)
        {
            case WHAT:
                return thing.getWhat().toLowerCase().trim();
            case WHERE:
                return thing.getWhere().toLowerCase().trim();
        }
        return null;
    }



}
