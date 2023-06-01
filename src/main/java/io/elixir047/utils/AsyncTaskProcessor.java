package io.elixir047.utils;
/**
 *@Author: natsumi
 *@CreateTime: 2023-05-26  12:46
 *@Description: ?
 */

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

public final class AsyncTaskProcessor {
    private final ExecutorService executor;
    private volatile ConcurrentSkipListMap<Long,RunnableFutureX<?>> taskMap;
    private volatile ConcurrentHashMap<Long,Optional<RunnableFutureX<?>>> sclMap;

    public AsyncTaskProcessor() {
        int parallelism = Runtime.getRuntime().availableProcessors();
        executor = Executors.newWorkStealingPool(parallelism);
        taskMap = new ConcurrentSkipListMap<>();
        sclMap = new ConcurrentHashMap<>();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()->{
            try {
                startTaskProcessing();
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }, 0,1, TimeUnit.MILLISECONDS);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
            this::handleResult, 0,1, TimeUnit.MILLISECONDS);

    }

    private void startTaskProcessing() throws ExecutionException, InterruptedException, TimeoutException {
        Map.Entry<Long, RunnableFutureX<?>> longRunnableFutureXEntry = taskMap.firstEntry();
        if(longRunnableFutureXEntry==null){
            return;
        }
        Future<RunnableFutureX<?>> submit = executor.submit(longRunnableFutureXEntry.getValue());
        RunnableFutureX<?> runnableFutureX = submit.get(1000, TimeUnit.MILLISECONDS);
        sclMap.put(longRunnableFutureXEntry.getKey(), Optional.ofNullable(runnableFutureX));
    }

    public synchronized void processTask(RunnableFutureX<?> task) {
        long l = System.nanoTime();
        taskMap.put(l, task);
        sclMap.put(l, Optional.empty());
    }

    public void handleResult() {
        if (sclMap.isEmpty()) {
            return;
        }
        Map.Entry<Long, RunnableFutureX<?>> longRunnableFutureXEntry_ = taskMap.firstEntry();
        if(longRunnableFutureXEntry_==null){
            return;
        }
        Optional<RunnableFutureX<?>> longRunnableFutureXEntry = sclMap.get(longRunnableFutureXEntry_.getKey());
        if (longRunnableFutureXEntry == null || !longRunnableFutureXEntry.isPresent()) {
            return;
        }
        sclMap.remove(longRunnableFutureXEntry_.getKey());
        taskMap.pollFirstEntry();
        executor.submit(() -> longRunnableFutureXEntry.get().completedProcess());
    }


    public void shutdown(){
        if(!executor.isShutdown()){
            executor.shutdown();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskProcessor{");
        sb.append("executor=").append(executor);
        sb.append(", taskMap=").append(taskMap);
        sb.append(", sclMap=").append(sclMap);
        sb.append('}');
        return sb.toString();
    }
}