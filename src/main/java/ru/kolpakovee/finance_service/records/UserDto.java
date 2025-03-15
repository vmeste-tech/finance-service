package ru.kolpakovee.finance_service.records;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        @NotNull UUID id,
        @NotEmpty String username,
        @Email String email,
        @NotEmpty String firstNam,
        @NotEmpty String lastName,
        @Nullable String profilePictureUrl,
        LocalDateTime createdAt
) {
}
