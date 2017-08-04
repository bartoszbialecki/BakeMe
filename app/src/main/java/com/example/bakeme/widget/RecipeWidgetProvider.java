package com.example.bakeme.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.example.bakeme.R;
import com.example.bakeme.model.Recipe;
import com.example.bakeme.presentation.activity.RecipeDetailActivity;
import com.example.bakeme.presentation.activity.RecipeListActivity;

public class RecipeWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetManager manager = new WidgetManager(context);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            Intent serviceIntent = new Intent(context, RecipeWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            views.setRemoteAdapter(R.id.ingredients_list_view, serviceIntent);

            Recipe recipe = manager.getRecipe();

            if (recipe != null) {
                views.setTextViewText(R.id.recipe_name_text_view, recipe.getName());
            } else {
                views.setTextViewText(R.id.recipe_name_text_view, context.getString(R.string.no_recipe));
            }

            Intent intent = new Intent(context, RecipeListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.recipe_name_text_view, pendingIntent);

            Intent clickIntentTemplate = new Intent(context, RecipeDetailActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.ingredients_list_view, clickPendingIntentTemplate);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.ingredients_list_view);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
