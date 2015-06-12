package com.grebski.cache.model;

/**
 * Created by rgrebski on 07.06.15.
 */
public class OffHeapUserSample implements OffHeapUser {
    private String username;
    private long accountValidUntil;
    private Role[] roles = RoleSample.forStrings("ROLE1", "ROLE2");

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public long getAccountValidUntil() {
        return accountValidUntil;
    }

    @Override
    public void setAccountValidUntil(long accountValidUntil) {
        this.accountValidUntil = accountValidUntil;
    }

    @Override
    public void setRoleAt(int index, Role role) {
        roles[index] = role;
    }

    @Override
    public Role getRoleAt(int index) {
        return roles[index];
    }
}
