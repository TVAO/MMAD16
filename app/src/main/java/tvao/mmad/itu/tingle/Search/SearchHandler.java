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
public class SearchHandler extends AsyncTask<String, Void, List<Thing>> {

    private List<Thing> mThings;

    // Determine how to sort and on what parameter to sort content
    ISort mSortHandler;
    SearchType mSearchType;

    /**
     * Set the current parameter to search on.
     * Can either be name of item, location of item or date of when item was registered.
     * @param searchType - parameter used to search items.
     */
    public void setSearchType(SearchType searchType)
    {
        mSearchType = searchType;
    }

    // SearchType of search either based on name or location
    public enum SearchType
    {
        SEARCH_WHAT,
        SEARCH_WHERE
    }

    public AsyncResponse delegate = null;

    // Interface used to get result of OnPostExecute() in main fragment 'TingleMainFragment'
    // This has been done to avoid this class being a private inner class since it may be used in both 'TingleMainFragment' and 'ThingDetailFragment'
    @Deprecated
    public interface AsyncResponse
    {
        void processFinish(List<Thing> searchResult);
    }

    public SearchHandler(ISort sortHandler)
    {
        mSortHandler = sortHandler;
        setSearchType(SearchType.SEARCH_WHAT); //set name as default search parameter

    }

    // Previously used for async search
    @Deprecated
    public SearchHandler(List<Thing> things, ISort sortHandler, AsyncResponse searchResult)
    {
        mThings = things;
        mSortHandler = sortHandler;
        setSearchType(SearchType.SEARCH_WHAT);
        delegate = searchResult;
    }

    @Override
    @Deprecated
    protected List<Thing> doInBackground(String... param)
    {
        String searchString = param[0].toLowerCase().trim();
        return search(searchString, mThings);

    }

    @Override
    @Deprecated
    protected void onPostExecute(List<Thing> result)
    {
        delegate.processFinish(result);
    }

    /**
     * Sorts a given list of items based on a sorting parameter.
     * @param things - list of items.
     * @param sortingOrder - sorting parameter (name, location or date)
     * @return sorted list - new list in sorted order.
     */
    public List sort(List<Thing> things, ISort.SortingOrder sortingOrder)
    {
        Thing[] thingArr = things.toArray(new Thing[things.size()]); // convert list to array
        mSortHandler.sort(thingArr, sortingOrder);
        return Arrays.asList(thingArr);
    }

//    /**
//     * Sorts a given list of items based on a sorting parameter.
//     * @param things - list of items.
//     * @param type - sorting parameter (name, location or date)
//     * @return true if list is sorted.
//     */
//    public boolean sort(List<Thing> things)
//    {
//        Thing[] thingArr = things.toArray(new Thing[things.size()]); // convert list to array
//        mSortHandler.sort(thingArr, ISort.SortingOrder.SEARCH_WHAT);
//        return mSortHandler.isSorted(thingArr);
//    }

    /**
     * Default sorting based on name of item.
     * @param things - items to be sorted.
     * @return sorted list - new list in sorted order based on name.
     */
    public List sortDefault(List<Thing> things)
    {
        return sort(things, ISort.SortingOrder.WHAT);
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
        int end = things.size() - 1;
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
                case SEARCH_WHAT:
                    if(thing.getWhat() != null && !thing.getWhat().isEmpty())
                    {
                        locatedChar = thing.getWhat().charAt(0);
                    }
                    break;
                case SEARCH_WHERE:
                    if (thing.getWhere() != null && !thing.getWhere().isEmpty())
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
                    case SEARCH_WHAT:
                        if(thing.getWhat() != null && !thing.getWhat().isEmpty())
                        {
                            locatedChar = thing.getWhat().charAt(0);
                        }
                        break;
                    case SEARCH_WHERE:
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
            case SEARCH_WHAT:
                auxThings = sort(auxThings, ISort.SortingOrder.WHAT);
                break;
            case SEARCH_WHERE:
                auxThings = sort(auxThings, ISort.SortingOrder.WHERE);
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
            case SEARCH_WHAT:
                return thing.getWhat().toLowerCase().trim();
            case SEARCH_WHERE:
                return thing.getWhere().toLowerCase().trim();
        }
        return null;
    }

}
