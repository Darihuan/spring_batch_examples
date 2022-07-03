package com.example.chunk_example_batch.content.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
  private Long orderId;
  private String firstName;
  private String lastName;
  private String email;
  private BigDecimal cost;
  private String itemId;
  private String itemName;
  private Date shipDate;
}
