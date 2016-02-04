package org.korjus.news;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class OldNews {
    private static final String TAG = "u8i9 OldNews";
    public Long _id; // for cupboard
    String oldId;

    static {
        // register our models
        cupboard().register(OldNews.class);
    }

    public OldNews() { // for cupboard
    }

    public OldNews(String oldId) {
        this.oldId = oldId;
    }
}
