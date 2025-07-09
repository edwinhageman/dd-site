package emh.dd_site.event.repository;

import emh.dd_site.event.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, PagingAndSortingRepository<Course, Long> {
}
