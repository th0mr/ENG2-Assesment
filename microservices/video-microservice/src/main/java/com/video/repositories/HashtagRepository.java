package com.video.repositories;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

import com.video.domain.Hashtag;
import com.video.dto.HashtagDTO;

@Repository
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);
    
    Optional<Hashtag> findById(long id);
    
    Optional<Hashtag> findOne(long id);
    
}
