package tvao.mmad.itu.tingle.Search;

import tvao.mmad.itu.tingle.Model.Thing;

/**
 * This interface outlines the sorting mechanisms common to all sorting algorithms used when searching for item names and locations in the SearchHandler.
 */
public interface ISort {

    void sort(Thing[] items, sortingParameter t);

    enum sortingParameter
    {
        WHAT,
        WHERE,
        DATE
    }

}
