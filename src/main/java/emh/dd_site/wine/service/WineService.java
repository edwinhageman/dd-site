package emh.dd_site.wine.service;

import emh.dd_site.wine.dto.WineMapper;
import emh.dd_site.wine.dto.WineResponse;
import emh.dd_site.wine.dto.WineUpsertRequest;
import emh.dd_site.wine.entity.Wine;
import emh.dd_site.wine.exception.WineNotFoundException;
import emh.dd_site.wine.repository.WineRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WineService {

	private final WineRepository wineRepository;

	private final WineMapper wineMapper;

	public Page<WineResponse> listAll(Pageable pageable) {
		var page = wineRepository.findAllIds(pageable);
		if (page.isEmpty()) {
			return Page.empty(pageable);
		}
		var entities = mapToPageWithEntities(page);
		return entities.map(wineMapper::toWineResponse);
	}

	public Page<WineResponse> listByEvent(long eventId, Pageable pageable) {
		var page = wineRepository.findIdsByCourseEventId(eventId, pageable);
		if (page.isEmpty()) {
			return Page.empty(pageable);
		}
		var entities = mapToPageWithEntities(page);
		return entities.map(wineMapper::toWineResponse);
	}

	public WineResponse findById(long id) {
		var entity = getById(id);
		return wineMapper.toWineResponse(entity);
	}

	public WineResponse create(@NonNull WineUpsertRequest request) {
		var entity = wineMapper.fromWineUpsertRequest(request);
		entity = wineRepository.save(entity);
		return wineMapper.toWineResponse(entity);
	}

	public WineResponse update(long id, @NonNull WineUpsertRequest request) {
		var entity = getById(id);
		entity = wineMapper.mergeWithWineUpsertRequest(entity, request);
		entity = wineRepository.save(entity);
		return wineMapper.toWineResponse(entity);
	}

	public void delete(long id) {
		wineRepository.deleteById(id);
	}

	private Wine getById(long id) {
		return wineRepository.findById(id).orElseThrow(() -> new WineNotFoundException(id));
	}

	private Page<Wine> mapToPageWithEntities(Page<Long> page) {
		var entityLookupTable = wineRepository.findAllWithStylesAndGrapesByIdIn(page.getContent())
			.stream()
			.collect(Collectors.toMap(Wine::getId, Function.identity()));
		return page.map(entityLookupTable::get);
	}

}
