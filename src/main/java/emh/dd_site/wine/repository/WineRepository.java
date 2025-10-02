package emh.dd_site.wine.repository;

import emh.dd_site.wine.entity.Wine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long>, PagingAndSortingRepository<Wine, Long> {

	@Query("SELECT w FROM Wine w LEFT JOIN FETCH w.courses WHERE w.id = :id")
	Optional<Wine> findByIdWithCourses(long id);

	@Query("SELECT w FROM Wine w LEFT JOIN FETCH w.courses c WHERE c.event.id = :id")
	Page<Wine> findByEventId(long id, Pageable pageable);

}
