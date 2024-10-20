package edu.neu.coe.info6205.pq;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Priority Queue Data Structure which uses a binary heap.
 * <p/>
 * It is unlimited in capacity, although there is no code to grow it after it has been constructed.
 * It can serve as a minPQ or a maxPQ (define "max" as either false or true, respectively).
 * <p/>
 * It can support the root at index 1 or the root at index 2 variants.
 * <p/>
 * It follows the code from Sedgewick and Wayne more or less. I have changed the names a bit. For example,
 * the methods to insert and remove the max (or min) element are called "give" and "take," respectively.
 * <p/>
 * It operates on arbitrary Object types which implies that it requires a Comparator to be passed in.
 * <p/>
 * For all details on usage, please see PriorityQueueTest.java
 *
 * @param <K>
 */
public class PriorityQueue<K> implements Iterable<K> {
    // PriorityQueue 类中的字段定义部分
    private K highestPrioritySpill = null;  // 用于记录最高优先级的溢出元素



    /**
     * Basic constructor that takes the max value, an actual array of elements, and a comparator.
     *
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param binHeap    a pre-formed array with length one greater than the required capacity.
     * @param first      the index of the root element.
     * @param last       the number of elements in binHeap
     * @param comparator a comparator for the type K
     * @param floyd      true if we use Floyd's trick
     */
    public PriorityQueue(boolean max, Object[] binHeap, int first, int last, Comparator<K> comparator, boolean floyd) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.last = last;
        //noinspection unchecked
        this.binHeap = (K[]) binHeap;
        this.floyd = floyd;
    }

    /**
     * Constructor which takes only the priority queue's maximum capacity and a comparator
     *
     * @param n          the desired maximum capacity.
     * @param first      the index to use for the first (root) element.
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {

        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(max, new Object[n + first], first, 0, comparator, floyd);
    }

    /**
     * Constructor which takes only the priority queue's maximum capacity and a comparator
     *
     * @param n          the desired maximum capacity.
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue(int n, boolean max, Comparator<K> comparator, boolean floyd) {

        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(n, 1, max, comparator, floyd);
    }

    /**
     * Constructor which takes only the priority queue's maximum capacity and a comparator
     *
     * @param n          the desired maximum capacity.
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue(int n, boolean max, Comparator<K> comparator) {

        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(n, 1, max, comparator, false);
    }

    /**
     * Constructor which takes only the priority queue's maximum capacity and a comparator
     *
     * @param n          the desired maximum capacity.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue(int n, Comparator<K> comparator) {
        this(n, 1, true, comparator, true);
    }

    /**
     * @return true if the current size is zero.
     */
    public boolean isEmpty() {
        return last == 0;
    }

    /**
     * @return the number of elements actually stored in this Priority Queue
     */
    public int size() {
        return last;
    }

    /**
     * Insert an element with the given key into this Priority Queue.
     *
     * @param key the value of the key to give
     */
    public void give(K key) {
        if (last == binHeap.length - first)
            last--; // if we are already at capacity, then we arbitrarily trash the least eligible element
        // (even if it's more eligible than key).
        binHeap[++last + first - 1] = key; // insert the key into the binary heap just after the last element
        swimUp(last + first - 1);
        if (last == binHeap.length - first) {
            // 如果堆已满，获取堆顶元素，视为溢出元素
            K spilled = binHeap[first];  // 获取堆顶元素（通常是最大或最小的元素）

            // 将新的元素替换掉堆顶元素
            binHeap[first] = key;  // 直接替换堆顶元素为新的元素
            sink(first);  // 调整堆，保持堆的性质

            // 比较溢出元素的优先级，并更新 highestPrioritySpill
            if (highestPrioritySpill == null || comparator.compare(spilled, highestPrioritySpill) > 0) {
                highestPrioritySpill = spilled;
            }
        } else {
            binHeap[++last + first - 1] = key;  // 如果堆未满，直接插入新元素
            swimUp(last + first - 1);  // 重新排序堆
        }

    }

    public K getHighestPrioritySpill() {
        return highestPrioritySpill;
    }

    /**
     * Remove the root element from this Priority Queue and adjust the binary heap accordingly.
     * If max is true, then the result will be the maximum element, else the minimum element.
     * NOTE that this method is called DelMax (or DelMin) in the book.
     *
     * @return If max is true, then the maximum element, otherwise the minimum element.
     * @throws PQException if this priority queue is empty
     */
    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        if (floyd) return doTake(this::snake);
        else return doTake(this::sink);
    }

    K doTake(Consumer<Integer> f) {
        K result = binHeap[first]; // get the root element (the largest or smallest, according to field max)
        swap(first, last-- + first - 1); // swap the root element with the last element
        f.accept(first); // invoke the function f so that it is ordered again
        binHeap[last + first] = null; // prevent loitering
        return result;
    }

    /**
     * Sink the element at index k down
     */
    void sink(@SuppressWarnings("SameParameterValue") int k) {
        doHeapify(k, (a, b) -> !unordered(a, b));
    }

    private int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (firstChild(i) <= last + first - 1) {
            int j = firstChild(i);
            if (j < last + first - 1 && unordered(j, j + 1)) j++;
            if (p.test(i, j)) break;
            swap(i, j);
            i = j;
        }
        return i;
    }

    //Special sink method that sink the element and then swim the element back
    void snake(@SuppressWarnings("SameParameterValue") int k) {
        swimUp(doHeapify(k, (a, b) -> !unordered(a, b)));
    }

    /**
     * Swim the element at index k up
     */
    void swimUp(int k) {
        int i = k;
        while (i > first && unordered(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Exchange the values at indices i and j
     */
    private void swap(int i, int j) {
        K tmp = binHeap[i];
        binHeap[i] = binHeap[j];
        binHeap[j] = tmp;
    }

    /**
     * Compare the elements at indices i and j.
     * We expect the first index (the smaller one) to be greater than the second, assuming that max is true.
     * In this case, we return false.
     *
     * @param i the lower index, numerically
     * @param j the higher index, numerically
     * @return true if the values are out of order.
     */
    boolean unordered(int i, int j) {
        return (comparator.compare(binHeap[i], binHeap[j]) > 0) ^ max;
    }

    /**
     * Get the index of the parent of the element at index k
     */
    private int parent(int k) {
        return (k + 1 - first) / 2 + first - 1;
    }

    /**
     * Get the index of the first child of the element at index k.
     * The index of the second child will be one greater than the result.
     */
    private int firstChild(int k) {
        return (k + 1 - first) * 2 + first - 1;
    }

    /**
     * The following methods are for unit testing ONLY!!
     */

    @SuppressWarnings("unused")
    private K peek(int k) {
        return binHeap[k];
    }

    @SuppressWarnings("unused")
    private boolean getMax() {
        return max;
    }

    private final boolean max;
    private final int first;
    private final Comparator<K> comparator;
    private final K[] binHeap; // binHeap[i] is ith element of binary heap (first element is reserved)
    private int last; // number of elements in the binary heap
    private final boolean floyd; //Determine whether floyd's snake method is on or off inside the take method

    /**
     * Non-mutating iterator over all values of this PriorityQueue.
     * NOTE: after the first element, there is no definite ordering of the remaining elements.
     *
     * @return an iterator based on a copy of the underlying array.
     */
    public Iterator<K> iterator() {
        Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(binHeap, last + first)));
        Iterator<K> result = copy.iterator();
        if (first > 0) result.next(); // strip off the leading null value.
        return result;
    }

    public static void main(String[] args) {
        doMain();
    }

    //3
    public static class FourAryHeap<K> extends PriorityQueue<K> {
        private static final int NUM_CHILDREN = 4;

        public FourAryHeap(int capacity, boolean max, Comparator<K> comparator, boolean floyd) {
            super(capacity, max, comparator, floyd);
        }


        protected int parent(int k) {
            return (k + NUM_CHILDREN - 2) / NUM_CHILDREN;
        }

        protected int firstChild(int k) {
            return NUM_CHILDREN * (k - 1) + 2;
        }
    }


    //4
    public static class FourAryHeapWithFloyd<K> extends FourAryHeap<K> {

        public FourAryHeapWithFloyd(int capacity, boolean max, Comparator<K> comparator) {
            super(capacity, max, comparator, true);
        }
    }

    //5
    public static class FibonacciHeap<K> {
        private Node<K> min;
        private int size;
        private final Comparator<K> comparator;

        public FibonacciHeap(Comparator<K> comparator) {
            this.comparator = comparator;
        }
        public void insert(K key) {
            Node<K> node = new Node<>(key);
            if (min == null) {
                min = node;
            } else {
                insertIntoRootList(node);
                if (compare(min, node) > 0) {
                    min = node;
                }
            }
            size++;
        }

        private void consolidate() {
            List<Node<K>> aux = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                aux.add(null);
            }
            Node<K> current = min;
            int numRoots = 0;
            if (current != null) {
                numRoots++;
                current = current.right;
                while (current != min) {
                    numRoots++;
                    current = current.right;
                }
            }
            while (numRoots > 0) {
                int degree = current.degree;
                Node<K> next = current.right;
                while (aux.get(degree) != null) {
                    Node<K> other = aux.get(degree);
                    if (compare(current, other) > 0) {
                        Node<K> temp = current;
                        current = other;
                        other = temp;
                    }
                    link(other, current);
                    aux.set(degree, null);
                    degree++;
                }
                aux.set(degree, current);
                current = next;
                numRoots--;
            }
            min = null;
            for (Node<K> node : aux) {
                if (node != null) {
                    if (min == null) {
                        min = node;
                    } else {
                        insertIntoRootList(node);
                        if (compare(node, min) < 0) {
                            min = node;
                        }
                    }
                }
            }
        }
        private void link(Node<K> child, Node<K> parent) {
            removeNodeFromRootList(child);
            child.parent = parent;
            if (parent.child == null) {
                parent.child = child;
            } else {
                child.right = parent.child;
                child.left = parent.child.left;
                parent.child.left.right = child;
                parent.child.left = child;
            }
            parent.degree++;
            child.mark = false;
        }
        private void removeNodeFromRootList(Node<K> node) {
            if (node.right == node) {
                min = null;
            } else {
                node.left.right = node.right;
                node.right.left = node.left;
                if (min == node) {
                    min = node.right;
                }
            }
        }
        private void mergeChildrenIntoRootList(Node<K> node) {
            if (node.child == null) return;

            Node<K> child = node.child;
            do {
                Node<K> next = child.right;
                child.left = child.right = child;
                insertIntoRootList(child);
                child.parent = null;
                child = next;
            } while (child != node.child);
        }
        private void insertIntoRootList(Node<K> node) {
            if (min == null) {
                min = node;
                node.left = node;
                node.right = node;
            } else {
                node.right = min;
                node.left = min.left;
                min.left.right = node;
                min.left = node;
            }
        }
        private int compare(Node<K> n1, Node<K> n2) {
            return comparator.compare(n1.key, n2.key);
        }

        public boolean isEmpty() {
            return min == null;
        }

        public static class Node<K> {
            K key;
            Node<K> parent, child, left, right;
            int degree;
            boolean mark;
            public Node(K key) {
                this.key = key;
                this.left = this;
                this.right = this;
            }
        }
        public K extractMin() {
            if (min == null) {
                throw new IllegalStateException("Heap is empty, cannot extract minimum.");
            }

            Node<K> oldMin = min;
            if (min != null) {
                if (min.child != null) {
                    mergeChildrenIntoRootList(min);
                }
                removeNodeFromRootList(min);
                if (min == min.right) {
                    min = null;
                } else {
                    min = min.right;
                    consolidate();
                }
                size--;
            }
            return oldMin.key;
        }

    }

    /**
     * XXX Huh?
     */
    static void doMain() {
        String[] s1 = new String[5]; //Created a string type array with size 5
        s1[0] = "A";
        s1[1] = "B";
        s1[2] = "C";
        s1[3] = "D";
        s1[4] = "E";
        boolean max = true;
        boolean floyd = true;
        PriorityQueue<String> PQ_string_floyd = new PriorityQueue<>(max, s1, 1, 5, Comparator.comparing(String::toString), floyd);
        PriorityQueue<String> PQ_string_nofloyd = new PriorityQueue<>(max, s1, 1, 5, Comparator.comparing(String::toString), false);
        Integer[] s2 = new Integer[5]; //created an Integer type array with size 5
        for (int i = 0; i < 5; i++) {
            s2[i] = i;
        }
        PriorityQueue<Integer> PQ_int_floyd = new PriorityQueue<>(max, s2, 1, 5, Comparator.comparing(Integer::intValue), floyd);
        PriorityQueue<Integer> PQ_int_nofloyd = new PriorityQueue<>(max, s2, 1, 5, Comparator.comparing(Integer::intValue), false);
    }
}