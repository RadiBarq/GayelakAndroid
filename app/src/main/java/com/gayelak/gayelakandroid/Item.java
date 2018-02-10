package com.gayelak.gayelakandroid;

/**
 * Created by radibarq on 2/6/18.
 */

public class Item {

    public String category;
    public String currency;
    public String description;
    public String displayName;
    public double favourites;
    public int imagesCount;
    public String price;
    public double timestamp;
    public String title;
    public String userId;

    Item()
    {

    }

    Item(String category, String currency, String description, String displayName, double favourites, int imagesCount, String price, double timestamp
    , String title, String userId
    )
    {
            this.category = category;
            this.currency = currency;
            this.description = description;
            this.displayName = displayName;
            this.favourites = favourites;
            this.imagesCount = imagesCount;
            this.price = price;
            this.timestamp = timestamp;
            this.title = title;
            this.userId = userId;

    }


}
