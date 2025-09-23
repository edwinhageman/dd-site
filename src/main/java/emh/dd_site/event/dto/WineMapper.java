package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Wine;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WineMapper {

	private final EntityManagerFactory emf;

	private final CourseMapper courseMapper;

	public WineResponse toWineResponse(Wine entity) {
		if (entity == null) {
			return null;
		}

		List<CourseResponse> courses = Collections.emptyList();

		var util = emf.getPersistenceUnitUtil();
		if (util.isLoaded(entity.getCourses())) {
			courses = entity.getCourses().stream().map(courseMapper::toCourseResponse).toList();
		}

		return new WineResponse(entity.getId(), entity.getName(), entity.getType(), entity.getGrape(),
				entity.getCountry(), entity.getRegion(), entity.getYear(), courses);
	}

	public Wine fromWineUpsertRequest(WineUpsertRequest request) {
		if (request == null) {
			return null;
		}
		var wine = new Wine(request.name(), request.type(), request.grape(), request.country());
		wine.setRegion(request.region());
		wine.setYear(request.year());
		return wine;
	}

	public Wine mergeWithWineUpsertRequest(Wine wine, WineUpsertRequest request) {
		if (wine == null) {
			return null;
		}
		if (request == null) {
			return wine;
		}
		wine.setName(request.name());
		wine.setType(request.type());
		wine.setGrape(request.grape());
		wine.setCountry(request.country());
		wine.setRegion(request.region());
		wine.setYear(request.year());
		return wine;
	}

}
