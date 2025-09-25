package emh.dd_site.event.dto;

import jakarta.validation.constraints.NotNull;

public record DishResponse(@NotNull Long id, @NotNull String name, String mainIngredient) {

}
