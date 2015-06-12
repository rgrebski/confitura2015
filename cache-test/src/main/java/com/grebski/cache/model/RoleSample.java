package com.grebski.cache.model;

import com.grebski.cache.model.OffHeapUser.Role;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Role sample for {@link Role}
 */
class RoleSample implements Role {
    private String role;

    private RoleSample(String role) {

        this.role = role;
    }

    static Role forString(String role) {
        return new RoleSample(role);
    }

    static Role[] forStrings(String... roles) {
        Role[] rolesArray = new Role[roles.length];
        return (Role[]) Stream.of(roles)
                .map(role -> new RoleSample(role))
                .collect(Collectors.toList())
                .toArray(rolesArray);
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }
}
