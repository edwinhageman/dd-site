package emh.dd_site.event.service;

import emh.dd_site.event.dto.CourseMapper;
import emh.dd_site.event.dto.CourseResponse;
import emh.dd_site.event.dto.CourseUpsertRequest;
import emh.dd_site.event.entity.Course;
import emh.dd_site.event.exception.CourseNotFoundException;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.repository.CourseRepository;
import emh.dd_site.event.repository.EventRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final EventRepository eventRepository;

	private final CourseRepository courseRepository;

	private final CourseMapper courseMapper;

	public Page<CourseResponse> listAll(Pageable pageable) {
		var courses = courseRepository.findAll(pageable);
		return courses.map(courseMapper::toCourseResponse);
	}

	public Page<CourseResponse> listByEvent(long eventId, Pageable pageable) {
		var courses = courseRepository.findByEventId(eventId, pageable);
		return courses.map(courseMapper::toCourseResponse);
	}

	public CourseResponse findById(long id) {
		var course = getById(id);
		return courseMapper.toCourseResponse(course);
	}

	public CourseResponse create(long eventId, @NonNull CourseUpsertRequest request) {
		var event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
		var course = courseMapper.fromCourseUpsertRequest(event, request);
		course = courseRepository.save(course);
		return courseMapper.toCourseResponse(course);
	}

	public CourseResponse update(long id, @NonNull CourseUpsertRequest request) {
		var course = getById(id);
		course = courseMapper.mergeWithCourseUpsertRequest(course, request);
		course = courseRepository.save(course);
		return courseMapper.toCourseResponse(course);
	}

	public void delete(long id) {
		courseRepository.deleteById(id);
	}

	private Course getById(long id) {
		return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
	}

}
