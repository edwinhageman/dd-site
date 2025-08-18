
package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.TestCourseBuilder;
import emh.dd_site.event.entity.TestDishBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CourseDtoMapper")
class CourseDtoMapperTest {

	private final CourseDtoMapper mapper = new CourseDtoMapper(new DishDtoMapper());

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
			Dish dish = TestDishBuilder.aDish()
				.withId(1L)
				.withName("Test Dish")
				.withMainIngredient("Test Main Ingredient")
				.build();
			Course course = TestCourseBuilder.aCourse()
				.withId(1L)
				.withCourseNo(1)
				.withCook("Cook name")
				.withDish(dish)
				.build();

			// when
			CourseDto result = mapper.toDto(course);

			// then
			assertThat(result).isNotNull().satisfies(courseDto -> {
				assertThat(courseDto.id()).isEqualTo(course.getId());
				assertThat(courseDto.courseNo()).isEqualTo(course.getCourseNo());
				assertThat(courseDto.cook()).isEqualTo(course.getCook());
				assertThat(courseDto.dish()).isNotNull().satisfies(dishDto -> {
					assertThat(dishDto.id()).isEqualTo(dish.getId());
					assertThat(dishDto.name()).isEqualTo(dish.getName());
					assertThat(dishDto.mainIngredient()).isEqualTo(dish.getMainIngredient());
				});
			});
		}

		@Test
		@DisplayName("should handle null dish")
		void shouldHandleNullDish() {
			// given
			Course course = TestCourseBuilder.aCourse()
				.withId(1L)
				.withCourseNo(1)
				.withCook("Test Cook")
				.withDish(null)
				.build();

			// when
			CourseDto result = mapper.toDto(course);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(course.getId());
				assertThat(dto.courseNo()).isEqualTo(course.getCourseNo());
				assertThat(dto.cook()).isEqualTo(course.getCook());
				assertThat(dto.dish()).isNull();
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
			Dish dish1 = TestDishBuilder.aDish()
				.withId(1L)
				.withName("Dish 1")
				.withMainIngredient("Main Ingredient 1")
				.build();
			Course course1 = TestCourseBuilder.aCourse()
				.withId(1L)
				.withCourseNo(1)
				.withCook("Cook 1")
				.withDish(dish1)
				.build();
			Course course2 = TestCourseBuilder.aCourse()
				.withId(2L)
				.withCourseNo(2)
				.withCook("Cook 2")
				.withDish(null)
				.build();

			List<Course> courses = List.of(course1, course2);

			// when
			List<CourseDto> result = mapper.toDtoList(courses);

			// then
			assertThat(result).hasSize(2).satisfies(dtos -> {
				// First course
				assertThat(dtos.get(0)).satisfies(courseDto -> {
					assertThat(courseDto.id()).isEqualTo(course1.getId());
					assertThat(courseDto.courseNo()).isEqualTo(course1.getCourseNo());
					assertThat(courseDto.cook()).isEqualTo(course1.getCook());
					assertThat(courseDto.dish()).isNotNull().satisfies(dishDto -> {
						assertThat(dishDto.id()).isEqualTo(dish1.getId());
						assertThat(dishDto.name()).isEqualTo(dish1.getName());
						assertThat(dishDto.mainIngredient()).isEqualTo(dish1.getMainIngredient());
					});
				});
				// Second course
				assertThat(dtos.get(1)).satisfies(dto -> {
					assertThat(dto.id()).isEqualTo(course2.getId());
					assertThat(dto.courseNo()).isEqualTo(course2.getCourseNo());
					assertThat(dto.cook()).isEqualTo(course2.getCook());
					assertThat(dto.dish()).isNull();
				});
			});
		}

	}

}
