package emh.dd_site.wine.service;

import emh.dd_site.wine.dto.WineStyleMapper;
import emh.dd_site.wine.dto.WineStyleResponse;
import emh.dd_site.wine.dto.WineStyleUpsertRequest;
import emh.dd_site.wine.entity.WineStyle;
import emh.dd_site.wine.exception.WineStyleNotFoundException;
import emh.dd_site.wine.repository.WineStyleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WineStyleService {

	private final WineStyleRepository wineStyleRepository;

	private final WineStyleMapper wineStyleMapper;

	public Page<WineStyleResponse> listAll(Pageable pageable) {
		var page = wineStyleRepository.findAll(pageable);
		return page.map(wineStyleMapper::toWineStyleResponse);
	}

	public WineStyleResponse findById(int id) {
		var entity = getById(id);
		return wineStyleMapper.toWineStyleResponse(entity);
	}

	public WineStyleResponse create(@NonNull WineStyleUpsertRequest request) {
		var entity = wineStyleMapper.fromWineStyleUpsertRequest(request);
		entity = wineStyleRepository.save(entity);
		return wineStyleMapper.toWineStyleResponse(entity);
	}

	public WineStyleResponse update(int id, @NonNull WineStyleUpsertRequest request) {
		var entity = getById(id);
		entity = wineStyleMapper.mergeWithWineStyleUpsertRequest(entity, request);
		entity = wineStyleRepository.save(entity);
		return wineStyleMapper.toWineStyleResponse(entity);
	}

	public void delete(int id) {
		wineStyleRepository.deleteById(id);
	}

	private WineStyle getById(int id) {
		return wineStyleRepository.findById(id).orElseThrow(() -> new WineStyleNotFoundException(id));
	}

}
