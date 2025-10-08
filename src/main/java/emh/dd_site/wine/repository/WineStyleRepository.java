package emh.dd_site.wine.repository;

import emh.dd_site.wine.entity.WineStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WineStyleRepository extends JpaRepository<WineStyle, Integer> {

}
