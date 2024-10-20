import java.util.Random;
import java.util.function.Supplier;
import java.util.function.Consumer;

import edu.neu.coe.info6205.pq.PQException;
import edu.neu.coe.info6205.pq.PriorityQueue;
import edu.neu.coe.info6205.util.Benchmark_Timer;

public class HeapBenchmark {

    private static final int M = 4095;  // 堆的最大容量
    private static final int INSERT_COUNT = 16000;  // 插入的元素数量
    private static final int REMOVE_COUNT = 4000;   // 删除的元素数量

    public static void main(String[] args) {
        Random random = new Random();

        Supplier<PriorityQueue<Integer>> binaryHeapSupplier = () -> new PriorityQueue<>(M, true, Integer::compareTo);
        Consumer<PriorityQueue<Integer>> binaryHeapTest = heap -> {
            for (int i = 0; i < INSERT_COUNT; i++) {
                heap.give(random.nextInt());  // 插入随机元素
            }
            for (int i = 0; i < REMOVE_COUNT; i++) {
                try {
                    heap.take();  // 删除元素
                } catch (PQException e) {
                    throw new RuntimeException(e);
                }
            }
            // 报告优先级最高的溢出元素
            System.out.println("Highest priority spilled element: " + heap.getHighestPrioritySpill());
        };
        runBenchmark("Basic Binary Heap", binaryHeapSupplier, binaryHeapTest);


        // 测试带 Floyd 技巧的二叉堆
        Supplier<PriorityQueue<Integer>> floydBinaryHeapSupplier = () -> new PriorityQueue<>(M, true, Integer::compareTo, true);
        runBenchmark("Floyd Binary Heap", floydBinaryHeapSupplier, binaryHeapTest);

        // 测试4-叉堆
        Supplier<PriorityQueue<Integer>> fourAryHeapSupplier = () -> new PriorityQueue.FourAryHeap<>(M, true, Integer::compareTo, false);
        runBenchmark("4-ary Heap", fourAryHeapSupplier, binaryHeapTest);

        // 测试带 Floyd 技巧的4-叉堆
        Supplier<PriorityQueue<Integer>> floydFourAryHeapSupplier = () -> new PriorityQueue.FourAryHeap<>(M, true, Integer::compareTo, true);
        runBenchmark("Floyd 4-ary Heap", floydFourAryHeapSupplier, binaryHeapTest);

        // 测试斐波那契堆
        Supplier<PriorityQueue.FibonacciHeap<Integer>> fibonacciHeapSupplier = () -> new PriorityQueue.FibonacciHeap<>(Integer::compareTo);
        Consumer<PriorityQueue.FibonacciHeap<Integer>> fibonacciHeapTest = heap -> {
            for (int i = 0; i < INSERT_COUNT; i++) {
                heap.insert(random.nextInt());  // 插入随机元素
            }

            // 确保只有在堆非空时才进行删除操作
            for (int i = 0; i < REMOVE_COUNT && !heap.isEmpty(); i++) {
                try {
                    heap.extractMin();  // 删除最小元素
                } catch (IllegalStateException e) {
                    System.out.println("Heap is empty during extractMin operation.");
                }
            }
        };

    }

    // 运行基准测试的方法
    private static <T> void runBenchmark(String description, Supplier<T> supplier, Consumer<T> test) {
        Benchmark_Timer<T> benchmark = new Benchmark_Timer<>(description, test);
        double time = benchmark.runFromSupplier(supplier, 10);  // 测试10次，返回平均时间
        System.out.println(description + " 平均时间: " + time + " ms");
    }
}