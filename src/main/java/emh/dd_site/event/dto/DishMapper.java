package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Dish;
import org.springframework.stereotype.Component;

@Component
public class DishMapper {

	public DishResponse toDishResponse(Dish entity) {
		if (entity == null) {
			return null;
		}
		return new DishResponse(entity.getId(), entity.getName(), entity.getMainIngredient());
	}

	public Dish fromDishUpsertRequest(DishUpsertRequest request) {
		if (request == null) {
			return null;
		}
		var dish = new Dish(request.name());
		dish.setMainIngredient(request.mainIngredient());
		return dish;
	}

	public Dish mergeWithDishUpsertRequest(Dish dish, DishUpsertRequest request) {
		if (dish == null) {
			return null;
		}
		if (request == null) {
			return dish;
		}
		dish.setName(request.name());
		dish.setMainIngredient(request.mainIngredient());
		return dish;
	}

}
