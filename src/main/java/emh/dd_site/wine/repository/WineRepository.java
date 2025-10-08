package emh.dd_site.wine.repository;

import emh.dd_site.wine.entity.Wine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long>, PagingAndSortingRepository<Wine, Long> {

	@EntityGraph("Wine.withStylesAndGrapes")
	@Query("SELECT w FROM Wine w WHERE w.id = :id")
	Optional<Wine> findWithStylesAndGrapesById(long id);

	@Query("SELECT w.id FROM Wine w")
	Page<Long> findAllIds(Pageable pageable);

	@Query("SELECT w.id FROM Wine w JOIN w.courses c WHERE c.event.id = :id")
	Page<Long> findIdsByCourseEventId(long id, Pageable pageable);

	@EntityGraph("Wine.withStylesAndGrapes")
	@Query("SELECT w FROM Wine w WHERE w.id IN :ids")
	List<Wine> findAllWithStylesAndGrapesByIdIn(Iterable<Long> ids);

}
