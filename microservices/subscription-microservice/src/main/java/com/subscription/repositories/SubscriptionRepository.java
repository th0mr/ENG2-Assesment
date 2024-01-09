package com.subscription.repositories;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.subscription.domain.Subscription;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
	
	@Override
	Optional<Subscription> findById(@NotNull Long id);

	Optional<Subscription> findOne(long id);
	
	@Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.hashtagId = :hashtagId")
    Optional<Subscription> findByUserIdAndHashtagId(Long userId, Long hashtagId);
	
	@Query("SELECT s FROM Subscription s WHERE s.hashtagId = :hashtagId")
    List<Subscription> findAllByHashtagId(Long hashtagId);

}
