package emh.dd_site.wine.controller;

import emh.dd_site.wine.dto.GrapeResponse;
import emh.dd_site.wine.dto.GrapeUpsertRequest;
import emh.dd_site.wine.service.GrapeService;
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
public class GrapeController {

	private final Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");

	private final GrapeService grapeService;

	@GetMapping("/api/wines/grapes")
	public PagedModel<GrapeResponse> listGrapes(Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_NAME);
		var page = grapeService.listAll(pageRequest);
		return new PagedModel<>(page);
	}

	@GetMapping("/api/wines/grapes/{id}")
	public GrapeResponse getGrapeById(@PathVariable int id) {
		return grapeService.findById(id);
	}

	@PostMapping("/api/wines/grapes")
	public GrapeResponse createGrape(@Valid @RequestBody GrapeUpsertRequest request) {
		return grapeService.create(request);
	}

	@PutMapping("/api/wines/grapes/{id}")
	public GrapeResponse updateGrape(@PathVariable int id, @Valid @RequestBody GrapeUpsertRequest request) {
		return grapeService.update(id, request);
	}

	@DeleteMapping("/api/wines/grapes/{id}")
	public ResponseEntity<Void> deleteGrape(@PathVariable int id) {
		grapeService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
