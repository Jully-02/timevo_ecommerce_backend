package com.timevo_ecommerce_backend.services.payment_method;

import com.timevo_ecommerce_backend.dtos.PaymentMethodDTO;
import com.timevo_ecommerce_backend.entities.PaymentMethod;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;

import java.util.List;

public interface IPaymentMethodService {

    PaymentMethod insertPaymentMethod (PaymentMethodDTO paymentMethodDTO) throws ExistDataException;
    PaymentMethod updatePaymentMethod (Long id, PaymentMethodDTO paymentMethodDTO) throws DataNotFoundException;

    PaymentMethod getPaymentMethod (Long id) throws DataNotFoundException;

    List<PaymentMethod> getAllPaymentMethods ();

    void deletePaymentMethod (Long id);
}
