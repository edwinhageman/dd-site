package emh.dd_site.wine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "wine_grape_composition")
@IdClass(WineGrapeCompositionId.class)
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class WineGrapeComposition {

	@Id
	@ManyToOne
	@JoinColumn(name = "wine_id", nullable = false)
	@NonNull
	@Getter
	@ToString.Include
	private Wine wine;

	@Id
	@ManyToOne
	@JoinColumn(name = "grape_id", nullable = false)
	@NonNull
	@Getter
	@ToString.Include
	private Grape grape;

	@Column(precision = 4, scale = 3)
	@Getter
	@Setter
	@ToString.Include
	private BigDecimal percentage;

	public WineGrapeComposition() {
	}

	public WineGrapeComposition(@NonNull Wine wine, @NonNull Grape grape, BigDecimal percentage) {
		this.wine = wine;
		this.grape = grape;
		this.percentage = percentage;
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
		WineGrapeComposition wineGrapeComposition = (WineGrapeComposition) o;
		return Objects.equals(wine.getId(), wineGrapeComposition.wine.getId())
				&& Objects.equals(grape.getId(), wineGrapeComposition.grape.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}
