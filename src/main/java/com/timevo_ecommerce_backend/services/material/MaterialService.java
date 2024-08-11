package com.timevo_ecommerce_backend.services.material;

import com.timevo_ecommerce_backend.dtos.MaterialDTO;
import com.timevo_ecommerce_backend.entities.Material;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.repositories.MaterialRepository;
import com.timevo_ecommerce_backend.responses.MaterialResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaterialService implements IMaterialService{

    private final MaterialRepository materialRepository;
    private final ModelMapper modelMapper;
    @Override
    public Material insertMaterial(MaterialDTO materialDTO) throws ExistDataException {
        if (materialRepository.existsByName(materialDTO.getName())) {
            throw new ExistDataException("Material name is duplicated");
        }
        Material newMaterial = modelMapper.map(materialDTO, Material.class);
        return materialRepository.save(newMaterial);
    }

    @Override
    public Material getMaterialById(Long materialId) throws DataNotFoundException {
        return materialRepository.findById(materialId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + materialId));
    }

    @Override
    public Material updateMaterial(Long materialId, MaterialDTO materialDTO) throws DataNotFoundException {
        Material existingMaterial = materialRepository.findById(materialId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + materialId));
        existingMaterial.setName(materialDTO.getName());
        return materialRepository.save(existingMaterial);
    }

    @Override
    public void deleteMaterial(Long materialId) {
        materialRepository.deleteById(materialId);
    }

    @Override
    public Page<MaterialResponse> getAllMaterials(PageRequest pageRequest) {
        return materialRepository.getAllMaterial(pageRequest);
    }
}
