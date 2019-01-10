package com.kbaquri.craftsbeer;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private final int duration = 11500;

    private boolean webServiceDone = false;
    private boolean animationDone = false;

    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        rootView = findViewById(R.id.root_view_splash);
        ImageView splashImageView = findViewById(R.id.splash_image_view);

        Glide
                .with(this)
                .asGif()
                .load(R.raw.loadingbeer)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        resource.setLoopCount(1);
                        return false;
                    }
                })
                .into(splashImageView);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            animationDone = true;
            if (webServiceDone) {
                startActivity();
            }
        }, duration);

        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            // ...

            ApiService apiService = new ApiService();
            apiService.execute(this);
        } else {

            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.splash_image_view),
                    "Internet connection not available. Connect and re-open app.",
                    Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Quit!", v -> {
                finish();
                System.exit(0);
            });
            mySnackbar.show();
        }
    }

    private void startActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        SplashActivity.this.finish();
    }


    private class ApiService extends AsyncTask<Context, Void, Void> {

        private final String URL = "http://starlord.hackerearth.com/beercraft";

        private RequestQueue requestQueue;

        private ArrayList<BeerItem> beerItemArrayList = new ArrayList<>();

        @Override
        protected Void doInBackground(Context... contexts) {

            requestQueue = Volley.newRequestQueue(contexts[0]);

            //Retrieve JSON Array from API
            JsonArrayRequest request = new JsonArrayRequest
                    (Request.Method.GET, URL, null,
                            response -> {
                                beerItemArrayList.clear();
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        //Get BeerItem JSONObject
                                        JSONObject jsonObject = response.getJSONObject(i);

                                        //Add BeerItem into beerItemArrayList
                                        beerItemArrayList.add(
                                                new BeerItem(
                                                        jsonObject.getString("abv"),
                                                        jsonObject.getString("ibu"),
                                                        jsonObject.getString("id"),
                                                        jsonObject.getString("name"),
                                                        jsonObject.getString("style"),
                                                        jsonObject.getString("ounces")
                                                )
                                        );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                EventBus.getDefault().postSticky(new BeerItemsEventBus(beerItemArrayList));

                                webServiceDone = true;
                                if (animationDone)
                                    startActivity();
                            },
                            error -> {
                                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.splash_image_view),
                                        "Api no available, HackerEarth might have deleted it from server after challenge.",
                                        Snackbar.LENGTH_INDEFINITE);
                                mySnackbar.setAction("Quit!", v -> {
                                    finish();
                                    System.exit(0);
                                });
                                mySnackbar.show();
                            });

            requestQueue.add(request);

            return null;
        }
    }
}
