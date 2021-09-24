package com.example.trivia.data;

import android.util.Log;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    ArrayList<Question> questionArrayList = new ArrayList<>();
    String URL = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
    private final String TAG = "Repository";

    public List<Question> getQuestions(final AnswerListAsyncResponse callBack){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (URL, response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            Question question = new Question(response.getJSONArray(i).getString(0),
                                    response.getJSONArray(i).getBoolean(1));
                            questionArrayList.add(question);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(callBack != null){
                        callBack.OnTaskCompleted(questionArrayList);
                    }
                    Log.d(TAG, "onResponse: " + response.length() + " ->" + response.toString());
                }, error -> Log.d(TAG, "onErrorResponse: !Failed"));

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return questionArrayList;
    }
}
