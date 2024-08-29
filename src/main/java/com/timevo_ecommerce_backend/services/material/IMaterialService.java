package com.timevo_ecommerce_backend.services.material;

import com.timevo_ecommerce_backend.dtos.MaterialDTO;
import com.timevo_ecommerce_backend.entities.Material;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.material.MaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IMaterialService {
    Material insertMaterial (MaterialDTO materialDTO) throws ExistDataException;

    Material getMaterialById (Long materialId) throws DataNotFoundException;

    Material updateMaterial (Long materialId, MaterialDTO materialDTO) throws DataNotFoundException;

    void deleteMaterial (Long materialId);

    Page<MaterialResponse> getAllMaterials (PageRequest pageRequest);
}
