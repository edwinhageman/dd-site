package emh.dd_site.wine.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

public record WineUpsertRequest(@NotBlank String name, String winery, String country, String region, String appellation,
		Year vintage, List<Integer> styles, List<GrapeComposition> grapes) {

	record GrapeComposition(Integer grapeId, BigDecimal percentage) {
	}
}
