
package emh.dd_site.event.dto;

import emh.dd_site.event.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CourseMapper tests")
class CourseMapperTest {

	private final CourseMapper mapper = new CourseMapper(new EventMapper(), new DishMapper());

	private Event testEvent;

	private Course testCourse;

	private CourseUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		testEvent = TestEventBuilder.anEvent().withId(1L).withHost("Test Host").withLocation("Test Location").build();

		var testDish = TestDishBuilder.aDish()
			.withId(10L)
			.withName("Original Dish")
			.withMainIngredient("Original Main Ingredient")
			.build();

		testCourse = TestCourseBuilder.aCourse()
			.withId(1L)
			.withEvent(testEvent)
			.withCourseNo(1)
			.withCook("Cook name")
			.withDish(testDish)
			.build();

		var testDishRequest = new DishUpsertRequest("Upsert Dish Name", "Upsert Main Ingredient");
		testRequest = new CourseUpsertRequest(999, "Upsert Cook", testDishRequest);
	}

	@Nested
	@DisplayName("Course to CourseResponse mapping tests")
	class ToCourseResponseTests {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toCourseResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			// when
			CourseResponse result = mapper.toCourseResponse(testCourse);

			// then
			assertThat(result).isNotNull().satisfies(courseDto -> {
				assertThat(courseDto.id()).isEqualTo(testCourse.getId());
				assertThat(courseDto.courseNo()).isEqualTo(testCourse.getCourseNo());
				assertThat(courseDto.cook()).isEqualTo(testCourse.getCook());
				assertThat(courseDto.event()).isNotNull().satisfies(eventDto -> {
					assertThat(eventDto.id()).isEqualTo(testCourse.getEvent().getId());
					assertThat(eventDto.date()).isEqualTo(testCourse.getEvent().getDate());
					assertThat(eventDto.host()).isEqualTo(testCourse.getEvent().getHost());
					assertThat(eventDto.location()).isEqualTo(testCourse.getEvent().getLocation());
				});
				assertThat(courseDto.dish()).isNotNull().satisfies(dishDto -> {
					assertThat(dishDto.id()).isEqualTo(testCourse.getDish().getId());
					assertThat(dishDto.name()).isEqualTo(testCourse.getDish().getName());
					assertThat(dishDto.mainIngredient()).isEqualTo(testCourse.getDish().getMainIngredient());
				});
			});
		}

		@Test
		@DisplayName("should handle null dish")
		void shouldHandleNullDish() {
			// given
			testCourse.setDish(null);

			// when
			CourseResponse result = mapper.toCourseResponse(testCourse);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(testCourse.getId());
				assertThat(dto.courseNo()).isEqualTo(testCourse.getCourseNo());
				assertThat(dto.cook()).isEqualTo(testCourse.getCook());
				assertThat(dto.dish()).isNull();
			});
		}

	}

	@Nested
	@DisplayName("Course from CourseUpsertRequest mapping tests")
	class FromCourseUpsertRequestTests {

		@Test
		@DisplayName("should return null when event is null")
		void shouldReturnNullWhenEventIsNull() {
			assertThat(mapper.fromCourseUpsertRequest(null, testRequest)).isNull();
		}

		@Test
		@DisplayName("should return null when request is null")
		void shouldReturnNullWhenRequestIsNull() {
			assertThat(mapper.fromCourseUpsertRequest(testEvent, null)).isNull();
		}

		@Test
		@DisplayName("should return null when event and request are null")
		void shouldReturnNullWhenEventAndRequestAreNull() {
			assertThat(mapper.fromCourseUpsertRequest(null, null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.fromCourseUpsertRequest(testEvent, testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isNull();
				assertThat(course.getCourseNo()).isEqualTo(testRequest.courseNo());
				assertThat(course.getCook()).isEqualTo(testRequest.cook());

				assertThat(course.getDish()).isNotNull().satisfies(dish -> {
					assertThat(dish.getId()).isNull();
					assertThat(dish.getName()).isEqualTo(testRequest.dish().name());
					assertThat(dish.getMainIngredient()).isEqualTo(testRequest.dish().mainIngredient());
				});
			});
		}

		@Test
		@DisplayName("should handle null dish")
		void shouldHandleNullDish() {
			testRequest = new CourseUpsertRequest(1, "cook", null);

			var result = mapper.fromCourseUpsertRequest(testEvent, testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isNull();
				assertThat(course.getCourseNo()).isEqualTo(testRequest.courseNo());
				assertThat(course.getCook()).isEqualTo(testRequest.cook());
				assertThat(course.getDish()).isNull();
			});
		}

	}

	@Nested
	@DisplayName("Course merge with CourseUpsertRequest mapping tests")
	class MergeWithCourseUpsertRequestTests {

		@Test
		@DisplayName("should return null when event is null")
		void shouldReturnNullWhenEventIsNull() {
			assertThat(mapper.mergeWithCourseUpsertRequest(null, testRequest)).isNull();
		}

		@Test
		@DisplayName("should return course when request is null")
		void shouldReturnCourseWhenRequestIsNull() {
			assertThat(mapper.mergeWithCourseUpsertRequest(testCourse, null)).isEqualTo(testCourse);
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.mergeWithCourseUpsertRequest(testCourse, testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isEqualTo(1);
				assertThat(course.getCourseNo()).isEqualTo(testRequest.courseNo());
				assertThat(course.getCook()).isEqualTo(testRequest.cook());

				assertThat(course.getDish()).isNotNull().satisfies(dish -> {
					assertThat(dish.getId()).isEqualTo(10);
					assertThat(dish.getName()).isEqualTo(testRequest.dish().name());
					assertThat(dish.getMainIngredient()).isEqualTo(testRequest.dish().mainIngredient());
				});
			});
		}

		@Test
		@DisplayName("should handle null dish request")
		void shouldHandleNullDishRequest() {
			testRequest = new CourseUpsertRequest(1, "Cook", null);

			var result = mapper.mergeWithCourseUpsertRequest(testCourse, testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isEqualTo(testCourse.getId());
				assertThat(course.getCourseNo()).isEqualTo(testRequest.courseNo());
				assertThat(course.getCook()).isEqualTo(testRequest.cook());
				assertThat(course.getDish()).isNull();
			});
		}

		@Test
		@DisplayName("should handle course null dish")
		void shouldHandleCourseNullDish() {
			testCourse.setDish(null);

			var result = mapper.mergeWithCourseUpsertRequest(testCourse, testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isEqualTo(testCourse.getId());
				assertThat(course.getCourseNo()).isEqualTo(testRequest.courseNo());
				assertThat(course.getCook()).isEqualTo(testRequest.cook());

				assertThat(course.getDish()).isNotNull().satisfies(dish -> {
					assertThat(dish.getId()).isNull();
					assertThat(dish.getName()).isEqualTo(testRequest.dish().name());
					assertThat(dish.getMainIngredient()).isEqualTo(testRequest.dish().mainIngredient());
				});
			});
		}

	}

}
