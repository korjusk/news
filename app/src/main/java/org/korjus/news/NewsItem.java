package org.korjus.news;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class NewsItem {
    private static final String TAG = "u8i9 NewsItem";
    String content; // direct link
    String id;
    String link;
    String published; // later change to date
    String title;
    public Long _id; // for cupboard

    static {
        // register our models
        cupboard().register(NewsItem.class);
    }

    public NewsItem() { // for cupboard
    }

    public NewsItem(String content, String id, String link, String published, String title) {
        this.content = content;
        this.id = id;
        this.link = link;
        this.published = published;
        this.title = title;

    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", link='" + link + '\'' +
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
}