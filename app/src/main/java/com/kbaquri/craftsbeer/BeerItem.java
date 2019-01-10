package com.kbaquri.craftsbeer;

public class BeerItem {

    private String abv;
    private String ibu;
    private String id;
    private String name;
    private String style;
    private String ounces;

    BeerItem(String abv, String ibu, String id, String name, String style, String ounces) {

        this.abv = abv;
        this.ibu = ibu;
        this.id = id;
        this.name = name;
        this.style = style;
        this.ounces = ounces;

    }


    public String getAbv() {
        return abv;
    }

    public String getIbu() {
        return ibu;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public String getOunces() {
        return ounces;
    }
}
