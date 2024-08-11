package com.timevo_ecommerce_backend.controllers;

import com.github.javafaker.Faker;
import com.timevo_ecommerce_backend.dtos.ProductDTO;
import com.timevo_ecommerce_backend.dtos.ProductImageDTO;
import com.timevo_ecommerce_backend.dtos.ProductVariantDTO;
import com.timevo_ecommerce_backend.entities.Color;
import com.timevo_ecommerce_backend.entities.ProductImage;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.CloudinaryResponse;
import com.timevo_ecommerce_backend.responses.ProductListResponse;
import com.timevo_ecommerce_backend.responses.ProductResponse;
import com.timevo_ecommerce_backend.services.color.IColorService;
import com.timevo_ecommerce_backend.services.file_upload.IFileUploadService;
import com.timevo_ecommerce_backend.services.product.IProductService;
import com.timevo_ecommerce_backend.services.variant.IProductVariantService;
import com.timevo_ecommerce_backend.utils.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("${api.prefix}products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final IProductVariantService productVariantService;
    private final IColorService colorService;

    @PostMapping("")
    public ResponseEntity<?> insertProduct (
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            ProductResponse productResponse = productService.insertProduct(productDTO);
            return ResponseEntity.ok(productResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            List<ProductImage> productImages = new ArrayList<>();
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
                ProductImage productImage = productService.insertProductImage(
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
    public ResponseEntity<?> getProducts (
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "", name = "category-ids") String categoryIds,
            @RequestParam(defaultValue = "", name = "color-ids") String colorIds,
            @RequestParam(defaultValue = "", name = "material-ids") String materialIds,
            @RequestParam(defaultValue = "", name = "screen-size-ids") String screenSizeIds,
            @RequestParam(defaultValue = "", name = "collection-ids") String collectionIds,
            @RequestParam(defaultValue = "default", name ="sort") String sortOption,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int limit
    ) {
        Sort sort = switch (sortOption) {
            case "popularity" -> Sort.by("id").descending();
            case "latest" -> Sort.by("createdAt").descending();
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
                ProductListResponse.builder()
                        .productResponses(productResponses)
                        .totalPages(totalPages)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById (@PathVariable("id") Long productId) throws DataNotFoundException {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct (
            @PathVariable("id") Long productId,
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            return ResponseEntity.ok(productService.updateProduct(productId, productDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct (@PathVariable("id") Long productId) throws DataNotFoundException {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Delete Successfully");
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
