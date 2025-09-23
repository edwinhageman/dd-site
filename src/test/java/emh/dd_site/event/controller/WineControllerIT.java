package emh.dd_site.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import emh.dd_site.event.WineType;
import emh.dd_site.event.dto.WineResponse;
import emh.dd_site.event.dto.WineUpsertRequest;
import emh.dd_site.event.exception.WineNotFoundException;
import emh.dd_site.event.service.WineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WineController.class)
@DisplayName("WineController Integration Tests")
public class WineControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private WineService wineService;

	private final WineResponse testResponse = new WineResponse(1L, "Test Wine", WineType.RED, "Test Grape",
			"Test Country", "Test Region", Year.of(2020), Collections.emptyList());

	private final WineResponse testResponse2 = new WineResponse(2L, "Test Wine 2", WineType.ROSE, "Test Grape 2",
			"Test Country 2", "Test Region 2", Year.of(2022), Collections.emptyList());

	private final WineResponse upsertResponse = new WineResponse(1L, "Upsert Wine", WineType.WHITE, "Upsert Grape",
			"Upsert Country", "Upsert Region", Year.of(2023), Collections.emptyList());

	private final WineUpsertRequest testRequest = new WineUpsertRequest("Upsert Wine", WineType.WHITE, "Upsert Grape",
			"Upsert Country", "Upsert Region", Year.of(2023));

	@Nested
	@DisplayName("GET /api/wines")
	class ListAll {

		@Test
		@DisplayName("should sort by name asc")
		void shouldSortByNameAsc() throws Exception {
			var page = new PageImpl<>(List.of(testResponse, testResponse2), PageRequest.of(1, 10), 2);

			given(wineService.listAll(any(Pageable.class))).willReturn(page);

			mockMvc.perform(get("/api/wines").param("page", "1").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)));

			var captor = ArgumentCaptor.forClass(Pageable.class);
			verify(wineService).listAll(captor.capture());
			Pageable pageReq = captor.getValue();
			assertThat(pageReq.getPageNumber()).isEqualTo(1);
			assertThat(pageReq.getPageSize()).isEqualTo(10);
			assertThat(pageReq.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "name"));
		}

	}

	@Nested
	@DisplayName("GET /api/events/{eventId}/wines")
	class ListByEvent {

		@Test
		@DisplayName("should sort by event date desc")
		void shouldSortByEventDateDesc() throws Exception {
			var eventId = 1L;
			var page = new PageImpl<>(List.of(testResponse, testResponse2), PageRequest.of(0, 15), 2);
			given(wineService.listByEvent(eq(1L), any(Pageable.class))).willReturn(page);

			mockMvc.perform(get("/api/events/{eventId}/wines", eventId).param("size", "15"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)));

			var captor = ArgumentCaptor.forClass(Pageable.class);
			verify(wineService).listByEvent(eq(eventId), captor.capture());
			Pageable pageReq = captor.getValue();
			assertThat(pageReq.getPageNumber()).isEqualTo(0);
			assertThat(pageReq.getPageSize()).isEqualTo(15);
			assertThat(pageReq.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "event.date"));
		}

	}

	@Nested
	@DisplayName("GET /api/wines/{id}")
	class GetOne {

		@Test
		@DisplayName("should return a wine by id")
		void shouldReturnWine() throws Exception {
			given(wineService.findById(1L)).willReturn(testResponse);

			mockMvc.perform(get("/api/wines/{id}", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test Wine"))
				.andExpect(jsonPath("$.type").value("RED"))
				.andExpect(jsonPath("$.grape").value("Test Grape"))
				.andExpect(jsonPath("$.country").value("Test Country"))
				.andExpect(jsonPath("$.region").value("Test Region"))
				.andExpect(jsonPath("$.year").value(2020))
				.andExpect(jsonPath("$.courses").isEmpty());
		}

		@Test
		@DisplayName("should return 404 when wine not found")
		void shouldReturn4040WhenCourseNotFound() throws Exception {
			given(wineService.findById(404L)).willThrow(new WineNotFoundException(404L));

			mockMvc.perform(get("/api/wines/{id}", 404L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find wine 404"));
		}

	}

	@Nested
	@DisplayName("POST /api/wines")
	class Create {

		@Test
		@DisplayName("should create with valid body")
		void shouldCreate() throws Exception {
			given(wineService.create(testRequest)).willReturn(upsertResponse);

			mockMvc
				.perform(post("/api/wines").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Upsert Wine"))
				.andExpect(jsonPath("$.type").value("WHITE"))
				.andExpect(jsonPath("$.grape").value("Upsert Grape"))
				.andExpect(jsonPath("$.country").value("Upsert Country"))
				.andExpect(jsonPath("$.region").value("Upsert Region"))
				.andExpect(jsonPath("$.year").value(2023))
				.andExpect(jsonPath("$.courses").isEmpty());
		}

		@Test
		@DisplayName("should return 400 when receiving invalid request body")
		void shouldReturn400WhenRequestBodyInvalid() throws Exception {
			String invalidJson = """
					{"name":"","type":"","grape":" ","country":null,"region":"Test Region","year":2020}
					""";
			mockMvc.perform(post("/api/wines").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest());

			verifyNoInteractions(wineService);
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/{id}")
	class Update {

		@Test
		@DisplayName("should update with valid body")
		void shouldUpdate() throws Exception {
			given(wineService.update(1L, testRequest)).willReturn(upsertResponse);

			mockMvc
				.perform(put("/api/wines/{id}", 1).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Upsert Wine"))
				.andExpect(jsonPath("$.type").value("WHITE"))
				.andExpect(jsonPath("$.grape").value("Upsert Grape"))
				.andExpect(jsonPath("$.country").value("Upsert Country"))
				.andExpect(jsonPath("$.region").value("Upsert Region"))
				.andExpect(jsonPath("$.year").value(2023))
				.andExpect(jsonPath("$.courses").isEmpty());
		}

		@Test
		@DisplayName("should return 400 when receiving invalid request body")
		void shouldReturn400WhenRequestBodyInvalid() throws Exception {
			String invalidJson = """
					{"name":"","type":"","grape":" ","country":null,"region":"Test Region","year":2020}
					""";
			mockMvc.perform(post("/api/wines").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest());

			verifyNoInteractions(wineService);
		}

		@Test
		@DisplayName("should return 404 when wine not found")
		void shouldReturn404WhenCourseNotFound() throws Exception {
			given(wineService.update(404L, testRequest)).willThrow(new WineNotFoundException(404L));

			mockMvc
				.perform(put("/api/wines/{id}", 404L).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find wine 404"));
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/{id}")
	class Delete {

		@Test
		@DisplayName("should delete and return no content")
		void shouldDelete() throws Exception {
			doNothing().when(wineService).delete(1L);

			mockMvc.perform(delete("/api/wines/{id}", 1)).andExpect(status().isNoContent());

			verify(wineService).delete(1L);
		}

	}

}
