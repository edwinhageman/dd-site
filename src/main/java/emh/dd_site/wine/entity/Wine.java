package emh.dd_site.wine.entity;

import emh.dd_site.event.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@NamedEntityGraph(name = "Wine.withStylesAndGrapes",
		attributeNodes = { @NamedAttributeNode("styles"),
				@NamedAttributeNode(value = "grapeComposition", subgraph = "WineGrapeComposition.withGrape") },
		subgraphs = { @NamedSubgraph(name = "WineGrapeComposition.withGrape",
				attributeNodes = @NamedAttributeNode("grape")) })
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

	@Getter
	@Setter
	@ToString.Include
	private String winery;

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
	private String appellation;

	@Getter
	@Setter
	@ToString.Include
	private Year vintage;

	@Column(name = "vivino_url")
	@Getter
	@Setter
	@ToString.Include
	private String vivinoUrl;

	@ManyToMany
	@JoinTable(name = "wine_styles", joinColumns = @JoinColumn(name = "wine_id"),
			inverseJoinColumns = @JoinColumn(name = "wine_style_id"))
	private final Set<WineStyle> styles = new HashSet<>();

	@OneToMany(mappedBy = "wine", orphanRemoval = true, cascade = { PERSIST, MERGE, REMOVE })
	private final Set<WineGrapeComposition> grapeComposition = new HashSet<>();

	@OneToMany(mappedBy = "wine", orphanRemoval = true)
	private final Set<Course> courses = new HashSet<>();

	protected Wine() {
	}

	public Set<WineStyle> getStyles() {
		return Collections.unmodifiableSet(styles);
	}

	public void addStyle(@NonNull WineStyle style) {
		this.styles.add(style);
	}

	public void removeStyle(@NonNull WineStyle style) {
		this.styles.remove(style);
	}

	public void clearStyles() {
		this.styles.clear();
	}

	public Set<WineGrapeComposition> getGrapeComposition() {
		return Collections.unmodifiableSet(grapeComposition);
	}

	public void addGrape(@NonNull Grape grape, BigDecimal percentage) {
		if (percentage != null) {
			// make sure the percentage value matches the db column definition
			if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(BigDecimal.ONE) > 0) {
				throw new IllegalArgumentException("Blend percentage must be between 0 and 1");
			}
			percentage = percentage.setScale(3, RoundingMode.HALF_UP);
		}
		var composition = new WineGrapeComposition(this, grape, percentage);
		this.grapeComposition.add(composition);
	}

	public void removeGrape(@NonNull Grape grape) {
		var composition = new WineGrapeComposition(this, grape, BigDecimal.ZERO);
		this.grapeComposition.remove(composition);
	}

	public void clearGrapeComposition() {
		this.grapeComposition.clear();
	}

	public Set<Course> getCourses() {
		return Collections.unmodifiableSet(this.courses);
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

	public void clearCourses() {
		this.courses.clear();
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
