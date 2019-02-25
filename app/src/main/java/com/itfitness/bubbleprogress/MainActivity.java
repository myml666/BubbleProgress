package com.itfitness.bubbleprogress;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itfitness.bubbleprogress.widget.BubbleProgressView;

public class MainActivity extends AppCompatActivity {
    private BubbleProgressView bpv;
    private Button bt;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = (Button) findViewById(R.id.bt);
        bpv = (BubbleProgressView) findViewById(R.id.bpv);
        et = (EditText) findViewById(R.id.et);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpv.setProgressWithAnim(Float.valueOf(et.getText().toString().trim()));
            }
        });
    }
}
