package com.example.bakeme.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bakeme.R;
import com.example.bakeme.adapter.RecipeListAdapter;
import com.example.bakeme.app.App;
import com.example.bakeme.data.Repository;
import com.example.bakeme.model.Recipe;
import com.example.bakeme.presentation.fragment.RecipeDetailFragment;
import com.example.bakeme.util.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.mateware.snacky.Snacky;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecipeListActivity extends AppCompatActivity {
    // region CONSTANTS
    private static final String STATE_VISIBLE_POSITION = "position";
    // endregion

    // region VARIABLES
    @BindView(R.id.recipe_list)
    RecyclerView recipeListRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_text_view)
    TextView emptyTextView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    private Unbinder mUnbinder;
    private GridLayoutManager mLayoutManager;
    private RecipeListAdapter mAdapter;
    private int mCurrentVisiblePosition = -1;
    private Repository mRepository;
    // endregion

    // region LIFE CYCLE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_list);

        mUnbinder = ButterKnife.bind(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_VISIBLE_POSITION)) {
                mCurrentVisiblePosition = savedInstanceState.getInt(STATE_VISIBLE_POSITION);
            }
        }

        mRepository = ((App) getApplication()).getRepository();

        setupActionBar();
        setupUI();
        setupAdapter();
        fetchRecipes(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnbinder.unbind();
    }
    // endregion

    // region STATE METHODS
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mAdapter.getItemCount() > 0) {
            outState.putInt(STATE_VISIBLE_POSITION, mLayoutManager.findFirstVisibleItemPosition());
        }

        super.onSaveInstanceState(outState);
    }
    // endregion

    // region PRIVATE METHODS
    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    private void setupUI() {
        mLayoutManager = new GridLayoutManager(this, numberOfColumns());
        recipeListRecyclerView.setLayoutManager(mLayoutManager);

        refreshLayout.setColorSchemeResources(R.color.colorAccent);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    private void setupAdapter() {
        mAdapter = new RecipeListAdapter();

        mAdapter.setOnItemClickListener(new RecipeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Recipe recipe = mAdapter.getRecipe(position);

                if (recipe != null) {
                    Intent intent = new Intent(RecipeListActivity.this, RecipeDetailActivity.class);
                    intent.putExtra(RecipeDetailFragment.ARG_RECIPE_ID, recipe.getId());
                    startActivity(intent);
                }
            }
        });

        recipeListRecyclerView.setAdapter(mAdapter);
    }

    private void fetchRecipes(boolean forceLoad) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNoConnectionMessage();

            return;
        }

        mRepository.getRecipes(forceLoad)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Recipe>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<Recipe> recipes) {
                        hideProgressBar();
                        showRecipes(recipes);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        refreshLayout.setRefreshing(false);
                        hideProgressBar();
                        showErrorMessage(e.getLocalizedMessage());
                    }
                });
    }

    private void refreshList() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNoConnectionMessage();
            refreshLayout.setRefreshing(false);

            return;
        }

        clearList();

        mRepository.refreshList();
        fetchRecipes(true);
    }

    private void showRecipes(List<Recipe> recipes) {
        if (mAdapter != null) {
            mAdapter.addRecipes(recipes);

            if (mAdapter.getItemCount() == 0) {
                showEmptyResultsView();
            } else {
                hideEmptyResultsView();
            }

            if (mCurrentVisiblePosition != -1) {
                recipeListRecyclerView.scrollToPosition(mCurrentVisiblePosition);
                mCurrentVisiblePosition = -1;
            }
        }

        refreshLayout.setRefreshing(false);
    }

    private void clearList() {
        if (mAdapter != null) {
            mAdapter.clear();
            recipeListRecyclerView.scrollToPosition(0);
        }
    }

    private void showEmptyResultsView() {
        if (emptyTextView.getVisibility() == View.GONE) {
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideEmptyResultsView() {
        if (emptyTextView.getVisibility() == View.VISIBLE) {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 300;
        int width = displayMetrics.widthPixels;

        return width / widthDivider;
    }

    private void showProgressBar() {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showErrorMessage(String errorMessage) {
        Snacky.builder()
                .setActivty(this)
                .setText(errorMessage)
                .error()
                .show();
    }

    private void showNoConnectionMessage() {
        showErrorMessage(getString(R.string.no_network));
    }
    // endregion
}
