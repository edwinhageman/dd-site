package emh.dd_site.wine.repository;

import emh.dd_site.TestcontainersConfig;
import emh.dd_site.wine.entity.WineStyle;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
public class WineStyleRepositoryIT {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private WineStyleRepository wineStyleRepository;

	private WineStyle style1, style2, style3;

	private EntityManagerFactory entityManagerFactory;

	@BeforeEach
	void setUp() {
		style1 = new WineStyle("Style 1");
		style2 = new WineStyle("Style 2");
		style3 = new WineStyle("Style 3");

		wineStyleRepository.saveAll(List.of(style1, style2, style3));
	}

	@Test
	@DisplayName("Should save and retrieve wine style")
	void shouldSaveAndRetrieve() {
		var result = wineStyleRepository.findById(style1.getId());

		assertThat(result).hasValueSatisfying(style -> assertThat(style.getName()).isEqualTo(style1.getName()));
	}

	@Test
	@DisplayName("Should update wine style")
	void shouldUpdate() {
		style1.setName("Updated name");

		var updated = wineStyleRepository.save(style1);

		assertThat(updated.getName()).isEqualTo("Updated name");
	}

	@Test
	@DisplayName("Should delete style")
	void shouldDelete() {
		wineStyleRepository.deleteById(style1.getId());

		var shouldNotExist = wineStyleRepository.findById(style1.getId());
		assertThat(shouldNotExist).isEmpty();
	}

	@Test
	@DisplayName("Should list all styles")
	void shouldListAllStyles() {
		var result = wineStyleRepository.findAll();

		assertThat(result).hasSize(3);
		assertThat(result).containsExactly(style1, style2, style3);
	}

	@Test
	@DisplayName("Should paginate and sort styles")
	void shouldPaginateAndSortStyles() {
		var result = wineStyleRepository.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "name")));

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getTotalPages()).isEqualTo(2);
		assertThat(result.getContent().get(0).getName()).isEqualTo(style3.getName());
		assertThat(result.getContent().get(1).getName()).isEqualTo(style2.getName());
	}

	@Test
	@DisplayName("When persisting style with existing name should throw DataIntegrityViolationException")
	void whenPersistingStyleWithExistingName_shouldThrowDataIntegrityViolationException() {
		wineStyleRepository.save(new WineStyle("Duplicate name"));
		assertThrows(DataIntegrityViolationException.class,
				// constraint should be case-insensitive
				() -> wineStyleRepository.save(new WineStyle("Duplicate Name")));
	}

}
