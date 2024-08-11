package com.timevo_ecommerce_backend.services.screen_size;

import com.timevo_ecommerce_backend.dtos.ScreenSizeDTO;
import com.timevo_ecommerce_backend.entities.ScreenSize;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.ScreenSizeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IScreenSizeService {
    ScreenSize insertScreenSize (ScreenSizeDTO screenSizeDTO) throws ExistDataException;

    ScreenSize getScreenSizeById (Long screenSizeId) throws DataNotFoundException;

    ScreenSize updateScreenSize (Long screenSizeId, ScreenSizeDTO screenSizeDTO) throws DataNotFoundException;

    void deleteScreenSize (Long screenSizeId);

    Page<ScreenSizeResponse> getAllScreenSizes (PageRequest pageRequest);
}
