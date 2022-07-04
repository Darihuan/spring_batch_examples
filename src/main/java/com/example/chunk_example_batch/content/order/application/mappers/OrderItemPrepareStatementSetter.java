package com.example.chunk_example_batch.content.order.application.mappers;

import com.example.chunk_example_batch.content.order.domain.Order;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItemPrepareStatementSetter implements ItemPreparedStatementSetter<Order> {
    @Override
    public void setValues(Order order, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(1,order.getOrderId());
        preparedStatement.setString(2,order.getFirstName());
        preparedStatement.setString(3,order.getLastName() );
        preparedStatement.setString(4, order.getEmail());
        preparedStatement.setBigDecimal(5,order.getCost());
        preparedStatement.setString(6,order.getItemId());
        preparedStatement.setString(7,order.getItemName());
        preparedStatement.setDate(8, new Date(order.getShipDate().getTime()));
    }
}
