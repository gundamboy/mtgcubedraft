package com.ragingclaw.mtgcubedraftsimulator.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ragingclaw.mtgcubedraftsimulator.R;


public class DraftCompleteDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    public DraftCompleteDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_draft_complete, null))
                .setNegativeButton(R.string.dialog_draft_complete_confirm, (dialog, id) -> {
                    // User cancelled the dialog
                    DraftCompleteDialogFragment.this.getDialog().cancel();
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
