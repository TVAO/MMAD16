package tvao.mmad.itu.tingle.Search;

import java.util.List;

import tvao.mmad.itu.tingle.Model.Thing;

/**
 * Abstract class containing methods common for all sorting algorithm classes.
 */
public abstract class GenericSort {

    protected ISort.SortingOrder mSortingOrder; // What, where or date

    /**
     * Helper sorting function used to sort a list of items to correct alphabetical ordering or ordering according to registered date.
     *
     * @param items - list of items
     * @param i - first item to be swapped
     * @param j - second item to be swapped
     */
    protected void exch(Thing[] items, int i, int j)
    {
        // Exchange a[i] and a[j]
        Thing thingToSwap = items[i];
        items[i] = items[j];
        items[j] = thingToSwap;
    }

    /**
     * Helper sorting function used to check if content of item 1 is less than content of item 2.
     * By way of example, the item "Book" comes before the item "Plane" due to the alphabetical ordering.
     * @param item1 - first item of comparison
     * @param item2 - second item of comparison
     * @return true if item1 comes before item2 based on alphabetical ordering or date
     */
    protected boolean less(Thing item1, Thing item2)
    {
        switch (mSortingOrder)
        {
            case WHAT : // Sort alphabetically by name
                return (item1.getWhat().toLowerCase().trim().compareTo(item2.getWhat().toLowerCase().trim()) < 0);

            case WHERE : // Sort alphabetically by location
                return (item1.getWhere().toLowerCase().trim().compareTo(item2.getWhere().toLowerCase().trim()) < 0);

            case DATE : // Sort by registered date
                return (item1.getDate().compareTo(item2.getDate()) < 0);
            default:
                return false; // Other

        }
    }

    /**
     * Checks if list of items is sorted using the less function.
     * The less function checks the sorting based on a sorting parameter, e.g. what item, location of the item or date upon registration of item.
     * @param list - list of items
     * @return true if items are sorted based on sorting parameter.
     */
    public boolean isSorted(List<Thing> list)
    {
        for (int i = 1; i < list.size(); i++)
        {
            if (less(list.get(i), list.get(i - 1))) return false;
        }
        return true;
    }
}
