package emh.dd_site.event.dto;

import emh.dd_site.event.WineType;

import java.time.Year;
import java.util.List;

public record WineResponse(Long id, String name, WineType type, String grape, String country, String region, Year year,
		List<CourseResponse> courses) {

}
