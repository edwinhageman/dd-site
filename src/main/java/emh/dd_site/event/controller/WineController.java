package emh.dd_site.event.controller;

import emh.dd_site.event.dto.WineResponse;
import emh.dd_site.event.dto.WineUpsertRequest;
import emh.dd_site.event.service.WineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WineController {

	private final Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");

	private final Sort SORT_BY_EVENT_DATE = Sort.by(Sort.Direction.DESC, "event.date");

	private final WineService wineService;

	@GetMapping("/api/wines")
	public Page<WineResponse> list(Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_NAME);
		return wineService.listAll(pageRequest);
	}

	@GetMapping("/api/events/{eventId}/wines")
	public Page<WineResponse> listByEvent(@PathVariable long eventId, Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_EVENT_DATE);
		return wineService.listByEvent(eventId, pageRequest);
	}

	@GetMapping("/api/wines/{id}")
	public WineResponse one(@PathVariable long id) {
		return wineService.findById(id);
	}

	@PostMapping("/api/wines")
	public WineResponse create(@Valid @RequestBody WineUpsertRequest request) {
		return wineService.create(request);
	}

	@PutMapping("/api/wines/{id}")
	public WineResponse update(@PathVariable long id, @Valid @RequestBody WineUpsertRequest request) {
		return wineService.update(id, request);
	}

	@DeleteMapping("/api/wines/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) {
		wineService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
