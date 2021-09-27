package com.example.flixter.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixter.DetailActivity;
import com.example.flixter.R;
import com.example.flixter.databinding.ItemBackdropLayoutBinding;
import com.example.flixter.databinding.ItemLayoutBinding;
import com.example.flixter.models.Movie;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MovieAdapter";

    public static final int POPULAR = 1, NOT_POPULAR = 0;

    private final int radius = 24;
    private final int margin = 0;


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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case POPULAR:
                ItemBackdropLayoutBinding itemBackdropLayoutBinding = ItemBackdropLayoutBinding.inflate(inflater, parent, false);
                viewHolder =  new ViewHolder2(itemBackdropLayoutBinding);
                break;
            default:
                ItemLayoutBinding defaultBinding = ItemLayoutBinding.inflate(inflater, parent, false);
                viewHolder =  new ViewHolder1(defaultBinding);
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
        private ItemLayoutBinding binding;

        TextView title;
        TextView overview;
        ImageView imageView;
        ConstraintLayout container;


        public ViewHolder1(ItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

//            binding = ItemLayoutBinding.bind(itemView);
//            title = itemView.findViewById(R.id.title);
//            overview = itemView.findViewById(R.id.overview);
//            imageView = itemView.findViewById(R.id.imageView);
//            container = itemView.findViewById(R.id.container);
        }

        public void bind(@NotNull Movie movie) {
            binding.title.setText(movie.getTitle());
            binding.overview.setText(movie.getOverview());
            String imageUrl;

            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
            } else {
                imageUrl = movie.getPosterPath();
            }

            Glide.with(context)
                    .load(imageUrl)
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .placeholder(R.drawable.placeholder_cat)
                    .into(binding.imageView);

            binding.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("movie", Parcels.wrap(movie));

//                    intent.putExtra(DetailActivity.EXTRA_CONTACT, contact);
                    Pair<View, String> p1 = Pair.create((View)binding.title, "title");
                    Pair<View, String> p2 = Pair.create((View)binding.overview, "overview");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, p1, p2);
                    context.startActivity(intent, options.toBundle());
                }
            });
        }
    }

    // popular
    public class ViewHolder2 extends RecyclerView.ViewHolder {
        private ItemBackdropLayoutBinding binding;

        public ViewHolder2(ItemBackdropLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Movie movie) {

            Glide.with(context)
                    .load(movie.getBackdropPath())
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(binding.imageView);

            binding.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("movie", Parcels.wrap(movie));
                    context.startActivity(i);
                }
            });
        }
    }
}
