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

public class CheckInFragment extends DialogFragment {

    private Listener listener;
    private EditText etDescription;

    public CheckInFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static CheckInFragment newInstance() {
        return new CheckInFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listener){
            this.listener = (Listener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_check_in, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDescription = (EditText) view.findViewById(R.id.etDescription);
        // Show soft keyboard automatically and request focus to field
        etDescription.requestFocus();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelClick();
            }
        });

        Button btnCheckIn = (Button) view.findViewById(R.id.btnCheckIn);
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCheckInClick(etDescription.getText().toString());
            }
        });
    }

    public interface Listener {
        void onCancelClick();

        void onCheckInClick(String description);
    }
}
