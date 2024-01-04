package com.video.repositories;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.video.domain.Video;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface VideosRepository extends CrudRepository<Video, Long> {
	
	@Override
	Optional<Video> findById(@NotNull Long id);

	Optional<Video> findOne(long id);

}
