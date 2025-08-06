
package emh.dd_site.event.service;

import emh.dd_site.event.dto.CreateUpdateEventDto;
import emh.dd_site.event.dto.EventDto;
import emh.dd_site.event.dto.EventDtoMapper;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.repository.EventRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private EventDtoMapper eventDtoMapper;

	@InjectMocks
	private EventService eventService;

	@Nested
	class ListAll {

		@Test
		void shouldReturnMappedDtoList() {
			// given
			PageRequest pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "date"));

			Event e1 = new Event(LocalDate.now(), "Host 1");
			e1.setLocation("Location 1");
			Event e2 = new Event(LocalDate.now(), "Host 2");
			e2.setLocation("Location 2");

			Page<Event> entityPage = new PageImpl<>(List.of(e1, e2), pageable, 2);

			EventDto d1 = new EventDto(1L, e1.getDate(), e1.getHost(), e1.getLocation());
			EventDto d2 = new EventDto(2L, e2.getDate(), e2.getHost(), e2.getLocation());
			Page<EventDto> dtoPage = new PageImpl<>(List.of(d1, d2), pageable, 2);

			given(eventRepository.findAll(any(PageRequest.class))).willReturn(entityPage);
			given(eventDtoMapper.toDtoPage(entityPage)).willReturn(dtoPage);

			// when
			Page<EventDto> result = eventService.listAll(pageable);

			// then
			assertThat(result.getContent()).containsExactly(d1, d2);
			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(20);

			verify(eventRepository).findAll(pageable);
			verify(eventDtoMapper).toDtoPage(entityPage);
			verifyNoMoreInteractions(eventRepository, eventDtoMapper);
		}

	}

	@Nested
	class FindById {

		@Test
		void shouldReturnMappedDto_whenEventExists() {
			// given
			long id = 1L;
			Event event = new Event(LocalDate.now(), "Host");
			EventDto expectedDto = new EventDto(id, LocalDate.now(), "Host", "Location");

			given(eventRepository.findById(id)).willReturn(Optional.of(event));
			given(eventDtoMapper.toDto(event)).willReturn(expectedDto);

			// when
			EventDto result = eventService.findById(id);

			// then
			assertThat(result).isEqualTo(expectedDto);
			verify(eventRepository).findById(id);
			verify(eventDtoMapper).toDto(event);
			verifyNoMoreInteractions(eventRepository, eventDtoMapper);
		}

		@Test
		void shouldThrowEventNotFoundException_whenEventDoesNotExist() {
			// given
			long id = 1L;
			given(eventRepository.findById(id)).willReturn(Optional.empty());

			// when/then
			assertThatThrownBy(() -> eventService.findById(id)).isInstanceOf(EventNotFoundException.class)
				.hasMessageContaining("Could not find event " + id);

			verify(eventRepository).findById(id);
			verifyNoMoreInteractions(eventRepository, eventDtoMapper);
		}

	}

	@Nested
	class Create {

		@Test
		void shouldCreateAndReturnMappedDto() {
			// given
			CreateUpdateEventDto createDto = new CreateUpdateEventDto(LocalDate.now(), "Host", "Location");
			Event savedEvent = new Event(createDto.date(), createDto.host());
			savedEvent.setLocation(createDto.location());
			EventDto expectedDto = new EventDto(1L, createDto.date(), createDto.host(), createDto.location());

			given(eventRepository.save(any(Event.class))).willReturn(savedEvent);
			given(eventDtoMapper.toDto(savedEvent)).willReturn(expectedDto);

			// when
			EventDto result = eventService.create(createDto);

			// then
			assertThat(result).isEqualTo(expectedDto);
			verify(eventRepository).save(any(Event.class));
			verify(eventDtoMapper).toDto(savedEvent);
			verifyNoMoreInteractions(eventRepository, eventDtoMapper);
		}

	}

	@Nested
	class Update {

		@Test
		void shouldUpdateAndReturnMappedDto_whenEventExists() {
			// given
			long id = 1L;
			CreateUpdateEventDto updateDto = new CreateUpdateEventDto(LocalDate.now(), "Updated Host",
					"Updated Location");
			Event existingEvent = new Event(LocalDate.now(), "Original Host");
			Event updatedEvent = new Event(updateDto.date(), updateDto.host());
			updatedEvent.setLocation(updateDto.location());
			EventDto expectedDto = new EventDto(id, updateDto.date(), updateDto.host(), updateDto.location());

			given(eventRepository.findById(id)).willReturn(Optional.of(existingEvent));
			given(eventRepository.save(any(Event.class))).willReturn(updatedEvent);
			given(eventDtoMapper.toDto(updatedEvent)).willReturn(expectedDto);

			// when
			EventDto result = eventService.update(id, updateDto);

			// then
			assertThat(result).isEqualTo(expectedDto);
			verify(eventRepository).findById(id);
			verify(eventRepository).save(any(Event.class));
			verify(eventDtoMapper).toDto(updatedEvent);
			verifyNoMoreInteractions(eventRepository, eventDtoMapper);
		}

		@Test
		void shouldThrowEventNotFoundException_whenEventDoesNotExist() {
			// given
			long id = 1L;
			CreateUpdateEventDto updateDto = new CreateUpdateEventDto(LocalDate.now(), "Host", "Location");
			given(eventRepository.findById(id)).willReturn(Optional.empty());

			// when/then
			assertThatThrownBy(() -> eventService.update(id, updateDto)).isInstanceOf(EventNotFoundException.class)
				.hasMessageContaining("Could not find event " + id);

			verify(eventRepository).findById(id);
			verifyNoMoreInteractions(eventRepository, eventDtoMapper);
		}

	}

	@Nested
	class Delete {

		@Test
		void shouldDeleteEvent() {
			// given
			long id = 1L;

			// when
			eventService.delete(id);

			// then
			verify(eventRepository).deleteById(id);
			verifyNoMoreInteractions(eventRepository, eventDtoMapper);
		}

	}

}
