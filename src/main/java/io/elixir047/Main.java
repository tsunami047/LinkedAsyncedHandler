package io.elixir047;

import io.elixir047.utils.LinkedTask;
import io.elixir047.utils.AsyncTaskProcessor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

/**
 * @Author: ${USER}
 * @CreateTime: ${YEAR}-${MONTH}-${DAY}  ${HOUR}:${MINUTE}
 * @Description: ?
 */
public final class Main {
    public static void main(String[] args) throws InterruptedException {
        AsyncTaskProcessor asyncTaskProcessor = new AsyncTaskProcessor();
        List<Integer> list1 = new CopyOnWriteArrayList<>();
        List<Integer> list2 = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            new Thread(()->{
                LinkedTask<Integer> integerLinkedTask = new LinkedTask<>(() -> {
                    for (int j = 0,k=0; j < Math.random()*1000000; j++) k++;
                    return finalI;
                },
                        list1::add
                );
                list2.add(finalI);
                asyncTaskProcessor.processTask(integerLinkedTask);
            }).start();
            Thread.sleep(0,10);
        }
        Thread.sleep(5000);
        System.out.println("input list"+list2);
        System.out.println("output list"+list1);
        System.out.println(list1.containsAll(list2));
        Thread.sleep(10000);
        asyncTaskProcessor.shutdown();

    }
}