package com.pbm;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;

public interface OnTaskCompleted{
    void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException;
}