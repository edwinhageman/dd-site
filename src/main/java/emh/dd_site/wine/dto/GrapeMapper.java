package emh.dd_site.wine.dto;

import emh.dd_site.wine.entity.Grape;
import org.springframework.stereotype.Component;

@Component
public class GrapeMapper {

	public GrapeResponse toGrapeResponse(Grape entity) {
		if (entity == null) {
			return null;
		}
		return new GrapeResponse(entity.getId(), entity.getName());
	}

	public Grape fromGrapeUpsertRequest(GrapeUpsertRequest request) {
		if (request == null) {
			return null;
		}

		return new Grape(request.name());
	}

	public Grape mergeWithGrapeUpsertRequest(Grape grape, GrapeUpsertRequest request) {
		if (grape == null) {
			return null;
		}
		if (request == null) {
			return grape;
		}
		grape.setName(request.name());
		return grape;
	}

}
