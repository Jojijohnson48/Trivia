package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Preferences;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Score score;
    private Preferences prefs;
    private final String TAG = "MainActivity: ";
    private int currentQuestionIndex = 0;
    private int finalIndex = 20;
    private static final String MESSAGE_ID = "last_score";
    private int scoreCounter = 0;
    List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        score = new Score();
        prefs = new Preferences(MainActivity.this);

        // sets up the question index -> default: 1
        currentQuestionIndex = prefs.getState();


        // sets up the highest Score -> default: 0
        binding.highestScore.setText(String.format(getString(R.string.high_score), prefs.getHighestScore()));

        questions = new Repository().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void OnTaskCompleted(ArrayList<Question> questionArrayList) {
                Log.d(TAG, "OnTaskCompleted: " + (currentQuestionIndex + 1)+ "-->"
                        + questions.get(currentQuestionIndex).isAnswerTrue() + "-->"
                        + questions.get(currentQuestionIndex).getQuestion());
                binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted),
                        currentQuestionIndex+1, finalIndex));
                binding.questionTextView.setText(questions.get(currentQuestionIndex).getQuestion());
            }
        });


        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNextQuestion();
            }
        });

        binding.buttonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               boolean res = checkAnswer(true);

               if(!res){
                   shakeAnimation();
               }
               else {
                   fadeAnimation();
               }
                showCurrentScore();
            }
        });

        binding.buttonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res = checkAnswer(false);

                if(!res){
                    shakeAnimation();
                }
                else {
                    fadeAnimation();
                }
                showCurrentScore();

            }
        });

        binding.clearScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.clearScore();
                currentQuestionIndex = prefs.getState();
                binding.highestScore.setText(String.format(getString(R.string.high_score), prefs.getHighestScore()));
                binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted),
                        currentQuestionIndex+1, finalIndex));
                binding.questionTextView.setText(questions.get(currentQuestionIndex).getQuestion());
                binding.buttonNext.setEnabled(true);
            }
        });

    }

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1);
        updateQuestion();
    }

    private boolean checkAnswer(boolean answerGiven) {
        boolean answerTrue = questions.get(currentQuestionIndex).isAnswerTrue();
        int messageId = 0;
        boolean res = true;
        if(answerTrue == answerGiven){
            messageId = R.string.correctAnswer;
            addPoints();
        }
        else {
            messageId = R.string.incorrectAnswer;
            res = false;
            deductPoints();
        }
        Snackbar.make(binding.questionTextView,messageId,
                Snackbar.LENGTH_SHORT).show();
        return res;
    }

    private void updateQuestion() {
        if(currentQuestionIndex<finalIndex){
            binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted),
                    currentQuestionIndex + 1, finalIndex));
            Log.d(TAG, "onClick: " + (currentQuestionIndex+1) + "-->"
                    + questions.get(currentQuestionIndex).isAnswerTrue() + "-->"
                    + questions.get(currentQuestionIndex).getQuestion());
            String question = questions.get(currentQuestionIndex).getQuestion();
            binding.questionTextView.setText(question);
            if(currentQuestionIndex >= finalIndex-1) {
                binding.buttonNext.setEnabled(false);
                currentQuestionIndex = finalIndex;
            }
        }
    }
    private void showCurrentScore() {
        int scoreValue  = score.getScore();
        binding.currentScore.setText(String.format(getString(R.string.currentScore), scoreValue));
    }
    private void addPoints(){
        scoreCounter += 5;
        score.setScore(scoreCounter);
        Log.d(TAG, "addPoints: " + score.getScore());
    }
    private void deductPoints() {
        scoreCounter -= 5;
        if(scoreCounter < 0) {
            scoreCounter = 0;
        }
        score.setScore(scoreCounter);
        Log.d(TAG, "deductPoints: " + score.getScore());
    }

    private void fadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setDuration(200);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion(); //will go to the next question once answered
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    /*AlphaAnimation-->An animation that controls the alpha level of an object. Useful for fading
    things in and out. This animation ends up changing the alpha property of a Transformation.
    Constructor to use when building an AlphaAnimation from code:
    public AlphaAnimation (float fromAlpha, float toAlpha)
    fromAlpha -> Starting alpha value for the animation, where 1.0 means fully opaque and
                 0.0 means fully transparent.
     toAlpha -> Ending alpha value for the animation.
    */

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        binding.cardView.startAnimation(shake);
        /* use startAnimation(shake) If you want the animation to play immediately, use startAnimation.
         This method provides allows fine-grained control over the start time and invalidation,
         but you must make sure that:
                    1) the animation has a start time set,
                    2) the view will be invalidated when the animation is supposed to start
        * otherwise use setAnimation -> Sets the next animation to play for this view.
        * But view animation does not start yet.*/
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}