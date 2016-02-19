package org.korjus.news;

public class NewsItem {
    private static final String TAG = "u8i9 NewsItem";
    String content; // direct link
    String code;
    String commentsLink;
    String published; // later change to date
    String title;



    public NewsItem(String content, String code, String commentsLink, String published, String title) {
        this.content = content;
        this.code = code;
        this.commentsLink = commentsLink;
        this.published = published;
        this.title = title;

    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "content='" + content + '\'' +
                ", code='" + code + '\'' +
                ", commentsLink='" + commentsLink + '\'' +
                ", published='" + published + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }

    public String getCommentsLink() {
        return commentsLink;
    }

    public String getPublished() {
        return published;
    }
}