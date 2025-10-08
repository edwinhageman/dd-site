
package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.TestDishBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DishMapper tests")
class DishMapperTest {

	private final DishMapper mapper = new DishMapper();

	private Dish testDish;

	private DishUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		testDish = TestDishBuilder.builder()
			.withId(1L)
			.withName("Original Dish")
			.withMainIngredient("Original Main Ingredient")
			.build();
		testRequest = new DishUpsertRequest("Upsert Dish", "Upsert Main Ingredient");
	}

	@Nested
	@DisplayName("Dish to DishResponse mapping tests")
	class ToDishResponseTests {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toDishResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			// when
			DishResponse result = mapper.toDishResponse(testDish);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(testDish.getId());
				assertThat(dto.name()).isEqualTo(testDish.getName());
				assertThat(dto.mainIngredient()).isEqualTo(testDish.getMainIngredient());
			});
		}

		@Test
		@DisplayName("should handle null main ingredient")
		void shouldHandleNullMainIngredient() {
			// given
			testDish.setMainIngredient(null);

			// when
			DishResponse result = mapper.toDishResponse(testDish);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(testDish.getId());
				assertThat(dto.name()).isEqualTo(testDish.getName());
				assertThat(dto.mainIngredient()).isNull();
			});
		}

	}

	@Nested
	@DisplayName("Dish from DishUpsertRequest mapping tests")
	class FromDishUpsertRequestTests {

		@Test
		@DisplayName("should return null when request is null")
		void shouldReturnNullWhenRequestIsNull() {
			assertThat(mapper.fromDishUpsertRequest(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.fromDishUpsertRequest(testRequest);

			assertThat(result).isNotNull().satisfies(dish -> {
				assertThat(dish.getId()).isNull();
				assertThat(dish.getName()).isEqualTo(testRequest.name());
				assertThat(dish.getMainIngredient()).isEqualTo(testRequest.mainIngredient());
			});
		}

		@Test
		@DisplayName("should handle null main ingredient")
		void shouldHandleNullMainIngredient() {
			testRequest = new DishUpsertRequest("Dish Name", null);

			var result = mapper.fromDishUpsertRequest(testRequest);

			assertThat(result).isNotNull().satisfies(dish -> {
				assertThat(dish.getId()).isNull();
				assertThat(dish.getName()).isEqualTo(testRequest.name());
				assertThat(dish.getMainIngredient()).isNull();
			});
		}

	}

	@Nested
	@DisplayName("Dish merge with DishUpsertRequest mapping tests")
	class MergeWithDishUpsertRequestTests {

		@Test
		@DisplayName("should return null when dish is null")
		void shouldReturnNullWhenDishIsNull() {
			assertThat(mapper.mergeWithDishUpsertRequest(null, testRequest)).isNull();
		}

		@Test
		@DisplayName("should return dish when request is null")
		void shouldReturnDishWhenRequestIsNull() {
			assertThat(mapper.mergeWithDishUpsertRequest(testDish, null)).isEqualTo(testDish);
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.mergeWithDishUpsertRequest(testDish, testRequest);

			assertThat(result).isNotNull().satisfies(dish -> {
				assertThat(dish.getId()).isEqualTo(testDish.getId());
				assertThat(dish.getName()).isEqualTo(testRequest.name());
				assertThat(dish.getMainIngredient()).isEqualTo(testRequest.mainIngredient());
			});
		}

		@Test
		@DisplayName("should handle null main ingredient")
		void shouldHandleNullMainIngredient() {
			testRequest = new DishUpsertRequest("Dish Name", null);

			var result = mapper.mergeWithDishUpsertRequest(testDish, testRequest);

			assertThat(result).isNotNull().satisfies(dish -> {
				assertThat(dish.getId()).isEqualTo(testDish.getId());
				assertThat(dish.getName()).isEqualTo(testRequest.name());
				assertThat(dish.getMainIngredient()).isNull();
			});
		}

	}

}
