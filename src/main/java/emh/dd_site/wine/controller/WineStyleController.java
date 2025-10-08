package emh.dd_site.wine.controller;

import emh.dd_site.wine.dto.WineStyleResponse;
import emh.dd_site.wine.dto.WineStyleUpsertRequest;
import emh.dd_site.wine.service.WineStyleService;
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
public class WineStyleController {

	private final Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");

	private final WineStyleService wineStyleService;

	@GetMapping("/api/wines/styles")
	public PagedModel<WineStyleResponse> listWineStyles(Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_NAME);
		var page = wineStyleService.listAll(pageRequest);
		return new PagedModel<>(page);
	}

	@GetMapping("/api/wines/styles/{id}")
	public WineStyleResponse getWineStyleById(@PathVariable int id) {
		return wineStyleService.findById(id);
	}

	@PostMapping("/api/wines/styles")
	public WineStyleResponse createWineStyle(@Valid @RequestBody WineStyleUpsertRequest request) {
		return wineStyleService.create(request);
	}

	@PutMapping("/api/wines/styles/{id}")
	public WineStyleResponse updateWineStyle(@PathVariable int id, @Valid @RequestBody WineStyleUpsertRequest request) {
		return wineStyleService.update(id, request);
	}

	@DeleteMapping("/api/wines/styles/{id}")
	public ResponseEntity<Void> deleteWineStyle(@PathVariable int id) {
		wineStyleService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
