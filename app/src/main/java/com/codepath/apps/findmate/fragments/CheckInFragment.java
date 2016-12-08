package com.codepath.apps.findmate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.findmate.R;

public class CheckInFragment extends DialogFragment {

    private Listener listener;
    private EditText etDescription;
    private ImageView ivMap;
    private TextView tvLocation;

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
        tvLocation = (TextView) view.findViewById(R.id.tvLocation);

        ivMap = (ImageView) view.findViewById(R.id.ivMap);
        String address = getActivity().getIntent().getStringExtra("ADDRESS");
        String latLong = getActivity().getIntent().getDoubleExtra("LAT",0) + "," + getActivity().getIntent().getDoubleExtra("LONG", 0);
        Log.i("Checkin::",getActivity().getIntent().getStringExtra("ADDRESS") + " LONG:"+getActivity().getIntent().getDoubleExtra("LAT",0) + "," + getActivity().getIntent().getDoubleExtra("LONG", 0));

        tvLocation.setText(address);

        Glide.with(getContext())
                .load("http://maps.google.com/maps/api/staticmap?center="+latLong+"&zoom=15&size=200x200&sensor=false")
                .into(ivMap);

        // Show soft keyboard automatically and request focus to field
        etDescription.requestFocus();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

//        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onCancelClick();
//            }
//        });
//
//        Button btnCheckIn = (Button) view.findViewById(R.id.btnCheckIn);
//        btnCheckIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onCheckInClick(etDescription.getText().toString());
//            }
//        });
    }

    public interface Listener {
        void onCancelClick();

        void onCheckInClick(String description);
    }
}
