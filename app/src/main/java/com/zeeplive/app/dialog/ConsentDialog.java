package com.zeeplive.app.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeeplive.app.R;
import com.zeeplive.app.fragment.HomeFragment;
import com.zeeplive.app.utils.SessionManager;

public class ConsentDialog extends Dialog {

    HomeFragment context;

    public ConsentDialog(HomeFragment context) {
        super(context.getContext());

        this.context = context;

        init();
    }

    void init() {
        this.setContentView(R.layout.dialog_consent);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(false);
        show();

        TextView closeDialog = findViewById(R.id.close_dialog);
        TextView logout = findViewById(R.id.logout);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                context.getActivity().finish();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SessionManager(getContext()).setConsent(true);
                dismiss();
            }
        });
    }
}