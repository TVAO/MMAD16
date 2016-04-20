package tvao.mmad.itu.tingle.Helpers.Search;

import tvao.mmad.itu.tingle.Model.Thing;

/**
 * This interface outlines the sorting mechanisms common to all sorting algorithms
 * used when searching for item names and locations in the SearchHandler.
 */
public interface ISort {

    void sort(Thing[] items, SortingOrder sortOrder);

    //boolean isSorted(Thing[] items);

    enum SortingOrder
    {
        WHAT,
        WHERE,
        DATE
    }

}
