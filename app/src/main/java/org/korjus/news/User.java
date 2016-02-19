package org.korjus.news;

public class User {
    public String userName;
    public String profilePictureUrl;

    @Override
    public String toString() {
        return "User{" +
                "profilePictureUrl='" + profilePictureUrl + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}