
package emh.dd_site.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseMapper")
class BaseMapperTest {

	// Simple test entity and DTO classes for testing
	record TestEntity(String value) {
	}

	record TestDto(String value) {
	}

	// Concrete implementation of BaseMapper for testing
	static class TestMapper implements BaseMapper<TestDto, TestEntity> {

		@Override
		public TestDto toDto(TestEntity entity) {
			if (entity == null) {
				return null;
			}
			return new TestDto(entity.value());
		}

	}

	private final TestMapper mapper = new TestMapper();

	@Nested
	@DisplayName("toDtoList")
	class ToDtoList {

		@Test
		@DisplayName("should return empty list when collection is null")
		void shouldReturnEmptyListWhenCollectionIsNull() {
			assertThat(mapper.toDtoList(null)).isEmpty();
		}

		@Test
		@DisplayName("should return empty list when collection is empty")
		void shouldReturnEmptyListWhenCollectionIsEmpty() {
			assertThat(mapper.toDtoList(List.of())).isEmpty();
		}

		@Test
		@DisplayName("should map all entities in collection")
		void shouldMapAllEntitiesInCollection() {
			// given
			List<TestEntity> entities = List.of(new TestEntity("value1"), new TestEntity("value2"),
					new TestEntity("value3"));

			// when
			List<TestDto> result = mapper.toDtoList(entities);

			// then
			assertThat(result).hasSize(3).extracting(TestDto::value).containsExactly("value1", "value2", "value3");
		}

		@Test
		@DisplayName("should handle collection with null elements")
		void shouldHandleCollectionWithNullElements() {
			// given
			List<TestEntity> entities = new ArrayList<>();
			entities.add(new TestEntity("value1"));
			entities.add(null);
			entities.add(new TestEntity("value3"));

			// when
			List<TestDto> result = mapper.toDtoList(entities);

			// then
			assertThat(result).hasSize(3).satisfies(dtos -> {
				assertThat(dtos.get(0)).isNotNull().extracting(TestDto::value).isEqualTo("value1");
				assertThat(dtos.get(1)).isNull();
				assertThat(dtos.get(2)).isNotNull().extracting(TestDto::value).isEqualTo("value3");
			});
		}

		@Test
		@DisplayName("should maintain order of elements")
		void shouldMaintainOrderOfElements() {
			// given
			List<TestEntity> entities = List.of(new TestEntity("first"), new TestEntity("second"),
					new TestEntity("third"));

			// when
			List<TestDto> result = mapper.toDtoList(entities);

			// then
			assertThat(result).extracting(TestDto::value).containsExactly("first", "second", "third");
		}

		@Test
		@DisplayName("should handle different collection types")
		void shouldHandleDifferentCollectionTypes() {
			// Test with different collection types
			Collection<TestEntity> arrayList = new ArrayList<>(List.of(new TestEntity("test")));
			Collection<TestEntity> set = Set.of(new TestEntity("test"));

			assertThat(mapper.toDtoList(arrayList)).hasSize(1);
			assertThat(mapper.toDtoList(set)).hasSize(1);
		}

		@Test
		@DisplayName("should create new list instance for each call")
		void shouldCreateNewListInstanceForEachCall() {
			// given
			List<TestEntity> entities = List.of(new TestEntity("test"));

			// when
			List<TestDto> result1 = mapper.toDtoList(entities);
			List<TestDto> result2 = mapper.toDtoList(entities);

			// then
			assertThat(result1).isNotSameAs(result2).isEqualTo(result2);
		}

	}

	@Nested
	@DisplayName("toDto")
	class ToDto {

		@Test
		@DisplayName("should map single entity correctly")
		void shouldMapSingleEntityCorrectly() {
			// given
			TestEntity entity = new TestEntity("test");

			// when
			TestDto result = mapper.toDto(entity);

			// then
			assertThat(result).isNotNull().extracting(TestDto::value).isEqualTo("test");
		}

		@Test
		@DisplayName("should handle null entity")
		void shouldHandleNullEntity() {
			assertThat(mapper.toDto(null)).isNull();
		}

	}

}
