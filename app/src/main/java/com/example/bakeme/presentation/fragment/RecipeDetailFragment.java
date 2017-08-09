package com.example.bakeme.presentation.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakeme.R;
import com.example.bakeme.adapter.RecipeIngredientsAdapter;
import com.example.bakeme.adapter.RecipeStepsAdapter;
import com.example.bakeme.app.App;
import com.example.bakeme.data.Repository;
import com.example.bakeme.model.Recipe;
import com.example.bakeme.model.Step;
import com.example.bakeme.widget.WidgetManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeDetailFragment extends Fragment {
    // region CONSTANTS
    public static final String ARG_RECIPE_ID = "recipe_id";
    private static final String STATE_SELECTED_STEP_POSITION = "selected_step_position";
    // endregion

    // region VARIABLES
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;
    @BindView(R.id.ingredients_recycler_view)
    RecyclerView ingredientsRecyclerView;
    @BindView(R.id.recipe_steps_recycler_view)
    RecyclerView stepsRecyclerView;

    private Recipe mRecipe;
    private Unbinder mUnbinder;
    private RecipeStepsAdapter mStepsAdapter;
    private OnRecipeStepSelectedListener mListener;
    private int mSelectedStepPosition = -1;
    private WidgetManager mWidgetManager;
    private Repository mRepository;
    // endregion

    // region CONSTRUCTOR
    public RecipeDetailFragment() {
    }
    // endregion

    // region LISTENERS
    public interface OnRecipeStepSelectedListener {
        void onStepSelected(int recipeId, int stepId);
    }
    // endregion

    // region LIFE CYCLE METHODS
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = ((App) getActivity().getApplication()).getRepository();

        setHasOptionsMenu(true);

        initArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        mWidgetManager = new WidgetManager(getActivity());

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_SELECTED_STEP_POSITION)) {
                mSelectedStepPosition = savedInstanceState.getInt(STATE_SELECTED_STEP_POSITION);
            }
        }

        setupUI();
        setupAdapters();
        updateUI();

        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onActivityAttached(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        onActivityAttached(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }
    // endregion

    // region STATE METHODS
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedStepPosition > -1) {
            outState.putInt(STATE_SELECTED_STEP_POSITION, mSelectedStepPosition);
        }

        super.onSaveInstanceState(outState);
    }
    // endregion

    // region MENU METHODS
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_detail, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_to_widget_action) {
            if (mRecipe != null) {
                mWidgetManager.addRecipientToWidget(mRecipe);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // endregion

    // region PUBLIC METHODS
    public static RecipeDetailFragment newInstance(int recipeId) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();

        if (recipeId > 0) {
            args.putInt(ARG_RECIPE_ID, recipeId);
        }

        fragment.setArguments(args);

        return fragment;
    }
    // endregion

    // region PRIVATE METHODS
    private void initArguments() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.containsKey(ARG_RECIPE_ID)) {
                int recipeId = bundle.getInt(ARG_RECIPE_ID, 0);

                if (recipeId > 0) {
                    mRecipe = mRepository.getRecipe(recipeId);
                }
            }
        }
    }

    private void setupAdapters() {
        RecipeIngredientsAdapter ingredientsAdapter = new RecipeIngredientsAdapter();
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        mStepsAdapter = new RecipeStepsAdapter(getResources().getBoolean(R.bool.isTablet));

        mStepsAdapter.setOnItemClickListener(new RecipeStepsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Step step = mStepsAdapter.getStep(position);

                if (step != null) {
                    mSelectedStepPosition = position;

                    if (mListener != null) {
                        mListener.onStepSelected(mRecipe.getId(), step.getId());
                    }
                }
            }
        });

        stepsRecyclerView.setAdapter(mStepsAdapter);

        if (mRecipe != null) {
            ingredientsAdapter.addIngredients(mRecipe.getIngredients());
            mStepsAdapter.addSteps(mRecipe.getSteps());

            if (mSelectedStepPosition > -1) {
                mStepsAdapter.setSelectedItem(mSelectedStepPosition);

                stepsRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        View viewItem = stepsRecyclerView.getLayoutManager().findViewByPosition(mSelectedStepPosition);
                        float y = viewItem.getY();

                        scrollView.smoothScrollTo(0, (int) y);
                    }
                });
            }
        }
    }

    private void setupUI() {
        if (null != ingredientsRecyclerView) {
            ingredientsRecyclerView.setNestedScrollingEnabled(false);
            ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ingredientsRecyclerView.setHasFixedSize(true);
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            ingredientsRecyclerView.addItemDecoration(itemDecoration);
        }

        if (null != stepsRecyclerView) {
            stepsRecyclerView.setNestedScrollingEnabled(false);
            stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            stepsRecyclerView.setHasFixedSize(true);
        }
    }

    private void updateUI() {
        if (mRecipe != null) {
            getActivity().setTitle(mRecipe.getName());
        }
    }

    private void onActivityAttached(Context context) {
        if (null != mListener) {
            return;
        }

        try {
            mListener = (OnRecipeStepSelectedListener) context;
        } catch (ClassCastException ignored) {
        }

        if (mListener == null) {
            mListener = (OnRecipeStepSelectedListener) getTargetFragment();
        }
    }
    // endregion
}
