package emh.dd_site.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "course")
@ToString(onlyExplicitlyIncluded = true)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", updatable = false, nullable = false)
    @Getter
    @ToString.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @NonNull
    @Getter
    @Setter
    @ToString.Include
    private Event event;

    @Column(name = "course_no", nullable = false)
    @NonNull
    @Getter
    @Setter
    @ToString.Include
    private Integer courseNo;

    @Column(nullable = false)
    @NonNull
    @Getter
    @Setter
    @ToString.Include
    private String cook;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "dish_id", nullable = false)
    @NonNull
    @Getter
    @ToString.Include
    private Dish dish;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wine_id", nullable = false)
    @NonNull
    @Getter
    @ToString.Include
    private Wine wine;

    protected Course() {
    }

    public Course(@NonNull Event event, @NonNull Integer courseNo, @NonNull String cook, @NonNull Dish dish, @NonNull Wine wine) {
        this.event = event;
        this.courseNo = courseNo;
        this.cook = cook;
        this.dish = dish;
        if (this.dish.getCourse() != this) {
            this.dish.setCourse(this);
        }
        this.wine = wine;
        if (!this.wine.getCourses().contains(this)) {
            this.wine.addCourse(this);
        }
    }

    public void setDish(@NonNull Dish dish) {
        this.dish = dish;
        if (this.dish.getCourse() != this) {
            this.dish.setCourse(this);
        }
    }

    public void setWine(@NonNull Wine wine) {
        if (this.wine.getCourses().contains(this)) {
            this.wine.removeCourse(this);
        }
        this.wine = wine;
        if (!this.wine.getCourses().contains(this)) {
            this.wine.addCourse(this);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Course course = (Course) o;
        return id != null && Objects.equals(id, course.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
