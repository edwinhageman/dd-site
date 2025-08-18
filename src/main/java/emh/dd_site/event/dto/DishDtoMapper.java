package emh.dd_site.event.dto;

import emh.dd_site.dto.BaseMapper;
import emh.dd_site.event.entity.Dish;
import org.springframework.stereotype.Component;

@Component
public class DishDtoMapper implements BaseMapper<DishDto, Dish> {

	@Override
	public DishDto toDto(Dish entity) {
		if (entity == null) {
			return null;
		}
		return new DishDto(entity.getId(), entity.getName(), entity.getMainIngredient());
	}

}
