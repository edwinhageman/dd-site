package emh.dd_site.event.controller;

import emh.dd_site.event.dto.CourseDto;
import emh.dd_site.event.dto.CreateUpdateCourseDto;
import emh.dd_site.event.service.CourseService;
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
public class CourseController {

	private final Sort SORT_BY_EVENT_DATE = Sort.by(Sort.Direction.DESC, "event.date");

	private final Sort SORT_BY_COURSE_NO = Sort.by(Sort.Direction.DESC, "courseNo");

	private final CourseService courseService;

	@GetMapping("/api/courses")
	public Page<CourseDto> list(Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_EVENT_DATE);
		return courseService.listAll(pageRequest);
	}

	@GetMapping("/api/events/{eventId}/courses")
	public Page<CourseDto> listByEvent(@PathVariable long eventId, Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_COURSE_NO);
		return courseService.listByEvent(eventId, pageRequest);
	}

	@GetMapping("/api/courses/{id}")
	public CourseDto one(@PathVariable long id) {
		return courseService.findById(id);
	}

	@PostMapping("/api/events/{eventId}/courses")
	public CourseDto create(@PathVariable long eventId, @Valid @RequestBody CreateUpdateCourseDto data) {
		return courseService.create(eventId, data);
	}

	@PutMapping("/api/courses/{id}")
	public CourseDto update(@PathVariable long id, @Valid @RequestBody CreateUpdateCourseDto data) {
		return courseService.update(id, data);
	}

	@DeleteMapping("/api/courses/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) {
		courseService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
