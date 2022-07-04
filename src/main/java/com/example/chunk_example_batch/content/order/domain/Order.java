package com.example.chunk_example_batch.content.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
  private Long orderId;
  private String firstName;
  private String lastName;
  @Pattern(regexp = ".*\\.com")
  private String email;
  private BigDecimal cost;
  @NotBlank
  private String itemId;
  private String itemName;
  private Date shipDate;
}
