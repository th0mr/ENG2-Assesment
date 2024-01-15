package com.sm.repositories;

import java.util.Optional;

import com.subscription.domain.Hashtag;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Named;

@Repository
@Named("video-db")
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);
    
    Optional<Hashtag> findById(long id);
    
    Optional<Hashtag> findOne(long id);
    
}
