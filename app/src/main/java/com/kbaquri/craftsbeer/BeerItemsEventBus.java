package com.kbaquri.craftsbeer;

import java.util.ArrayList;

public class BeerItemsEventBus {

    private ArrayList<BeerItem> beerItemArrayList;

    BeerItemsEventBus(ArrayList<BeerItem> beerItemArrayList) {
        this.beerItemArrayList = beerItemArrayList;
    }


    public ArrayList<BeerItem> getBeerItemArrayList() {
        return beerItemArrayList;
    }
}
