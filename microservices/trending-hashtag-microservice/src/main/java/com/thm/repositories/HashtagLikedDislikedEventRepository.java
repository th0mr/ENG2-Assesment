package com.thm.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.thm.domain.HashtagLikedDislikedEvent;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface HashtagLikedDislikedEventRepository extends CrudRepository<HashtagLikedDislikedEvent, Long> {

    @Query("SELECT h FROM HashtagLikedDislikedEvent h WHERE h.timestamp >= :startTime")
    List<HashtagLikedDislikedEvent> findEventsAfterTime(@Parameter("startTime") LocalDateTime startTime);
}
