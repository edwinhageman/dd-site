package emh.dd_site.event.repository;

import emh.dd_site.TestcontainersConfig;
import emh.dd_site.event.entity.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@Import(TestcontainersConfig.class)
class EventRepositoryIT {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private EventRepository eventRepository;

	@Test
	void whenSaveEvent_thenEventIsPersisted() {
		// Given
		Event event = new Event(LocalDate.now(), "Test Host");
		event.setLocation("Test Location");

		// When
		Event savedEvent = entityManager.persistAndFlush(event);

		// Then
		assertThat(savedEvent.getId()).isNotNull();
		assertThat(savedEvent.getHost()).isEqualTo("Test Host");
		assertThat(savedEvent.getLocation()).isEqualTo("Test Location");
	}

	@Test
	void whenFindById_thenReturnEvent() {
		// Given
		Event event = new Event(LocalDate.now(), "Test Host");
		event.setLocation("Test Location");
		Event savedEvent = entityManager.persistAndFlush(event);

		// When
		Optional<Event> foundEvent = eventRepository.findById(savedEvent.getId());

		// Then
		assertThat(foundEvent).isPresent().hasValueSatisfying(e -> {
			assertThat(e.getHost()).isEqualTo("Test Host");
			assertThat(e.getLocation()).isEqualTo("Test Location");
			assertThat(e.getDate()).isEqualTo(LocalDate.now());
		});
	}

	@Test
	void whenFindAllWithPagination_thenReturnEventPage() {
		// Given
		Event event1 = new Event(LocalDate.now(), "Host 1");
		Event event2 = new Event(LocalDate.now().plusDays(1), "Host 2");
		Event event3 = new Event(LocalDate.now().plusDays(2), "Host 3");

		entityManager.persist(event1);
		entityManager.persist(event2);
		entityManager.persist(event3);
		entityManager.flush();

		// When
		Page<Event> eventPage = eventRepository.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "date")));

		// Then
		assertThat(eventPage.getTotalElements()).isEqualTo(3);
		assertThat(eventPage.getTotalPages()).isEqualTo(2);
		assertThat(eventPage.getContent()).hasSize(2).extracting(Event::getHost).containsExactly("Host 1", "Host 2");
	}

	@Test
	void whenDeleteEvent_thenEventIsRemoved() {
		// Given
		Event event = new Event(LocalDate.now(), "Test Host");
		Event savedEvent = entityManager.persistAndFlush(event);

		// When
		eventRepository.deleteById(savedEvent.getId());
		entityManager.flush();

		// Then
		Event foundEvent = entityManager.find(Event.class, savedEvent.getId());
		assertThat(foundEvent).isNull();
	}

	@Test
	void whenUpdateEvent_thenEventIsUpdated() {
		// Given
		Event event = new Event(LocalDate.now(), "Original Host");
		Event savedEvent = entityManager.persistAndFlush(event);

		// When
		savedEvent.setHost("Updated Host");
		savedEvent.setLocation("Updated Location");
		eventRepository.save(savedEvent);
		entityManager.flush();

		// Then
		Event updatedEvent = entityManager.find(Event.class, savedEvent.getId());
		assertThat(updatedEvent.getHost()).isEqualTo("Updated Host");
		assertThat(updatedEvent.getLocation()).isEqualTo("Updated Location");
	}

}