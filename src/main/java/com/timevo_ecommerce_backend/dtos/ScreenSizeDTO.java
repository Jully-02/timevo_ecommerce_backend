package com.timevo_ecommerce_backend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScreenSizeDTO {
    @NotNull(message = "Screen size is required")
    @Min(value = 1, message = "Screen size must be greater than 1")
    private float size;
}
