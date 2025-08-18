package emh.dd_site.event.service;

import emh.dd_site.event.dto.CourseDto;
import emh.dd_site.event.dto.CourseDtoMapper;
import emh.dd_site.event.dto.CreateUpdateCourseDto;
import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.exception.CourseNotFoundException;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.repository.CourseRepository;
import emh.dd_site.event.repository.EventRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final EventRepository eventRepository;

	private final CourseRepository courseRepository;

	private final CourseDtoMapper courseDtoMapper;

	public Page<CourseDto> listAll(Pageable pageable) {
		var courses = courseRepository.findAll(pageable);
		return courseDtoMapper.toDtoPage(courses);
	}

	public Page<CourseDto> listByEvent(long eventId, Pageable pageable) {
		var courses = courseRepository.findByEventId(eventId, pageable);
		return courseDtoMapper.toDtoPage(courses);
	}

	public CourseDto findById(long id) {
		var course = getById(id);
		return courseDtoMapper.toDto(course);
	}

	public CourseDto create(long eventId, @Valid CreateUpdateCourseDto data) {
		var event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
		var course = new Course(event, data.courseNo(), data.cook());
		var dish = new Dish(data.dish().name());
		dish.setMainIngredient(data.dish().mainIngredient());
		course.setDish(dish);
		course = courseRepository.save(course);
		return courseDtoMapper.toDto(course);
	}

	public CourseDto update(long id, @Valid CreateUpdateCourseDto data) {
		var course = getById(id);
		course.setCourseNo(data.courseNo());
		course.setCook(data.cook());
		course.getDish().setName(data.dish().name());
		course.getDish().setMainIngredient(data.dish().mainIngredient());
		course = courseRepository.save(course);
		return courseDtoMapper.toDto(course);
	}

	public void delete(long id) {
		courseRepository.deleteById(id);
	}

	private Course getById(long id) {
		return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
	}

}
