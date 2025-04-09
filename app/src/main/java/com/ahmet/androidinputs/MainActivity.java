package com.ahmet.androidinputs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ahmet.androidinputs.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements InputManager.InputDeviceListener {

    private ActivityMainBinding binding;
    private InputManager inputManager;
    private ControllerInputManager controllerInputManager;
    private InputEventAdapter adapter;

    private ArrayAdapter<String> deviceAdapter;
    private final List<String> deviceList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new InputEventAdapter(this, new ArrayList<>());
        binding.eventListView.setAdapter(adapter);

        controllerInputManager = new ControllerInputManager(updatedList -> runOnUiThread(() -> {
            adapter.clear();
            adapter.addAll(updatedList);
        }));

        binding.clearButton.setOnClickListener(v -> controllerInputManager.clear());

        inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);

        deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        binding.deviceListView.setAdapter(deviceAdapter);
        binding.deviceListView.setOnItemClickListener((parent, view, position, id) -> {
            int[] deviceIds = inputManager.getInputDeviceIds();
            if (position < deviceIds.length) {
                InputDevice device = inputManager.getInputDevice(deviceIds[position]);
                if (supportsVibration(device)) {
                    testVibration(device);
                } else {
                    Toast.makeText(this, "Bu cihaz titreşim desteklemiyor", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showConnectedDevices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inputManager.registerInputDeviceListener(this, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        inputManager.unregisterInputDeviceListener(this);
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        showConnectedDevices();
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        showConnectedDevices();
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        showConnectedDevices();
    }

    private void showConnectedDevices() {
        deviceList.clear();
        int[] deviceIds = inputManager.getInputDeviceIds();

        for (int id : deviceIds) {
            InputDevice device = inputManager.getInputDevice(id);
            if (device == null) continue;

            String deviceInfo = "🕹️ " + device.getName() +
                    "\nID: " + device.getId() +
                    " | Vendor: " + device.getVendorId() +
                    " | Product: " + device.getProductId() +
                    " | Descriptor: " + device.getDescriptor() +
                    "\nSources: " + getSourceNames(device.getSources());

            deviceList.add(deviceInfo);
        }

        if (deviceList.isEmpty()) {
            deviceList.add("🔌 Hiçbir cihaz bağlı değil.");
        }

        deviceAdapter.notifyDataSetChanged();
    }

    private String getSourceNames(int sources) {
        List<String> sourceNames = new ArrayList<>();
        if ((sources & InputDevice.SOURCE_JOYSTICK) != 0) sourceNames.add("JOYSTICK");
        if ((sources & InputDevice.SOURCE_GAMEPAD) != 0) sourceNames.add("GAMEPAD");
        if ((sources & InputDevice.SOURCE_DPAD) != 0) sourceNames.add("DPAD");
        if ((sources & InputDevice.SOURCE_TOUCHSCREEN) != 0) sourceNames.add("TOUCH");
        if ((sources & InputDevice.SOURCE_MOUSE) != 0) sourceNames.add("MOUSE");
        if ((sources & InputDevice.SOURCE_TOUCHPAD) != 0) sourceNames.add("TOUCHPAD");
        if ((sources & InputDevice.SOURCE_KEYBOARD) != 0) sourceNames.add("KEYBOARD");

        return TextUtils.join(", ", sourceNames);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        controllerInputManager.handleMotionEvent(event);
        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        controllerInputManager.handleKeyEvent(event);
        return super.dispatchKeyEvent(event);
    }


    private void testVibration(InputDevice device) {
        if (device == null) return;

        Vibrator vibrator = device.getVibrator();
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect effect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            } else {
                vibrator.vibrate(300); // eski API için
            }
        }
    }

    private boolean supportsVibration(InputDevice device) {
        return device != null && device.getVibrator().hasVibrator();
    }
}
