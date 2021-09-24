package com.example.trivia.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public static final String HIGHEST_SCORE = "Highest Score";
    public static final String CURRENT_INDEX = "Current Index";
    private SharedPreferences preferences;

    public Preferences(Activity context) {
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);
    }
    public void saveHighestScore(int score){
        int currentScore = score;
        int lastScore = preferences.getInt(HIGHEST_SCORE, 0);

        if(lastScore < currentScore) {
            preferences.edit().putInt(HIGHEST_SCORE, currentScore).apply();
        }
    }
    public int getHighestScore() {
        return preferences.getInt(HIGHEST_SCORE, 0);
    }
    public void clearScore() {
        preferences.edit().clear().apply();
    }

    public void setState(int currentIndex) {
        preferences.edit().putInt(CURRENT_INDEX, currentIndex).apply();
    }
    public int getState() {
        return preferences.getInt(CURRENT_INDEX,0);
    }

}
