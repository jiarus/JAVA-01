package io.github.jiarus.conc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Homework03 {
    
    public final static int IO_THREAD_SIZE = 8;
    
    public final static int QUEUE_CAPACITY = 35800;
    
    private static ExecutorService threadPool;
    
    static {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("invoke-third-party-thread-%d").build();
        //超出队列容量，拒绝添加任务
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        threadPool = new ThreadPoolExecutor(IO_THREAD_SIZE, IO_THREAD_SIZE,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY), threadFactory, handler);
    }
    
    public static void main(String[] args) throws Exception {
        
        long start = System.currentTimeMillis();
        
        Supplier<Integer> method = Homework03::sum;
        //01 使用ExecutorService submit
        Future<Integer> result01 = sync01(method);
        //02 使用CompletableFuture supplyAsync
        CompletableFuture<Integer> result02 = sync02(method);
        //03 使用FutureTask包装任务
        Future<Integer> result03 = sync03(method);
        //04 CountDownLatch
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Future<Integer> result04 = sync04(countDownLatch, method);
        //05 CyclicBarrier
        Future<Integer> result05 = sync05(method);
        //06 Semaphore
        Semaphore semaphore = new Semaphore(0);
        Future<Integer> result06 = sync06(semaphore, method);
        //07 Lock
        Lock lock = new ReentrantLock();
        Condition finCond = lock.newCondition();
        
        Future<Integer> result07 = sync07(lock, finCond, method);
        
        
        // 确保  拿到result 并输出
        System.out.println("sync01异步计算结果为：" + result01.get());
        System.out.println("sync02异步计算结果为：" + result02.get());
        System.out.println("sync03异步计算结果为：" + result03.get());
        System.out.println("sync04异步计算结果为：" + result04.get());
        countDownLatch.await();
        System.out.println("sync05异步计算结果为：" + result05.get());
        semaphore.acquire();
        System.out.println("sync06异步计算结果为：" + result06.get());
        lock.lock();
        finCond.await();
        lock.unlock();
        System.out.println("sync07异步计算结果为：" + result07.get());
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        threadPool.shutdown();
        // 然后退出main线程
    }
    
    private static int sum() {
        return fibo(36);
    }
    
    /**
     * a = 1 return 1
     * a = 2 ... 1+1 = 2
     * a = 3 ... 2+1 = 3
     * a = 4 ... 3+2 = 5
     * a = 5 ... 5+3 = 8
     * n -> f(n-1)+f(n-2)
     * 斐波那契数列
     *
     * @param a
     * @return
     */
    private static int fibo(int a) {
        if (a < 2)
            return 1;
        return fibo(a - 1) + fibo(a - 2);
    }
    
    /**
     * executor.submit
     *
     * @param method
     * @return
     */
    private static Future<Integer> sync01(Supplier<Integer> method) {
        return threadPool.submit(method::get);
    }
    
    /**
     * CompletableFuture
     *
     * @param method
     * @return
     */
    private static CompletableFuture<Integer> sync02(Supplier<Integer> method) {
        List<CompletableFuture<Integer>> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(method, threadPool);
            list.add(future);
        }
        return list.get(0);
    }
    
    /**
     * FutureTask
     *
     * @param method
     * @return
     */
    private static Future<Integer> sync03(Supplier<Integer> method) {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() {
                return method.get();
            }
        });
        threadPool.execute(futureTask);
        return futureTask;
    }
    
    /**
     * CountDownLatch
     *
     * @param method
     * @return
     */
    private static Future<Integer> sync04(CountDownLatch countDownLatch, Supplier<Integer> method) throws InterruptedException, ExecutionException {
        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws InterruptedException {
                int result = method.get();
//                Thread.sleep(1000);
                countDownLatch.countDown();
                return result;
            }
        });
//        System.out.println("不await直接获取结果:" + future.get());
        countDownLatch.await();
        return future;
    }
    
    /**
     * CyclicBarrier
     *
     * @param method
     * @return
     */
    private static Future<Integer> sync05(Supplier<Integer> method) throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws BrokenBarrierException, InterruptedException {
                int result = method.get();
                //子线程阻塞
                cyclicBarrier.await();
                return result;
            }
        });
        return future;
    }
    
    /**
     * Semaphore
     *
     * @param method
     * @return
     */
    private static Future<Integer> sync06(Semaphore semaphore, Supplier<Integer> method) throws InterruptedException {
        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                int result = method.get();
                //子线程阻塞
                semaphore.release();
                return result;
            }
        });
        return future;
    }
    
    /**
     * Condition
     *
     * @param method
     * @return
     */
    private static Future<Integer> sync07(Lock lock, Condition condition, Supplier<Integer> method) throws InterruptedException {
        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws InterruptedException {
                int result = method.get();
                try {
                    lock.lock();
                    condition.signalAll();
                } finally {
                    lock.unlock();
                }
                return result;
            }
        });
        return future;
    }
}