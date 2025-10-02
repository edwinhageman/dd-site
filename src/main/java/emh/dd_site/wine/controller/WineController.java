package emh.dd_site.wine.controller;

import emh.dd_site.wine.dto.WineResponse;
import emh.dd_site.wine.dto.WineUpsertRequest;
import emh.dd_site.wine.service.WineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WineController {

	private final Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");

	private final Sort SORT_BY_EVENT_DATE = Sort.by(Sort.Direction.DESC, "event.date");

	private final WineService wineService;

	@GetMapping("/api/wines")
	public PagedModel<WineResponse> listWines(Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_NAME);
		var page = wineService.listAll(pageRequest);
		return new PagedModel<>(page);
	}

	@GetMapping("/api/events/{eventId}/wines")
	public PagedModel<WineResponse> listWinesByEvent(@PathVariable long eventId, Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_EVENT_DATE);
		var page = wineService.listByEvent(eventId, pageRequest);
		return new PagedModel<>(page);
	}

	@GetMapping("/api/wines/{id}")
	public WineResponse getWineById(@PathVariable long id) {
		return wineService.findById(id);
	}

	@PostMapping("/api/wines")
	public WineResponse createWine(@Valid @RequestBody WineUpsertRequest request) {
		return wineService.create(request);
	}

	@PutMapping("/api/wines/{id}")
	public WineResponse updateWine(@PathVariable long id, @Valid @RequestBody WineUpsertRequest request) {
		return wineService.update(id, request);
	}

	@DeleteMapping("/api/wines/{id}")
	public ResponseEntity<Void> deleteWine(@PathVariable long id) {
		wineService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
