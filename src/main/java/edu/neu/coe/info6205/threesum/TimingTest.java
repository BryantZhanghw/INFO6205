package edu.neu.coe.info6205.threesum;
import java.util.Random;

public class TimingTest {

    public static void main(String[] args) {
        int[] sizes = {100, 200, 400, 800, 1600};

        for (int size : sizes) {
            int[] nums = generateRandomArray(size);

            System.out.println("Array size: " + size);
            long startTime = System.nanoTime();
            ThreeSumQuadratic threeSumQuadratic = new ThreeSumQuadratic(nums);
            threeSumQuadratic.getTriples();
            long endTime = System.nanoTime();
            System.out.println("Quadratic approach: " + (endTime - startTime) / 1_000_000.0 + " ms");

            startTime = System.nanoTime();
            ThreeSumQuadraticWithCalipers threeSumQuadraticWithCalipers = new ThreeSumQuadraticWithCalipers(nums);
            threeSumQuadraticWithCalipers.getTriples();
            endTime = System.nanoTime();
            System.out.println("QuadraticWithCalipers approach: " + (endTime - startTime) / 1_000_000.0 + " ms");

            startTime = System.nanoTime();
            ThreeSumQuadrithmic threeSumQuadrithmic = new ThreeSumQuadrithmic(nums);
            threeSumQuadrithmic.getTriples();
            endTime = System.nanoTime();
            System.out.println("Quadrithmic approach: " + (endTime - startTime) / 1_000_000.0 + " ms");

            System.out.println("--------------------------------------");
        }
    }

    public static int[] generateRandomArray(int size) {
        Random rand = new Random();
        int[] nums = new int[size];
        for (int i = 0; i < size; i++) {
            nums[i] = rand.nextInt(2001) - 1000;
        }
        return nums;
    }
}

