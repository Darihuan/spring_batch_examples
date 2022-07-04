package com.example.chunk_example_batch.content.TrackedOrder.domain;

import com.example.chunk_example_batch.content.order.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackedOrder extends Order {
  private String trackingNumber;
  private Boolean freeShipping;

  public TrackedOrder(Order order) {
    this.setOrderId(order.getOrderId());
    this.setFirstName(order.getFirstName());
    this.setLastName(order.getLastName());
    this.setEmail(order.getEmail());
    this.setCost(order.getCost());
    this.setItemId(order.getItemId());
    this.setItemName(order.getItemName());
    this.setShipDate(order.getShipDate());

  }
}
