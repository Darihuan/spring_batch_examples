package com.example.chunk_example_batch.content.order.application.mappers;

import com.example.chunk_example_batch.content.order.domain.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<Order> {
  @Override
  public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
    Order order = new Order();
    order.setOrderId(rs.getLong("order_id"));
    order.setCost(rs.getBigDecimal("cost"));
    order.setEmail(rs.getString("email"));
    order.setFirstName(rs.getString("first_name"));
    order.setLastName(rs.getString("last_name"));
    order.setItemId(rs.getString("item_id"));
    order.setItemName(rs.getString("item_name"));
    order.setShipDate(rs.getDate("ship_date"));
    return order;
  }
}
