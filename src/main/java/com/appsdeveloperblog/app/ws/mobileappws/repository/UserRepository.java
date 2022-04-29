package com.appsdeveloperblog.app.ws.mobileappws.repository;

import org.springframework.data.repository.CrudRepository;

import com.appsdeveloperblog.app.ws.mobileappws.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository <UserEntity, Long> {
    UserEntity findUserByEmail(String email);
}
