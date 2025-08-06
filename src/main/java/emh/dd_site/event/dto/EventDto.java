package emh.dd_site.event.dto;

import java.time.LocalDate;

public record EventDto(Long id, LocalDate date, String host, String location) {
}
