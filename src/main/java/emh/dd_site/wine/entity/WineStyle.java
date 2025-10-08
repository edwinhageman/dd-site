package emh.dd_site.wine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "wine_style")
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class WineStyle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wine_style_id", updatable = false, nullable = false)
	@Getter
	@ToString.Include
	private Integer id;

	@Column(nullable = false)
	@NonNull
	@Getter
	@Setter
	@ToString.Include
	private String name;

	protected WineStyle() {
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
		WineStyle wineStyle = (WineStyle) o;
		return getId() != null && Objects.equals(getId(), wineStyle.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}
