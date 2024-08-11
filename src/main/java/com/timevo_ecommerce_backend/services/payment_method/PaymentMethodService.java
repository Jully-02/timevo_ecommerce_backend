package com.timevo_ecommerce_backend.services.payment_method;

import com.timevo_ecommerce_backend.dtos.PaymentMethodDTO;
import com.timevo_ecommerce_backend.entities.PaymentMethod;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.repositories.PaymentMethodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService implements IPaymentMethodService{

    private final PaymentMethodRepository paymentMethodRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public PaymentMethod insertPaymentMethod(PaymentMethodDTO paymentMethodDTO) throws ExistDataException {
        if (paymentMethodRepository.existsByName(paymentMethodDTO.getName())) {
            throw new ExistDataException("Payment method is duplicated");
        }
        PaymentMethod newPaymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);
        return paymentMethodRepository.save(newPaymentMethod);
    }

    @Override
    @Transactional
    public PaymentMethod updatePaymentMethod(Long id, PaymentMethodDTO paymentMethodDTO) throws DataNotFoundException {
        PaymentMethod existingPaymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Payment method with ID = " + id));
        existingPaymentMethod.setName(paymentMethodDTO.getName());
        existingPaymentMethod.setCost(paymentMethodDTO.getCost());
        existingPaymentMethod.setDescription(paymentMethodDTO.getDescription());
        return paymentMethodRepository.save(existingPaymentMethod);
    }

    @Override
    public PaymentMethod getPaymentMethod(Long id) throws DataNotFoundException {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Payment method with ID = " + id));
    }

    @Override
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Override
    @Transactional
    public void deletePaymentMethod(Long id) {
        paymentMethodRepository.deleteById(id);
    }
}
