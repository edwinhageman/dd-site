package emh.dd_site.wine.dto;

import jakarta.validation.constraints.NotBlank;

public record WineStyleUpsertRequest(@NotBlank String name) {

}
