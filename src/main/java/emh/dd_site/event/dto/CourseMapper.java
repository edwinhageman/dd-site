package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {

	private final EventMapper eventMapper;

	private final DishMapper dishMapper;

	public CourseResponse toCourseResponse(Course entity) {
		if (entity == null) {
			return null;
		}

		var event = eventMapper.toEventResponse(entity.getEvent());
		var dishDto = dishMapper.toDishResponse(entity.getDish());
		return new CourseResponse(entity.getId(), event, entity.getCourseNo(), entity.getCook(), dishDto);
	}

	public Course fromCourseUpsertRequest(Event event, CourseUpsertRequest request) {
		if (event == null || request == null) {
			return null;
		}
		var course = new Course(event, request.courseNo(), request.cook());
		if (request.dish() != null) {
			course.setDish(dishMapper.fromDishUpsertRequest(request.dish()));
		}
		return course;
	}

	public Course mergeWithCourseUpsertRequest(Course course, CourseUpsertRequest request) {
		if (course == null) {
			return null;
		}
		if (request == null) {
			return course;
		}
		course.setCourseNo(request.courseNo());
		course.setCook(request.cook());
		Dish dish = null;
		if (request.dish() != null) {
			if (course.getDish() == null) {
				dish = dishMapper.fromDishUpsertRequest(request.dish());
			}
			else {
				dish = dishMapper.mergeWithDishUpsertRequest(course.getDish(), request.dish());
			}
		}
		course.setDish(dish);
		return course;
	}

}
