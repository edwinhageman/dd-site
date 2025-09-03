package emh.dd_site.event.service;

import emh.dd_site.event.dto.*;
import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.exception.CourseNotFoundException;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.repository.CourseRepository;
import emh.dd_site.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

	@Mock
	private CourseRepository courseRepository;

	@Mock
	private EventRepository eventRepository;

	@Mock
	private CourseMapper courseMapper;

	@InjectMocks
	private CourseService courseService;

	private Pageable pageable;

	private CourseResponse response1, response2;

	@BeforeEach
	void setup() {
		pageable = PageRequest.of(0, 10, Sort.by("courseNo").ascending());
		var event = new EventResponse(1L, LocalDate.now(), "Host", "Location");
		var dish1 = new DishResponse(1L, "Dish 1", "Ingredient 1");
		var dish2 = new DishResponse(2L, "Dish 2", "Ingredient 2");
		response1 = new CourseResponse(1L, event, 1, "Cook A", dish1);
		response2 = new CourseResponse(2L, event, 2, "Cook B", dish2);
	}

	@Nested
	@DisplayName("List courses tests")
	class ListAllCoursesTests {

		@Test
		@DisplayName("should return paged list of mapped DTOs")
		void shouldReturnMappedPage_whenCoursesAvailable() {
			Course course1 = mock(Course.class);
			Course course2 = mock(Course.class);
			Page<Course> coursePage = new PageImpl<>(List.of(course1, course2), pageable, 2);

			given(courseRepository.findAll(pageable)).willReturn(coursePage);
			given(courseMapper.toCourseResponse(course1)).willReturn(response1);
			given(courseMapper.toCourseResponse(course2)).willReturn(response2);

			Page<CourseResponse> result = courseService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getContent()).containsExactly(response1, response2);
		}

		@Test
		@DisplayName("should return empty list when no courses available")
		void shouldReturnEmptyList_whenNoCoursesAvailable() {
			Page<Course> coursesPage = new PageImpl<>(List.of(), pageable, 0);

			given(courseRepository.findAll(pageable)).willReturn(coursesPage);

			var result = courseService.listAll(pageable);

			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(10);

			verify(courseRepository).findAll(pageable);
			verifyNoInteractions(courseMapper);
		}

	}

	@Nested
	@DisplayName("List courses by event tests")
	class ListByEventTests {

		@Test
		@DisplayName("should return paged list of mapped DTOs")
		void shouldReturnMappedPage_whenCoursesAvailableForEvent() {
			long eventId = 42L;
			Course course = mock(Course.class);
			Page<Course> coursePage = new PageImpl<>(List.of(course), pageable, 1);

			given(courseRepository.findByEventId(eventId, pageable)).willReturn(coursePage);
			given(courseMapper.toCourseResponse(course)).willReturn(response1);

			Page<CourseResponse> result = courseService.listByEvent(eventId, pageable);

			assertThat(result.getTotalElements()).isEqualTo(1);
			assertThat(result.getContent()).containsExactly(response1);
		}

		@Test
		@DisplayName("should return empty list when no courses available")
		void shouldReturnEmptyList_whenNoCoursesAvailableForEvent() {
			long eventId = 42L;
			Page<Course> coursesPage = new PageImpl<>(List.of(), pageable, 0);

			given(courseRepository.findByEventId(eventId, pageable)).willReturn(coursesPage);

			var result = courseService.listByEvent(eventId, pageable);

			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(10);

			verifyNoInteractions(courseMapper);
		}

	}

	@Nested
	@DisplayName("Find by id tests")
	class FindByIdTests {

		@Test
		@DisplayName("should return mapped DTO when course exists")
		void shouldReturnMappedDto_whenCourseExists() {
			long id = 5L;
			Course course = mock(Course.class);

			given(courseRepository.findById(id)).willReturn(Optional.of(course));
			given(courseMapper.toCourseResponse(course)).willReturn(response1);

			CourseResponse result = courseService.findById(id);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw CourseNotFoundException when course does not exist")
		void shouldThrowCourseNotFoundException_whenCourseNotFound() {
			long id = 999L;
			given(courseRepository.findById(id)).willReturn(Optional.empty());

			assertThatThrownBy(() -> courseService.findById(id)).isInstanceOf(CourseNotFoundException.class)
				.hasMessageContaining(String.valueOf(id));

			verifyNoInteractions(courseMapper);
		}

	}

	@Nested
	@DisplayName("Create course tests")
	class CreateCourseTest {

		@Test
		@DisplayName("should create and return mapped DTO")
		void shouldCreateAndReturnMappedDto() {
			long eventId = 77L;

			var event = new Event(LocalDate.now(), "Host");
			var course = new Course(event, 4, "New Cook");
			var dish = new Dish("Dish Name");
			course.setDish(dish);

			var request = new CourseUpsertRequest(4, "New Cook", new DishUpsertRequest("Dish Name", "Ingredient X"));

			given(eventRepository.findById(eventId)).willReturn(Optional.of(event));
			given(courseMapper.fromCourseUpsertRequest(event, request)).willReturn(course);
			given(courseRepository.save(course)).willReturn(course);
			given(courseMapper.toCourseResponse(course)).willReturn(response1);

			CourseResponse result = courseService.create(eventId, request);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw NullPointerException when upsertRequest is null")
		void shouldThrowNullPointerException_whenUpsertRequestIsNull() {
			assertThatThrownBy(() -> courseService.create(1L, null)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("should throw EventNotFoundException when event does not exist")
		void shouldThrowEventNotFoundException_whenEventNotFound() {
			long eventId = 404L;
			given(eventRepository.findById(eventId)).willReturn(Optional.empty());

			CourseUpsertRequest input = new CourseUpsertRequest(1, "Cook", new DishUpsertRequest("Dish", "Ingredient"));

			assertThatThrownBy(() -> courseService.create(eventId, input)).isInstanceOf(EventNotFoundException.class);

			verify(courseRepository, never()).save(any());
			verifyNoInteractions(courseMapper);
		}

	}

	@Nested
	@DisplayName("Update course tests")
	class UpdateCourseTest {

		@Test
		@DisplayName("should update and return mapped DTO when course exists")
		void shouldUpdateAndReturnMappedDto_whenCourseExists() {
			long id = 55L;
			Event event = new Event(LocalDate.now(), "Host");
			Dish existingDish = new Dish("Old Dish");
			existingDish.setMainIngredient("Old Ingredient");
			Course existingCourse = new Course(event, 2, "Old Cook");

			Dish updatedDish = new Dish("New Dish");
			updatedDish.setMainIngredient("New Ingredient");
			Course updatedCourse = new Course(event, 1, "New Cook");
			updatedCourse.setDish(updatedDish);

			CourseUpsertRequest request = new CourseUpsertRequest(7, "New Cook",
					new DishUpsertRequest("New Dish", "New Ingredient"));

			given(courseRepository.findById(id)).willReturn(Optional.of(existingCourse));
			given(courseMapper.mergeWithCourseUpsertRequest(existingCourse, request)).willReturn(updatedCourse);
			given(courseRepository.save(updatedCourse)).willReturn(updatedCourse);
			given(courseMapper.toCourseResponse(updatedCourse)).willReturn(response1);

			CourseResponse result = courseService.update(id, request);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw NullPointerException when upsertRequest is null")
		void shouldThrowNullPointerException_whenUpsertRequestIsNull() {
			assertThatThrownBy(() -> courseService.update(1L, null)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("should throw CourseNotFoundException when course does not exist")
		void shouldThrowCourseNotFoundException_whenCourseNotFound() {
			long id = 404L;
			given(courseRepository.findById(id)).willReturn(Optional.empty());

			CourseUpsertRequest input = new CourseUpsertRequest(1, "Cook", new DishUpsertRequest("Dish", "Ingredient"));

			assertThatThrownBy(() -> courseService.update(id, input)).isInstanceOf(CourseNotFoundException.class);

			verify(courseRepository, never()).save(any());
			verifyNoInteractions(courseMapper);
		}

	}

	@Nested
	@DisplayName("Delete course tests")
	class DeleteTests {

		@Test
		void shouldDeleteCourse() {
			long id = 11L;

			courseService.delete(id);

			verify(courseRepository).deleteById(id);
			verifyNoInteractions(courseMapper);
		}

	}

}