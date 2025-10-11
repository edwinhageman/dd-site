package emh.dd_site.wine.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

public record WineUpsertRequest(@NotBlank String name, String winery, String country, String region, String appellation,
		Year vintage, @URL(host = "vivino.com") String vivinoUrl, List<Integer> styles, List<GrapeComposition> grapes) {

	public record GrapeComposition(Integer grapeId, BigDecimal percentage) {
	}
}
