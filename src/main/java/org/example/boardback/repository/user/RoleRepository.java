package org.example.boardback.repository.user;

import org.example.boardback.common.enums.RoleType;
import org.example.boardback.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, RoleType> {
}
