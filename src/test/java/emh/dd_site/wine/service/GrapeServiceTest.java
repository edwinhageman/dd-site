package emh.dd_site.wine.service;

import emh.dd_site.wine.dto.GrapeMapper;
import emh.dd_site.wine.dto.GrapeResponse;
import emh.dd_site.wine.dto.GrapeUpsertRequest;
import emh.dd_site.wine.entity.Grape;
import emh.dd_site.wine.entity.TestGrapeBuilder;
import emh.dd_site.wine.exception.GrapeNotFoundException;
import emh.dd_site.wine.repository.GrapeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GrapeService Unit Tests")
public class GrapeServiceTest {

	@Mock
	private GrapeRepository grapeRepository;

	@Mock
	private GrapeMapper grapeMapper;

	@InjectMocks
	private GrapeService grapeService;

	private Grape testGrape1, testGrape2;

	private Pageable pageable;

	private GrapeUpsertRequest request;

	private GrapeResponse response1, response2;

	@BeforeEach
	void setUp() {
		testGrape1 = TestGrapeBuilder.builder().withId(1).withName("Grape 1").build();
		testGrape2 = TestGrapeBuilder.builder().withId(2).withName("Grape 2").build();
		pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
		request = new GrapeUpsertRequest("Upsert Grape 1");
		response1 = new GrapeResponse(1, "Grape 1");
		response2 = new GrapeResponse(2, "Grape 2");
	}

	@Nested
	@DisplayName("List tests")
	class ListAllGrapesTests {

		@Test
		@DisplayName("should load styles and return paged list of mapped DTOs")
		void shouldLoadGrapesAndReturnMappedDtoList_whenGrapesAvailable() {
			var page = new PageImpl<>(List.of(testGrape1, testGrape2), pageable, 2);

			given(grapeRepository.findAll(pageable)).willReturn(page);
			given(grapeMapper.toGrapeResponse(testGrape1)).willReturn(response1);
			given(grapeMapper.toGrapeResponse(testGrape2)).willReturn(response2);

			var result = grapeService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getContent()).containsExactly(response1, response2);
		}

		@Test
		@DisplayName("should load styles return empty page when no styles available")
		void shouldLoadGrapesReturnEmptyPage_whenNoGrapesAvailable() {
			var page = Page.<Grape>empty(pageable);

			given(grapeRepository.findAll(pageable)).willReturn(page);

			var result = grapeService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(10);

			verifyNoMoreInteractions(grapeRepository);
			verifyNoInteractions(grapeMapper);
		}

	}

	@Nested
	@DisplayName("Find by id tests")
	class FindByIdTests {

		@Test
		@DisplayName("should load style return mapped DTO when style exists")
		void shouldLoadGrapeReturnMappedDto_whenGrapeExists() {
			var id = 1;

			given(grapeRepository.findById(id)).willReturn(Optional.of(testGrape1));
			given(grapeMapper.toGrapeResponse(testGrape1)).willReturn(response1);

			var result = grapeService.findById(id);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw GrapeNotFoundException when style does not exist")
		void shouldThrowNotFoundException_whenGrapeDoesNotExist() {
			var id = 999;

			given(grapeRepository.findById(id)).willReturn(Optional.empty());

			assertThatThrownBy(() -> grapeService.findById(id)).isInstanceOf(GrapeNotFoundException.class)
				.hasMessageContaining(String.valueOf(id));

			verifyNoInteractions(grapeMapper);
		}

	}

	@Nested
	@DisplayName("Create tests")
	class CreateTests {

		@Test
		@DisplayName("should save style and return mapped DTO")
		void shouldSaveGrapeAndReturnMappedDto() {
			given(grapeMapper.fromGrapeUpsertRequest(request)).willReturn(testGrape1);
			given(grapeRepository.save(testGrape1)).willReturn(testGrape1);
			given(grapeMapper.toGrapeResponse(testGrape1)).willReturn(response1);

			var result = grapeService.create(request);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw NullPointException when upsertRequest is null")
		void shouldThrowNullPointeException_whenUpsertRequestIsNull() {
			assertThatThrownBy(() -> grapeService.create(null)).isInstanceOf(NullPointerException.class);
		}

	}

	@Nested
	@DisplayName("Update tests")
	class UpdateTests {

		@Test
		@DisplayName("should load and save style and return mapped DTO when style exists")
		void shouldLoadAndSaveGrapeAndReturnMappedDto_whenGrapeExists() {
			var id = 1;

			given(grapeRepository.findById(id)).willReturn(Optional.of(testGrape1));
			given(grapeMapper.mergeWithGrapeUpsertRequest(testGrape1, request)).willReturn(testGrape1);
			given(grapeRepository.save(testGrape1)).willReturn(testGrape1);
			given(grapeMapper.toGrapeResponse(testGrape1)).willReturn(response1);

			var result = grapeService.update(id, request);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw NullPointException when upsertRequest is null")
		void shouldThrowNullPointeException_whenUpsertRequestIsNull() {
			assertThatThrownBy(() -> grapeService.update(1, null)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("should throw GrapeNotFoundException when style does not exist")
		void shouldThrowNotFoundException_whenGrapeDoesNotExist() {
			var id = 404;

			given(grapeRepository.findById(id)).willReturn(Optional.empty());

			assertThatThrownBy(() -> grapeService.update(id, request)).isInstanceOf(GrapeNotFoundException.class)
				.hasMessageContaining(String.valueOf(id));
			verify(grapeRepository, never()).save(any());
			verifyNoInteractions(grapeMapper);
		}

	}

	@Nested
	@DisplayName("Delete tests")
	class DeleteTests {

		@Test
		public void shouldCallRepositoryDelete() {
			var id = 11;

			grapeService.delete(id);

			verify(grapeRepository).deleteById(id);
			verifyNoInteractions(grapeMapper);
		}

	}

}
