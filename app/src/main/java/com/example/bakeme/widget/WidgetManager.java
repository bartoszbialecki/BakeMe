package com.example.bakeme.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.bakeme.R;
import com.example.bakeme.model.Recipe;
import com.google.gson.Gson;

public class WidgetManager {
    // region CONSTANTS
    private static final String PREFERENCES_NAME = "RecipeWidget";
    private static final String PREF_KEY_RECIPE = "recipe";
    // endregion

    // region VARIABLES
    private Context mContext;
    // endregion

    // region CONSTRUCTOR
    public WidgetManager(Context context) {
        mContext = context;
    }
    // endregion

    // region PUBLIC METHODS
    public void addRecipientToWidget(Recipe recipe) {
        Gson gson = new Gson();
        String json = gson.toJson(recipe);
        SharedPreferences preferences = getPreferences();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_KEY_RECIPE, json);
        editor.apply();

        updateWidget();
    }

    public Recipe getRecipe() {
        String json = getPreferences().getString(PREF_KEY_RECIPE, null);

        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();

            try {
                return gson.fromJson(json, Recipe.class);
            } catch (Exception ignored) {

            }
        }

         return null;
    }
    // endregion

    // region PRIVATE METHODS
    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, RecipeWidgetProvider.class));
        RecipeWidgetProvider appWidget = new RecipeWidgetProvider();
        appWidget.onUpdate(mContext, appWidgetManager, ids);

        Intent intent = new Intent(mContext, RecipeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        mContext.sendBroadcast(intent);
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.ingredients_list_view);
    }
    // endregion
}