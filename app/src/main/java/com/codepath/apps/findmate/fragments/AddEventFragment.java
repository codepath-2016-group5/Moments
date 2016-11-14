package com.codepath.apps.findmate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.models.Event;

public class AddEventFragment extends DialogFragment {

    private EditText etEventName;
    private EditText etStartDate;
    private EditText etEndDate;
    private EditText etLocation;
    private EditText etDetails;

    private Button btnCancel;
    private Button btnCreate;

    private OnEventCreateListener listener;

    public AddEventFragment() {
    }

    public static AddEventFragment newInstance() {
        return new AddEventFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        // Set to adjust screen height automatically, when soft keyboard appears on screen
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.fragment_add_event, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEventName = (EditText) view.findViewById(R.id.etEventName);
        etEventName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        etStartDate = (EditText) view.findViewById(R.id.etStartDate);
        etEndDate = (EditText) view.findViewById(R.id.etEndDate);
        etLocation = (EditText) view.findViewById(R.id.etLocation);
        etDetails = (EditText) view.findViewById(R.id.etEventDetails);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCreate = (Button) view.findViewById(R.id.btnCreate);
        btnCancel.setOnClickListener(new OnCancelClickListener());
        btnCreate.setOnClickListener(new OnCreateClickListener());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventCreateListener) {
            listener = (OnEventCreateListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnEventCreateListener.OnCreate");
        }
    }

    public interface OnEventCreateListener {
        void onCreate(Event event);
    }

    private class OnCancelClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            AddEventFragment.this.dismiss();
        }
    }

    private class OnCreateClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            Event event = new Event();
            event.setName(etEventName.getText().toString());
            event.setDetails(etDetails.getText().toString());
            listener.onCreate(event);
            AddEventFragment.this.dismiss();
        }
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}
