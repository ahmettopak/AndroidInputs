package com.ahmet.androidinputs;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 04/09/2025
 */

public class InputEventAdapter extends ArrayAdapter<InputEventEntry> {
    public InputEventAdapter(Context context, List<InputEventEntry> entries) {
        super(context, android.R.layout.simple_list_item_1, entries);
    }
}