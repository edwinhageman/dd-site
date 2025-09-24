package emh.dd_site.event.controller;

import emh.dd_site.event.dto.CourseResponse;
import emh.dd_site.event.dto.CourseUpsertRequest;
import emh.dd_site.event.service.CourseService;
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
public class CourseController {

	private final Sort SORT_BY_EVENT_DATE = Sort.by(Sort.Direction.DESC, "event.date");

	private final Sort SORT_BY_COURSE_NO = Sort.by(Sort.Direction.DESC, "courseNo");

	private final CourseService courseService;

	@GetMapping("/api/courses")
	public PagedModel<CourseResponse> listCourses(Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_EVENT_DATE);
		var page = courseService.listAll(pageRequest);
		return new PagedModel<>(page);
	}

	@GetMapping("/api/events/{eventId}/courses")
	public PagedModel<CourseResponse> listCoursesByEvent(@PathVariable long eventId, Pageable pageable) {
		var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), SORT_BY_COURSE_NO);
		var page = courseService.listByEvent(eventId, pageRequest);
		return new PagedModel<>(page);
	}

	@GetMapping("/api/courses/{id}")
	public CourseResponse getCourseById(@PathVariable long id) {
		return courseService.findById(id);
	}

	@PostMapping("/api/events/{eventId}/courses")
	public CourseResponse createCourse(@PathVariable long eventId, @Valid @RequestBody CourseUpsertRequest request) {
		return courseService.create(eventId, request);
	}

	@PutMapping("/api/courses/{id}")
	public CourseResponse updateCourse(@PathVariable long id, @Valid @RequestBody CourseUpsertRequest request) {
		return courseService.update(id, request);
	}

	@DeleteMapping("/api/courses/{id}")
	public ResponseEntity<Void> deleteCourse(@PathVariable long id) {
		courseService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
