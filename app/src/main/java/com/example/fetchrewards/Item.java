package com.example.fetchrewards;

import com.squareup.moshi.Json;

public class Item {
    private final int id;
    @Json(name = "listId")
    private final int listId;
    private final String name;

    public Item(int id, int listId, String name) {
        this.id = id;
        this.listId = listId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }
}
