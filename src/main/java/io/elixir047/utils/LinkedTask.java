package io.elixir047.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *@Author: natsumi
 *@CreateTime: 2023-05-26  12:56
 *@Description: ?
 */
public class LinkedTask<V> implements RunnableFutureX<V>{

    Supplier<V> supplier;
    V v;
    Consumer<V> consumer;

    public LinkedTask(Supplier<V> supplier, Consumer<V> consumer) {
        this.supplier = supplier;
        this.consumer = consumer;
    }

    public RunnableFutureX<V> beforeProcess(){
        this.v = supplier.get();
        return this;
    }

    public final void completedProcess(){
        consumer.accept(this.v);
    }

    @Override
    public RunnableFutureX<?> call() throws Exception {
        return beforeProcess();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LinkedTask{");
        sb.append("supplier=").append(supplier);
        sb.append(", v=").append(v);
        sb.append(", consumer=").append(consumer);
        sb.append('}');
        return sb.toString();
    }
}
