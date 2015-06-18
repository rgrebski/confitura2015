package com.grebski.redis.test;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final int id;
    private final String login;

    private User(String firstName, String lastName, String email, int id, String login) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.login = login;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public String getLogin() {
        return login;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User that = (User) o;

        return Objects.equal(this.firstName, that.firstName) &&
                Objects.equal(this.lastName, that.lastName) &&
                Objects.equal(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(firstName, lastName, email);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("email", email)
                .toString();
    }

    public static class UserBuilder {
        private String firstName;
        private String lastName;
        private String email;
        private int id;
        private String login;

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder id(int id) {
            this.id = id;
            return this;
        }

        public UserBuilder login(String login) {
            this.login = login;
            return this;
        }

        public User build() {
            return new User(firstName, lastName, email, id, login);
        }
    }
}
