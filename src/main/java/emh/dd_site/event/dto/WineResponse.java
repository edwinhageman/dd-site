package emh.dd_site.event.dto;

import emh.dd_site.event.WineType;
import jakarta.validation.constraints.NotNull;

import java.time.Year;
import java.util.List;

public record WineResponse(@NotNull Long id, @NotNull String name, @NotNull WineType type, @NotNull String grape,
		@NotNull String country, String region, Year year, List<CourseResponse> courses) {

}
