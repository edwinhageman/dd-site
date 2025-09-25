package emh.dd_site.event.dto;

import jakarta.validation.constraints.NotNull;

public record CourseResponse(@NotNull Long id, @NotNull EventResponse event, @NotNull Integer courseNo,
		@NotNull String cook, @NotNull DishResponse dish) {

}