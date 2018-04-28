package org.feezu.liuli.timeselectordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.feezu.liuli.timeselector.TimeSelector;
import org.feezu.liuli.timeselector.TimeSelector1;


public class MainActivity extends AppCompatActivity {
    private TimeSelector timeSelector;
    private TimeSelector1 timeSelector1;
    private TimeSelector1 timeSelector2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                Toast.makeText(getApplicationContext(), time, Toast.LENGTH_LONG).show();
            }
        }, "1989-01-30 00:00", "2018-12-31 00:00");

        timeSelector.setIsLoop(true);








    }

    public void show(View v) {
        timeSelector.show();
    }

    /**
     * 有时间限制，如果没有默认展示时间，展示最后的时间（注意：开始时间不能和结束时间）
     *
     * @param v
     */
    public void show1(View v) {
        timeSelector1 = new TimeSelector1(MainActivity.this);
        timeSelector1.startData("2015-09-25 00:00:00")
                .endData("2018-10-25 00:16:00")
                        .showTime("2017-01-05 00:00:00")
                .build(new TimeSelector1.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        Toast.makeText(MainActivity.this, time, Toast.LENGTH_SHORT).show();

                    }
                });
        timeSelector1.setMode(TimeSelector1.MODE.YM).setIsLoop(true);
        timeSelector1.show();
    }

    public void show2(View v) {
        timeSelector2 = new TimeSelector1(MainActivity.this);
        timeSelector2.build(new TimeSelector1.ResultHandler() {
            @Override
            public void handle(String time) {
                Toast.makeText(MainActivity.this, time, Toast.LENGTH_SHORT).show();

            }
        });
        timeSelector2.setMode(TimeSelector1.MODE.YMDHMS).setIsLoop(true);
        timeSelector2.show();
    }

}
