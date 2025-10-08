package emh.dd_site.wine.repository;

import emh.dd_site.TestcontainersConfig;
import emh.dd_site.wine.entity.Grape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Testcontainers
@Import(TestcontainersConfig.class)
public class GrapeRepositoryIT {

	@Autowired
	private GrapeRepository grapeRepository;

	private Grape grape1, grape2, grape3;

	@BeforeEach
	void setUp() {
		grape1 = new Grape("Grape 1");
		grape2 = new Grape("Grape 2");
		grape3 = new Grape("Grape 3");

		grapeRepository.saveAll(List.of(grape1, grape2, grape3));
	}

	@Test
	@DisplayName("Should save and retrieve grape")
	void shouldSaveAndRetrieve() {
		var result = grapeRepository.findById(grape1.getId());

		assertThat(result).hasValueSatisfying(grape -> assertThat(grape.getName()).isEqualTo(grape1.getName()));
	}

	@Test
	@DisplayName("Should update grape")
	void shouldUpdate() {
		grape1.setName("Updated name");

		var updated = grapeRepository.save(grape1);

		assertThat(updated.getName()).isEqualTo("Updated name");
	}

	@Test
	@DisplayName("Should delete grape")
	void shouldDelete() {
		grapeRepository.deleteById(grape1.getId());

		var shouldNotExist = grapeRepository.findById(grape1.getId());
		assertThat(shouldNotExist).isEmpty();
	}

	@Test
	@DisplayName("Should list all grapes")
	void shouldListAllStyles() {
		var result = grapeRepository.findAll();

		assertThat(result).hasSize(3);
		assertThat(result).containsExactly(grape1, grape2, grape3);
	}

	@Test
	@DisplayName("Should paginate and sort grapes")
	void shouldPaginateAndSortStyles() {
		var result = grapeRepository.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "name")));

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getTotalPages()).isEqualTo(2);
		assertThat(result.getContent().get(0).getName()).isEqualTo(grape3.getName());
		assertThat(result.getContent().get(1).getName()).isEqualTo(grape2.getName());
	}

	@Test
	@DisplayName("When persisting grape with existing name should throw DataIntegrityViolationException")
	void whenPersistingStyleWithExistingName_shouldThrowDataIntegrityViolationException() {
		grapeRepository.save(new Grape("Duplicate name"));
		assertThrows(DataIntegrityViolationException.class,
				// constraint should be case-insensitive
				() -> grapeRepository.save(new Grape("Duplicate Name")));
	}

}
