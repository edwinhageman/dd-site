package emh.dd_site.event.dto;

import emh.dd_site.dto.BaseMapper;
import emh.dd_site.event.entity.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseDtoMapper implements BaseMapper<CourseDto, Course> {

	private final DishDtoMapper dishDtoMapper;

	@Override
	public CourseDto toDto(Course entity) {
		if (entity == null) {
			return null;
		}
		var dishDto = dishDtoMapper.toDto(entity.getDish());
		return new CourseDto(entity.getId(), entity.getCourseNo(), entity.getCook(), dishDto);
	}

}
