package com.sm.repositories;


import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.subscription.domain.User;
import com.subscription.dto.UserDTO;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Named;

	
@Repository
@Named("video-db")
public interface UsersRepository extends CrudRepository<User, Long> {

	@Override
	Optional<User> findById(@NotNull Long id);
	
	Optional<UserDTO> findOne(long id);

}
