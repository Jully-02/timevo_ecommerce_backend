package com.timevo_ecommerce_backend.services.product;

import com.timevo_ecommerce_backend.dtos.ProductDTO;
import com.timevo_ecommerce_backend.dtos.ProductImageDTO;
import com.timevo_ecommerce_backend.entities.*;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.exceptions.InvalidParamException;
import com.timevo_ecommerce_backend.repositories.*;
import com.timevo_ecommerce_backend.responses.CloudinaryResponse;
import com.timevo_ecommerce_backend.responses.ProductResponse;
import com.timevo_ecommerce_backend.responses.ProductVariantResponse;
import com.timevo_ecommerce_backend.services.file_upload.IFileUploadService;
import com.timevo_ecommerce_backend.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;
    private final ProductImageRepository productImageRepository;
    private final IFileUploadService fileUploadService;
    private final ColorRepository colorRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public ProductResponse insertProduct(ProductDTO productDTO) throws Exception {
        if (productRepository.existsByTitle(productDTO.getTitle())) {
            throw new ExistDataException("Product name is duplicated");
        }
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Category with ID = " + productDTO.getCategoryId()));

        List<Collection> collections = new ArrayList<>();
        if (!productDTO.getCollectionIds().isEmpty()) {
            productDTO.getCollectionIds().forEach(
                    collectionId -> {
                        try {
                            collections.add(collectionRepository.findById(collectionId)
                                    .orElseThrow(() -> new DataNotFoundException("Cannot find Collection with ID = " + collectionId)));
                        } catch (DataNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
        Product newProduct = modelMapper.map(productDTO, Product.class);
        newProduct.setCategory(existingCategory);
        if (!collections.isEmpty()) {
            newProduct.setCollections(collections);
        }
        newProduct = productRepository.save(newProduct);
        return modelMapper.map(newProduct, ProductResponse.class);
    }

    @Override
    public ProductResponse getProductById(Long productId) throws DataNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new DataNotFoundException("Cannot find Product with ID = " + productId);
        }
       else {
            Product product = optionalProduct.get();
            ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
            List<ProductVariantResponse> variantResponses = new ArrayList<>();
            product.getProductVariants().forEach(
                    variant -> variantResponses.add(modelMapper.map(variant, ProductVariantResponse.class))
            );
            productResponse.setVariants(variantResponses);
            return productResponse;
        }
    }

    @Override
    public Page<ProductResponse> searchProducts(List<Long> categoryIds, long categoryCount, List<Long> collectionIds, long collectionCount, List<Long> colorIds, long colorCount, List<Long> materialIds, long materialCount, List<Long> screenSizeIds, long screenSizeCount, String keyword, PageRequest pageRequest) {
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryIds, categoryCount, collectionIds, collectionCount, colorIds, colorCount, materialIds, materialCount, screenSizeIds, screenSizeCount, keyword, pageRequest);

        return productPage.map(
                product -> {
                    ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
                    List<ProductVariantResponse> variantResponses = new ArrayList<>();
                    product.getProductVariants().forEach(
                            variant -> variantResponses.add(modelMapper.map(variant, ProductVariantResponse.class))
                    );
                    productResponse.setVariants(variantResponses);
                    return productResponse;
                }
        );
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductDTO productDTO) throws Exception {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Category with ID = " + productDTO.getCategoryId()));
            List<Collection> collections = new ArrayList<>();
            if (!productDTO.getCollectionIds().isEmpty()) {
                productDTO.getCollectionIds().forEach(
                        collectionId -> {
                            try {
                                collections.add(collectionRepository.findById(collectionId)
                                        .orElseThrow(() -> new DataNotFoundException("Cannot find Collection with ID = " + collectionId)));
                            } catch (DataNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            }
            existingProduct.setTitle(productDTO.getTitle());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDiscount(productDTO.getDiscount());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setAverageRate(productDTO.getAverageRate());
            existingProduct.setQuantityStock(productDTO.getQuantityStock());
            existingProduct.setCategory(existingCategory);
            if (collections.isEmpty()) {
                existingProduct.setCollections(collections);
            }
            productRepository.save(existingProduct);

            return modelMapper.map(existingProduct, ProductResponse.class);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) throws DataNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existByTitle(String title) {
        return productRepository.existsByTitle(title);
    }

    @Override
    @Transactional
    public ProductImage insertProductImage(Long productId, Long colorId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository.findById(productImageDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + productImageDTO.getProductId()));

        Color existingColor = colorRepository.findById(colorId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Color with ID = " + colorId));

        List<ProductVariant> existingVariant = productVariantRepository.findByProductIdAndColorId(productId, colorId);
        if (existingVariant == null) {
            throw new Exception("Product and Color not exist");
        }
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageName(productImageDTO.getImageName())
                .imageUrl(productImageDTO.getImageUrl())
                .color(existingColor)
                .isMainImage(false)
                .build();

        // Do not insert more than 4 images for 1 product
        int size = productImageRepository.findByProductIdAndColorId(productId, colorId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_COLOR_OF_PRODUCT) {
            throw new InvalidParamException("Number of images must be <= " + ProductImage.MAXIMUM_IMAGES_PER_COLOR_OF_PRODUCT);
        }
        if (size == 0) {
            newProductImage.setMainImage(true);
        }
        if (Objects.equals(existingProduct.getThumbnail(), "") || existingProduct.getThumbnail() == null) {
            existingProduct.setThumbnail(productImageDTO.getImageUrl());
        }
        productRepository.save(existingProduct);
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<ProductResponse> getProductsByIds(List<Long> productIds) {
        List<Product> products = productRepository.getProductsByIds(productIds);
        return products.stream()
                .map(product -> {
                    ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
                    List<ProductVariantResponse> variantResponses = new ArrayList<>();
                    product.getProductVariants().forEach(
                            variant -> variantResponses.add(modelMapper.map(variant, ProductVariantResponse.class))
                    );
                    productResponse.setVariants(variantResponses);
                    return productResponse;
                }).toList();
    }

    @Override
    public CloudinaryResponse uploadImage(MultipartFile file) throws Exception {
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = fileUploadService.uploadFile(file, fileName);
        return response;
    }
}
