package emh.dd_site.event.service;

import emh.dd_site.event.dto.CourseDto;
import emh.dd_site.event.dto.CourseDtoMapper;
import emh.dd_site.event.dto.CreateUpdateCourseDto;
import emh.dd_site.event.dto.CreateUpdateDishDto;
import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.exception.CourseNotFoundException;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.repository.CourseRepository;
import emh.dd_site.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CourseServiceTest {

	@Mock
	private CourseRepository courseRepository;

	@Mock
	private EventRepository eventRepository;

	@Mock
	private CourseDtoMapper courseDtoMapper;

	@InjectMocks
	private CourseService courseService;

	private Pageable pageable;

	@BeforeEach
	void setup() {
		pageable = PageRequest.of(0, 10, Sort.by("courseNo").ascending());
	}

	@Test
	void listAll_shouldReturnMappedPage() {
		// Arrange
		Course course1 = mock(Course.class);
		Course course2 = mock(Course.class);
		Page<Course> coursePage = new PageImpl<>(List.of(course1, course2), pageable, 2);

		CourseDto dto1 = new CourseDto(1L, 1, "Cook A", null);
		CourseDto dto2 = new CourseDto(2L, 2, "Cook B", null);
		Page<CourseDto> dtoPage = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

		when(courseRepository.findAll(pageable)).thenReturn(coursePage);
		when(courseDtoMapper.toDtoPage(coursePage)).thenReturn(dtoPage);

		// Act
		Page<CourseDto> result = courseService.listAll(pageable);

		// Assert
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).containsExactly(dto1, dto2);
		verify(courseRepository).findAll(pageable);
		verify(courseDtoMapper).toDtoPage(coursePage);
	}

	@Test
	void listByEvent_shouldReturnMappedPage() {
		// Arrange
		long eventId = 42L;
		Course course = mock(Course.class);
		Page<Course> coursePage = new PageImpl<>(List.of(course), pageable, 1);

		CourseDto dto = new CourseDto(10L, 1, "Cook X", null);
		Page<CourseDto> dtoPage = new PageImpl<>(List.of(dto), pageable, 1);

		when(courseRepository.findByEventId(eventId, pageable)).thenReturn(coursePage);
		when(courseDtoMapper.toDtoPage(coursePage)).thenReturn(dtoPage);

		// Act
		Page<CourseDto> result = courseService.listByEvent(eventId, pageable);

		// Assert
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).containsExactly(dto);
		verify(courseRepository).findByEventId(eventId, pageable);
		verify(courseDtoMapper).toDtoPage(coursePage);
	}

	@Test
	void findById_shouldReturnMappedDto_whenFound() {
		// Arrange
		long id = 5L;
		Course course = mock(Course.class);
		CourseDto dto = new CourseDto(id, 3, "Cook Y", null);

		when(courseRepository.findById(id)).thenReturn(Optional.of(course));
		when(courseDtoMapper.toDto(course)).thenReturn(dto);

		// Act
		CourseDto result = courseService.findById(id);

		// Assert
		assertThat(result).isEqualTo(dto);
		verify(courseRepository).findById(id);
		verify(courseDtoMapper).toDto(course);
	}

	@Test
	void findById_shouldThrow_whenNotFound() {
		// Arrange
		long id = 999L;
		when(courseRepository.findById(id)).thenReturn(Optional.empty());

		// Act / Assert
		assertThatThrownBy(() -> courseService.findById(id)).isInstanceOf(CourseNotFoundException.class)
			.hasMessageContaining(String.valueOf(id));

		verify(courseRepository).findById(id);
		verifyNoInteractions(courseDtoMapper);
	}

	@Test
	void create_shouldPersistCourseAndReturnDto() {
		// Arrange
		long eventId = 77L;

		// The service now looks up the event first
		Event event = new Event(LocalDate.now(), "Host");
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

		CreateUpdateCourseDto input = new CreateUpdateCourseDto(4, "New Cook",
				new CreateUpdateDishDto("Dish Name", "Ingredient X"));

		// Capture the entity passed to save to verify construction and relationships
		ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);

		// Mock repository save to return a persisted course
		when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
			Course toSave = invocation.getArgument(0);
			Course saved = new Course(event, toSave.getCourseNo(), toSave.getCook());
			saved.setDish(toSave.getDish());
			return saved;
		});

		CourseDto expectedDto = new CourseDto(123L, 4, "New Cook", null);
		when(courseDtoMapper.toDto(any(Course.class))).thenReturn(expectedDto);

		// Act
		CourseDto result = courseService.create(eventId, input);

		// Assert
		assertThat(result).isEqualTo(expectedDto);
		verify(eventRepository).findById(eventId);
		verify(courseRepository).save(courseCaptor.capture());
		Course created = courseCaptor.getValue();
		assertThat(created.getEvent()).isSameAs(event);
		assertThat(created.getCourseNo()).isEqualTo(4);
		assertThat(created.getCook()).isEqualTo("New Cook");
		assertThat(created.getDish()).isNotNull();
		assertThat(created.getDish().getName()).isEqualTo("Dish Name");
		assertThat(created.getDish().getMainIngredient()).isEqualTo("Ingredient X");
		verify(courseDtoMapper).toDto(any(Course.class));
	}

	@Test
	void create_shouldThrow_whenEventNotFound() {
		// Arrange
		long eventId = 404L;
		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		CreateUpdateCourseDto input = new CreateUpdateCourseDto(1, "Cook",
				new CreateUpdateDishDto("Dish", "Ingredient"));

		// Act / Assert
		assertThatThrownBy(() -> courseService.create(eventId, input)).isInstanceOf(EventNotFoundException.class);

		verify(eventRepository).findById(eventId);
		verify(courseRepository, never()).save(any());
		verifyNoInteractions(courseDtoMapper);
	}

	@Test
	void update_shouldModifyExistingCourse_andReturnDto() {
		// Arrange
		long id = 55L;
		Dish existingDish = new Dish("Old Dish");
		existingDish.setMainIngredient("Old Ingredient");
		Course existing = mock(Course.class, RETURNS_DEEP_STUBS);

		when(courseRepository.findById(id)).thenReturn(Optional.of(existing));

		// Mutations expectations on existing entity
		// We'll use doNothing() stubs and verify calls
		doNothing().when(existing).setCourseNo(7);
		doNothing().when(existing).setCook("Updated Cook");
		when(existing.getDish()).thenReturn(existingDish);

		// After save, return the same instance (typical for JPA)
		when(courseRepository.save(existing)).thenReturn(existing);

		CourseDto expectedDto = new CourseDto(id, 7, "Updated Cook", null);
		when(courseDtoMapper.toDto(existing)).thenReturn(expectedDto);

		CreateUpdateCourseDto input = new CreateUpdateCourseDto(7, "Updated Cook",
				new CreateUpdateDishDto("New Dish", "New Ingredient"));

		// Act
		CourseDto result = courseService.update(id, input);

		// Assert
		assertThat(result).isEqualTo(expectedDto);
		verify(courseRepository).findById(id);
		verify(existing).setCourseNo(7);
		verify(existing).setCook("Updated Cook");
		assertThat(existing.getDish().getName()).isEqualTo("New Dish");
		assertThat(existing.getDish().getMainIngredient()).isEqualTo("New Ingredient");
		verify(courseRepository).save(existing);
		verify(courseDtoMapper).toDto(existing);
	}

	@Test
	void update_shouldThrow_whenCourseNotFound() {
		// Arrange
		long id = 404L;
		when(courseRepository.findById(id)).thenReturn(Optional.empty());

		CreateUpdateCourseDto input = new CreateUpdateCourseDto(1, "Cook",
				new CreateUpdateDishDto("Dish", "Ingredient"));

		// Act / Assert
		assertThatThrownBy(() -> courseService.update(id, input)).isInstanceOf(CourseNotFoundException.class);

		verify(courseRepository).findById(id);
		verify(courseRepository, never()).save(any());
		verifyNoInteractions(courseDtoMapper);
	}

	@Test
	void delete_shouldDelegateToRepository() {
		// Arrange
		long id = 11L;

		// Act
		courseService.delete(id);

		// Assert
		verify(courseRepository).deleteById(id);
		verifyNoInteractions(courseDtoMapper);
	}

}