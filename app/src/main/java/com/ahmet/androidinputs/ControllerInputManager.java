package com.ahmet.androidinputs;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 04/09/2025
 */

public class ControllerInputManager {
    private final List<InputEventEntry> eventList = new ArrayList<>();
    private final int maxSize = 200;
    private final Consumer<List<InputEventEntry>> onUpdateCallback;

    public ControllerInputManager(Consumer<List<InputEventEntry>> callback) {
        this.onUpdateCallback = callback;
    }

    public void handleMotionEvent(MotionEvent event) {
        InputDevice device = event.getDevice();
        if (device == null) return;

        for (InputDevice.MotionRange range : device.getMotionRanges()) {
            int axis = range.getAxis();
            float value = event.getAxisValue(axis);

            if (Math.abs(value) > 0.01f) {
                addEvent(new InputEventEntry(
                        device.getName(),
                        device.getId(),
                        "Motion",
                        MotionEvent.axisToString(axis),
                        value
                ));
            }
        }
    }

    public void handleKeyEvent(KeyEvent event) {
        InputDevice device = event.getDevice();
        if (device == null) return;

        String action = (event.getAction() == KeyEvent.ACTION_DOWN) ? "KeyDown" : "KeyUp";

        addEvent(new InputEventEntry(
                device.getName(),
                device.getId(),
                action,
                KeyEvent.keyCodeToString(event.getKeyCode()),
                1f
        ));
    }

    private void addEvent(InputEventEntry entry) {
        eventList.add(0, entry);
        if (eventList.size() > maxSize) {
            eventList.remove(eventList.size() - 1);
        }
        onUpdateCallback.accept(new ArrayList<>(eventList));
    }

    public void clear() {
        eventList.clear();
        onUpdateCallback.accept(new ArrayList<>(eventList));
    }
}
