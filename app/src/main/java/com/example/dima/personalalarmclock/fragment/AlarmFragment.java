package com.example.dima.personalalarmclock.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dima.personalalarmclock.R;
import com.example.dima.personalalarmclock.controller.AlarmController;
import com.example.dima.personalalarmclock.entity.Alarm;
import com.example.dima.personalalarmclock.entity.WeekDay;
import com.example.dima.personalalarmclock.helper.AlarmHelper;
import com.example.dima.personalalarmclock.helper.DateAndTimeHelper;
import com.example.dima.personalalarmclock.util.AppConstants;
import com.fastaccess.datetimepicker.DatePickerFragmentDialog;
import com.fastaccess.datetimepicker.TimePickerFragmentDialog;
import com.fastaccess.datetimepicker.callback.DatePickerCallback;
import com.fastaccess.datetimepicker.callback.TimePickerCallback;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;
import com.touchboarder.weekdaysbuttons.WeekdaysDrawableProvider;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.dima.personalalarmclock.helper.DateAndTimeHelper.getDateOnly;
import static com.example.dima.personalalarmclock.helper.DateAndTimeHelper.getTimeOnly;


public class AlarmFragment extends BaseFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        DatePickerCallback, TimePickerCallback {

    private AlarmController mAlarmController;
    private WeekdaysDataSource mWeekdaysDataSource;
    private Alarm mAlarmClock;

    private TextView mTime;
    private TextView mDate;
    private TextView mTextDate;
    private EditText mMessage;
    private CheckBox mIsRepeated;
    private Button btnSet;
    private Button btnDelete;
    private Button btnCancel;
    private ViewStub mWeekdaysStub;

    public AlarmFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        Bundle bundle = this.getArguments();
        mAlarmClock = bundle.getParcelable(AppConstants.ALARM_ENTITY_KEY);

        mAlarmController = new AlarmController();

        mMessage = (EditText) view.findViewById(R.id.alFr_et_massage);
        mMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mAlarmClock.setMessage(editable.toString());
            }
        });
        mTime = (TextView) view.findViewById(R.id.alFr_tv_time);
        mTime.setOnClickListener(this);
        mDate = (TextView) view.findViewById(R.id.alFr_tv_date);
        mDate.setOnClickListener(this);
        mTextDate = (TextView) view.findViewById(R.id.alFr_text_date);
        mIsRepeated = (CheckBox) view.findViewById(R.id.alFr_check_repeat);
        mIsRepeated.setOnCheckedChangeListener(this);

        mWeekdaysStub = (ViewStub) view.findViewById(R.id.alFr_weekdays_stub);

        mWeekdaysDataSource = new WeekdaysDataSource((AppCompatActivity) getActivity(), R.id.alFr_weekdays_stub, view)
                .setLocale(AppConstants.LOCALE)
                .setDrawableType(WeekdaysDrawableProvider.MW_ROUND)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setSelectedColorRes(android.R.color.holo_blue_dark)
                .setUnselectedColor(Color.TRANSPARENT)
                .setTextColorUnselectedRes(android.R.color.black)
                .setFontTypeFace(Typeface.MONOSPACE)
                .setFontBaseSize(24)
                .setNumberOfLetters(2)
                .start(new WeekdaysDataSource.Callback() {
                    @Override
                    public void onWeekdaysItemClicked(int i, WeekdaysDataItem weekdaysDataItem) {
                        String nameOfDay = weekdaysDataItem.getLetters().toUpperCase();
                        mAlarmClock.addOrRemoveDayForRepeat(nameOfDay);
                    }

                    @Override
                    public void onWeekdaysSelected(int i, ArrayList<WeekdaysDataItem> arrayList) {

                    }
                });
        mWeekdaysDataSource.setVisible(false);

        bind();

        btnSet = (Button) view.findViewById(R.id.alFr_btn_set);
        btnSet.setOnClickListener(this);
        btnDelete = (Button) view.findViewById(R.id.alFr_btn_delete);
        btnDelete.setOnClickListener(this);
        btnCancel = (Button) view.findViewById(R.id.alFr_btn_cancel);
        btnCancel.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alFr_btn_set:
                setAlarm();
                break;
            case R.id.alFr_btn_delete:
                deleteAlarm();
                break;
            case R.id.alFr_btn_cancel:
                replaceFragment(new AlarmListFragment());
                break;
            case R.id.alFr_tv_time:
                TimePickerFragmentDialog.newInstance(true).show(getChildFragmentManager(), DateAndTimeHelper.TIME_PICKER_FRAGMENT_DIALOG);
                break;
            case R.id.alFr_tv_date:
                DatePickerFragmentDialog.newInstance()
                        .show(getChildFragmentManager(), DateAndTimeHelper.DATE_PICKER_FRAGMENT_DIALOG);
                break;
            default:
                break;
        }
    }

    private void deleteAlarm() {
        mAlarmController.removeAlarm(mAlarmClock);
        AlarmHelper.cancelAlarm(getActivity(), mAlarmClock);
        replaceFragment(new AlarmListFragment());
    }

    private void setAlarm() {
        mAlarmController.saveAlarm(mAlarmClock);
        AlarmHelper.setAlarm(getActivity(), mAlarmClock);
        replaceFragment(new AlarmListFragment());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            mWeekdaysStub.setVisibility(View.VISIBLE);
            mTextDate.setVisibility(View.GONE);
            mDate.setVisibility(View.GONE);
        } else {
            mWeekdaysStub.setVisibility(View.GONE);
            mTextDate.setVisibility(View.VISIBLE);
            mDate.setVisibility(View.VISIBLE);
        }
    }

    private void bind() {
        int hours = mAlarmClock.getHours();
        int minutes = mAlarmClock.getMinutes();

        // parse hours and minutes to string
        String hoursStr = parseNumberToString(hours);
        String minutesStr = parseNumberToString(minutes);

        mTime.setText(new StringBuilder()
                .append(hoursStr)
                .append(':')
                .append(minutesStr));

        ArrayList<String> weekDays = mAlarmClock.getRepeatingDays();
        if (weekDays.size() > 0) {
            mIsRepeated.setChecked(true);
            for (String day : weekDays) {
                WeekDay weekDay = WeekDay.fromValue(day);
                mWeekdaysDataSource.setSelectedDays(weekDay.getValue());
            }
        } else {
            mDate.setText(String.format(AppConstants.LOCALE, "%1$te/%1$tm/%1$tY", mAlarmClock.getDate()));
        }

        mMessage.setText(mAlarmClock.getMessage());
    }

    @NonNull
    private String parseNumberToString(int number) {
        String result = String.valueOf(number);
        if (number < 10) {
            result = "0" + result;
        }
        return result;
    }


    @Override
    public void onDateSet(long date) {
        mDate.setText(getDateOnly(date));
    }

    @Override
    public void onTimeSet(long timeOnly, long dateWithTime) {
        mTime.setText(getTimeOnly(timeOnly));

    }
}
