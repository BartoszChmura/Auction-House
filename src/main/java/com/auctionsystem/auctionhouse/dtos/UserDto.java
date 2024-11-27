package com.auctionsystem.auctionhouse.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Schema(description = "Unique identifier of the user, generated automatically", readOnly = true)
    private Long id;

    private String username;

    private String email;

    private String password;

    @Schema(description = "Creation time of the user, generated automatically", readOnly = true)
    private LocalDateTime createdAt;

    @Schema(description = "Update time of the user, generated automatically", readOnly = true)
    private LocalDateTime updatedAt;
}