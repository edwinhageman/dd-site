package emh.dd_site.event.service;

import emh.dd_site.event.dto.EventMapper;
import emh.dd_site.event.dto.EventResponse;
import emh.dd_site.event.dto.EventUpsertRequest;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.exception.EventNotFoundException;
import emh.dd_site.event.repository.EventRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;

	private final EventMapper eventMapper;

	public Page<EventResponse> listAll(Pageable pageable) {
		var events = eventRepository.findAll(pageable);
		return events.map(eventMapper::toEventResponse);
	}

	public EventResponse findById(long id) {
		var event = getById(id);
		return eventMapper.toEventResponse(event);
	}

	public EventResponse create(@NonNull EventUpsertRequest request) {
		var event = eventMapper.fromEventUpsertRequest(request);
		event = eventRepository.save(event);
		return eventMapper.toEventResponse(event);
	}

	public EventResponse update(long id, @NonNull EventUpsertRequest request) {
		var event = getById(id);
		event = eventMapper.mergeWithEventUpsertRequest(event, request);
		event = eventRepository.save(event);
		return eventMapper.toEventResponse(event);
	}

	public void delete(long id) {
		eventRepository.deleteById(id);
	}

	private Event getById(long id) {
		return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
	}

}
