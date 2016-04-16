package tvao.mmad.itu.tingle.Search;

import tvao.mmad.itu.tingle.Model.Thing;

/**
 * Sorts a sequence of strings from standard input using selection sort.
 * This class is a concrete implementation of the Selection Sort algorithm used to sort items based on name, location or date upon search.
 *
 * The algorithm implementation has been taken from the book 'Algorithms' (Fourth Edition) by Robert Sedgewick and Kevin Wayne.
 *
 * Algorithm Sequence:
 *
 * 1) Algorithm divides input list into two parts containing sub list of sorted and unsorted items.
 * 2) Already sorted items are built up from left to right at the front (left) of the list).
 * 3) The sub list of unsorted items is sorted to occupy the rest of the list.
 * 4) Initially, the sorted sub list is empty and the unsorted sub list takes up the entire input list.
 * 5) The algorithm iterates through the items by finding the smallest (or largest, depending on sorting order) element in the unsorted sub list.
 * 6) Then it swaps the smallest found item with the leftmost unsorted element (putting it in sorted order).
 * 7) Finally, the algorithm moves the sub list boundaries one element to the right.
 *
 */
public class SelectionSort extends GenericSort implements ISort {

    /**
     * Sort a list of items using the selection sort algorithm.
     * @param items - items to sort.
     * @param sortingParameter - parameter used to determine what to sort on (name, location or date).
     */
    @Override
    public void sort(Thing[] items, sortingParameter sortingParameter)
    {

        mSortingParameter = sortingParameter; // What, where or date

        int N = items.length; // Determine size of list

        for (int i = 0; i < N; i++)
        {
            int min = i; // Find smallest element
            for (int j = i + 1; j < N; j++)
            {
                if (less(items[j], items[min]))
                {
                    min = j; // New minimum item
                }
                exch(items, i, min); // Exchange compared greater item with new lesser item
            }
        }
    }
}