package com.timevo_ecommerce_backend.services.shipping_method;

import com.timevo_ecommerce_backend.dtos.ShippingMethodDTO;
import com.timevo_ecommerce_backend.entities.ShippingMethod;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.repositories.ShippingMethodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingMethodService implements IShippingMethodService {
    private final ShippingMethodRepository shippingMethodRepository;
    private final ModelMapper modelMapper;
    @Override
    @Transactional
    public ShippingMethod insertShippingMethod(ShippingMethodDTO shippingMethodDTO) throws ExistDataException {
        if (shippingMethodRepository.existsByName(shippingMethodDTO.getName())) {
            throw new ExistDataException("Shipping method is duplicated");
        }
        ShippingMethod newShippingMethod = modelMapper.map(shippingMethodDTO, ShippingMethod.class);

        return shippingMethodRepository.save(newShippingMethod);
    }

    @Override
    @Transactional
    public ShippingMethod updateShippingMethod(Long id, ShippingMethodDTO shippingMethodDTO) throws DataNotFoundException {
        ShippingMethod existingShippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Shipping method with ID = " + id));
        existingShippingMethod.setName(shippingMethodDTO.getName());
        existingShippingMethod.setCost(shippingMethodDTO.getCost());
        existingShippingMethod.setDescription(shippingMethodDTO.getDescription());
        return shippingMethodRepository.save(existingShippingMethod);
    }

    @Override
    public ShippingMethod getShippingMethod(Long id) throws DataNotFoundException {
        return shippingMethodRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Shipping method with ID = " + id));
    }

    @Override
    public List<ShippingMethod> getAllShippingMethod() {
        return shippingMethodRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteShippingMethod(Long id) {
        shippingMethodRepository.deleteById(id);
    }
}
