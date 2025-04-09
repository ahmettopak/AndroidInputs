package com.ahmet.androidinputs;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 04/09/2025
 */

public class InputEventEntry {
    public String deviceName;
    public int deviceId;
    public String eventType;
    public String controlName;
    public float value;

    public InputEventEntry(String deviceName, int deviceId, String eventType, String controlName, float value) {
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.controlName = controlName;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s = %.2f", eventType, deviceName, controlName, value);
    }
}
