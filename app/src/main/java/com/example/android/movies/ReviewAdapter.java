package com.example.android.movies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import at.blogc.android.views.ExpandableTextView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    private List<Review> mReviews;
    private Context mContext;


    public ReviewAdapter(Context context) {
        this.mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       private TextView author;
        private ExpandableTextView review;
        private TextView textToggle;

        public ViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author_name);
            review = itemView.findViewById(R.id.expandableTextView);
            textToggle = itemView.findViewById(R.id.text_toggle);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.reviews_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = mReviews.get(position);

        TextView authorTextView = holder.author;
        final ExpandableTextView expandableTextView = holder.review;
        final TextView textToggle = holder.textToggle;

        String reviewString = review.getmReview();
        // remove white spaces and empty lines
        String adjusted = reviewString.replaceAll("(?m)^[ \t]*\r?\n", "");


        authorTextView.setText(review.getmAouther());
        expandableTextView.setText(adjusted);

        int length = adjusted.length();

        if (length<180){
            textToggle.setVisibility(View.INVISIBLE);
        }else {
            textToggle.setVisibility(View.VISIBLE);
        }

        // set animation duration via code,
        expandableTextView.setAnimationDuration(750L);

        // set interpolators for both expanding and collapsing animations
        expandableTextView.setInterpolator(new OvershootInterpolator());

        textToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (expandableTextView.isExpanded())
                {
                    expandableTextView.collapse();
                    textToggle.setText(R.string.expand);
                }
                else
                {
                    expandableTextView.expand();
                    textToggle.setText(R.string.collapse);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(null==mReviews){
            return 0;
        }
        return mReviews.size();
    }

    public void setReviewData(List<Review> reviewData) {
        mReviews = reviewData;
        notifyDataSetChanged();
    }
}
