package emh.dd_site.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventUpsertRequest(@NotNull LocalDate date, @NotBlank String host, String location) {
}
