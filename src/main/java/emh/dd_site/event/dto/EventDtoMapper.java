package emh.dd_site.event.dto;

import emh.dd_site.dto.BaseMapper;
import emh.dd_site.event.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventDtoMapper implements BaseMapper<EventDto, Event> {

	@Override
	public EventDto toDto(Event entity) {
		if (entity == null) {
			return null;
		}
		return new EventDto(entity.getId(), entity.getDate(), entity.getHost(), entity.getLocation());
	}

}
