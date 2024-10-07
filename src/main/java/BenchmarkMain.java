import edu.neu.coe.info6205.sort.elementary.InsertionSortBasic;
import edu.neu.coe.info6205.util.Timer;
import java.util.Arrays;
import java.util.Random;

public class BenchmarkMain {
    public static void main(String[] args) {
        int[] nValues = {1000, 2000, 4000, 8000, 16000};

        for (int n : nValues) {
            System.out.println("Testing for n = " + n);

            Integer[] randomArray = generateRandomArray(n);
            benchmark("Random Array", randomArray);

            Integer[] orderedArray = generateOrderedArray(n);
            benchmark("Ordered Array", orderedArray);

            Integer[] partiallyOrderedArray = generatePartiallyOrderedArray(n);
            benchmark("Partially Ordered Array", partiallyOrderedArray);

            Integer[] reverseOrderedArray = generateReverseOrderedArray(n);
            benchmark("Reverse Ordered Array", reverseOrderedArray);
        }
    }

    /**
     * @param description
     * @param array
     */
    private static void benchmark(String description, Integer[] array) {
        Timer timer = new Timer();  // 初始化 Timer
        InsertionSortBasic<Integer> sorter = InsertionSortBasic.create();

        // 在每次调用 repeat 前，确保 Timer 处于暂停状态
        timer.pause();  // 确保计时器已暂停

        double time = timer.repeat(10, false,
                () -> Arrays.copyOf(array, array.length),  // Supplier<T> 提供数组
                a -> sorter.sort(a),  // Consumer<T> 执行排序
                null);  // 后处理函数为 null

        System.out.println(description + ": " + time + " ms");
    }



    private static Integer[] generateRandomArray(int n) {
        Integer[] array = new Integer[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }

    private static Integer[] generateOrderedArray(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = i;
        }
        return array;
    }

    private static Integer[] generatePartiallyOrderedArray(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n / 2; i++) {
            array[i] = i;
        }
        for (int i = n / 2; i < n; i++) {
            array[i] = n - i;
        }
        return array;
    }

    private static Integer[] generateReverseOrderedArray(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = n - i;
        }
        return array;
    }
}
