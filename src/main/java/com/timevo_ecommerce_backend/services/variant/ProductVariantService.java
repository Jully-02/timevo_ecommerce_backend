package com.timevo_ecommerce_backend.services.variant;

import com.timevo_ecommerce_backend.dtos.ProductVariantDTO;
import com.timevo_ecommerce_backend.entities.*;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantService implements IProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ColorRepository colorRepository;
    private final ScreenSizeRepository screenSizeRepository;
    private final MaterialRepository materialRepository;
    private final ProductRepository productRepository;

    @Override
    public boolean existField(Long productId, Long colorId, Long materialId, Long screenSizeId) {
        return productVariantRepository.existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(productId, colorId, materialId, screenSizeId);
    }

    @Override
    public List<ProductVariant> insertVariant(List<ProductVariantDTO> productVariantDTOs) throws ExistDataException, DataNotFoundException {
        List<ProductVariant> variants = new ArrayList<>();
        for ( ProductVariantDTO productVariantDTO : productVariantDTOs) {
            if (productVariantRepository.existsByProductIdAndColorIdAndMaterialIdAndScreenSizeId(
                    productVariantDTO.getProductId(),
                    productVariantDTO.getColorId(),
                    productVariantDTO.getMaterialId(),
                    productVariantDTO.getScreenSizeId()
            )) {
                throw new ExistDataException("Variant is duplicated: " + productVariantDTO);
            }

            Product existingProduct = productRepository.findById(productVariantDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + productVariantDTO.getProductId()));

            Color existingColor = colorRepository.findById(productVariantDTO.getColorId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + productVariantDTO.getColorId()));

            Material existingMaterial = materialRepository.findById(productVariantDTO.getMaterialId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + productVariantDTO.getMaterialId()));

            ScreenSize existingScreenSize = screenSizeRepository.findById(productVariantDTO.getScreenSizeId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Screen Size with ID = " + productVariantDTO.getScreenSizeId()));


            ProductVariant variant = ProductVariant.builder()
                    .color(existingColor)
                    .screenSize(existingScreenSize)
                    .material(existingMaterial)
                    .product(existingProduct)
                    .quantity(productVariantDTO.getQuantity())
                    .build();
            variants.add(productVariantRepository.save(variant));
        }
        return variants;
    }

    @Override
    public ProductVariant getVariantById(Long variantId) throws DataNotFoundException {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Variant with ID = " + variantId));
    }

    @Override
    @Transactional
    public List<ProductVariant> updateVariant(Long productId, List<ProductVariantDTO> productVariantDTOs) throws DataNotFoundException {
        List<ProductVariant> existingVariants = productVariantRepository.findByProductId(productId);

        List<ProductVariant> updatedVariants = new ArrayList<>();

        for (ProductVariantDTO variantDTO : productVariantDTOs) {
            ProductVariant existingVariant = existingVariants.stream()
                    .filter(variant -> variant.getColor().getId() == variantDTO.getColorId() &&
                            variant.getMaterial().getId() == variantDTO.getMaterialId() &&
                            variant.getScreenSize().getId() == variantDTO.getScreenSizeId())
                    .findFirst()
                    .orElse(null);

            if (existingVariant != null) {
                existingVariant.setQuantity(variantDTO.getQuantity());
                updatedVariants.add(existingVariant);
            } else {
                ProductVariant newVariant = ProductVariant.builder()
                        .color(colorRepository.findById(variantDTO.getColorId())
                                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + variantDTO.getColorId())))
                        .material(materialRepository.findById(variantDTO.getMaterialId())
                                .orElseThrow(() -> new DataNotFoundException("Cannot find Material with ID = " + variantDTO.getMaterialId())))
                        .screenSize(screenSizeRepository.findById(variantDTO.getScreenSizeId())
                                .orElseThrow(() -> new DataNotFoundException("Cannot find Screen size with ID = " + variantDTO.getScreenSizeId())))
                        .quantity(variantDTO.getQuantity())
                        .product(productRepository.findById(productId)
                                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + productId)))
                        .build();
                updatedVariants.add(newVariant);
            }
        }

        for (ProductVariant existingVariant : existingVariants) {
            boolean stillExists = productVariantDTOs.stream()
                    .anyMatch(dto -> dto.getColorId().equals(existingVariant.getColor().getId()) &&
                            dto.getMaterialId().equals(existingVariant.getMaterial().getId()) &&
                            dto.getScreenSizeId().equals(existingVariant.getScreenSize().getId()));
            if (!stillExists) {
                productVariantRepository.delete(existingVariant);
            }
        }

        return productVariantRepository.saveAll(updatedVariants);
    }

    @Override
    public List<ProductVariant> getVariantByProductId(Long productId) {
        return productVariantRepository.findByProductId(productId);
    }

    @Override
    public List<ProductVariant> getAllVariants() {
        return productVariantRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteVariantByProductId(Long productId) {
        productVariantRepository.deleteByProductId(productId);
    }
}
