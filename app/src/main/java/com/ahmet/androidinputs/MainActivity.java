package com.ahmet.androidinputs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import com.ahmet.androidinputs.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

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
    private String currentDpadState = "D Pad X: 0\nD Pad Y: 0";

    private String deviceInfo = "Device: N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        updateDisplay();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputDevice device = event.getDevice();
        if (isController(device)) {
            handleButtonPress(event);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        InputDevice device = motionEvent.getDevice();
        if (isController(device)) {
            handleJoystickAndTrigger(motionEvent);
            return true;
        }

        return super.dispatchGenericMotionEvent(motionEvent);
    }

//    @Override
//    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
//        return super.dispatchPopulateAccessibilityEvent(event);
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return super.dispatchTouchEvent(ev);
//    }

    private void handleButtonPress(KeyEvent keyEvent) {
        String buttonName = KeyEvent.keyCodeToString(keyEvent.getKeyCode());
        if (buttonName != null) {
            currentButtonState = "Button: " + buttonName + " State: " + (keyEvent.getAction() == KeyEvent.ACTION_DOWN ? "Pressed" : "Released");
            updateDeviceInfo(keyEvent.getDevice());
            updateDisplay();
        }
        else{
            currentButtonState = "Button Name N/A State: " + (keyEvent.getAction() == KeyEvent.ACTION_DOWN ? "Pressed" : "Released");
            updateDeviceInfo(keyEvent.getDevice());
            updateDisplay();
        }
    }

    private void handleJoystickAndTrigger(MotionEvent motionEvent) {
        float leftJoystickX = motionEvent.getAxisValue(MotionEvent.AXIS_X);
        float leftJoystickY = motionEvent.getAxisValue(MotionEvent.AXIS_Y);
        float rightJoystickX = motionEvent.getAxisValue(MotionEvent.AXIS_Z);
        float rightJoystickY = motionEvent.getAxisValue(MotionEvent.AXIS_RZ);
        float leftTrigger = motionEvent.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        float rightTrigger = motionEvent.getAxisValue(MotionEvent.AXIS_RTRIGGER);

        float dPadX = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
        float dPadY = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);


        if (dPadX == 1) {
            handleButtonPress(new KeyEvent(motionEvent.getAction() , KeyEvent.KEYCODE_DPAD_RIGHT));
        }
        else if (dPadX == -1) {
            handleButtonPress(new KeyEvent(motionEvent.getAction() , KeyEvent.KEYCODE_DPAD_LEFT));
        }

        if (dPadY == 1) {
            handleButtonPress(new KeyEvent(motionEvent.getAction() , KeyEvent.KEYCODE_DPAD_DOWN));
        }
        else if (dPadY == -1) {
            handleButtonPress(new KeyEvent(motionEvent.getAction() , KeyEvent.KEYCODE_DPAD_UP));
        }

        currentJoystickState = String.format("Left Joystick: (%.2f, %.2f)\nRight Joystick: (%.2f, %.2f)",
                leftJoystickX, leftJoystickY, rightJoystickX, rightJoystickY);

        currentTriggerState = String.format("Left Trigger: %.2f\nRight Trigger: %.2f", leftTrigger, rightTrigger);

        currentDpadState = String.format("D Pad X: %.2f\nD Pad Y: %.2f", dPadX, dPadY);

        updateDeviceInfo(motionEvent.getDevice());
        updateDisplay();
    }

    private void updateDisplay() {
        String displayText = String.format("%s\n%s\n%s\n%s\n%s", currentButtonState, currentJoystickState, currentTriggerState, currentDpadState, deviceInfo);
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