package com.timevo_ecommerce_backend.services.order;

import com.timevo_ecommerce_backend.dtos.OrderDTO;
import com.timevo_ecommerce_backend.entities.Order;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {

    OrderResponse insertOrder (OrderDTO orderDTO) throws DataNotFoundException;

    OrderResponse getOrder (Long id) throws DataNotFoundException;

    Page<OrderResponse> getOrders(String keyword, Pageable pageable);

    OrderResponse updateOrder (Long id, OrderDTO orderDTO) throws DataNotFoundException;

    Page<Order> findByUserIdAndKeyword (Long id, Pageable pageable, String keyword);

    void deleteOrder (Long id) throws DataNotFoundException;

    long totalOrdersByUserId (Long userId);
}
