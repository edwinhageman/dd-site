package emh.dd_site.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import emh.dd_site.event.dto.CourseDto;
import emh.dd_site.event.dto.CreateUpdateCourseDto;
import emh.dd_site.event.exception.CourseNotFoundException;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.service.CourseService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@DisplayName("CourseController")
public class CourseControllerIT {

	@Resource
	private MockMvc mockMvc;

	@MockitoBean
	private CourseService courseService;

	@Resource
	private ObjectMapper objectMapper;

	@Nested
	@DisplayName("GET /api/courses")
	class ListAll {

		@Test
		@DisplayName("should delegate with DESC sort by event.date and requested page/size")
		void shouldSortByEventDateDesc() throws Exception {
			Page<CourseDto> emptyPage = new PageImpl<>(java.util.List.of(), PageRequest.of(2, 10), 0);
			when(courseService.listAll(any(Pageable.class))).thenReturn(emptyPage);

			mockMvc.perform(get("/api/courses").param("page", "2").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(0)));

			ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
			verify(courseService).listAll(captor.capture());
			Pageable pageReq = captor.getValue();
			assertThat(pageReq.getPageNumber()).isEqualTo(2);
			assertThat(pageReq.getPageSize()).isEqualTo(10);
			// Verify DESC sort by event.date
			Sort sort = pageReq.getSort();
			assertThat(sort).isEqualTo(Sort.by(Sort.Direction.DESC, "event.date"));
		}

	}

	@Nested
	@DisplayName("GET /api/events/{eventId}/courses")
	class ListByEvent {

		@Test
		@DisplayName("should delegate with eventId and DESC sort by courseNo")
		void shouldSortByCourseNoDesc() throws Exception {
			Page<CourseDto> emptyPage = new PageImpl<>(java.util.List.of(), PageRequest.of(0, 5), 0);
			when(courseService.listByEvent(eq(42L), any(Pageable.class))).thenReturn(emptyPage);

			mockMvc.perform(get("/api/events/{eventId}/courses", 42).param("page", "0").param("size", "5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(0)));

			ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
			verify(courseService).listByEvent(eq(42L), captor.capture());
			Pageable pageReq = captor.getValue();
			assertThat(pageReq.getPageNumber()).isEqualTo(0);
			assertThat(pageReq.getPageSize()).isEqualTo(5);
			assertThat(pageReq.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "courseNo"));
		}

	}

	@Nested
	@DisplayName("GET /api/courses/{id}")
	class GetOne {

		@Test
		@DisplayName("should return a course by id")
		void shouldReturnCourse() throws Exception {
			CourseDto dto = new CourseDto(7L, 2, "Cook Name", null);
			when(courseService.findById(7L)).thenReturn(dto);

			mockMvc.perform(get("/api/courses/{id}", 7))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.courseNo").value(2))
				.andExpect(jsonPath("$.cook").value("Cook Name"));
		}

		@Test
		@DisplayName("GET /api/course/{id} should return 404 when course not found")
		void shouldReturn404WhenCourseNotFound() throws Exception {
			// given
			given(courseService.findById(999L)).willThrow(new CourseNotFoundException(999L));

			// when/then
			mockMvc.perform(get("/api/courses/{id}", 999L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find course 999"));
		}

	}

	@Nested
	@DisplayName("POST /api/events/{eventId}/courses")
	class Create {

		@Test
		@DisplayName("should create with valid body and eventId")
		void shouldCreate() throws Exception {
			CreateUpdateCourseDto body = new CreateUpdateCourseDto(1, "Cook", null);
			CourseDto created = new CourseDto(100L, 1, "Cook", null);
			when(courseService.create(eq(99L), any(CreateUpdateCourseDto.class))).thenReturn(created);

			mockMvc
				.perform(post("/api/events/{eventId}/courses", 99).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(body)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(100))
				.andExpect(jsonPath("$.courseNo").value(1))
				.andExpect(jsonPath("$.cook").value("Cook"));

			ArgumentCaptor<CreateUpdateCourseDto> captor = ArgumentCaptor.forClass(CreateUpdateCourseDto.class);
			verify(courseService).create(eq(99L), captor.capture());
			CreateUpdateCourseDto passed = captor.getValue();
			assertThat(passed.courseNo()).isEqualTo(1);
			assertThat(passed.cook()).isEqualTo("Cook");
			assertThat(passed.dish()).isNull();
		}

		@Test
		@DisplayName("should return 404 when event not found")
		void shouldReturn404WhenEventNotFound() throws Exception {
			// given
			CreateUpdateCourseDto body = new CreateUpdateCourseDto(1, "Cook", null);
			given(courseService.create(eq(999L), any(CreateUpdateCourseDto.class)))
				.willThrow(new EventNotFoundException(999L));

			// when/then
			mockMvc
				.perform(post("/api/events/{eventId}/courses", 999L).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(body)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find event 999"));
		}

		@Test
		@DisplayName("should return 400 for invalid body (validation errors)")
		void shouldReturn400ForInvalidBody() throws Exception {
			// Invalid: courseNo <= 0 and cook blank
			String invalidJson = """
					{"courseNo":0,"cook":"  ","dish":null}
					""";

			mockMvc
				.perform(post("/api/events/{eventId}/courses", 1).contentType(MediaType.APPLICATION_JSON)
					.content(invalidJson))
				.andExpect(status().isBadRequest());

			verify(courseService, never()).create(anyLong(), any());
		}

	}

	@Nested
	@DisplayName("PUT /api/courses/{id}")
	class Update {

		@Test
		@DisplayName("should update with valid body")
		void shouldUpdate() throws Exception {
			CreateUpdateCourseDto body = new CreateUpdateCourseDto(3, "Updated Cook", null);
			CourseDto updated = new CourseDto(7L, 3, "Updated Cook", null);
			when(courseService.update(eq(7L), any(CreateUpdateCourseDto.class))).thenReturn(updated);

			mockMvc
				.perform(put("/api/courses/{id}", 7).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(body)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.courseNo").value(3))
				.andExpect(jsonPath("$.cook").value("Updated Cook"));

			ArgumentCaptor<CreateUpdateCourseDto> captor = ArgumentCaptor.forClass(CreateUpdateCourseDto.class);
			verify(courseService).update(eq(7L), captor.capture());
			CreateUpdateCourseDto passed = captor.getValue();
			assertThat(passed.courseNo()).isEqualTo(3);
			assertThat(passed.cook()).isEqualTo("Updated Cook");
		}

		@Test
		@DisplayName("should return 400 for invalid body (validation errors)")
		void shouldReturn400ForInvalidBody() throws Exception {
			String invalidJson = """
					{"courseNo":-5,"cook":""}
					""";

			mockMvc.perform(put("/api/courses/{id}", 9).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest());

			verify(courseService, never()).update(anyLong(), any());
		}

		@Test
		@DisplayName("should return 404 when course not found")
		void shouldReturn404WhenCourseNotFound() throws Exception {
			// given
			CreateUpdateCourseDto body = new CreateUpdateCourseDto(1, "Cook", null);
			given(courseService.update(eq(999L), any(CreateUpdateCourseDto.class)))
				.willThrow(new CourseNotFoundException(999L));

			// when/then
			mockMvc
				.perform(put("/api/courses/{id}", 999L).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(body)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find course 999"));
		}

	}

	@Nested
	@DisplayName("DELETE /api/courses/{id}")
	class Delete {

		@Test
		@DisplayName("should delete and return 204")
		void shouldDelete() throws Exception {
			doNothing().when(courseService).delete(12L);

			mockMvc.perform(delete("/api/courses/{id}", 12)).andExpect(status().isNoContent());

			verify(courseService).delete(12L);
		}

	}

}
