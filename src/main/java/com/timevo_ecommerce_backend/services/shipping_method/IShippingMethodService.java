package com.timevo_ecommerce_backend.services.shipping_method;

import com.timevo_ecommerce_backend.dtos.ShippingMethodDTO;
import com.timevo_ecommerce_backend.entities.ShippingMethod;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;

import java.util.List;

public interface IShippingMethodService {
    ShippingMethod insertShippingMethod (ShippingMethodDTO shippingMethodDTO) throws ExistDataException;

    ShippingMethod updateShippingMethod (Long id, ShippingMethodDTO shippingMethodDTO) throws DataNotFoundException;

    ShippingMethod getShippingMethod (Long id) throws DataNotFoundException;

    List<ShippingMethod> getAllShippingMethod ();

    void deleteShippingMethod (Long id);
}
