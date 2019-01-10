package com.kbaquri.craftsbeer;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    boolean sort = false;

    private ListView beerListView;
    private AutoCompleteTextView searchEditText;
    private AlertDialog.Builder styleBuilder;
    private AlertDialog.Builder cartBuilder;
    private ImageButton cartImageButton;


    private AlertDialog.Builder detailBuilder;
    private AlertDialog alertDialog;
    private View detailView;

    private TextView beerName;
    private TextView beerStyle;
    private TextView alcoholContent;
    private TextView ibu;
    private TextView ounces;
    private ImageButton addCart;


    private BeersAdapter beersAdapter;
    private CartAdapter cartAdapter;
    private ArrayAdapter<String> namesAdapter;
    private ArrayAdapter<String> stylesAdapter;


    private ArrayList<BeerItem> beerItemArrayList;
    private ArrayList<BeerItem> filteredBeerItemArrayList;
    private ArrayList<BeerItem> cartArrayList;
    private ArrayList<String> uniqueBeerStyles;
    private ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Initialise UI references */
        beerListView = findViewById(R.id.beer_list);
        searchEditText = findViewById(R.id.search_box);
        cartImageButton = findViewById(R.id.cart);

        /*Initialising arraylist variables*/
        beerItemArrayList = new ArrayList<>();
        filteredBeerItemArrayList = new ArrayList<>();
        cartArrayList = new ArrayList<>();
        uniqueBeerStyles = new ArrayList<>();
        names = new ArrayList<>();

        //ListView for all the beer
        beersAdapter = new BeersAdapter(this, this, filteredBeerItemArrayList);
        beerListView.setAdapter(beersAdapter);

        //Autocompleter  Searchbar
        namesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        searchEditText.setAdapter(namesAdapter);
        searchEditText.addTextChangedListener(this);

        //Filter Actionbar Button
        stylesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, uniqueBeerStyles);
        styleBuilder = new AlertDialog.Builder(this)
                .setTitle("Choose a Beer Style")
                .setAdapter(stylesAdapter,
                        (dialog, which) -> {
                            filterStyle(uniqueBeerStyles.get(which));
                        });

        //Cart Button Click
        cartAdapter = new CartAdapter(this, this, cartArrayList);
        cartBuilder = new AlertDialog.Builder(this)
                .setTitle("Shopping Cart")
                .setAdapter(cartAdapter,
                        (dialog, which) -> {
                            cartAdapter.notifyDataSetChanged();
                        });
        cartImageButton.setOnClickListener(v -> {
            cartBuilder.show();
        });

    }


    /**
     * Filter beers according to search string
     */
    private void filterBeers() {
        String query = searchEditText.getText().toString().trim().toLowerCase();
        filteredBeerItemArrayList.clear();
        for (BeerItem beer : beerItemArrayList) {
            if (beer.getName().toLowerCase().contains(query)) {
                filteredBeerItemArrayList.add(beer);
            }
        }
        beersAdapter.notifyDataSetChanged();
    }


    /**
     * Filter beers according to beer style
     */
    private void filterStyle(String style) {
        filteredBeerItemArrayList.clear();
        for (BeerItem beer : beerItemArrayList) {
            if (beer.getStyle().toLowerCase().contains(style.toLowerCase())) {
                filteredBeerItemArrayList.add(beer);
            }
        }
        beersAdapter.notifyDataSetChanged();
    }

    /**
     * Function to show beer detail on view click click
     *
     * @param beerItem
     */
    public void showDetail(BeerItem beerItem) {

        //Detail Button Click
        detailBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        detailView = inflater.inflate(R.layout.beer_item_detail, null);
        detailBuilder.setView(detailView);

        beerName = detailView.findViewById(R.id.beer_name_detail);
        beerStyle = detailView.findViewById(R.id.beer_style_detail);
        alcoholContent = detailView.findViewById(R.id.alcohol_content_detail);
        ibu = detailView.findViewById(R.id.bittering_unit_detail);
        ounces = detailView.findViewById(R.id.ounces_detail);
        addCart = detailView.findViewById(R.id.add_cart_detail);

        beerName.setText(beerItem.getName());
        beerStyle.setText(beerItem.getStyle());
        alcoholContent.setText("Alcohol Content: " + beerItem.getAbv());
        ibu.setText("IBU: " + beerItem.getIbu());
        ounces.setText("Beer Size: " + beerItem.getOunces());
        addCart.setOnClickListener(v -> addToCart(beerItem));

        alertDialog = detailBuilder.create();
        alertDialog.show();
    }


    /**
     * Function to add beer to shopping cart on add button click
     *
     * @param beerItem
     */
    public void addToCart(BeerItem beerItem) {
        cartArrayList.add(beerItem);
        cartAdapter.notifyDataSetChanged();
    }

    /**
     * Function to remove beer from shopping cart on remove button click
     *
     * @param beerItem
     */
    public void removeFromCart(BeerItem beerItem) {
        cartArrayList.remove(beerItem);
        cartAdapter.notifyDataSetChanged();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        filterBeers();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:

                if (!sort) {
                    item.setIcon(R.drawable.sort_asc);
                    sort = true;
                    //Sort Ascending
                    Collections.sort(filteredBeerItemArrayList, (BeerItem a, BeerItem b) -> a.getAbv().compareTo(b.getAbv()));
                    beersAdapter.notifyDataSetChanged();
                } else {
                    item.setIcon(R.drawable.sort_dsc);
                    sort = false;
                    //Sort Descending
                    Collections.sort(filteredBeerItemArrayList, (BeerItem a, BeerItem b) -> b.getAbv().compareTo(a.getAbv()));
                    beersAdapter.notifyDataSetChanged();
                }

                return true;

            case R.id.filter:
                styleBuilder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onBeerItemsEventBus(BeerItemsEventBus beerItemsEventBus) {
        this.beerItemArrayList = beerItemsEventBus.getBeerItemArrayList();

        for (BeerItem beerItem : beerItemArrayList) {
            names.add(beerItem.getName());

            if (!uniqueBeerStyles.contains(beerItem.getStyle())) {
                uniqueBeerStyles.add(beerItem.getStyle());
            }
        }

        namesAdapter.notifyDataSetChanged();
        stylesAdapter.notifyDataSetChanged();

        filterBeers();
    }
}
