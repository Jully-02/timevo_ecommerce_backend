package com.timevo_ecommerce_backend.services.category;

import com.timevo_ecommerce_backend.dtos.CategoryDTO;
import com.timevo_ecommerce_backend.entities.Category;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.exceptions.ExistDataException;
import com.timevo_ecommerce_backend.repositories.CategoryRepository;
import com.timevo_ecommerce_backend.responses.category.CategoryResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Category insertCategory(CategoryDTO categoryDTO) throws ExistDataException {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new ExistDataException("Category name is duplicated");
        }
        Category newCategory = modelMapper.map(categoryDTO, Category.class);
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(Long categoryId) throws DataNotFoundException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Category with ID = " + categoryId));
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Category with ID = " + categoryId));
        existingCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(PageRequest pageRequest) {
        return categoryRepository.getAllCategories(pageRequest);
    }
}
