package com.zeeplive.app.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zeeplive.app.R;

public class ComplaintDescription extends AppCompatActivity {

    TextView answer, query, heading;
    ImageView backArrow;
    String q, a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_complaint_description);

        heading = findViewById(R.id.heading);
        heading.setText("Complaint Resolution");
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> {
            finish();
        });

        query = findViewById(R.id.query);
        answer = findViewById(R.id.answer);

        q = getIntent().getStringExtra("query");
        a = getIntent().getStringExtra("answer");

        query.setText(q);

        if (a == null) {
            answer.setText("Your Complaint is under process");

        } else {
            answer.setText(a);
        }
    }
}