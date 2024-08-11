package com.timevo_ecommerce_backend.services.color;

import com.timevo_ecommerce_backend.dtos.ColorDTO;
import com.timevo_ecommerce_backend.entities.Color;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.repositories.ColorRepository;
import com.timevo_ecommerce_backend.responses.ColorResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColorService implements IColorService{

    private final ColorRepository colorRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Color insertColor(ColorDTO colorDTO) throws ExistDataException {
        if (colorRepository.existsByName(colorDTO.getName())) {
            throw new ExistDataException("Color name is duplicated");
        }
        Color newColor = modelMapper.map(colorDTO, Color.class);
        return colorRepository.save(newColor);
    }

    @Override
    public Color getColorById(Long colorId) throws DataNotFoundException {
        return colorRepository.findById(colorId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + colorId));
    }

    @Override
    @Transactional
    public Color updateColor(Long colorId, ColorDTO colorDTO) throws DataNotFoundException {
        Color existingColor = colorRepository.findById(colorId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + colorId));
        existingColor.setName(colorDTO.getName());
        existingColor.setRed(colorDTO.getRed());
        existingColor.setGreen(colorDTO.getGreen());
        existingColor.setBlue(colorDTO.getBlue());
        existingColor.setAlpha(colorDTO.getAlpha());
        return colorRepository.save(existingColor);
    }

    @Override
    @Transactional
    public void deleteColor(Long colorId) {
        colorRepository.deleteById(colorId);
    }

    @Override
    public Page<ColorResponse> getAllColor(PageRequest pageRequest) {
        return colorRepository.getAllColors(pageRequest);
    }
}
