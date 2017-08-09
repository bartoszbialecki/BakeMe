package com.example.bakeme.app;

import android.app.Application;

import com.example.bakeme.data.RecipesRepository;
import com.example.bakeme.data.Repository;

public class App extends Application {
    // region VARIABLES
    private Repository mRepository;
    // endregion

    // region APPLICATION METHODS
    @Override
    public void onCreate() {
        super.onCreate();

        mRepository = new RecipesRepository();
    }
    // endregion

    // region GETTERS AND SETTERS
    public Repository getRepository() {
        return mRepository;
    }

    public void setRepository(Repository repository) {
        mRepository = repository;
    }
    // endregion
}
