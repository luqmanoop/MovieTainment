package com.soundwebcraft.movietainment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soundwebcraft.movietainment.R;
import com.soundwebcraft.movietainment.db.model.Favorite;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private Context mContext;
    private List<Favorite> mFavorites;

    public FavoritesAdapter(Context context, List<Favorite> favorites) {
        mContext = context;
        mFavorites = favorites;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Favorite favorite = mFavorites.get(position);

        TextView favId = holder.favId;
        TextView favTitle = holder.favTitle;

        favId.setText(String.valueOf(favorite.getId()));
        favTitle.setText(favorite.getTitle());
    }

    @Override
    public int getItemCount() {
        return mFavorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.fav_id)
        TextView favId;
        @BindView(R.id.fav_title)
        TextView favTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
