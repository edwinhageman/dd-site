
package emh.dd_site.event.service;

import emh.dd_site.event.dto.EventMapper;
import emh.dd_site.event.dto.EventResponse;
import emh.dd_site.event.dto.EventUpsertRequest;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.repository.EventRepository;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Tests")
class EventServiceTest {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private EventMapper eventMapper;

	@InjectMocks
	private EventService eventService;

	@Nested
	@DisplayName("List event tests")
	class ListEventsTests {

		@Test
		@DisplayName("should return paged list of mapped DTOs")
		void shouldReturnMappedDtoList() {
			// given
			PageRequest pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "date"));

			Event e1 = new Event(LocalDate.now(), "Host 1");
			e1.setLocation("Location 1");
			Event e2 = new Event(LocalDate.now(), "Host 2");
			e2.setLocation("Location 2");

			Page<Event> entityPage = new PageImpl<>(List.of(e1, e2), pageable, 2);

			EventResponse d1 = new EventResponse(1L, e1.getDate(), e1.getHost(), e1.getLocation());
			EventResponse d2 = new EventResponse(2L, e2.getDate(), e2.getHost(), e2.getLocation());

			given(eventRepository.findAll(any(PageRequest.class))).willReturn(entityPage);
			given(eventMapper.toEventResponse(e1)).willReturn(d1);
			given(eventMapper.toEventResponse(e2)).willReturn(d2);

			// when
			Page<EventResponse> result = eventService.listAll(pageable);

			// then
			assertThat(result.getContent()).containsExactly(d1, d2);
			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(20);

