package com.timevo_ecommerce_backend.services.screen_size;

import com.timevo_ecommerce_backend.dtos.ScreenSizeDTO;
import com.timevo_ecommerce_backend.entities.ScreenSize;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.repositories.ScreenSizeRepository;
import com.timevo_ecommerce_backend.responses.screen_size.ScreenSizeResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScreenSizeService implements IScreenSizeService{

    private final ScreenSizeRepository screenSizeRepository;
    private final ModelMapper modelMapper;

    @Override
    public ScreenSize insertScreenSize(ScreenSizeDTO screenSizeDTO) throws ExistDataException {
        if (screenSizeRepository.existsBySize(screenSizeDTO.getSize())) {
            throw new ExistDataException("Size is duplicated");
        }
        ScreenSize newScreenSize = modelMapper.map(screenSizeDTO, ScreenSize.class);
        return screenSizeRepository.save(newScreenSize);
    }

    @Override
    public ScreenSize getScreenSizeById(Long screenSizeId) throws DataNotFoundException {
        return screenSizeRepository.findById(screenSizeId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + screenSizeId));
    }

    @Override
    public ScreenSize updateScreenSize(Long screenSizeId, ScreenSizeDTO screenSizeDTO) throws DataNotFoundException {
        ScreenSize existingScreenSize = screenSizeRepository.findById(screenSizeId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + screenSizeId));
        existingScreenSize.setSize(screenSizeDTO.getSize());
        return screenSizeRepository.save(existingScreenSize);
    }

    @Override
    public void deleteScreenSize(Long screenSizeId) {
        screenSizeRepository.deleteById(screenSizeId);
    }

    @Override
    public Page<ScreenSizeResponse> getAllScreenSizes(PageRequest pageRequest) {
        return screenSizeRepository.getAllScreenSizes(pageRequest);
    }
}
