package emh.dd_site.wine.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WineGrapeCompositionId implements Serializable {

	private Long wine;

	private Integer grape;

}
