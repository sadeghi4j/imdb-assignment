package com.imdb.imdbtitles.repository;

import com.imdb.imdbtitles.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByNameEqualsIgnoreCase(String name);

}
