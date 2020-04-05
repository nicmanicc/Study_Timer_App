package com.example.studytimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    //Track the state the chronometer is in. (Running or not running)
    private boolean _running = false;
    private Chronometer _timer;
    //Tracks the time the chronometer has been running for.
    private long _timeElapsed;
    private TextView _textView;
    //Preference file for saving the time previously studied.
    SharedPreferences sharedPref;
    //Key to get time studied from preference file.
    String TIMER_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Retrieve chronometer view from activity
        _timer = findViewById(R.id.chronometer);
        //Retrieve textView from activity
        _textView = findViewById(R.id.textView);
        //Retrieve preference: "com.example.studytimer" file
        sharedPref = this.getSharedPreferences("com.example.studytimer",
                Context.MODE_PRIVATE);
        //Check for any previous study times stored in preference file
        checkSharedPref();
        //Check if the activity is being recreated through orientation change
        if (savedInstanceState != null)
        {
            //Restore and check if the chronometer was running or not
            _running = savedInstanceState.getBoolean("STATE_RUNNING");
            //Restore and set the time to what it was before orientation change
            _timer.setBase(SystemClock.elapsedRealtime() - savedInstanceState.getLong("ELAPSED_TIME"));
            //Update the time elapsed
            setTimeElapsed();
            //If the chronometer was running, start it again.
            if (_running)
            {
                _timer.start();
            }
        }
    }

    //Saves values that will be restored when the activity is restored after orientation change.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Get the chronometers current time elapsed at the time of orientation change
        if (_running)
        {
            setTimeElapsed();
        }
        //Store the chronometers running state to be restored
        outState.putBoolean("STATE_RUNNING", _running);
        //Store the chronometers elapsed time to be restored
        outState.putLong("ELAPSED_TIME", _timeElapsed);
    }
    //Starts the chronometer counting from 0 seconds or the point it stopped.
    public void startTimer(View view)
    {
        //Make sure chronometer isn't running already
        if (!_running)
        {
            //Set the correct time of the chronometer
            _timer.setBase(SystemClock.elapsedRealtime() - _timeElapsed);
            //Starts the chronometer counting
            _timer.start();
            _running = true;
        }
    }
    //Pauses the chronometer at the point it was counting.
    public void pauseTimer(View view)
    {
        //Make sure the chronometer isn't paused already
        if (_running)
        {
            //Stops the chronometer from counting up
            _timer.stop();
            //Updates the current time elapsed at the point of pausing
            setTimeElapsed();
            _running = false;
        }
    }
    //Resets the chronometer and saves the time it was reset at.
    public void resetTimer(View view)
    {
        //Stops the chronometer from counting
        _timer.stop();
        //Put the current time into the sharedPref file
        sharedPref.edit().putString(TIMER_TIME, _timer.getText().toString()).apply();
        //Change the text of the text view to show appropriate time
        editTextView(_timer.getText().toString());
        //Set chronometer back to 00:00
        _timer.setBase(SystemClock.elapsedRealtime());
        //Set time elapsed back to 0
        _timeElapsed = 0;
        _running = false;
    }
    //Updates the time that the chronometer has been running for.
    public void setTimeElapsed()
    {
        _timeElapsed = SystemClock.elapsedRealtime() - _timer.getBase();
    }
    //Checks if there is any stored previous time in the shared preference file
    public void checkSharedPref()
    {
        String prevTime = sharedPref.getString(TIMER_TIME, "00:00");
        //Sends the previous time studied to the text view
        editTextView(prevTime);
    }
    //Edits the text view to show the time the user previously studied for.
    @SuppressLint("SetTextI18n")
    public void editTextView(String time)
    {
        _textView.setText("You spent " + time + " studying last time.");
    }
}
