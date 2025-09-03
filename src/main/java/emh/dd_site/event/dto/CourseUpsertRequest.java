package emh.dd_site.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CourseUpsertRequest(@NotNull @Positive Integer courseNo, @NotBlank String cook,
		@Valid DishUpsertRequest dish) {

}
