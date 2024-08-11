package com.timevo_ecommerce_backend.services.category;

import com.timevo_ecommerce_backend.dtos.CategoryDTO;
import com.timevo_ecommerce_backend.entities.Category;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.responses.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ICategoryService {
    Category insertCategory (CategoryDTO categoryDTO) throws ExistDataException;

    Category getCategoryById (Long categoryId) throws DataNotFoundException;

    Category updateCategory (Long categoryId, CategoryDTO categoryDTO) throws DataNotFoundException;

    void deleteCategory (Long categoryId);

    Page<CategoryResponse> getAllCategories (PageRequest pageRequest);

}
