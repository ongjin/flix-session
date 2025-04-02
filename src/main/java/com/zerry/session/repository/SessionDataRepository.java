package com.zerry.session.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zerry.session.model.SessionData;

@Repository
public interface SessionDataRepository extends CrudRepository<SessionData, String> {

}
