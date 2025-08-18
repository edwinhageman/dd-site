package emh.dd_site.event.controller;

import emh.dd_site.event.dto.CourseDto;
import emh.dd_site.event.dto.CreateUpdateCourseDto;
import emh.dd_site.event.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseController Unit Tests")
class CourseControllerTest {

	@Mock
	private CourseService courseService;

	@InjectMocks
	private CourseController courseController;

	private final CourseDto testCourseDto = new CourseDto(1L, 2, "Cook Name", null);

	private final CreateUpdateCourseDto createDto = new CreateUpdateCourseDto(2, "Cook Name", null);

	@Nested
	@DisplayName("GET /api/courses")
	class ListCourses {

		@Test
		@DisplayName("should return paged list and enforce DESC sort by event.date")
		void shouldReturnPagedListWithEventDateSort() {
			// given
			PageRequest input = PageRequest.of(1, 10); // controller will enforce sort
			PageRequest expected = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "event.date"));
			PageImpl<CourseDto> page = new PageImpl<>(java.util.List.of(testCourseDto), expected, 1);
			given(courseService.listAll(any(PageRequest.class))).willReturn(page);

			// when
			Page<CourseDto> result = courseController.list(input);

			// then
			assertThat(result.getContent()).containsExactly(testCourseDto);
			assertThat(result.getNumber()).isEqualTo(1);
			assertThat(result.getSize()).isEqualTo(10);

			// verify sort is DESC by event.date
			verify(courseService).listAll(argThat(pr -> {
				Sort.Order order = pr.getSort().getOrderFor("event.date");
				return pr.getPageNumber() == 1 && pr.getPageSize() == 10 && order != null
						&& order.getDirection() == Sort.Direction.DESC;
			}));
		}

	}

	@Nested
	@DisplayName("GET /api/events/{eventId}/courses")
	class ListCoursesByEvent {

		@Test
		@DisplayName("should return paged list filtered by event and enforce DESC sort by courseNo")
		void shouldReturnPagedListWithCourseNoSort() {
			// given
			long eventId = 42L;
			PageRequest input = PageRequest.of(0, 5);
			PageRequest expected = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "courseNo"));
			PageImpl<CourseDto> page = new PageImpl<>(java.util.List.of(testCourseDto), expected, 1);
			given(courseService.listByEvent(eq(eventId), any(PageRequest.class))).willReturn(page);

			// when
			Page<CourseDto> result = courseController.listByEvent(eventId, input);

			// then
			assertThat(result.getContent()).containsExactly(testCourseDto);

			// verify page and sort
			verify(courseService).listByEvent(eq(eventId), argThat(pr -> {
				Sort.Order order = pr.getSort().getOrderFor("courseNo");
				return pr.getPageNumber() == 0 && pr.getPageSize() == 5 && order != null
						&& order.getDirection() == Sort.Direction.DESC;
			}));
		}

	}

	@Nested
	@DisplayName("GET /api/courses/{id}")
	class GetOneCourse {

		@Test
		@DisplayName("should return single course")
		void shouldReturnSingleCourse() {
			// given
			given(courseService.findById(7L)).willReturn(testCourseDto);

			// when
			CourseDto result = courseController.one(7L);

			// then
			assertThat(result).isEqualTo(testCourseDto);
			verify(courseService).findById(7L);
		}

	}

	@Nested
	@DisplayName("POST /api/events/{eventId}/courses")
	class CreateCourse {

		@Test
		@DisplayName("should create and return course")
		void shouldCreateAndReturnCourse() {
			// given
			long eventId = 9L;
			given(courseService.create(eq(eventId), any(CreateUpdateCourseDto.class))).willReturn(testCourseDto);

			// when
			CourseDto result = courseController.create(eventId, createDto);

			// then
			assertThat(result).isEqualTo(testCourseDto);
			verify(courseService).create(eventId, createDto);
		}

	}

	@Nested
	@DisplayName("PUT /api/courses/{id}")
	class UpdateCourse {

		@Test
		@DisplayName("should update and return course")
		void shouldUpdateAndReturnCourse() {
			// given
			long id = 5L;
			given(courseService.update(eq(id), any(CreateUpdateCourseDto.class))).willReturn(testCourseDto);

			// when
			CourseDto result = courseController.update(id, createDto);

			// then
			assertThat(result).isEqualTo(testCourseDto);
			verify(courseService).update(id, createDto);
		}

	}

	@Nested
	@DisplayName("DELETE /api/courses/{id}")
	class DeleteCourse {

		@Test
		@DisplayName("should delete and return 204 No Content")
		void shouldDeleteAndReturnNoContent() {
			// when
			var response = courseController.delete(11L);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
			verify(courseService).delete(11L);
		}

	}

}