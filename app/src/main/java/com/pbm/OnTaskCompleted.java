package com.pbm;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

interface OnTaskCompleted {
    void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException;
}