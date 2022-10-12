package com.example.stopwatch

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    lateinit var chronometer: Chronometer
    var running = false
    var paused = false
    var offset: Long = 0

    val OFFSET_KEY = "offset"
    val RUNNING_KEY = "running"
    val BASE_KEY = "base"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get Reference of all the UI elements
        chronometer = findViewById<Chronometer>(R.id.stopwatch)
        var startButton = findViewById<Button>(R.id.start_button)
        var pauseButton = findViewById<Button>(R.id.pause_button)
        var resetButton = findViewById<Button>(R.id.reset_button)
        var input = findViewById<EditText>(R.id.baseEditText)

        //get values from savedInstanceState
        if (savedInstanceState !== null) {
            offset = savedInstanceState.getLong(OFFSET_KEY)
            running = savedInstanceState.getBoolean(RUNNING_KEY)
            if (running) {
                chronometer.base = savedInstanceState.getLong(BASE_KEY)
                chronometer.start()
            } else {
                setBaseTime()
            }
        }

        startButton.setOnClickListener(View.OnClickListener {
            if (!running) {
                if (input.text.toString() != "" && !paused) {    //checking if user has entered any value in editText and then passing that value to set to base of chronometer.
                    setBaseTime(Integer.parseInt(input.text.toString()))
                } else {
                    paused = false
                    setBaseTime()   // If user has not entered any value just start by default from 0.
                }

                chronometer.start()
                running = true
            }
        })

        pauseButton.setOnClickListener(View.OnClickListener {
            if (running) {
                saveOffset()
                chronometer.stop();
                running = false
                paused = true
            }

        })

        resetButton.setOnClickListener(View.OnClickListener {
            offset = 0
            setBaseTime()

        })

        chronometer.setOnChronometerTickListener {
            println(it.text.toString())
            if (running) {
                if (it.text.toString() == "00:00") {  // when chronometer reaches 00:00 it will be stopped.
                    chronometer.stop()
                    offset = 0
                    running = false
                    paused = false
                }
            }

        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putLong(OFFSET_KEY, offset)
        savedInstanceState.putBoolean(RUNNING_KEY, running)
        savedInstanceState.putLong(BASE_KEY, chronometer.base)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        if (running) {
            saveOffset()
            chronometer.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (running) {
            setBaseTime()
            chronometer.start()
            offset = 0
        }
    }

    private fun saveOffset() {
        offset = SystemClock.elapsedRealtime() - chronometer.base

    }

    private fun setBaseTime(time: Int = 0) {  // By default the value of time will be 0.
        if (time != 0 && !running) {
            chronometer.base =
                SystemClock.elapsedRealtime() + ((time * 1000) + 1)  //The value entered will be set to the base of chronometer.
        } else {
            chronometer.base =
                SystemClock.elapsedRealtime() - offset   // if user has not entered any value in edit text then by default start from 0.
        }
    }

}