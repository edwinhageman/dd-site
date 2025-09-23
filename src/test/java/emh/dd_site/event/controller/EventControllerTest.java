
package emh.dd_site.event.controller;

import emh.dd_site.event.dto.EventResponse;
import emh.dd_site.event.dto.EventUpsertRequest;
import emh.dd_site.event.service.EventService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventController Unit Tests")
class EventControllerTest {

	@Mock
	private EventService eventService;

	@InjectMocks
	private EventController eventController;

	private final LocalDate testDate = LocalDate.of(2024, 1, 1);

	private final EventResponse testEventResponse = new EventResponse(1L, testDate, "Test Host", "Test Location");

	private final EventUpsertRequest createDto = new EventUpsertRequest(testDate, "Test Host", "Test Location");

	@Nested
	@DisplayName("GET /api/events")
	class ListEvents {

		@Test
		@DisplayName("should return paged list of events")
		void shouldReturnPageOfEvents() {
			// given
			PageRequest inputRequest = PageRequest.of(0, 20); // controller will enforce
																// DESC
			// date sort
			PageRequest expectedRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "date"));
			PageImpl<EventResponse> page = new PageImpl<>(List.of(testEventResponse), expectedRequest, 1);
			given(eventService.listAll(any(PageRequest.class))).willReturn(page);

			// when
			var result = eventController.list(inputRequest);

			// then
			assertThat(result.getContent()).containsExactly(testEventResponse);
			assertThat(result.getMetadata()).isNotNull();
			assertThat(result.getMetadata().totalElements()).isEqualTo(1);
			assertThat(result.getMetadata().number()).isEqualTo(0);
			assertThat(result.getMetadata().size()).isEqualTo(20);

			// verify sort is enforced to DESC by date
			verify(eventService).listAll(argThat(pr -> {
				Sort.Order order = pr.getSort().getOrderFor("date");
				return order != null && order.getDirection() == Sort.Direction.DESC;
			}));
		}

	}

	@Nested
	@DisplayName("GET /api/events/{id}")
	class GetOneEvent {

		@Test
		@DisplayName("should return single event")
		void shouldReturnSingleEvent() {
			// given
			given(eventService.findById(1L)).willReturn(testEventResponse);

			// when
			EventResponse result = eventController.one(1L);

			// then
			assertThat(result).isEqualTo(testEventResponse);
			verify(eventService).findById(1L);
		}

	}

	@Nested
	@DisplayName("POST /api/events")
	class CreateEvent {

		@Test
		@DisplayName("should create and return event")
		void shouldCreateAndReturnEvent() {
			// given
			given(eventService.create(any(EventUpsertRequest.class))).willReturn(testEventResponse);

			// when
			EventResponse result = eventController.create(createDto);

			// then
			assertThat(result).isEqualTo(testEventResponse);
			verify(eventService).create(createDto);
		}

	}

	@Nested
	@DisplayName("PUT /api/events/{id}")
	class UpdateEvent {

		@Test
		@DisplayName("should update and return event")
		void shouldUpdateAndReturnEvent() {
			// given
			given(eventService.update(eq(1L), any(EventUpsertRequest.class))).willReturn(testEventResponse);

			// when
			EventResponse result = eventController.update(1L, createDto);

			// then
			assertThat(result).isEqualTo(testEventResponse);
			verify(eventService).update(1L, createDto);
		}

	}

	@Nested
	@DisplayName("DELETE /api/events/{id}")
	class DeleteEvent {

		@Test
		@DisplayName("should delete event and return no content")
		void shouldDeleteEventAndReturnNoContent() {
			// when
			var response = eventController.delete(1L);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
			verify(eventService).delete(1L);
		}

	}

}
