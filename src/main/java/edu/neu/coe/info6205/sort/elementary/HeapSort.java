package edu.neu.coe.info6205.sort.elementary;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.SortWithComparableHelper;

public class HeapSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public HeapSort(Helper<X> helper) {
        super(helper);
    }

    public void sort(X[] array, int from, int to) {
        if (array == null || array.length <= 1) return;

        // XXX construction phase
        buildMaxHeap(array, from, to);

        // XXX sort-down phase
        Helper<X> helper = getHelper();
        for (int i = to - 1; i > from; i--) {
            helper.swap(array, from, i); // Move the root of the heap to the end
            maxHeap(array, from, i, from); // Rebuild the heap with the reduced size
        }
    }

    private void buildMaxHeap(X[] array, int from, int to) {
        int half = (to + from) / 2;
        for (int i = half - 1; i >= from; i--) {
            maxHeap(array, from, to, i);
        }
    }

    private void maxHeap(X[] array, int from, int heapSize, int index) {
        Helper<X> helper = getHelper();
        final int left = from + (index - from) * 2 + 1; // Calculate left child index
        final int right = from + (index - from) * 2 + 2; // Calculate right child index
        int largest = index;

        // Compare with left child
        if (left < heapSize && helper.compare(array, largest, left) < 0) {
            largest = left;
        }
        // Compare with right child
        if (right < heapSize && helper.compare(array, largest, right) < 0) {
            largest = right;
        }
        // If the largest is not the current node, swap and continue heapifying
        if (largest != index) {
            helper.swap(array, index, largest);
            maxHeap(array, from, heapSize, largest);
        }
    }
}