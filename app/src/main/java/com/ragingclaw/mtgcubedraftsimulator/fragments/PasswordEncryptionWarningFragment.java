package com.ragingclaw.mtgcubedraftsimulator.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;

import com.ragingclaw.mtgcubedraftsimulator.R;

import org.jetbrains.annotations.NotNull;


public class PasswordEncryptionWarningFragment extends DialogFragment {

    public PasswordEncryptionWarningFragment() {
        // Required empty public constructor
    }

    @SuppressLint("InflateParams")
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_password_encryption_warning, null))
                .setNegativeButton(R.string.dialog_password_encryption_warning_confirm, (dialog, id) -> {
                    // User cancelled the dialog
                    PasswordEncryptionWarningFragment.this.getDialog().cancel();
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}