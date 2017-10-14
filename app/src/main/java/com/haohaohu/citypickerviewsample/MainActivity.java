package com.haohaohu.citypickerviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.haohaohu.citypickerview.CityPickerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        final CityPickerView mView = (CityPickerView) findViewById(R.id.citypickerview);
        mView.setLoc("北京", "北京", "海淀区");

        findViewById(R.id.citypickerview_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mView.getName();
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
