package emh.dd_site.event.controller;

import emh.dd_site.event.dto.CourseResponse;
import emh.dd_site.event.dto.CourseUpsertRequest;
import emh.dd_site.event.dto.EventResponse;
import emh.dd_site.event.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

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

	private CourseResponse testResponse;

	private CourseUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		var event = new EventResponse(1L, LocalDate.now(), "Event Name", null);
		testResponse = new CourseResponse(1L, event, 2, "Cook Name", null);
		testRequest = new CourseUpsertRequest(2, "Cook Name", null);
	}

	@Nested
	@DisplayName("GET /api/courses")
	class ListCoursesTests {

		@Test
		@DisplayName("should return paged list and enforce DESC sort by event.date")
		void shouldReturnPagedListWithEventDateSort() {
			// given
			PageRequest input = PageRequest.of(1, 10); // controller will enforce sort
			PageRequest expected = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "event.date"));
			PageImpl<CourseResponse> page = new PageImpl<>(java.util.List.of(testResponse), expected, 1);
			given(courseService.listAll(any(PageRequest.class))).willReturn(page);

			// when
			var result = courseController.listCourses(input);

			// then
			assertThat(result.getContent()).containsExactly(testResponse);

			assertThat(result.getMetadata()).isNotNull();
			assertThat(result.getMetadata().number()).isEqualTo(1);
			assertThat(result.getMetadata().size()).isEqualTo(10);

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
	class ListCoursesByEventTests {

		@Test
		@DisplayName("should return paged list filtered by event and enforce DESC sort by courseNo")
		void shouldReturnPagedListWithCourseNoSort() {
			// given
			long eventId = 42L;
			PageRequest input = PageRequest.of(0, 5);
			PageRequest expected = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "courseNo"));
			PageImpl<CourseResponse> page = new PageImpl<>(java.util.List.of(testResponse), expected, 1);
			given(courseService.listByEvent(eq(eventId), any(PageRequest.class))).willReturn(page);

			// when
			var result = courseController.listCoursesByEvent(eventId, input);

			// then
			assertThat(result.getContent()).containsExactly(testResponse);

			// verify page and sort
			verify(courseService).listByEvent(eq(eventId), argThat(pr -> {
				Sort.Order order = pr.getSort().getOrderFor("courseNo");
				return pr.getPageNumber() == 0 && pr.getPageSize() == 5 && order != null
						&& order.getDirection() == Sort.Direction.ASC;
			}));
		}

	}

	@Nested
	@DisplayName("GET /api/courses/{id}")
	class GetOneCourseTests {

		@Test
		@DisplayName("should return single course")
		void shouldReturnSingleCourse() {
			// given
			given(courseService.findById(7L)).willReturn(testResponse);

			// when
			CourseResponse result = courseController.getCourseById(7L);

			// then
			assertThat(result).isEqualTo(testResponse);
			verify(courseService).findById(7L);
		}

	}

	@Nested
	@DisplayName("POST /api/events/{eventId}/courses")
	class CreateCourseTests {

		@Test
		@DisplayName("should create and return course")
		void shouldCreateAndReturnCourse() {
			// given
			long eventId = 9L;
			given(courseService.create(eq(eventId), any(CourseUpsertRequest.class))).willReturn(testResponse);

			// when
			CourseResponse result = courseController.createCourse(eventId, testRequest);

			// then
			assertThat(result).isEqualTo(testResponse);
			verify(courseService).create(eventId, testRequest);
		}

	}

	@Nested
	@DisplayName("PUT /api/courses/{id}")
	class UpdateCourseTests {

		@Test
		@DisplayName("should update and return course")
		void shouldUpdateAndReturnCourse() {
			// given
			long id = 5L;
			given(courseService.update(eq(id), any(CourseUpsertRequest.class))).willReturn(testResponse);

			// when
			CourseResponse result = courseController.updateCourse(id, testRequest);

			// then
			assertThat(result).isEqualTo(testResponse);
			verify(courseService).update(id, testRequest);
		}

	}

	@Nested
	@DisplayName("DELETE /api/courses/{id}")
	class DeleteCourseTests {

		@Test
		@DisplayName("should delete and return 204 No Content")
		void shouldDeleteAndReturnNoContent() {
			// when
			var response = courseController.deleteCourse(11L);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
			verify(courseService).delete(11L);
		}

	}

}