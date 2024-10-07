package com.timevo_ecommerce_backend.services.order;

import com.timevo_ecommerce_backend.dtos.CartItemDTO;
import com.timevo_ecommerce_backend.dtos.OrderDTO;
import com.timevo_ecommerce_backend.entities.*;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.repositories.*;
import com.timevo_ecommerce_backend.responses.order.OrderResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ColorRepository colorRepository;
    private final MaterialRepository materialRepository;
    private final ScreenSizeRepository screenSizeRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public OrderResponse insertOrder(OrderDTO orderDTO) throws DataNotFoundException {
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + orderDTO.getUserId()));

        ShippingMethod existingShippingMethod = shippingMethodRepository.findById(orderDTO.getShippingMethodId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Shipping method with ID = " + orderDTO.getPaymentMethodId()));

        PaymentMethod existingPaymentMethod = paymentMethodRepository.findById(orderDTO.getPaymentMethodId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Payment method with ID = " + orderDTO.getPaymentMethodId()));

        Order order = modelMapper.map(orderDTO, Order.class);

        order.setUser(existingUser);
        order.setShippingMethod(existingShippingMethod);
        order.setPaymentMethod(existingPaymentMethod);
        order.setPaymentCost(existingPaymentMethod.getCost());
        order.setShippingCost(existingShippingMethod.getCost());

        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(false);
        order.setOrderDate(new Date());
        order.setTrackingNumber("");

        order.setActive(true);
        orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        int subTotal = 0;
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Product existingProduct = productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + cartItemDTO.getProductId()));

            Color existingColor = colorRepository.findById(cartItemDTO.getColorId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + cartItemDTO.getColorId()));

            Material existingMaterial = materialRepository.findById(cartItemDTO.getMaterialId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + cartItemDTO.getMaterialId()));

            ScreenSize existingScreenSize = screenSizeRepository.findById(cartItemDTO.getScreenSizeId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + cartItemDTO.getScreenSizeId()));

            if (!productVariantRepository.existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(
                    existingProduct.getId(),
                    existingColor.getId(),
                    existingMaterial.getId(),
                    existingScreenSize.getId())
            ) {
                throw new DataNotFoundException("No products found with these attributes");
            }
            orderDetail.setQuantity(cartItemDTO.getQuantity());
            orderDetail.setProduct(existingProduct);
            orderDetail.setColor(existingColor);
            orderDetail.setMaterial(existingMaterial);
            orderDetail.setScreenSize(existingScreenSize);
            orderDetail.setPrice(existingProduct.getPrice());
            orderDetail.setTotalMoney(orderDetail.getQuantity() * orderDetail.getPrice());
            subTotal += orderDetail.getTotalMoney();
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);

        order.setSubTotal(subTotal);
        order.setTotalMoney(subTotal + existingShippingMethod.getCost() + existingPaymentMethod.getCost());

        orderRepository.save(order);
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        orderResponse.setUserId(order.getUser().getId());
        orderResponse.setShippingMethodId(order.getShippingMethod().getId());
        orderResponse.setPaymentMethodId(order.getPaymentMethod().getId());
        orderResponse.setShippingMethodName(order.getShippingMethod().getName());
        orderResponse.setPaymentMethodName(order.getPaymentMethod().getName());
        order.setPaymentStatus(orderDTO.isPaymentStatus());
        return orderResponse;
    }

    @Override
    public OrderResponse getOrder(Long id) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Order with ID = " + id));
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        orderResponse.setShippingMethodId(order.getShippingMethod().getId());
        orderResponse.setPaymentMethodId(order.getPaymentMethod().getId());
        orderResponse.setUserId(order.getUser().getId());
        orderResponse.setShippingMethodName(order.getShippingMethod().getName());
        orderResponse.setPaymentMethodName(order.getPaymentMethod().getName());
        return orderResponse;
    }

    @Override
    public Page<OrderResponse> getOrders(String keyword, Pageable pageable) {
        Page<Order> ordersPage;
        ordersPage = orderRepository.findAll(keyword, pageable);
        return ordersPage
                .map(order -> {
                    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
                    orderResponse.setShippingMethodId(order.getShippingMethod().getId());
                    orderResponse.setPaymentMethodId(order.getPaymentMethod().getId());
                    orderResponse.setUserId(order.getUser().getId());
                    orderResponse.setShippingMethodName(order.getShippingMethod().getName());
                    orderResponse.setPaymentMethodName(order.getPaymentMethod().getName());
                    return orderResponse;
                });
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Order with ID = " + id));
//        modelMapper.map(orderDTO, existingOrder);
        existingOrder.setFirstName(orderDTO.getFirstName());
        existingOrder.setLastName(orderDTO.getLastName());
        existingOrder.setEmail(orderDTO.getEmail());
        existingOrder.setPhoneNumber(orderDTO.getPhoneNumber());
        existingOrder.setAddress(orderDTO.getAddress());
        existingOrder.setNote(orderDTO.getNote());
        existingOrder.setTotalMoney(orderDTO.getTotalMoney());
        existingOrder.setShippingAddress(orderDTO.getShippingAddress());
        existingOrder.setPaymentStatus(orderDTO.isPaymentStatus());
        existingOrder.setSubTotal(orderDTO.getSubTotal());
        if (existingOrder.getUser().getId() != orderDTO.getUserId()) {
            User exisitngUser = userRepository.findById(orderDTO.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + orderDTO.getUserId()));
            existingOrder.setUser(exisitngUser);
        }

        if (existingOrder.getShippingMethod().getId() != orderDTO.getShippingMethodId()) {
            ShippingMethod existingShippingMethod = shippingMethodRepository.findById(orderDTO.getShippingMethodId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Shipping method with ID = " + orderDTO.getShippingMethodId()));
            existingOrder.setShippingMethod(existingShippingMethod);
            existingOrder.setShippingCost(existingShippingMethod.getCost());
        }

        if (existingOrder.getPaymentMethod().getId() != orderDTO.getPaymentMethodId()) {
            PaymentMethod existingPaymentMethod = paymentMethodRepository.findById(orderDTO.getPaymentMethodId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Payment method with ID = " + orderDTO.getPaymentMethodId()));
            existingOrder.setPaymentMethod(existingPaymentMethod);
            existingOrder.setPaymentCost(existingPaymentMethod.getCost());
        }

        orderRepository.save(existingOrder);
        OrderResponse orderResponse = modelMapper.map(existingOrder, OrderResponse.class);
        orderResponse.setShippingMethodId(existingOrder.getShippingMethod().getId());
        orderResponse.setPaymentMethodId(existingOrder.getPaymentMethod().getId());
        orderResponse.setUserId(existingOrder.getUser().getId());
        orderResponse.setShippingMethodName(existingOrder.getShippingMethod().getName());
        orderResponse.setPaymentMethodName(existingOrder.getPaymentMethod().getName());

        return orderResponse;
    }

    @Override
    public Page<Order> findByUserIdAndKeyword(Long id, Pageable pageable, String keyword) {
        return orderRepository.findByUserIdAndKeyword(id, keyword, pageable);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) throws DataNotFoundException {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Order with ID = " + id));
        existingOrder.setActive(false);
        orderRepository.save(existingOrder);
    }

    @Override
    public long totalOrdersByUserId (Long userId) {
        return orderRepository.countByUserId(userId);
    }
}
