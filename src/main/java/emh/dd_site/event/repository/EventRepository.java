package emh.dd_site.event.repository;

import emh.dd_site.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {
}
