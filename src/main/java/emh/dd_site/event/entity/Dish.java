package emh.dd_site.event.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "dish")
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Dish {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dish_id", updatable = false, nullable = false)
	@Getter
	@ToString.Include
	private Long id;

	@OneToOne(mappedBy = "dish")
	@Getter
	private Course course;

	@Column(nullable = false)
	@NonNull
	@Getter
	@Setter
	@ToString.Include
	private String name;

	@Column(name = "main_ingredient")
	@Getter
	@Setter
	@ToString.Include
	private String mainIngredient;

	protected Dish() {
	}

	public void setCourse(@NonNull Course course) {
		this.course = course;
		if (course.getDish() != this) {
			course.setDish(this);
		}
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
		Dish dish = (Dish) o;
		return id != null && Objects.equals(id, dish.id);
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}
