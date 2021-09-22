package com.example.flixter.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixter.R;
import com.example.flixter.models.Movie;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MovieAdapter";

    public static final int POPULAR = 1, NOT_POPULAR = 0;


    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getItemViewType(int position) {
        double voteAverage = movies.get(position).getVoteAverage();
        Log.d(TAG, movies.get(position).getTitle() + "; " + voteAverage);

        if (voteAverage >= 8) {
            return POPULAR;
        } else if (voteAverage >= 0) {
            return NOT_POPULAR;
        }
        return -1;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case POPULAR:
                View v2 = inflater.inflate(R.layout.item_backdrop_layout, parent, false);
                viewHolder = new ViewHolder2(v2);
                break;
            case NOT_POPULAR:
                View v1 = inflater.inflate(R.layout.item_layout, parent, false);
                viewHolder = new ViewHolder1(v1);
                break;
            default:
                View v = inflater.inflate(R.layout.item_backdrop_layout, parent, false);
                viewHolder = new ViewHolder1(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        Movie movie = movies.get(position);

        switch (holder.getItemViewType()) {
            case NOT_POPULAR:
                ViewHolder1 viewHolder1 = (ViewHolder1) holder;
                viewHolder1.bind(movie);
                break;
            case POPULAR:
                ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                viewHolder2.bind(movie);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // not popular
    public class ViewHolder1 extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textDesc;
        ImageView imageView;


        public ViewHolder1(@NonNull @NotNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(@NotNull Movie movie) {
            textTitle.setText(movie.getTitle());
            textDesc.setText(movie.getOverview());
            String imageUrl;

            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
            } else {
                imageUrl = movie.getPosterPath();
            }

            Glide.with(context).load(imageUrl).placeholder(R.drawable.placeholder_cat).into(imageView);
        }
    }

    // popular
    public class ViewHolder2 extends RecyclerView.ViewHolder {
        ImageView imageView;


        public ViewHolder2(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(Movie movie) {
            Glide.with(context).load(movie.getBackdropPath()).into(imageView);
        }
    }
}
