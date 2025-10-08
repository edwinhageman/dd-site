package emh.dd_site.wine.dto;

import jakarta.validation.constraints.NotNull;

public record WineStyleResponse(@NotNull Integer id, @NotNull String name) {
}
