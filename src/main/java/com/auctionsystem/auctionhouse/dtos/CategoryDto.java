package com.auctionsystem.auctionhouse.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    @Schema(description = "Unique identifier of the category, generated automatically", readOnly = true)
    private Long id;

    private String categoryName;
}