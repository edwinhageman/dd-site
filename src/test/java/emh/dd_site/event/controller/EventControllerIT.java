package emh.dd_site.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import emh.dd_site.event.dto.CreateUpdateEventDto;
import emh.dd_site.event.dto.EventDto;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@DisplayName("EventController Integration Tests")
class EventControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private EventService eventService;

	private final LocalDate testDate = LocalDate.of(2024, 1, 1);

	private final EventDto testEventDto = new EventDto(1L, testDate, "Test Host", "Test Location");

	private final CreateUpdateEventDto createDto = new CreateUpdateEventDto(testDate, "Test Host", "Test Location");

	@Test
	@DisplayName("GET /api/events should return paged list of events")
	void shouldReturnPageOfEvents() throws Exception {
		// given
		PageRequest request = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "date"));
		given(eventService.listAll(any(PageRequest.class)))
			.willReturn(new PageImpl<>(List.of(testEventDto), request, 1));

		// when/then
		mockMvc.perform(get("/api/events"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content[0].id").value(testEventDto.id()))
			.andExpect(jsonPath("$.content[0].date").value(testEventDto.date().toString()))
			.andExpect(jsonPath("$.content[0].host").value(testEventDto.host()))
			.andExpect(jsonPath("$.content[0].location").value(testEventDto.location()))
			.andExpect(jsonPath("$.totalElements").value(1))
			.andExpect(jsonPath("$.number").value(0))
			.andExpect(jsonPath("$.size").value(20));
	}

	@Test
	@DisplayName("GET /api/events/{id} should return event")
	void shouldReturnSingleEvent() throws Exception {
		// given
		given(eventService.findById(1L)).willReturn(testEventDto);

		// when/then
		mockMvc.perform(get("/api/events/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(testEventDto.id()))
			.andExpect(jsonPath("$.date").value(testEventDto.date().toString()))
			.andExpect(jsonPath("$.host").value(testEventDto.host()))
			.andExpect(jsonPath("$.location").value(testEventDto.location()));
	}

	@Test
	@DisplayName("GET /api/events/{id} should return 404 when event not found")
	void shouldReturn404WhenEventNotFound() throws Exception {
		// given
		given(eventService.findById(999L)).willThrow(new EventNotFoundException(999L));

		// when/then
		mockMvc.perform(get("/api/events/{id}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.detail").value("Could not find event 999"));
	}

	@Test
	@DisplayName("POST /api/events should create and return event")
	void shouldCreateAndReturnEvent() throws Exception {
		// given
		given(eventService.create(any(CreateUpdateEventDto.class))).willReturn(testEventDto);

		// when/then
		mockMvc
			.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(testEventDto.id()))
			.andExpect(jsonPath("$.date").value(testEventDto.date().toString()))
			.andExpect(jsonPath("$.host").value(testEventDto.host()))
			.andExpect(jsonPath("$.location").value(testEventDto.location()));

		verify(eventService).create(any(CreateUpdateEventDto.class));
	}

	@Test
	@DisplayName("PUT /api/events/{id} should update and return event")
	void shouldUpdateAndReturnEvent() throws Exception {
		// given
		given(eventService.update(eq(1L), any(CreateUpdateEventDto.class))).willReturn(testEventDto);

		// when/then
		mockMvc
			.perform(put("/api/events/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(testEventDto.id()))
			.andExpect(jsonPath("$.date").value(testEventDto.date().toString()))
			.andExpect(jsonPath("$.host").value(testEventDto.host()))
			.andExpect(jsonPath("$.location").value(testEventDto.location()));

		verify(eventService).update(eq(1L), any(CreateUpdateEventDto.class));
	}

	@Test
	@DisplayName("DELETE /api/events/{id} should return no content")
	void shouldDeleteEventAndReturnNoContent() throws Exception {
		// when/then
		mockMvc.perform(delete("/api/events/{id}", 1L)).andExpect(status().isNoContent());

		verify(eventService).delete(1L);
	}

	@Test
	@DisplayName("DELETE /api/events/{id} should return 404 when event not found")
	void shouldReturn404WhenDeletingNonExistentEvent() throws Exception {
		// given
		doThrow(new EventNotFoundException(999L)).when(eventService).delete(999L);

		// when/then
		mockMvc.perform(delete("/api/events/{id}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.detail").value("Could not find event 999"));
	}

	@Test
	@DisplayName("POST /api/events should return 400 when date is null")
	void shouldReturn400WhenDateIsNull() throws Exception {
		CreateUpdateEventDto invalidDto = new CreateUpdateEventDto(null, "Test Host", "Test Location");

		mockMvc
			.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.fieldErrors.date[0]").value("must not be null"));
	}

	@Test
	@DisplayName("POST /api/events should return 400 when host is blank")
	void shouldReturn400WhenHostIsBlank() throws Exception {
		CreateUpdateEventDto invalidDto = new CreateUpdateEventDto(LocalDate.now(), "", "Test Location");

		mockMvc
			.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.fieldErrors.host[0]").value("must not be blank"));
	}

	@Test
	@DisplayName("PUT /api/events/{id} should return 400 when request body is invalid")
	void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {
		CreateUpdateEventDto invalidDto = new CreateUpdateEventDto(null, null, "Test Location");

		mockMvc
			.perform(put("/api/events/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.fieldErrors.date[0]").value("must not be null"))
			.andExpect(jsonPath("$.fieldErrors.host[0]").value("must not be blank"));
	}

	@Test
	@DisplayName("PUT /api/events/{id} should return 404 when updating non-existent event")
	void shouldReturn404WhenUpdatingNonExistentEvent() throws Exception {
		given(eventService.update(eq(999L), any(CreateUpdateEventDto.class)))
			.willThrow(new EventNotFoundException(999L));

		mockMvc
			.perform(put("/api/events/{id}", 999L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.detail").value("Could not find event 999"));
	}

	@Test
	@DisplayName("POST /api/events should return 400 when request body is invalid JSON")
	void shouldReturn400WhenRequestBodyIsInvalidJson() throws Exception {
		String invalidJson = "{\"date\": \"not-a-date\", \"host\": \"Test Host\"}";

		mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/events should return 415 when content type is not JSON")
	void shouldReturn415WhenContentTypeIsNotJson() throws Exception {
		mockMvc.perform(post("/api/events").contentType(MediaType.TEXT_PLAIN).content(createDto.toString()))
			.andExpect(status().isUnsupportedMediaType());
	}

}
