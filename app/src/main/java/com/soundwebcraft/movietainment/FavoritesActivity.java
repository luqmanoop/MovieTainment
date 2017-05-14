package com.soundwebcraft.movietainment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soundwebcraft.movietainment.adapters.FavoritesAdapter;
import com.soundwebcraft.movietainment.db.model.Favorite;
import com.soundwebcraft.movietainment.networking.utils.TmdbUtils;
import com.soundwebcraft.movietainment.utils.AppUtils;
import com.soundwebcraft.movietainment.utils.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.soundwebcraft.movietainment.db.FavoritesContract.FavoritesEntry.BASE_CONTENT_URI;

public class FavoritesActivity extends AppCompatActivity {

    @BindView(R.id.rv_favorites)
    EmptyRecyclerView favoriteRecycler;

    private FavoritesAdapter adapter;
    private List<Favorite> mFavorites = new ArrayList<>();
    private Context mContext;


    @BindView(R.id.empty_view_tv)
    TextView emptyViewTextView;
    @BindView(R.id.empty_view_iv)
    ImageView emptyViewImageView;
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    private boolean isLoading = true;
    private static final String TAG = FavoritesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ButterKnife.bind(this);

        mContext = this;
        adapter = new FavoritesAdapter(mContext, mFavorites);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Favorite");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View emptyView = findViewById(R.id.empty_view);
        favoriteRecycler.setEmptyView(emptyView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        favoriteRecycler.setLayoutManager(linearLayoutManager);
        favoriteRecycler.setHasFixedSize(true);
        favoriteRecycler.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        favoriteRecycler.setAdapter(adapter);

        loadFavorites();
    }

    void loadFavorites() {
        Cursor cursor = getContentResolver().query(BASE_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (isLoading) {
                loadingIndicator.setVisibility(View.GONE);
                isLoading = false;
            }
            if (cursor.getCount() <= 0) {
                TmdbUtils.emptyStateNoData(emptyViewImageView, emptyViewTextView, getString(R.string.no_favorites));
            } else {
                while (cursor.moveToNext()) {
                    mFavorites.add(new Favorite(
                            cursor.getInt(0),
                            cursor.getString(1)
                    ));
                }
                AppUtils.updateRecycler(adapter, mFavorites);
            }
            cursor.close();
        }
    }
}
