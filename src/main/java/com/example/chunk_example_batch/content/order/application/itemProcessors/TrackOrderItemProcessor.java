package com.example.chunk_example_batch.content.order.application.itemProcessors;

import com.example.chunk_example_batch.configuration.ErrorProcessingExpection;
import com.example.chunk_example_batch.content.TrackedOrder.domain.TrackedOrder;
import com.example.chunk_example_batch.content.order.domain.Order;
import org.springframework.batch.item.ItemProcessor;

import java.util.UUID;

public class TrackOrderItemProcessor implements ItemProcessor<Order, TrackedOrder> {
    @Override
    public TrackedOrder process(Order order) throws Exception {
        TrackedOrder trackedOrder = new TrackedOrder(order);
        if (Math.random() < 0.01)
            throw new ErrorProcessingExpection();
        trackedOrder.setTrackingNumber(UUID.randomUUID().toString());
        return trackedOrder;
    }
}
