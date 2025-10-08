package emh.dd_site.wine.repository;

import emh.dd_site.wine.entity.Grape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrapeRepository extends JpaRepository<Grape, Integer> {

}
