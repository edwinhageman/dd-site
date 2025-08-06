package emh.dd_site.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper<D, E> {

	D toDto(E entity);

	default Page<D> toDtoPage(Page<E> page) {
		if (page == null) {
			return new PageImpl<>(Collections.emptyList());
		}
		return page.map(this::toDto);
	}

	default List<D> toDtoList(Collection<E> entities) {
		if (entities == null) {
			return Collections.emptyList();
		}
		return entities.stream().map(this::toDto).collect(Collectors.toList());
	}

}
