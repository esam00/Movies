package com.example.android.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{
    private List<String> trailerKeys;
    private Context mContext;
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener{
        void onListItemClick(String youtubeKey);
    }

    public TrailerAdapter(Context context,ListItemClickListener listener) {
        this.mContext = context;
        mOnClickListener =listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
      private ImageButton trailerButton ;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerButton = itemView.findViewById(R.id.trailer_button);
            trailerButton.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String clickedItem = trailerKeys.get(clickedPosition);
            mOnClickListener.onListItemClick(clickedItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String key = trailerKeys.get(position);
        ImageButton trailer = holder.trailerButton;


    }

    @Override
    public int getItemCount() {
        if(null==trailerKeys){
            return 0;
        }
        return trailerKeys.size();
    }

    public void setTrailerData(List<String> keysDta) {
        trailerKeys = keysDta;
        notifyDataSetChanged();
    }
}
