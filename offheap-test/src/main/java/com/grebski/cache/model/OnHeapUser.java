package com.grebski.cache.model;

import com.google.common.base.Objects;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * User object that will be stored on-heap
 */
public class OnHeapUser {

    private String username;
    private LocalDateTime accountValidUntil;
    private List<Role> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getAccountValidUntil() {
        return accountValidUntil;
    }

    public void setAccountValidUntil(LocalDateTime accountValidUntil) {
        this.accountValidUntil = accountValidUntil;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OnHeapUser that = (OnHeapUser) o;

        return Objects.equal(this.username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    public static class Role {
        private String role;

        private Role(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public static Role forString(String role) {
            return new Role(role);
        }

        public static List<Role> forStrings(String... roles) {
            return Stream.of(roles)
                    .map(role -> new Role(role))
                    .collect(toList());
        }
    }




}
