
package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Event;
import emh.dd_site.event.entity.TestEventBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EventDtoMapper")
class EventDtoMapperTest {

	private final EventDtoMapper mapper = new EventDtoMapper();

	private final LocalDate testDate = LocalDate.of(2024, 1, 1);

	@Nested
	@DisplayName("toDto")
	class ToDto {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toDto(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			// given
			Event event = TestEventBuilder.anEvent()
				.withId(1L)
				.withDate(testDate)
				.withHost("Test Host")
				.withLocation("Test Location")
				.build();

			// when
			EventDto result = mapper.toDto(event);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(event.getId());
				assertThat(dto.date()).isEqualTo(event.getDate());
				assertThat(dto.host()).isEqualTo(event.getHost());
				assertThat(dto.location()).isEqualTo(event.getLocation());
			});
		}

		@Test
		@DisplayName("should handle null location")
		void shouldHandleNullLocation() {
			// given
			Event event = TestEventBuilder.anEvent()
				.withId(1L)
				.withDate(testDate)
				.withHost("Test Host")
				.withLocation(null)
				.build();

			// when
			EventDto result = mapper.toDto(event);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(event.getId());
				assertThat(dto.date()).isEqualTo(event.getDate());
				assertThat(dto.host()).isEqualTo(event.getHost());
				assertThat(dto.location()).isNull();
			});
		}

	}

	@Nested
	@DisplayName("toDtoList")
	class ToDtoList {

		@Test
		@DisplayName("should return empty list when entities is null")
		void shouldReturnEmptyListWhenEntitiesIsNull() {
			assertThat(mapper.toDtoList(null)).isEmpty();
		}

		@Test
		@DisplayName("should return empty list when entities is empty")
		void shouldReturnEmptyListWhenEntitiesIsEmpty() {
			assertThat(mapper.toDtoList(List.of())).isEmpty();
		}

		@Test
		@DisplayName("should map all entities correctly")
		void shouldMapAllEntitiesCorrectly() {
			// given
			Event event1 = TestEventBuilder.anEvent()
				.withId(1L)
				.withDate(testDate)
				.withHost("Host 1")
				.withLocation("Location 1")
				.build();
			Event event2 = TestEventBuilder.anEvent()
				.withId(2L)
				.withDate(testDate.plusDays(1))
				.withHost("Host 2")
				.withLocation("Location 2")
				.build();

			List<Event> events = List.of(event1, event2);

			// when
			List<EventDto> result = mapper.toDtoList(events);

			// then
			assertThat(result).hasSize(2).satisfies(dtos -> {
				// First event
				assertThat(dtos.get(0)).satisfies(dto -> {
					assertThat(dto.id()).isEqualTo(event1.getId());
					assertThat(dto.date()).isEqualTo(event1.getDate());
					assertThat(dto.host()).isEqualTo(event1.getHost());
					assertThat(dto.location()).isEqualTo(event1.getLocation());
				});
				// Second event
				assertThat(dtos.get(1)).satisfies(dto -> {
					assertThat(dto.id()).isEqualTo(event2.getId());
					assertThat(dto.date()).isEqualTo(event2.getDate());
					assertThat(dto.host()).isEqualTo(event2.getHost());
					assertThat(dto.location()).isEqualTo(event2.getLocation());
				});
			});
		}

		@Test
		@DisplayName("should handle mixed null locations")
		void shouldHandleMixedNullLocations() {
			// given
			Event event1 = TestEventBuilder.anEvent()
				.withId(1L)
				.withDate(testDate.plusDays(1))
				.withHost("Host 1")
				.withLocation("Location 1")
				.build();

			Event event2 = TestEventBuilder.anEvent()
				.withId(2L)
				.withDate(testDate.plusDays(2))
				.withHost("Host 2")
				.withLocation(null)
				.build();

			List<Event> events = List.of(event1, event2);

			// when
			List<EventDto> result = mapper.toDtoList(events);

			// then
			assertThat(result).hasSize(2).satisfies(dtos -> {
				assertThat(dtos.get(0).location()).isEqualTo("Location 1");
				assertThat(dtos.get(1).location()).isNull();
			});
		}

	}

}
