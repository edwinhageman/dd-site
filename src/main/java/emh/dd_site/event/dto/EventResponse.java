package emh.dd_site.event.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventResponse(@NotNull Long id, @NotNull LocalDate date, @NotNull String host, String location) {
}
