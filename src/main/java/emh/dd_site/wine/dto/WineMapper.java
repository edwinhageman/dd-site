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
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
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

		List<WineResponse.GrapeCompositionResponse> grapes = Collections.emptyList();
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

	public WineResponse.GrapeCompositionResponse toWineGrapeCompositionResponse(WineGrapeComposition entity) {
		if (entity == null) {
			return null;
		}
		return new WineResponse.GrapeCompositionResponse(grapeMapper.toGrapeResponse(entity.getGrape()),
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

		// clear wine styles
		if (request.styles().isEmpty()) {
			wine.clearStyles();
			return;
		}

		// create lookup table
		var currentById = wine.getStyles().stream().collect(Collectors.toMap(WineStyle::getId, Function.identity()));

		var desiredStyleIds = request.styles();

		// find new styles
		var styleIdsToFetch = request.styles()
			.stream()
			.filter(id -> !currentById.containsKey(id))
			.collect(Collectors.toSet());

		// remove styles that are no longer desired
		var stylesToRemove = wine.getStyles().stream().filter(s -> !desiredStyleIds.contains(s.getId())).toList();
		stylesToRemove.forEach(wine::removeStyle);

		// fetch and relate new styles
		if (!styleIdsToFetch.isEmpty()) {
			getWineStylesById(styleIdsToFetch).forEach(wine::addStyle);
		}
	}

	private void mapGrapesFromRequest(Wine wine, WineUpsertRequest request) {
		// when we don't receive a grape composition, leave the current composition as is
		if (request.grapeComposition() == null) {
			return;
		}

		// clear grape composition
		if (request.grapeComposition().isEmpty()) {
			wine.clearGrapeComposition();
			return;
		}

		// create lookup tables
		var newById = request.grapeComposition()
			.stream()
			.collect(Collectors.toMap(WineUpsertRequest.GrapeCompositionRequest::grapeId,
					WineUpsertRequest.GrapeCompositionRequest::percentage));
		var currentByGrapeId = wine.getGrapeComposition()
			.stream()
			.collect(Collectors.toMap(c -> c.getGrape().getId(), Function.identity()));

		var desiredGrapeIds = new HashSet<Integer>();
		var grapeIdsToFetch = new HashSet<Integer>();

		// find and update existing grapes
		for (var gc : request.grapeComposition()) {
			desiredGrapeIds.add(gc.grapeId());
			var existing = currentByGrapeId.get(gc.grapeId());
			if (existing != null) {
				existing.setPercentage(gc.percentage());
			}
			else {
				grapeIdsToFetch.add(gc.grapeId());
			}
		}

		// remove grapes that are no longer desired
		var grapesToRemove = wine.getGrapeComposition()
			.stream()
			.map(WineGrapeComposition::getGrape)
			.filter(g -> !desiredGrapeIds.contains(g.getId()))
			.toList();

		grapesToRemove.forEach(wine::removeGrape);

		// load and relate new grapes
		if (!grapeIdsToFetch.isEmpty()) {
			getGrapesById(grapeIdsToFetch).forEach(g -> {
				wine.addGrape(g, newById.get(g.getId()));
			});
		}
	}

	private List<WineStyle> getWineStylesById(Collection<Integer> ids) {
		return wineStyleRepository.findAllById(ids);
	}

	private List<Grape> getGrapesById(Collection<Integer> ids) {
		return grapeRepository.findAllById(ids);
	}

}