			verify(eventRepository).findAll(pageable);
			verify(eventMapper, times(2)).toEventResponse(any(Event.class));
			verifyNoMoreInteractions(eventRepository, eventMapper);
		}

		@Test
		@DisplayName("should return empty list when no events available")
		void shouldReturnEmptyListWhenNoEventsAvailable() {
			// given
			PageRequest pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "date"));

			Page<Event> entityPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

			given(eventRepository.findAll(any(PageRequest.class))).willReturn(entityPage);

			// when
			Page<EventResponse> result = eventService.listAll(pageable);

			// then
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(20);

			verify(eventRepository).findAll(pageable);
			verifyNoInteractions(eventMapper);
			verifyNoMoreInteractions(eventRepository, eventMapper);
		}

	}

	@Nested
	@DisplayName("Find event tests")
	class FindEventTests {

		@Test
		@DisplayName("should return mapped DTO when event exists")
		void shouldReturnMappedDto_whenEventExists() {
			// given
			long id = 1L;
			Event event = new Event(LocalDate.now(), "Host");
			EventResponse expectedDto = new EventResponse(id, LocalDate.now(), "Host", "Location");

			given(eventRepository.findById(id)).willReturn(Optional.of(event));
			given(eventMapper.toEventResponse(event)).willReturn(expectedDto);

			// when
			EventResponse result = eventService.findById(id);

			// then
			assertThat(result).isEqualTo(expectedDto);
			verify(eventRepository).findById(id);
			verify(eventMapper).toEventResponse(event);
			verifyNoMoreInteractions(eventRepository, eventMapper);
		}

		@Test
		@DisplayName("should throw EventNotFoundException when event does not exist")
		void shouldThrowEventNotFoundException_whenEventDoesNotExist() {
			// given
			long id = 1L;
			given(eventRepository.findById(id)).willReturn(Optional.empty());

			// when/then
			assertThatThrownBy(() -> eventService.findById(id)).isInstanceOf(EventNotFoundException.class)
				.hasMessageContaining("Could not find event " + id);

			verify(eventRepository).findById(id);
			verifyNoMoreInteractions(eventRepository, eventMapper);
		}

	}

	@Nested
	@DisplayName("Create event tests")
	class CreateEventTest {

		@Test
		@DisplayName("should create and return mapped DTO")
		void shouldCreateAndReturnMappedDto() {
			// given
			EventUpsertRequest upsertRequest = new EventUpsertRequest(LocalDate.now(), "Host", "Location");
			Event newEvent = new Event(upsertRequest.date(), upsertRequest.host());
			newEvent.setLocation(upsertRequest.location());
			EventResponse expectedDto = new EventResponse(1L, upsertRequest.date(), upsertRequest.host(),
					upsertRequest.location());

			given(eventMapper.fromEventUpsertRequest(upsertRequest)).willReturn(newEvent);
			given(eventRepository.save(newEvent)).willReturn(newEvent);
			given(eventMapper.toEventResponse(newEvent)).willReturn(expectedDto);

			// when
			EventResponse result = eventService.create(upsertRequest);

			// then
			assertThat(result).isEqualTo(expectedDto);
			verify(eventMapper).fromEventUpsertRequest(upsertRequest);
			verify(eventRepository).save(newEvent);
			verify(eventMapper).toEventResponse(newEvent);
			verifyNoMoreInteractions(eventRepository, eventMapper);
		}

		@Test
		@DisplayName("should throw NullPointerException when upsertRequest is null")
		void shouldThrowNullPointerException_whenUpsertRequestIsNull() {
			// given
			EventUpsertRequest request = null;

			// when/then
			assertThatThrownBy(() -> eventService.create(request)).isInstanceOf(NullPointerException.class)
				.hasMessageContaining("request is marked non-null but is null");

			verifyNoInteractions(eventRepository, eventMapper);
		}

	}

	@Nested
	@DisplayName("Update event tests")
	class Update {

		@Test
		@DisplayName("should update and return mapped DTO when event exists")
		void shouldUpdateAndReturnMappedDto_whenEventExists() {
			// given
			long id = 1L;
			EventUpsertRequest upsertRequest = new EventUpsertRequest(LocalDate.now(), "Updated Host",
					"Updated Location");
			Event existingEvent = new Event(LocalDate.now(), "Original Host");
			Event updatedEvent = new Event(upsertRequest.date(), upsertRequest.host());
			updatedEvent.setLocation(upsertRequest.location());
			EventResponse expectedDto = new EventResponse(id, upsertRequest.date(), upsertRequest.host(),
					upsertRequest.location());

			given(eventRepository.findById(id)).willReturn(Optional.of(existingEvent));
			given(eventMapper.mergeWithEventUpsertRequest(existingEvent, upsertRequest)).willReturn(updatedEvent);
			given(eventRepository.save(updatedEvent)).willReturn(updatedEvent);
			given(eventMapper.toEventResponse(updatedEvent)).willReturn(expectedDto);

			// when
			EventResponse result = eventService.update(id, upsertRequest);

			// then
			assertThat(result).isEqualTo(expectedDto);
			verify(eventRepository).findById(id);
			verify(eventMapper).mergeWithEventUpsertRequest(existingEvent, upsertRequest);
			verify(eventRepository).save(updatedEvent);
			verify(eventMapper).toEventResponse(updatedEvent);
			verifyNoMoreInteractions(eventRepository, eventMapper);
		}

		@Test
		@DisplayName("should throw NullPointerException when upsertRequest is null")
		void shouldThrowNullPointerException_whenUpsertRequestIsNull() {
			// given
			long id = 1L;
			EventUpsertRequest request = null;

			// when/then
			assertThatThrownBy(() -> eventService.update(id, request)).isInstanceOf(NullPointerException.class)
				.hasMessageContaining("request is marked non-null but is null");

			verifyNoInteractions(eventRepository, eventMapper);
		}

		@Test
		@DisplayName("should throw EventNotFoundException when event does not exist")
		void shouldThrowEventNotFoundException_whenEventDoesNotExist() {
			// given
			long id = 1L;
			EventUpsertRequest updateDto = new EventUpsertRequest(LocalDate.now(), "Host", "Location");
			given(eventRepository.findById(id)).willReturn(Optional.empty());

			// when/then
			assertThatThrownBy(() -> eventService.update(id, updateDto)).isInstanceOf(EventNotFoundException.class)
				.hasMessageContaining("Could not find event " + id);

			verify(eventRepository).findById(id);
			verifyNoMoreInteractions(eventRepository, eventMapper);
		}

	}

	@Nested
	@DisplayName("Delete event tests")
	class Delete {

		@Test
		@DisplayName("should delete event by id")
		void shouldDeleteEvent() {
			// given
			long id = 1L;

			// when
			eventService.delete(id);

			// then
			verify(eventRepository).deleteById(id);
			verifyNoMoreInteractions(eventRepository, eventMapper);
		}

	}

}
