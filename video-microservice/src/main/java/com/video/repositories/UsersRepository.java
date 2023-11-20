package com.video.repositories;


import java.util.Optional;

import com.video.domain.User;
import com.video.dto.UserDTO;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

	
@Repository
public interface UsersRepository extends CrudRepository<User, Long> {

	Optional<UserDTO> findOne(long id);

}
