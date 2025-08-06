package emh.dd_site.event.controller;

import emh.dd_site.event.dto.CreateUpdateEventDto;
import emh.dd_site.event.dto.EventDto;
import emh.dd_site.event.service.EventService;
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
public class EventController {

	private final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "date");

	private final EventService eventService;

	@GetMapping("/api/events")
	public Page<EventDto> list(Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_SORT);
		return eventService.listAll(pageRequest);
	}

	@GetMapping("/api/events/{id}")
	public EventDto one(@PathVariable long id) {
		return eventService.findById(id);
	}

	@PostMapping("/api/events")
	public EventDto create(@Valid @RequestBody CreateUpdateEventDto data) {
		return eventService.create(data);
	}

	@PutMapping("/api/events/{id}")
	public EventDto update(@PathVariable long id, @Valid @RequestBody CreateUpdateEventDto data) {
		return eventService.update(id, data);
	}

	@DeleteMapping("/api/events/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) {
		eventService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
