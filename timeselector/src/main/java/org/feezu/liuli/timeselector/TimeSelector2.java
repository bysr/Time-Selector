package org.feezu.liuli.timeselector;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.feezu.liuli.timeselector.Utils.DateUtil;
import org.feezu.liuli.timeselector.Utils.ScreenUtil;
import org.feezu.liuli.timeselector.view.PickerView;

import java.util.ArrayList;
import java.util.Calendar;


public class TimeSelector2 {

    public interface ResultHandler {
        void handle(String time);
    }

    public enum SCROLLTYPE {

        HOUR(1),
        MINUTE(2),
        SECOND(3);

        SCROLLTYPE(int value) {
            this.value = value;
        }

        public int value;

    }

    public enum MODE {

        YMD(1),
        YMDHM(2),
        YMDHMS(3),
        HMS(4);


        MODE(int value) {
            this.value = value;
        }

        public int value;

    }


    private int scrollUnits = SCROLLTYPE.HOUR.value + SCROLLTYPE.MINUTE.value;
    private ResultHandler handler;
    private Context context;
    private final String FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    private Dialog seletorDialog;
    private PickerView year_pv;
    private PickerView month_pv;
    private PickerView day_pv;
    private PickerView hour_pv;
    private PickerView minute_pv;
    private PickerView second_pv;

    private final int MAXMINUTE = 59;
    private int MAXHOUR = 23;
    private final int MINMINUTE = 0;
    private final int MINSECOND = 0;
    private int MINHOUR = 0;
    private final int MAXMONTH = 12;
    private final int MAXSECONED = 59;

    private ArrayList<String> year, month, day, hour, minute, second;
    private int startYear, startMonth, startDay, startHour, startMininute, startSecond, endYear, endMonth, endDay, endHour, endMininute, endSecond;
    //设置展示时间
    private int showYear, showMonth, showDay, showHour, showMininute, showSecond;
    private boolean spanYear, spanMon, spanDay, spanHour, spanMin, spanSec;
    private Calendar selectedCalender = Calendar.getInstance();
    private final long ANIMATORDELAY = 200L;
    private final long CHANGEDELAY = 90L;

    //开始时间
    private Calendar startCalendar;
    //结束时间
    private Calendar endCalendar;
    //设置时间
    private Calendar showCalendar;
    private TextView tv_cancle;
    private TextView tv_select, tv_title;
    private TextView year_text;
    private TextView month_text;
    private TextView day_text;
    private TextView hour_text;
    private TextView minute_text;
    private TextView second_text;
    private String startData, endData, showData;//接收传递过来的三个时间

    /**
     * 设置开始时间
     *
     * @param startDate
     * @return
     */
    public TimeSelector2 startData(String startDate) {
        this.startData = startDate;
        return this;
    }

    /**
     * 设置结束时间
     *
     * @param endDate
     * @return
     */
    public TimeSelector2 endData(String endDate) {
        this.endData = endDate;
        return this;
    }

    /**
     * 设置展示时间
     *
     * @param showTime
     */
    public TimeSelector2 showTime(String showTime) {
        this.showData = showTime;
        return this;
    }

    public TimeSelector2(Context context) {
        this.context = context;
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        showCalendar = Calendar.getInstance();
    }


    public void build(ResultHandler resultHandler) {
        this.handler = resultHandler;
        initDialog();
        initView();
    }


//    public TimeSelector1(Context context, ResultHandler resultHandler, String startDate, String endDate) {
//        this.context = context;
//        this.handler = resultHandler;
//        startCalendar = Calendar.getInstance();
//        endCalendar = Calendar.getInstance();
//        startCalendar.setTime(DateUtil.parse(startDate, FORMAT_STR));
//        endCalendar.setTime(DateUtil.parse(endDate, FORMAT_STR));
//
//    }


    public void show() {
        if (startCalendar.getTime().getTime() >= endCalendar.getTime().getTime()) {
            Toast.makeText(context, "start>end", Toast.LENGTH_LONG).show();
            return;
        }

        initParameter();
        initTimer();
        addListener();
        seletorDialog.show();


    }

    private void initDialog() {
        if (seletorDialog == null) {
            seletorDialog = new Dialog(context, R.style.time_dialog);
            seletorDialog.setCancelable(false);
            seletorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            seletorDialog.setContentView(R.layout.dialog_selector_1);
            Window window = seletorDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            int width = ScreenUtil.getInstance(context).getScreenWidth();
            lp.width = width;
            window.setAttributes(lp);
        }
    }

