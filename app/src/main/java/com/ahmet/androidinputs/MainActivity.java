package com.ahmet.androidinputs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.ahmet.androidinputs.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "XboxController";

    // Xbox Controller ve Logitech cihazlarının Vendor ve Product ID'leri
    private static final int VENDOR_ID_MICROSOFT = 0x045E;
    private static final int PRODUCT_ID_XBOX_CONTROLLER = 0x02FD;
    private static final int VENDOR_ID_LOGITECH = 1133;
    private static final int PRODUCT_ID_LOGITECH_XBOX_CONTROLLER = 49695;
    private static final int PRODUCT_ID_LOGITECH_GENERIC_CONTROLLER = 49689;

    private String currentButtonState = "Button: N/A";
    private String currentJoystickState = "Left Joystick: (0, 0)\nRight Joystick: (0, 0)";
    private String currentTriggerState = "Left Trigger: 0\nRight Trigger: 0";
    private String deviceInfo = "Device: N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        updateDisplay();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        InputDevice device = event.getDevice();
        if (isController(device)) {
            handleButtonPress(keyCode, device);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        InputDevice device = event.getDevice();
        if (isController(device)) {
            handleJoystickAndTrigger(event, device);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private void handleButtonPress(int keyCode, InputDevice device) {
        String buttonName = KeyEvent.keyCodeToString(keyCode);
        if (buttonName != null) {
            currentButtonState = "Button: " + buttonName + " Pressed";
            updateDeviceInfo(device);
            updateDisplay();
            Log.d(TAG, buttonName + " Pressed");
        }
    }

    private void handleJoystickAndTrigger(MotionEvent event, InputDevice device) {
        float leftJoystickX = event.getAxisValue(MotionEvent.AXIS_X);
        float leftJoystickY = event.getAxisValue(MotionEvent.AXIS_Y);
        float rightJoystickX = event.getAxisValue(MotionEvent.AXIS_Z);
        float rightJoystickY = event.getAxisValue(MotionEvent.AXIS_RZ);
        float leftTrigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        float rightTrigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);

        currentJoystickState = String.format("Left Joystick: (%.2f, %.2f)\nRight Joystick: (%.2f, %.2f)",
                leftJoystickX, leftJoystickY, rightJoystickX, rightJoystickY);
        currentTriggerState = String.format("Left Trigger: %.2f\nRight Trigger: %.2f", leftTrigger, rightTrigger);
        updateDeviceInfo(device);
        updateDisplay();
    }

    private void updateDisplay() {
        String displayText = String.format("%s\n%s\n%s\n%s", currentButtonState, currentJoystickState, currentTriggerState, deviceInfo);
        binding.debugTextView.setText(displayText);
    }

    private void updateDeviceInfo(InputDevice device) {
        if (device != null) {
            deviceInfo = String.format("Device: %s\nVendor ID: %d, Product ID: %d",
                    device.getName(), device.getVendorId(), device.getProductId());
        }
    }

    private boolean isController(InputDevice device) {
        if (device != null) {
            int vendorId = device.getVendorId();
            int productId = device.getProductId();
            return (vendorId == VENDOR_ID_MICROSOFT && productId == PRODUCT_ID_XBOX_CONTROLLER) ||
                    (vendorId == VENDOR_ID_LOGITECH &&
                            (productId == PRODUCT_ID_LOGITECH_XBOX_CONTROLLER || productId == PRODUCT_ID_LOGITECH_GENERIC_CONTROLLER));
        }
        return false;
    }
}