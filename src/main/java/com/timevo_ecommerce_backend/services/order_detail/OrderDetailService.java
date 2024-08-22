package com.timevo_ecommerce_backend.services.order_detail;

import com.timevo_ecommerce_backend.dtos.OrderDetailDTO;
import com.timevo_ecommerce_backend.entities.*;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.repositories.*;
import com.timevo_ecommerce_backend.responses.OrderDetailResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final MaterialRepository materialRepository;
    private final ScreenSizeRepository screenSizeRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public OrderDetailResponse insertOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Order with ID = " + orderDetailDTO.getOrderId()));

        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + orderDetailDTO.getProductId()));

        Color existingColor = colorRepository.findById(orderDetailDTO.getColorId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + orderDetailDTO.getColorId()));

        Material existingMaterial = materialRepository.findById(orderDetailDTO.getMaterialId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + orderDetailDTO.getMaterialId()));

        ScreenSize existingScreenSize = screenSizeRepository.findById(orderDetailDTO.getScreenSizeId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + orderDetailDTO.getScreenSizeId()));
        if (!productVariantRepository.existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(
                existingProduct.getId(),
                existingColor.getId(),
                existingMaterial.getId(),
                existingScreenSize.getId()
        )) {
            throw new DataNotFoundException("No products found with these attributes");
        }

        OrderDetail orderDetail = modelMapper.map(orderDetailDTO, OrderDetail.class);
        orderDetail.setOrder(existingOrder);
        orderDetail.setProduct(existingProduct);
        orderDetail.setColor(existingColor);
        orderDetail.setScreenSize(existingScreenSize);
        orderDetail.setMaterial(existingMaterial);
        orderDetailRepository.save(orderDetail);

        OrderDetailResponse orderDetailResponse = modelMapper.map(orderDetail, OrderDetailResponse.class);
        orderDetailResponse.setOrderId(orderDetail.getOrder().getId());
        orderDetailResponse.setProductId(orderDetail.getProduct().getId());
        orderDetailResponse.setColorId(orderDetail.getColor().getId());
        orderDetailResponse.setMaterialId(orderDetail.getMaterial().getId());
        orderDetailResponse.setScreenSizeId(orderDetail.getScreenSize().getId());
        return orderDetailResponse;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long id) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Order detail with ID = " + id));
        OrderDetailResponse orderDetailResponse = modelMapper.map(orderDetail, OrderDetailResponse.class);
        orderDetailResponse.setOrderId(orderDetail.getOrder().getId());
        orderDetailResponse.setProductId(orderDetail.getProduct().getId());
        orderDetailResponse.setColorId(orderDetail.getColor().getId());
        orderDetailResponse.setMaterialId(orderDetail.getMaterial().getId());
        orderDetailResponse.setScreenSizeId(orderDetail.getScreenSize().getId());
        return orderDetailResponse;
    }

    @Override
    @Transactional
    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Order detail with ID = " + id));
        if (existingOrderDetail.getProduct().getId() != orderDetailDTO.getProductId()) {
            Product product = productRepository.findById(orderDetailDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + orderDetailDTO.getProductId()));
            existingOrderDetail.setProduct(product);
        }

        if (existingOrderDetail.getColor().getId() != orderDetailDTO.getColorId()) {
            Color color = colorRepository.findById(orderDetailDTO.getColorId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + orderDetailDTO.getColorId()));
            existingOrderDetail.setColor(color);
        }

        if (existingOrderDetail.getMaterial().getId() != orderDetailDTO.getMaterialId()) {
            Material material = materialRepository.findById(orderDetailDTO.getMaterialId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + orderDetailDTO.getMaterialId()));
            existingOrderDetail.setMaterial(material);
        }

        if (existingOrderDetail.getScreenSize().getId() != orderDetailDTO.getScreenSizeId()) {
            ScreenSize screenSize = screenSizeRepository.findById(orderDetailDTO.getScreenSizeId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + orderDetailDTO.getScreenSizeId()));
            existingOrderDetail.setScreenSize(screenSize);
        }

        if (!productVariantRepository.existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(
                existingOrderDetail.getProduct().getId(),
                existingOrderDetail.getColor().getId(),
                existingOrderDetail.getMaterial().getId(),
                existingOrderDetail.getScreenSize().getId()
        )) {
            throw new DataNotFoundException("No products found with these attributes");
        }
//        modelMapper.map(orderDetailDTO, existingOrderDetail);
        orderDetailRepository.save(existingOrderDetail);
        existingOrderDetail.setQuantity(orderDetailDTO.getQuantity());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        OrderDetailResponse orderDetailResponse = modelMapper.map(existingOrderDetail, OrderDetailResponse.class);
        orderDetailResponse.setProductId(existingOrderDetail.getProduct().getId());
        orderDetailResponse.setOrderId(existingOrderDetail.getOrder().getId());
        orderDetailResponse.setColorId(existingOrderDetail.getColor().getId());
        orderDetailResponse.setMaterialId(existingOrderDetail.getMaterial().getId());
        orderDetailResponse.setScreenSizeId(existingOrderDetail.getScreenSize().getId());
        return orderDetailResponse;
    }

    @Override
    public List<OrderDetailResponse> findByOrderId(Long orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        return orderDetails
                .stream()
                .map(orderDetail -> {
                    OrderDetailResponse orderDetailResponse = modelMapper.map(orderDetail, OrderDetailResponse.class);
                    orderDetailResponse.setOrderId(orderDetail.getOrder().getId());
                    orderDetailResponse.setProductId(orderDetail.getProduct().getId());
                    orderDetailResponse.setColorId(orderDetail.getColor().getId());
                    orderDetailResponse.setMaterialId(orderDetail.getMaterial().getId());
                    orderDetailResponse.setScreenSizeId(orderDetail.getScreenSize().getId());
                    return orderDetailResponse;
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}
