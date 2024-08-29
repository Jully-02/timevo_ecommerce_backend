package com.timevo_ecommerce_backend.services.order_detail;

import com.timevo_ecommerce_backend.dtos.OrderDetailDTO;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.order_detail.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {
    OrderDetailResponse insertOrderDetail (OrderDetailDTO orderDetailDTO) throws DataNotFoundException;

    OrderDetailResponse getOrderDetail (Long id) throws DataNotFoundException;

    OrderDetailResponse updateOrderDetail (Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException;

    List<OrderDetailResponse> findByOrderId (Long orderId);

    void deleteOrderDetail (Long id);
}
