package emh.dd_site.event.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "event")
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", updatable = false, nullable = false)
    @Getter
    @ToString.Include
    private Long id;

    @Column(nullable = false)
    @NonNull
    @Getter
    @Setter
    @ToString.Include
    private LocalDate date;

    @Column(nullable = false)
    @NonNull
    @Getter
    @Setter
    @ToString.Include
    private String host;

    @Getter
    @Setter
    @ToString.Include
    private String location;

    @OneToMany(mappedBy = "event", orphanRemoval = true)
    private final List<Course> courses = new ArrayList<>();

    protected Event() {}

    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    public void addCourse(@NonNull Course course) {
        if (!this.courses.contains(course)) {
            this.courses.add(course);
        }
        course.setEvent(this);
    }

    public void removeCourse(@NonNull Course course) {
        this.courses.remove(course);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
