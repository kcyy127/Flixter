package com.example.flixter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.databinding.ActivityDetailBinding;
import com.example.flixter.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {
    private ActivityDetailBinding binding;

    private static final String YOUTUBE_API_KEY = "AIzaSyCk6CN_3pj4zIV0jgHgV6Oetl3f_ydCItw";
    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";

    private int videoMillis;
    private YouTubePlayer youTubePlayer;

    private Movie movie;
    private boolean isPopular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        videoMillis = 0;

        Intent i = getIntent();
        movie = Parcels.unwrap(i.getParcelableExtra("movie"));

        binding.title.setText(movie.getTitle());
        binding.overview.setText(movie.getOverview());
        binding.ratingBar.setRating((float)movie.getVoteAverage());
        isPopular = movie.getVoteAverage() >= 8;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEO_URL, movie.getId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0) {
                        return;
                    } else {
                        String youtubeKey = results.getJSONObject(0).getString("key");
                        Log.d("DetailActivity", youtubeKey);
                        initializeYoutube(youtubeKey);
                    }
                } catch (JSONException e) {
                    Log.e("DetailActivity", "Failed to parse JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
        });
    }

    private void initializeYoutube(final String youtubeKey) {

        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (videoMillis == 0) {
                    if (isPopular == true) {
                        youTubePlayer.loadVideo(youtubeKey);
                    } else {
                        youTubePlayer.cueVideo(youtubeKey);
                    }
                } else {
                    youTubePlayer.loadVideo(youtubeKey, videoMillis);
                }
                getYtp(youTubePlayer);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity", "YouTube initialization failed");
            }

        };
        binding.player.initialize(YOUTUBE_API_KEY, onInitializedListener);
    }

    private void getYtp(YouTubePlayer ytp) {
        this.youTubePlayer = ytp;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.videoMillis = youTubePlayer.getCurrentTimeMillis();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("videoMillis", videoMillis);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.videoMillis = savedInstanceState.getInt("videoMillis");
    }
}