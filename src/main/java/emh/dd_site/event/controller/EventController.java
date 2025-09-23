package emh.dd_site.event.controller;

import emh.dd_site.event.dto.EventResponse;
import emh.dd_site.event.dto.EventUpsertRequest;
import emh.dd_site.event.service.EventService;
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
public class EventController {

	private final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "date");

	private final EventService eventService;

	@GetMapping("/api/events")
	public PagedModel<EventResponse> list(Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_SORT);
		var page = eventService.listAll(pageRequest);
		return new PagedModel<>(page);
	}

	@GetMapping("/api/events/{id}")
	public EventResponse one(@PathVariable long id) {
		return eventService.findById(id);
	}

	@PostMapping("/api/events")
	public EventResponse create(@Valid @RequestBody EventUpsertRequest request) {
		return eventService.create(request);
	}

	@PutMapping("/api/events/{id}")
	public EventResponse update(@PathVariable long id, @Valid @RequestBody EventUpsertRequest request) {
		return eventService.update(id, request);
	}

	@DeleteMapping("/api/events/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) {
		eventService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
