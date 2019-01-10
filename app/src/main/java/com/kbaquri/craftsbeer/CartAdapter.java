package com.kbaquri.craftsbeer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends ArrayAdapter<BeerItem> {

    private MainActivity instance;

    CartAdapter(MainActivity instance, @NonNull Context context, @NonNull List<BeerItem> objects) {
        super(context, 0, objects);
        this.instance = instance;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newView = convertView;
        if (newView == null) {
            newView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item, null);
        }

        TextView beerNameTextView = newView.findViewById(R.id.beer_name);
        TextView beerStyleTextView = newView.findViewById(R.id.beer_style);
        TextView alcoholContentTextView = newView.findViewById(R.id.alcohol_content);
        TextView ouncesTextView = newView.findViewById(R.id.ounces);
        ImageButton addCartButton = newView.findViewById(R.id.remove_cart);

        final BeerItem beerItem = getItem(position);

        addCartButton.setOnClickListener(v -> instance.removeFromCart(beerItem));
        newView.setOnClickListener(v -> instance.showDetail(beerItem));


        if (beerItem != null) {
            beerNameTextView.setText(beerItem.getName());
            beerStyleTextView.setText(beerItem.getStyle());
            alcoholContentTextView.setText("Alcohol Content: " + beerItem.getAbv());
            ouncesTextView.setText(beerItem.getOunces() + " ounces");
        }

        return newView;

    }
}
