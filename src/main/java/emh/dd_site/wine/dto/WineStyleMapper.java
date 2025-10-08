package emh.dd_site.wine.dto;

import emh.dd_site.wine.entity.WineStyle;
import org.springframework.stereotype.Component;

@Component
public class WineStyleMapper {

	public WineStyleResponse toWineStyleResponse(WineStyle entity) {
		if (entity == null) {
			return null;
		}
		return new WineStyleResponse(entity.getId(), entity.getName());
	}

	public WineStyle fromWineStyleUpsertRequest(WineStyleUpsertRequest request) {
		if (request == null) {
			return null;
		}

		return new WineStyle(request.name());
	}

	public WineStyle mergeWithWineStyleUpsertRequest(WineStyle style, WineStyleUpsertRequest request) {
		if (style == null) {
			return null;
		}
		if (request == null) {
			return style;
		}
		style.setName(request.name());
		return style;
	}

}
