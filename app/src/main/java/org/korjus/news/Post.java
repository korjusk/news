package org.korjus.news;

public class Post {
    private static final String TAG = "u8i9 Post";
    public User user;
    public String text;

    @Override
    public String toString() {
        return "Post{" +
                "text='" + text + '\'' +
                ", user=" + user +
                '}';
    }
}
