package com.sm.repositories;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.subscription.domain.Video;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Named;

@Repository
@Named("video-db")
public interface VideosRepository extends CrudRepository<Video, Long> {
	
	@Override
	Optional<Video> findById(@NotNull Long id);

	Optional<Video> findOne(long id);

}
