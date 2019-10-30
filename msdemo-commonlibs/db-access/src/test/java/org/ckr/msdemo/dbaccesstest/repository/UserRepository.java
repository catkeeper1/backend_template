package org.ckr.msdemo.dbaccesstest.repository;

import org.ckr.msdemo.dbaccesstest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yukai.a.lin on 8/15/2017.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
