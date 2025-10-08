package emh.dd_site.wine.dto;

import jakarta.validation.constraints.NotBlank;

public record GrapeUpsertRequest(@NotBlank String name) {

}
