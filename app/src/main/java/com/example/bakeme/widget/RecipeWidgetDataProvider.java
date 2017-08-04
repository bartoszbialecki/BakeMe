package com.example.bakeme.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.bakeme.R;
import com.example.bakeme.model.Ingredient;
import com.example.bakeme.model.Recipe;
import com.example.bakeme.presentation.fragment.RecipeDetailFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class RecipeWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    // region VARIABLES
    private Context mContext;
    private Recipe mRecipe;
    private WidgetManager mWidgetManager;
    private NumberFormat mQuantityFormatter = new DecimalFormat("#.##");
    // endregion

    // region CONSTRUCTORS
    public RecipeWidgetDataProvider(Context context) {
        mContext = context;
    }
    // endregion

    // region REMOTE VIEWS FACTORY METHODS
    @Override
    public void onCreate() {
        mWidgetManager = new WidgetManager(mContext);
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        mRecipe = mWidgetManager.getRecipe();
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mRecipe != null) {
            if (mRecipe.getIngredients() != null) {
                return mRecipe.getIngredients().size();
            }
        }

        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_wigdet_recipe_ingredient);

        if (mRecipe != null) {
            if (mRecipe.getIngredients() != null) {
                Ingredient ingredient = mRecipe.getIngredients().get(position);

                views.setTextViewText(R.id.recipe_name_text_view, mRecipe.getName());
                views.setTextViewText(R.id.recipe_ingredient_name, ingredient.getIngredient());
                views.setTextViewText(R.id.recipe_ingredient_quantity,
                        String.format(Locale.getDefault(), "%s %s", mQuantityFormatter.format(ingredient.getQuantity()),
                                ingredient.getMeasure()));

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(RecipeDetailFragment.ARG_RECIPE_ID, mRecipe.getId());
                views.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent);
            }
        }

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mRecipe != null ? mRecipe.getId() : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    // endregion
}
