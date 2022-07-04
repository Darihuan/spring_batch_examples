package com.example.chunk_example_batch.content.order.application.itemProcessors;

import com.example.chunk_example_batch.content.TrackedOrder.domain.TrackedOrder;
import com.example.chunk_example_batch.content.order.domain.Order;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;

public class FreeShippingItemProcessor implements ItemProcessor<TrackedOrder, TrackedOrder> {
    @Override
    public TrackedOrder process(TrackedOrder order) throws Exception {

        order.setFreeShipping(order.getCost().compareTo(BigDecimal.valueOf(80))>=0);
        return order.getFreeShipping()?order:null;
    }
}
