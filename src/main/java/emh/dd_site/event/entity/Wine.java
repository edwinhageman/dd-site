package emh.dd_site.event.entity;

import emh.dd_site.event.WineType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "wine")
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Wine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wine_id", updatable = false, nullable = false)
	@Getter
	@ToString.Include
	private Long id;

	@Column(nullable = false)
	@NonNull
	@Getter
	@Setter
	@ToString.Include
	private String name;

	@Column(nullable = false)
	@NonNull
	@Getter
	@Setter
	@ToString.Include
	private WineType type;

	@Column(nullable = false)
	@NonNull
	@Getter
	@Setter
	@ToString.Include
	private String grape;

	@Column(nullable = false)
	@NonNull
	@Getter
	@Setter
	@ToString.Include
	private String country;

	@Getter
	@Setter
	@ToString.Include
	private String region;

	@Getter
	@Setter
	@ToString.Include
	private Year year;

	@OneToMany(mappedBy = "wine", orphanRemoval = true)
	private final List<Course> courses = new ArrayList<>();

	protected Wine() {
	}

	public List<Course> getCourses() {
		return Collections.unmodifiableList(this.courses);
	}

	public void addCourse(@NonNull Course course) {
		if (!this.courses.contains(course)) {
			this.courses.add(course);
			if (course.getWine() != this) {
				course.setWine(this);
			}
		}
	}

	public void removeCourse(@NonNull Course course) {
		this.courses.remove(course);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		Wine wine = (Wine) o;
		return id != null && Objects.equals(id, wine.id);
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}
