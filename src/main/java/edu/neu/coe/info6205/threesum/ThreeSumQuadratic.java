package edu.neu.coe.info6205.threesum;

import java.util.*;

/**
 * Implementation of ThreeSum which follows the approach of dividing the solution-space into
 * N sub-spaces where each subspace corresponds to a fixed value for the middle index of the three values.
 * Each subspace is then solved by expanding the scope of the other two indices outwards from the starting point.
 * Since each subspace can be solved in O(N) time, the overall complexity is O(N^2).
 * <p>
 * NOTE: The array provided in the constructor MUST be ordered.
 */
public class ThreeSumQuadratic implements ThreeSum {
    /**
     * Construct a ThreeSumQuadratic on a.
     *
     * @param a a sorted array.
     */
    public ThreeSumQuadratic(int[] a) {
        this.a = a.clone();
        Arrays.sort(this.a);
        length = a.length;
    }

    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < length; i++) triples.addAll(getTriples(i));
        Collections.sort(triples);
        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Get a list of Triples such that the middle index is the given value j.
     *
     * @param j the index of the middle value.
     * @return a Triple such that
     */

    public List<Triple> getTriples(int j) {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < a.length - 2; i++) {
            int target = -a[i];
            HashSet<Integer> seen = new HashSet<>();
            for (int k = i + 1; k < a.length; k++) {
                if (seen.contains(target - a[k])) {
                    triples.add(new Triple(a[i], a[k], target - a[k]));
                } else {
                    seen.add(a[k]);
                }
            }
        }
        Collections.sort(triples);
        return triples.stream().distinct().toList();
    }

    private final int[] a;
    private final int length;
}