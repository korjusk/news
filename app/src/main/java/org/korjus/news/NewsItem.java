package org.korjus.news;

public class NewsItem {
    private static final String TAG = "u8i9 NewsItem";
    String content; // direct link
    String id;
    String commentsLink;
    String published; // later change to date
    String title;



    public NewsItem(String content, String id, String commentsLink, String published, String title) {
        this.content = content;
        this.id = id;
        this.commentsLink = commentsLink;
        this.published = published;
        this.title = title;

    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", commentsLink='" + commentsLink + '\'' +
                ", published='" + published + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getCommentsLink() {
        return commentsLink;
    }
}