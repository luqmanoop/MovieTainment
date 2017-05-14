package com.soundwebcraft.movietainment.db.model;

public class Favorite {
    private Integer id;
    private String title;

    public Favorite(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
