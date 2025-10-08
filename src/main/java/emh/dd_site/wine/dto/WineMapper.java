package emh.dd_site.wine.dto;

import emh.dd_site.wine.entity.Grape;
import emh.dd_site.wine.entity.Wine;
import emh.dd_site.wine.entity.WineGrapeComposition;
import emh.dd_site.wine.entity.WineStyle;
import emh.dd_site.wine.repository.GrapeRepository;
import emh.dd_site.wine.repository.WineStyleRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WineMapper {

	private final EntityManagerFactory emf;

	private final WineStyleRepository wineStyleRepository;

	private final GrapeRepository grapeRepository;

	private final WineStyleMapper wineStyleMapper;

	private final GrapeMapper grapeMapper;

	public WineResponse toWineResponse(Wine entity) {
		if (entity == null) {
			return null;
		}

		var util = emf.getPersistenceUnitUtil();

		List<WineStyleResponse> styles = Collections.emptyList();
		if (util.isLoaded(entity, "styles")) {
			styles = entity.getStyles().stream().map(wineStyleMapper::toWineStyleResponse).toList();
		}

		List<WineResponse.GrapeComposition> grapes = Collections.emptyList();
		if (util.isLoaded(entity, "grapeComposition")) {
			grapes = entity.getGrapeComposition().stream().map(this::toWineGrapeCompositionResponse).toList();
		}

		return new WineResponse(entity.getId(), entity.getName(), entity.getWinery(), entity.getCountry(),
				entity.getRegion(), entity.getAppellation(), entity.getVintage(), entity.getVivinoUrl(), styles,
				grapes);
	}

	public Wine fromWineUpsertRequest(WineUpsertRequest request) {
		if (request == null) {
			return null;
		}

		var wine = new Wine(request.name());

		mapWinePropertiesFromRequest(wine, request);
		mapStylesFromRequest(wine, request);
		mapGrapesFromRequest(wine, request);

		return wine;
	}

	public Wine mergeWithWineUpsertRequest(Wine wine, WineUpsertRequest request) {
		if (wine == null) {
			return null;
		}
		if (request == null) {
			return wine;
		}

		mapWinePropertiesFromRequest(wine, request);
		mapStylesFromRequest(wine, request);
		mapGrapesFromRequest(wine, request);

		return wine;
	}

	public WineResponse.GrapeComposition toWineGrapeCompositionResponse(WineGrapeComposition entity) {
		if (entity == null) {
			return null;
		}
		return new WineResponse.GrapeComposition(grapeMapper.toGrapeResponse(entity.getGrape()),
				entity.getPercentage());
	}

	private void mapWinePropertiesFromRequest(Wine wine, WineUpsertRequest request) {
		wine.setName(request.name());
		wine.setWinery(request.winery());
		wine.setCountry(request.country());
		wine.setRegion(request.region());
		wine.setAppellation(request.appellation());
		wine.setVintage(request.vintage());
		wine.setVivinoUrl(request.vivinoUrl());
	}

	private void mapStylesFromRequest(Wine wine, WineUpsertRequest request) {
		// when we don't receive a new style configuration, leave the current styles as is
		if (request.styles() == null) {
			return;
		}

		// reset and reinitialize the wine styles

		wine.clearStyles();

		if (!request.styles().isEmpty()) {
			var styles = getWineStylesById(request.styles());
			styles.forEach(wine::addStyle);
		}
	}

	private void mapGrapesFromRequest(Wine wine, WineUpsertRequest request) {
		// when we don't receive a grape composition, leave the current composition as is
		if (request.grapes() == null) {
			return;
		}

		// reset and reinitialize the wine's grape composition

		wine.clearGrapeComposition();

		if (request.grapes().isEmpty()) {
			return;
		}

		var lookupTable = request.grapes()
			.stream()
			.collect(Collectors.toMap(WineUpsertRequest.GrapeComposition::grapeId,
					WineUpsertRequest.GrapeComposition::percentage));

		var grapes = getGrapesById(lookupTable.keySet());

		grapes.forEach(grape -> {
			var blendPercentage = lookupTable.get(grape.getId());
			wine.addGrape(grape, blendPercentage);
		});
	}

	private List<WineStyle> getWineStylesById(Collection<Integer> ids) {
		return wineStyleRepository.findAllById(ids);
	}

	private List<Grape> getGrapesById(Collection<Integer> ids) {
		return grapeRepository.findAllById(ids);
	}

}
