package emh.dd_site.wine.service;

import emh.dd_site.wine.dto.WineStyleMapper;
import emh.dd_site.wine.dto.WineStyleResponse;
import emh.dd_site.wine.dto.WineStyleUpsertRequest;
import emh.dd_site.wine.entity.TestWineStyleBuilder;
import emh.dd_site.wine.entity.WineStyle;
import emh.dd_site.wine.exception.WineStyleNotFoundException;
import emh.dd_site.wine.repository.WineStyleRepository;
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
@DisplayName("WineStyleService Unit Tests")
public class WineStyleServiceTest {

	@Mock
	private WineStyleRepository wineStyleRepository;

	@Mock
	private WineStyleMapper wineStyleMapper;

	@InjectMocks
	private WineStyleService wineStyleService;

	private WineStyle testStyle1, testStyle2;

	private Pageable pageable;

	private WineStyleUpsertRequest request;

	private WineStyleResponse response1, response2;

	@BeforeEach
	void setUp() {
		testStyle1 = TestWineStyleBuilder.builder().withId(1).withName("Style 1").build();
		testStyle2 = TestWineStyleBuilder.builder().withId(2).withName("Style 2").build();
		pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
		request = new WineStyleUpsertRequest("Upsert Style 1");
		response1 = new WineStyleResponse(1, "Style 1");
		response2 = new WineStyleResponse(2, "Style 2");
	}

	@Nested
	@DisplayName("List tests")
	class ListAllStylesTests {

		@Test
		@DisplayName("should load styles and return paged list of mapped DTOs")
		void shouldLoadStylesAndReturnMappedDtoList_whenStylesAvailable() {
			var page = new PageImpl<>(List.of(testStyle1, testStyle2), pageable, 2);

			given(wineStyleRepository.findAll(pageable)).willReturn(page);
			given(wineStyleMapper.toWineStyleResponse(testStyle1)).willReturn(response1);
			given(wineStyleMapper.toWineStyleResponse(testStyle2)).willReturn(response2);

			var result = wineStyleService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getContent()).containsExactly(response1, response2);
		}

		@Test
		@DisplayName("should load styles return empty page when no styles available")
		void shouldLoadStylesReturnEmptyPage_whenNoStylesAvailable() {
			var page = Page.<WineStyle>empty(pageable);

			given(wineStyleRepository.findAll(pageable)).willReturn(page);

			var result = wineStyleService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(10);

			verifyNoMoreInteractions(wineStyleRepository);
			verifyNoInteractions(wineStyleMapper);
		}

	}

	@Nested
	@DisplayName("Find by id tests")
	class FindByIdTests {

		@Test
		@DisplayName("should load style return mapped DTO when style exists")
		void shouldLoadStyleReturnMappedDto_whenStyleExists() {
			var id = 1;

			given(wineStyleRepository.findById(id)).willReturn(Optional.of(testStyle1));
			given(wineStyleMapper.toWineStyleResponse(testStyle1)).willReturn(response1);

			var result = wineStyleService.findById(id);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw WineStyleNotFoundException when style does not exist")
		void shouldThrowNotFoundException_whenStyleDoesNotExist() {
			var id = 999;

			given(wineStyleRepository.findById(id)).willReturn(Optional.empty());

			assertThatThrownBy(() -> wineStyleService.findById(id)).isInstanceOf(WineStyleNotFoundException.class)
				.hasMessageContaining(String.valueOf(id));

			verifyNoInteractions(wineStyleMapper);
		}

	}

	@Nested
	@DisplayName("Create tests")
	class CreateTests {

		@Test
		@DisplayName("should save style and return mapped DTO")
		void shouldSaveStyleAndReturnMappedDto() {
			given(wineStyleMapper.fromWineStyleUpsertRequest(request)).willReturn(testStyle1);
			given(wineStyleRepository.save(testStyle1)).willReturn(testStyle1);
			given(wineStyleMapper.toWineStyleResponse(testStyle1)).willReturn(response1);

			var result = wineStyleService.create(request);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw NullPointException when upsertRequest is null")
		void shouldThrowNullPointeException_whenUpsertRequestIsNull() {
			assertThatThrownBy(() -> wineStyleService.create(null)).isInstanceOf(NullPointerException.class);
		}

	}

	@Nested
	@DisplayName("Update tests")
	class UpdateTests {

		@Test
		@DisplayName("should load and save style and return mapped DTO when style exists")
		void shouldLoadAndSaveStyleAndReturnMappedDto_whenStyleExists() {
			var id = 1;

			given(wineStyleRepository.findById(id)).willReturn(Optional.of(testStyle1));
			given(wineStyleMapper.mergeWithWineStyleUpsertRequest(testStyle1, request)).willReturn(testStyle1);
			given(wineStyleRepository.save(testStyle1)).willReturn(testStyle1);
			given(wineStyleMapper.toWineStyleResponse(testStyle1)).willReturn(response1);

			var result = wineStyleService.update(id, request);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw NullPointException when upsertRequest is null")
		void shouldThrowNullPointeException_whenUpsertRequestIsNull() {
			assertThatThrownBy(() -> wineStyleService.update(1, null)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("should throw WineNotFoundException when style does not exist")
		void shouldThrowNotFoundException_whenStyleDoesNotExist() {
			var id = 404;

			given(wineStyleRepository.findById(id)).willReturn(Optional.empty());

			assertThatThrownBy(() -> wineStyleService.update(id, request))
				.isInstanceOf(WineStyleNotFoundException.class)
				.hasMessageContaining(String.valueOf(id));
			verify(wineStyleRepository, never()).save(any());
			verifyNoInteractions(wineStyleMapper);
		}

	}

	@Nested
	@DisplayName("Delete tests")
	class DeleteTests {

		@Test
		public void shouldCallRepositoryDelete() {
			var id = 11;

			wineStyleService.delete(id);

			verify(wineStyleRepository).deleteById(id);
			verifyNoInteractions(wineStyleMapper);
		}

	}

}
