package com.example.fitness;


import androidx.test.espresso.IdlingResource;

public class MyIdlingResource implements IdlingResource {
    private ResourceCallback resourceCallback;
    private boolean isIdle;

    @Override
    public String getName() {
        return MyIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(IdlingResource.ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public void setIdle(boolean isIdle) {
        this.isIdle = isIdle;
        if (isIdle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }
}

