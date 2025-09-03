package emh.dd_site.event.dto;

import jakarta.validation.constraints.NotBlank;

public record DishUpsertRequest(@NotBlank String name, String mainIngredient) {

}
