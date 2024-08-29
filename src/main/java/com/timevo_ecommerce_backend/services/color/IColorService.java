package com.timevo_ecommerce_backend.services.color;

import com.timevo_ecommerce_backend.dtos.ColorDTO;
import com.timevo_ecommerce_backend.entities.Color;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.color.ColorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IColorService {
    Color insertColor (ColorDTO colorDTO) throws ExistDataException;

    Color getColorById (Long colorId) throws DataNotFoundException;

    Color updateColor (Long colorId, ColorDTO colorDTO) throws DataNotFoundException;

    void deleteColor (Long colorId);

    Page<ColorResponse> getAllColor (PageRequest pageRequest);
}
