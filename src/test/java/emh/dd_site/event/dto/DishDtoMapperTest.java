
package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.TestDishBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DishDtoMapper")
class DishDtoMapperTest {

	private final DishDtoMapper mapper = new DishDtoMapper();

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

			// when
			DishDto result = mapper.toDto(dish);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(dish.getId());
				assertThat(dto.name()).isEqualTo(dish.getName());
				assertThat(dto.mainIngredient()).isEqualTo(dish.getMainIngredient());
			});
		}

		@Test
		@DisplayName("should handle null main ingredient")
		void shouldHandleNullMainIngredient() {
			// given
			Dish dish = TestDishBuilder.aDish().withId(1L).withName("Test Name").withMainIngredient(null).build();

			// when
			DishDto result = mapper.toDto(dish);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(dish.getId());
				assertThat(dto.name()).isEqualTo(dish.getName());
				assertThat(dto.mainIngredient()).isNull();
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
				.withName("Name 1")
				.withMainIngredient("Main Ingredient 1")
				.build();
			Dish dish2 = TestDishBuilder.aDish()
				.withId(2L)
				.withName("Name 2")
				.withMainIngredient("Main Ingredient 2")
				.build();

			List<Dish> dishes = List.of(dish1, dish2);

			// when
			List<DishDto> result = mapper.toDtoList(dishes);

			// then
			assertThat(result).hasSize(2).satisfies(dtos -> {
				// First dish
				assertThat(dtos.get(0)).satisfies(dto -> {
					assertThat(dto.id()).isEqualTo(dish1.getId());
					assertThat(dto.name()).isEqualTo(dish1.getName());
					assertThat(dto.mainIngredient()).isEqualTo(dish1.getMainIngredient());
				});
				// Second dish
				assertThat(dtos.get(1)).satisfies(dto -> {
					assertThat(dto.id()).isEqualTo(dish2.getId());
					assertThat(dto.name()).isEqualTo(dish2.getName());
					assertThat(dto.mainIngredient()).isEqualTo(dish2.getMainIngredient());
				});
			});
		}

		@Test
		@DisplayName("should handle mixed null locations")
		void shouldHandleMixedNullMainIngredients() {
			// given
			Dish dish1 = TestDishBuilder.aDish()
				.withId(1L)
				.withName("Dish 1")
				.withMainIngredient("Main Ingredient 1")
				.build();

			Dish event2 = TestDishBuilder.aDish().withId(2L).withName("Dish 2").withMainIngredient(null).build();

			List<Dish> dishes = List.of(dish1, event2);

			// when
			List<DishDto> result = mapper.toDtoList(dishes);

			// then
			assertThat(result).hasSize(2).satisfies(dtos -> {
				assertThat(dtos.get(0).mainIngredient()).isEqualTo("Main Ingredient 1");
				assertThat(dtos.get(1).mainIngredient()).isNull();
			});
		}

	}

}
