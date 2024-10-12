package com.timevo_ecommerce_backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import com.timevo_ecommerce_backend.components.LocalizationUtils;
import com.timevo_ecommerce_backend.dtos.ProductDTO;
import com.timevo_ecommerce_backend.dtos.ProductImageDTO;
import com.timevo_ecommerce_backend.dtos.ProductVariantDTO;
import com.timevo_ecommerce_backend.entities.Color;
import com.timevo_ecommerce_backend.entities.ProductImage;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.*;
import com.timevo_ecommerce_backend.responses.cloudinary.CloudinaryResponse;
import com.timevo_ecommerce_backend.responses.product.ProductImageResponse;
import com.timevo_ecommerce_backend.responses.product.ProductListResponse;
import com.timevo_ecommerce_backend.responses.product.ProductResponse;
import com.timevo_ecommerce_backend.services.color.IColorService;
import com.timevo_ecommerce_backend.services.product.IProductRedisService;
import com.timevo_ecommerce_backend.services.product.IProductService;
import com.timevo_ecommerce_backend.services.variant.IProductVariantService;
import com.timevo_ecommerce_backend.utils.FileUploadUtil;
import com.timevo_ecommerce_backend.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final IProductVariantService productVariantService;
    private final IColorService colorService;
    private final LocalizationUtils localizationUtils;
    private final IProductRedisService productRedisService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertProduct (
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) throws Exception {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }
            ProductResponse productResponse = productService.insertProduct(productDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(productResponse)
                            .status(HttpStatus.CREATED)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_SUCCESSFULLY))
                            .build()
            );
    }

    @PostMapping(value = "uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadImages (
            @RequestParam("product-id") Long productId,
            @RequestParam("color-id") Long colorId,
            @ModelAttribute("files") List<MultipartFile> files
    ) {
        try {
            ProductResponse existingProduct = productService.getProductById(productId);
            Color existingColor = colorService.getColorById(colorId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_COLOR_OF_PRODUCT) {
                return ResponseEntity.badRequest().body("You can only upload maximum " + ProductImage.MAXIMUM_IMAGES_PER_COLOR_OF_PRODUCT + " images");
            }
            List<ProductImageResponse> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                // Check file size and format
                if (file.getSize() > FileUploadUtil.MAX_FILE_SIZE) { // Size > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large! Maximum size is 10MB");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }
                // Save file and update thumbnail in DTO
//                String fileName = storeFile(file);
//                String name = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                CloudinaryResponse cloudinaryResponse = productService.uploadImage(file);
                ProductImageResponse productImage = productService.insertProductImage(
                        existingProduct.getId(),
                        existingColor.getId(),
                        ProductImageDTO.builder()
                                .productId(existingProduct.getId())
                                .imageName(cloudinaryResponse.getPublicId())
                                .imageUrl(cloudinaryResponse.getUrl())
                                .colorId(existingColor.getId())
                                .build()
                );
                productImages.add(productImage);
            }
            return ResponseEntity.ok(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<Response> getProducts (
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "", name = "category-ids") String categoryIds,
            @RequestParam(defaultValue = "", name = "color-ids") String colorIds,
            @RequestParam(defaultValue = "", name = "material-ids") String materialIds,
            @RequestParam(defaultValue = "", name = "screen-size-ids") String screenSizeIds,
            @RequestParam(defaultValue = "", name = "collection-ids") String collectionIds,
            @RequestParam(defaultValue = "default", name ="sort") String sortOption,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int limit
    ) throws JsonProcessingException {
        Sort sort = switch (sortOption) {
            case "popularity" -> Sort.by("id").descending();
            case "latest" -> Sort.by("createdAt").descending();
            case "oldest" -> Sort.by("createdAd").ascending();
            case "high" -> Sort.by("price").descending();
            case "low" -> Sort.by("price").ascending();
            default -> Sort.by("id").ascending(); // Default sorting
        };
        // Create Pageable from page and limit information
        PageRequest pageRequest = PageRequest.of(
                page, limit, sort
        );
        List<Long> categories = null;
        if (!categoryIds.equals("")) {
            categories = Arrays.stream(categoryIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (categories.isEmpty()) {
                categories = null;
            }
        }

        List<Long> colors = null;
        if (!colorIds.equals("")) {
            colors = Arrays.stream(colorIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (colors.isEmpty()){
                colors = null;
            }
        }

        List<Long> materials = null;
        if (!materialIds.equals("")) {
            materials = Arrays.stream(materialIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (materials.isEmpty()) {
                materials = null;
            }
        }

        List<Long> screenSizes = null;
        if (!screenSizeIds.equals("")) {
            screenSizes = Arrays.stream(screenSizeIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (screenSizes.isEmpty()) {
                screenSizes = null;
            }
        }

        List<Long> collections = null;
        if (!collectionIds.equals("")) {
            collections = Arrays.stream(collectionIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (collections.isEmpty()) {
                collections = null;
            }
        }
//        Page<ProductResponse> productPage = productRedisService
//                .getAllProducts(categoryIds, collectionIds, colorIds, materialIds, screenSizeIds, keyword, pageRequest);
        Page<ProductResponse> productPage = productService.searchProducts(
                categories, categories == null ? 0 : categories.size(),
                collections, collections == null ? 0 : collections.size(),
                colors, colors == null ? 0 : colors.size(),
                materials, materials == null ? 0 : materials.size(),
                screenSizes, screenSizes == null ? 0 : screenSizes.size(),
                keyword,
                pageRequest);
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> productResponses = productPage.getContent();

        return ResponseEntity.ok(
                Response.builder()
                        .data(ProductListResponse.builder()
                                .productResponses(productResponses)
                                .totalPages(totalPages)
                                .build())
                        .status(HttpStatus.OK)
                        .message("Get all products successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getProductById (@PathVariable("id") Long productId) throws DataNotFoundException {
        ProductResponse productResponse = productService.getProductById(productId);
        return ResponseEntity.ok(
                Response.builder()
                        .data(productResponse)
                        .status(HttpStatus.OK)
                        .message("Get product successfully")
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateProduct (
            @PathVariable("id") Long productId,
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) throws Exception {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }
            ProductResponse productResponse = productService.updateProduct(productId, productDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .data(productResponse)
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_SUCCESSFULLY))
                            .build()
            );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteProduct (@PathVariable("id") Long productId) throws DataNotFoundException {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/generate-fake-product")
    private ResponseEntity<?> generateFakeProduct () throws Exception {
        Faker faker = new Faker();
        for (int i = 0; i < 1_000_000; i++) {
            String title = faker.commerce().productName();
            if(productService.existByTitle(title)) {
                continue;
            }
            int collections = faker.number().numberBetween(1,3);
            Set<Long> collectionIds = new HashSet<>();
            for (int k = 0; k <= collections; k++) {
                collectionIds.add((long) faker.number().numberBetween(1,3));
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .price(faker.number().numberBetween(10000, 999999))
                    .averageRate(faker.number().numberBetween(1,5))
                    .categoryId(faker.number().numberBetween(1,5))
                    .collectionIds(collectionIds.stream().toList())
                    .description(faker.lorem().paragraph(3))
                    .discount(faker.number().numberBetween(0, 100))
                    .title(title)
                    .build();
            ProductResponse newProduct = productService.insertProduct(productDTO);
            int totalVariant = faker.number().numberBetween(1,4);
            Set<ProductVariantDTO> variants = new HashSet<>();
            int quantityStock = 0;
            for (int j = 0; j <= totalVariant; j++) {
                long colorId = faker.number().numberBetween(1,6);
                long materialId = faker.number().numberBetween(1,2);
                long screenSizeId = faker.number().numberBetween(1,5);
                if (!productVariantService.existField(newProduct.getId(), colorId, materialId, screenSizeId)) {
                    ProductVariantDTO productVariantDTO = ProductVariantDTO.builder()
                            .colorId(colorId)
                            .materialId(materialId)
                            .screenSizeId(screenSizeId)
                            .productId(newProduct.getId())
                            .quantity(faker.number().numberBetween(10, 100))
                            .build();
                    quantityStock += productVariantDTO.getQuantity();
                    variants.add(productVariantDTO);
                }
            }
            productVariantService.insertVariant(variants.stream().toList());
            productDTO.setQuantityStock(quantityStock);
            productService.updateProduct(newProduct.getId(), productDTO);
        }
        return ResponseEntity.ok("Insert Product Successfully");
    }
}
