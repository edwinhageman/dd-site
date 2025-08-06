package emh.dd_site.event.service;

import emh.dd_site.event.dto.CreateUpdateEventDto;
import emh.dd_site.event.dto.EventDto;
import emh.dd_site.event.dto.EventDtoMapper;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;

	private final EventDtoMapper eventDtoMapper;

	public Page<EventDto> listAll(Pageable pageable) {
		var events = eventRepository.findAll(pageable);
		return eventDtoMapper.toDtoPage(events);
	}

	public EventDto findById(long id) {
		var event = getById(id);
		return eventDtoMapper.toDto(event);
	}

	public EventDto create(CreateUpdateEventDto data) {
		var event = new Event(data.date(), data.host());
		event.setLocation(data.location());
		event = eventRepository.save(event);
		return eventDtoMapper.toDto(event);
	}

	public EventDto update(long id, CreateUpdateEventDto data) {
		var event = getById(id);
		event.setDate(data.date());
		event.setHost(data.host());
		event.setLocation(data.location());
		event = eventRepository.save(event);
		return eventDtoMapper.toDto(event);
	}

	public void delete(long id) {
		eventRepository.deleteById(id);
	}

	private Event getById(long id) {
		return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
	}

}
