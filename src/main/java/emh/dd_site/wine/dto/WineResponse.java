package emh.dd_site.wine.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Collections;
import java.util.List;

public record WineResponse(@NotNull Long id, @NotNull String name, String winery, String country, String region,
		String appellation, Year vintage, @NotNull List<WineStyleResponse> styles,
		@NotNull List<GrapeComposition> grapeComposition) {

	public WineResponse(Long id, String name, String winery, String country, String region, String appellation,
			Year vintage) {
		this(id, name, winery, country, region, appellation, vintage, Collections.emptyList(), Collections.emptyList());
	}

	public record GrapeComposition(@NotNull GrapeResponse grape, BigDecimal percentage) {
	}
}
