package emh.dd_site.wine.service;

import emh.dd_site.wine.dto.GrapeMapper;
import emh.dd_site.wine.dto.GrapeResponse;
import emh.dd_site.wine.dto.GrapeUpsertRequest;
import emh.dd_site.wine.entity.Grape;
import emh.dd_site.wine.exception.GrapeNotFoundException;
import emh.dd_site.wine.repository.GrapeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrapeService {

	private final GrapeRepository grapeRepository;

	private final GrapeMapper grapeMapper;

	public Page<GrapeResponse> listAll(Pageable pageable) {
		var page = grapeRepository.findAll(pageable);
		return page.map(grapeMapper::toGrapeResponse);
	}

	public GrapeResponse findById(int id) {
		var entity = getById(id);
		return grapeMapper.toGrapeResponse(entity);
	}

	public GrapeResponse create(@NonNull GrapeUpsertRequest request) {
		var entity = grapeMapper.fromGrapeUpsertRequest(request);
		entity = grapeRepository.save(entity);
		return grapeMapper.toGrapeResponse(entity);
	}

	public GrapeResponse update(int id, @NonNull GrapeUpsertRequest request) {
		var entity = getById(id);
		entity = grapeMapper.mergeWithGrapeUpsertRequest(entity, request);
		entity = grapeRepository.save(entity);
		return grapeMapper.toGrapeResponse(entity);
	}

	public void delete(int id) {
		grapeRepository.deleteById(id);
	}

	private Grape getById(int id) {
		return grapeRepository.findById(id).orElseThrow(() -> new GrapeNotFoundException(id));
	}

}
