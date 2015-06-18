package com.grebski.cache.model;

import net.openhft.lang.model.constraints.MaxSize;

/**
 * Chronicle Map OffHeap User model
 */
public interface OffHeapUser {

    String getUsername();
    void setUsername(@MaxSize(30) String username);

    long getAccountValidUntil();
    void setAccountValidUntil(long accountValidUntil);

    void setRoleAt(@MaxSize(2) int index, Role role);
    Role getRoleAt(@MaxSize(2) int index);

    static interface Role {
        String getRole();
        void setRole(@MaxSize(10) String role);
    }
}
