package com.video.repositories;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.video.domain.Video;
import com.video.dto.VideoDTO;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface VideosRepository extends CrudRepository<Video, Long> {
	
	@Join(value = "viewers", type = Join.Type.LEFT_FETCH)
	@Join(value = "dislikers", type = Join.Type.LEFT_FETCH)
	@Join(value = "likers", type = Join.Type.LEFT_FETCH)
	@Override
	Optional<Video> findById(@NotNull Long id);

	Optional<VideoDTO> findOne(long id);

}
