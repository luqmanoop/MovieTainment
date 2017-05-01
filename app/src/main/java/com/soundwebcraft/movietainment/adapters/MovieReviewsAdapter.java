package com.soundwebcraft.movietainment.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soundwebcraft.movietainment.R;
import com.soundwebcraft.movietainment.models.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ReviewsViewHolder> {
    private Context mContext;
    private List<Movie.MovieReviews> mReviews;

    public MovieReviewsAdapter(Context context, List<Movie.MovieReviews> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public MovieReviewsAdapter.ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_movie_review, parent, false);

        return (new ReviewsViewHolder(view));
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.ReviewsViewHolder holder, int position) {
        Movie.MovieReviews review = mReviews.get(position);
        TextView avatar = holder.avatar;
        TextView author = holder.author;
        TextView comment = holder.comment;

        author.setText(review.getAuthor());
        comment.setText(review.getContent());

        //get first letter of each String item
        String firstLetter = String.valueOf(review.getAuthor().charAt(0)).toUpperCase();
        avatar.setText(firstLetter);
        GradientDrawable circleAvatar = (GradientDrawable) avatar.getBackground();
        circleAvatar.setColor(randomAvatarColor());
    }

    private int randomAvatarColor () {
        int avatarColorResourceId;
        int gen = (int) Math.floor((Math.random() * 6) + 1);

        switch (gen) {
            case 1:
                avatarColorResourceId = R.color.avatar1;
                break;
            case 2:
                avatarColorResourceId = R.color.avatar2;
                break;
            case 5:
                avatarColorResourceId = R.color.avatar3;
                break;
            case 4:
                avatarColorResourceId = R.color.avatar4;
                break;
            case 3:
                avatarColorResourceId = R.color.avatar5;
                break;
            case 6:
                avatarColorResourceId = R.color.avatar6;
                break;
            default:
                avatarColorResourceId = R.color.avatar3;
                break;
        }
        return ContextCompat.getColor(mContext, avatarColorResourceId);
    }
    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.avatar)
        TextView avatar;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.comment)
        TextView comment;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
