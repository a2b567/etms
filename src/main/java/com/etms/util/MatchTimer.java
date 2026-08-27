package com.etms.util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;

/**
 * A simple match timer that can be paused, resumed, and reset.
 * Fires a callback every second.
 */
public class MatchTimer {
    private Timer timer;
    private Instant startTime;
    private Duration elapsed = Duration.ZERO;
    private boolean running = false;
    private TimerCallback callback;

    public interface TimerCallback {
        void onTick(Duration currentElapsed);
    }

    public MatchTimer(TimerCallback callback) {
        this.callback = callback;
        timer = new Timer(1000, e -> {
            if (running) {
                elapsed = Duration.between(startTime, Instant.now()).plus(elapsed);
                startTime = Instant.now(); // reset for next interval
                callback.onTick(elapsed);
            }
        });
    }

    public void start() {
        if (!running) {
            startTime = Instant.now();
            running = true;
            timer.start();
        }
    }

    public void pause() {
        if (running) {
            elapsed = Duration.between(startTime, Instant.now()).plus(elapsed);
            running = false;
            timer.stop();
        }
    }

    public void resume() {
        start();
    }

    public void reset() {
        timer.stop();
        running = false;
        elapsed = Duration.ZERO;
        callback.onTick(elapsed);
    }

    public boolean isRunning() {
        return running;
    }

    public Duration getElapsed() {
        if (running) {
            return Duration.between(startTime, Instant.now()).plus(elapsed);
        }
        return elapsed;
    }
}