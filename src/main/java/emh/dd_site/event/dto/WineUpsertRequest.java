package emh.dd_site.event.dto;

import emh.dd_site.event.WineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Year;

public record WineUpsertRequest(@NotBlank String name, @NotNull WineType type, @NotBlank String grape,
		@NotBlank String country, String region, Year year) {

}
