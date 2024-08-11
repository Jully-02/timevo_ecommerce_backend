package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColorDTO {

    @NotEmpty(message = "Color's name cannot be empty")
    @Size(min = 3, max = 500, message = "Color's name must be between 3 and 300 characters")
    private String name;

    @Min(value = 0, message = "Red must be greater than or equal to 0")
    @Max(value = 255, message = "Red must be less than or equal to 255")
    @NotNull(message = "Red is required")
    private int red;

    @Min(value = 0, message = "Green must be greater than or equal to 0")
    @Max(value = 255, message = "Green must be less than or equal to 255")
    @NotNull(message = "Green is required")
    private int green;

    @Min(value = 0, message = "Blue must be greater than or equal to 0")
    @Max(value = 255, message = "Blue must be less than or equal to 255")
    @NotNull(message = "Blue is required")
    private int blue;

    @Min(value = 0, message = "Alpha must be greater than or equal to 0")
    @Max(value = 1, message = "Alpha must be less than or equal to 1")
    private float alpha;
}
