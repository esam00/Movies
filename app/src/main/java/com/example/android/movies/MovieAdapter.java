package com.example.android.movies;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{
    private List<Movie> mMovies;
    private Context mContext;
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener{
        void onListItemClick(Movie clickedMovie);
    }

    public MovieAdapter(Context context, ListItemClickListener listener) {
        this.mContext = context;
        this.mOnClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
       private ImageView poster;
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.iv_poster);
            title = itemView.findViewById(R.id.tv_movie_title);
            itemView.setOnClickListener(this);

        }
        /**
         * Called whenever a user clicks on an item in the list.
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Movie clickedItem = mMovies.get(clickedPosition);
            mOnClickListener.onListItemClick(clickedItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        ImageView posterImageView = holder.poster;
        TextView movieTitleTextView = holder.title;

        movieTitleTextView.setText(movie.getMovieTitle());

        String posterUri = getImageUri(movie);

        Picasso.with(mContext)
                .load(posterUri)
                .error(R.drawable.noimage)
                .into(posterImageView);

    }

    @Override
    public int getItemCount() {
        if(null==mMovies){
            return 0;
        }
        return mMovies.size();
    }

    /**
     * This method is used to set the Movie data , if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param movieData The new weather data to be displayed.
     */
    public void setMovieData(List<Movie> movieData) {
        mMovies = movieData;
        notifyDataSetChanged();
    }

    /**
     * this method is used to build the url of the image
     * @param movie
     * @return string url
     */

    public static String getImageUri (Movie movie){

        final String BASE_URL = "image.tmdb.org";
        final String SIZE = "w185";
        String posterPath = movie.getMoviePoster();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(BASE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath(SIZE)
                .appendEncodedPath(posterPath);

        String myUrl = builder.build().toString();
        return myUrl;
    }

}
