package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zeeplive.app.R;
import com.zeeplive.app.fragment.MyAccountFragment;
import com.zeeplive.app.utils.SessionManager;

public class ExitDialog extends Dialog {

    MyAccountFragment context;

    public ExitDialog(@NonNull MyAccountFragment context) {
        super(context.getContext());

        this.context = context;

        init();
    }

    void init() {
        this.setContentView(R.layout.dialog_exit);
        this.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(false);
        show();

        TextView closeDialog = findViewById(R.id.close_dialog);
        TextView logout = findViewById(R.id.logout);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SessionManager(getContext()).logoutUser();
                context.getActivity().finish();
            }
        });
    }


/*        new AlertDialog.Builder(getContext())
            .setIcon(R.drawable.logo)
                .setTitle("Logout ?")
                .setMessage("Are you sure you want to logout ?")
                .setPositiveButton("Yes", (dialog, which) -> {
        new SessionManager(getContext()).logoutUser();
        getActivity().finish();
    })
            .setNegativeButton("No", null)
                .show();*/
}