package com.example.chunk_example_batch.content.order.application.slipListener;

import com.example.chunk_example_batch.content.TrackedOrder.domain.TrackedOrder;
import com.example.chunk_example_batch.content.order.domain.Order;
import org.springframework.batch.core.SkipListener;

public class CustomSkipListener implements SkipListener<Order, TrackedOrder> {

    @Override
    public void onSkipInRead(Throwable throwable) {

    }

    @Override
    public void onSkipInWrite(TrackedOrder trackedOrder, Throwable throwable) {

    }

    @Override
    public void onSkipInProcess(Order order, Throwable throwable) {
    System.out.println("SKIPING PROCESSEING ITEM ID:"+order.getItemId());
    }
}