    private void initView() {
        year_pv = (PickerView) seletorDialog.findViewById(R.id.year_pv);
        month_pv = (PickerView) seletorDialog.findViewById(R.id.month_pv);
        day_pv = (PickerView) seletorDialog.findViewById(R.id.day_pv);
        hour_pv = (PickerView) seletorDialog.findViewById(R.id.hour_pv);
        minute_pv = (PickerView) seletorDialog.findViewById(R.id.minute_pv);
        second_pv = (PickerView) seletorDialog.findViewById(R.id.second_pv);
        tv_cancle = (TextView) seletorDialog.findViewById(R.id.tv_cancle);
        tv_select = (TextView) seletorDialog.findViewById(R.id.tv_select);
        tv_title = (TextView) seletorDialog.findViewById(R.id.tv_title);
        year_text = (TextView) seletorDialog.findViewById(R.id.year_text);
        month_text = (TextView) seletorDialog.findViewById(R.id.month_text);
        day_text = (TextView) seletorDialog.findViewById(R.id.day_text);
        hour_text = (TextView) seletorDialog.findViewById(R.id.hour_text);
        minute_text = (TextView) seletorDialog.findViewById(R.id.minute_text);
        second_text = (TextView) seletorDialog.findViewById(R.id.second_text);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seletorDialog.dismiss();
            }
        });
        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.handle(DateUtil.format(selectedCalender.getTime(), FORMAT_STR));
                seletorDialog.dismiss();
            }
        });
        if (startData == null)
            startData = "2000-01-01 00:00:00";
        if (endData == null) {
            endData = DateUtil.getNowString(FORMAT_STR);
            showData = endData;
        }


        startCalendar.setTime(DateUtil.parse(startData, FORMAT_STR));
        endCalendar.setTime(DateUtil.parse(endData, FORMAT_STR));
        showCalendar.setTime(DateUtil.parse(showData, FORMAT_STR));
    }

    private void initParameter() {


        //展示时间
        showYear = showCalendar.get(Calendar.YEAR);
        showMonth = showCalendar.get(Calendar.MONTH) + 1;
        showDay = showCalendar.get(Calendar.DAY_OF_MONTH);
        showHour = showCalendar.get(Calendar.HOUR_OF_DAY);
        showMininute = showCalendar.get(Calendar.MINUTE);
        showSecond = showCalendar.get(Calendar.SECOND);

        //开始时间
        startYear = startCalendar.get(Calendar.YEAR);
        startMonth = startCalendar.get(Calendar.MONTH) + 1;
        startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        startMininute = startCalendar.get(Calendar.MINUTE);
        startSecond = startCalendar.get(Calendar.SECOND);


        //结束时间
        endYear = endCalendar.get(Calendar.YEAR);
        endMonth = endCalendar.get(Calendar.MONTH) + 1;
        endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        endMininute = endCalendar.get(Calendar.MINUTE);
        endSecond = endCalendar.get(Calendar.SECOND);

        //是否同一年...
        spanYear = startYear != endYear;
        spanMon = (!spanYear) && (startMonth != endMonth);
        spanDay = (!spanMon) && (startDay != endDay);
        spanHour = (!spanDay) && (startHour != endHour);
        spanMin = (!spanHour) && (startMininute != endMininute);
        spanSec = (!spanMin) && (startSecond != endSecond);
//        selectedCalender.setTime(startCalendar.getTime());

        //将展示时间设置为当前时间
        selectedCalender.setTime(showCalendar.getTime());
    }

    private void initTimer() {
        initArrayList();


        for (int i = startYear; i <= endYear; i++) {
            //year保存当前展示年份
            year.add(String.valueOf(i));
        }

        for (int i = 1; i <= endMonth; i++) {
            //加载当前展示月份
            month.add(fomatTimeUnit(i));
        }
                /*通过当前展示月份获取天数信息*/
        for (int i = 1; i <= showCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            day.add(fomatTimeUnit(i));
        }


        for (int i = 0; i <= MAXHOUR; i++) {
            hour.add(fomatTimeUnit(i));
        }


        for (int i = 0; i <= MAXMINUTE; i++) {
            minute.add(fomatTimeUnit(i));
        }


                 /*加载保存分钟数*/
        for (int i = 0; i <= MAXSECONED; i++) {
            second.add(fomatTimeUnit(i));
        }


        //少了n多判断,目前只展示和结束时间相同的时间 或者不同年份的时间
        loadComponent();

    }


    private String fomatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void initArrayList() {
        if (year == null) year = new ArrayList<>();
        if (month == null) month = new ArrayList<>();
        if (day == null) day = new ArrayList<>();
        if (hour == null) hour = new ArrayList<>();
        if (minute == null) minute = new ArrayList<>();
        if (second == null) second = new ArrayList<>();
        year.clear();
        month.clear();
        day.clear();
        hour.clear();
        minute.clear();
        second.clear();
    }


    private void addListener() {
        year_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.YEAR, Integer.parseInt(text));
                monthChange();


            }
        });
        month_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, 1);
                selectedCalender.set(Calendar.MONTH, Integer.parseInt(text) - 1);
                dayChange();


            }
        });
        day_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text));
                hourChange();

            }
        });
        hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(text));
                minuteChange();


            }
        });
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.MINUTE, Integer.parseInt(text));
                secondChange();


            }
        });

        second_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.SECOND, Integer.parseInt(text));

            }
        });

    }

    private void loadComponent() {
        year_pv.setData(year);
        month_pv.setData(month);
        day_pv.setData(day);
        hour_pv.setData(hour);
        minute_pv.setData(minute);
        second_pv.setData(second);


        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
        int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);
        int selectedMinute = selectedCalender.get(Calendar.MINUTE);
        int selectedSecond = selectedCalender.get(Calendar.SECOND);

        year_pv.setSelected(selectedYear + "");//年份是从0开始
        month_pv.setSelected(selectedMonth - 1);
        day_pv.setSelected(selectedDay - 1);
        hour_pv.setSelected(selectedHour);
        minute_pv.setSelected(selectedMinute);
        second_pv.setSelected(selectedSecond);
        excuteScroll();
    }

    private void excuteScroll() {
        //
        year_pv.setCanScroll(year.size() > 1);
        month_pv.setCanScroll(month.size() > 1);
        day_pv.setCanScroll(day.size() > 1);
        hour_pv.setCanScroll(hour.size() > 1 && (scrollUnits & SCROLLTYPE.HOUR.value) == SCROLLTYPE.HOUR.value);
        minute_pv.setCanScroll(minute.size() > 1 && (scrollUnits & SCROLLTYPE.MINUTE.value) == SCROLLTYPE.MINUTE.value);
        second_pv.setCanScroll(second.size() > 1 && (scrollUnits & SCROLLTYPE.SECOND.value) == SCROLLTYPE.SECOND.value);
    }

    private void monthChange() {

        month.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        if (selectedYear == startYear) {
            for (int i = startMonth; i <= MAXMONTH; i++) {
                month.add(fomatTimeUnit(i));
            }
        } else if (selectedYear == endYear) {
            for (int i = 1; i <= endMonth; i++) {
                month.add(fomatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= MAXMONTH; i++) {
                month.add(fomatTimeUnit(i));
            }
        }
        selectedCalender.set(Calendar.MONTH, Integer.parseInt(month.get(0)) - 1);
        month_pv.setData(month);
        month_pv.setSelected(0);
        excuteAnimator(ANIMATORDELAY, month_pv);

        month_pv.postDelayed(new Runnable() {
            @Override
            public void run() {
                dayChange();
            }
        }, CHANGEDELAY);

    }

    private void dayChange() {

        day.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (int i = startDay; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(fomatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (int i = 1; i <= endDay; i++) {
                day.add(fomatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(fomatTimeUnit(i));
            }
        }
        selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(0)));
        day_pv.setData(day);
        day_pv.setSelected(0);
        excuteAnimator(ANIMATORDELAY, day_pv);

        day_pv.postDelayed(new Runnable() {
            @Override
            public void run() {
                hourChange();
            }
        }, CHANGEDELAY);
    }

    private void hourChange() {
        if ((scrollUnits & SCROLLTYPE.HOUR.value) == SCROLLTYPE.HOUR.value) {
            hour.clear();
            int selectedYear = selectedCalender.get(Calendar.YEAR);
            int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
            int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);

            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay) {
                for (int i = startHour; i <= MAXHOUR; i++) {
                    hour.add(fomatTimeUnit(i));
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay) {
                for (int i = MINHOUR; i <= endHour; i++) {
                    hour.add(fomatTimeUnit(i));
                }
            } else {
                for (int i = MINHOUR; i <= MAXHOUR; i++) {
                    hour.add(fomatTimeUnit(i));
                }

            }
            selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.get(0)));
            hour_pv.setData(hour);
            hour_pv.setSelected(0);
            excuteAnimator(ANIMATORDELAY, hour_pv);
        }
        hour_pv.postDelayed(new Runnable() {
            @Override
            public void run() {
                minuteChange();
            }
        }, CHANGEDELAY);

    }

    private void minuteChange() {
        if ((scrollUnits & SCROLLTYPE.MINUTE.value) == SCROLLTYPE.MINUTE.value) {
            minute.clear();
            int selectedYear = selectedCalender.get(Calendar.YEAR);
            int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
            int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
            int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);

            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour) {
                for (int i = startMininute; i <= MAXMINUTE; i++) {
                    minute.add(fomatTimeUnit(i));
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
                for (int i = MINMINUTE; i <= endMininute; i++) {
                    minute.add(fomatTimeUnit(i));
                }
            } else {
                for (int i = MINMINUTE; i <= MAXMINUTE; i++) {
                    minute.add(fomatTimeUnit(i));
                }
            }
            selectedCalender.set(Calendar.MINUTE, Integer.parseInt(minute.get(0)));
            minute_pv.setData(minute);
            minute_pv.setSelected(0);
            excuteAnimator(ANIMATORDELAY, minute_pv);
        }
        minute_pv.postDelayed(new Runnable() {
            @Override
            public void run() {
                secondChange();
            }
        }, CHANGEDELAY);


    }

    private void secondChange() {
        if ((scrollUnits & SCROLLTYPE.SECOND.value) == SCROLLTYPE.SECOND.value) {
            second.clear();
            int selectedYear = selectedCalender.get(Calendar.YEAR);
            int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
            int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
            int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);
            int selectedMinute = selectedCalender.get(Calendar.MINUTE);

            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour && selectedMinute == startMininute) {
                for (int i = startSecond; i <= MAXSECONED; i++) {
                    second.add(fomatTimeUnit(i));
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour && selectedMinute == endMininute) {
                for (int i = MINSECOND; i <= endSecond; i++) {
                    second.add(fomatTimeUnit(i));
                }
            } /*else if (selectedMinute == minute_workStart) {
                for (int i = second_workStart; i <= MAXSECONED; i++) {
                    second.add(fomatTimeUnit(i));
                }
            } else if (selectedMinute == minute_workEnd) {
                for (int i = MINSECOND; i <= second_workEnd; i++) {
                    second.add(fomatTimeUnit(i));
                }
            } */ else {
                for (int i = MINSECOND; i <= MAXSECONED; i++) {
                    second.add(fomatTimeUnit(i));
                }
            }
            selectedCalender.set(Calendar.SECOND, Integer.parseInt(second.get(0)));
            second_pv.setData(second);
            second_pv.setSelected(0);
            excuteAnimator(ANIMATORDELAY, second_pv);
        }
        excuteScroll();


    }

    private void excuteAnimator(long ANIMATORDELAY, View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,
                0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(ANIMATORDELAY).start();
    }


    public void setNextBtTip(String str) {
        tv_select.setText(str);
    }

    public void setTitle(String str) {
        tv_title.setText(str);
    }

    public int disScrollUnit(SCROLLTYPE... scrolltypes) {
        if (scrolltypes == null || scrolltypes.length == 0)
            scrollUnits = SCROLLTYPE.HOUR.value + SCROLLTYPE.MINUTE.value;
        for (SCROLLTYPE scrolltype : scrolltypes) {
            scrollUnits ^= scrolltype.value;
        }
        return scrollUnits;
    }


    public TimeSelector2 setMode(MODE mode) {
        switch (mode.value) {
            case 1:
                disScrollUnit(SCROLLTYPE.HOUR, SCROLLTYPE.MINUTE);
                hour_pv.setVisibility(View.GONE);
                minute_pv.setVisibility(View.GONE);
                second_pv.setVisibility(View.GONE);
                hour_text.setVisibility(View.GONE);
                minute_text.setVisibility(View.GONE);
                second_text.setVisibility(View.GONE);
                break;
            case 2:
                disScrollUnit();
                hour_pv.setVisibility(View.VISIBLE);
                minute_pv.setVisibility(View.VISIBLE);
                second_pv.setVisibility(View.GONE);
                hour_text.setVisibility(View.VISIBLE);
                minute_text.setVisibility(View.VISIBLE);
                second_text.setVisibility(View.GONE);
                break;
            case 3:
                disScrollUnit();
                hour_pv.setVisibility(View.VISIBLE);
                minute_pv.setVisibility(View.VISIBLE);
                second_pv.setVisibility(View.VISIBLE);
                hour_text.setVisibility(View.VISIBLE);
                minute_text.setVisibility(View.VISIBLE);
                second_text.setVisibility(View.VISIBLE);
                break;
            case 4:
                disScrollUnit();
                year_pv.setVisibility(View.GONE);
                year_text.setVisibility(View.GONE);
                month_pv.setVisibility(View.GONE);
                month_text.setVisibility(View.GONE);
                day_pv.setVisibility(View.GONE);
                day_text.setVisibility(View.GONE);
                hour_pv.setVisibility(View.VISIBLE);
                minute_pv.setVisibility(View.VISIBLE);
                second_pv.setVisibility(View.VISIBLE);
                hour_text.setVisibility(View.VISIBLE);
                minute_text.setVisibility(View.VISIBLE);
                second_text.setVisibility(View.VISIBLE);
                break;
        }
        return this;
    }

    public TimeSelector2 setIsLoop(boolean isLoop) {
        this.year_pv.setIsLoop(isLoop);
        this.month_pv.setIsLoop(isLoop);
        this.day_pv.setIsLoop(isLoop);
        this.hour_pv.setIsLoop(isLoop);
        this.minute_pv.setIsLoop(isLoop);
        this.second_pv.setIsLoop(isLoop);
        return this;
    }
}
