package com.github.tlaabs.chatbot;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;

    private Context context;

    private Button deleteBtn;
    private Button submitBtn;
    private EditText subjectEdit;
    private EditText classroomEdit;
    private EditText professorEdit;
    private Spinner daySpinner;
    private TextView startTv;
    private TextView endTv;
    private TextView titleTv;

    // 2022-01-18 황우진
    // 시간표 변수 추가
    private TimetableView timetable;

    //request mode
    private int mode;

    private Schedule schedule;
    private int editIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
    }

    private void init(){
        this.context = this;
        deleteBtn = findViewById(R.id.delete_btn);
        submitBtn = findViewById(R.id.submit_btn);
        subjectEdit = findViewById(R.id.subject_edit);
        classroomEdit = findViewById(R.id.classroom_edit);
        professorEdit = findViewById(R.id.professor_edit);
        daySpinner = findViewById(R.id.day_spinner);
        startTv = findViewById(R.id.start_time);
        endTv = findViewById(R.id.end_time);

        // 2022-01-18 황우진
        // 시간표 수정 / 시간표 추가 타이틀 텍스트 뷰
        titleTv = findViewById(R.id.titleTv);

		// 2022-01-18 황우진
        // 시간표 변수 추가
        timetable = findViewById(R.id.timetable_edit);
        //set the default time
        schedule = new Schedule();
        schedule.setStartTime(new Time(10,0));
        schedule.setEndTime(new Time(13,30));

        checkMode();
        initView();
    }

    /** check whether the mode is ADD or EDIT */
    private void checkMode(){
        Intent i = getIntent();
        mode = i.getIntExtra("mode", TimeTableActivity.REQUEST_ADD);

        if(mode == TimeTableActivity.REQUEST_EDIT){
            loadScheduleData();
            deleteBtn.setVisibility(View.VISIBLE);
        }
    }
    private void initView(){

        // 2022-01-18 황우진
        // TimetableView 데이터 불러오기
        loadTimetableData();

        // 2022-01-18 황우진
        // 타이틀 글자 수정
        if(mode == TimeTableActivity.REQUEST_EDIT) {
            titleTv.setText("시간표 수정");
        } else if(mode == TimeTableActivity.REQUEST_ADD) {
            titleTv.setText("시간표 추가");
        }

        submitBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schedule.setDay(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,listener,schedule.getStartTime().getHour(), schedule.getStartTime().getMinute(), false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String[] str = endTv.getText().toString().split(":");

                    if(minute < 10) {
                        // 2022-01-17 황우진
                        if(Integer.parseInt(str[0]) < hourOfDay ) {
                            endTv.setText(hourOfDay+1 + ":0" + minute);
                            schedule.getEndTime().setHour(hourOfDay+1);
                            schedule.getEndTime().setMinute(minute);
                        }

                        startTv.setText(hourOfDay + ":0" + minute);
                    } else {
                        // 2022-01-17 황우진
                        if(Integer.parseInt(str[0]) < hourOfDay ) {
                            endTv.setText(hourOfDay+1 + ":" + minute);
                            schedule.getEndTime().setHour(hourOfDay+1);
                            schedule.getEndTime().setMinute(minute);
                        }

                        startTv.setText(hourOfDay + ":" + minute);
                    }

                    schedule.getStartTime().setHour(hourOfDay);
                    schedule.getStartTime().setMinute(minute);
                }
            };
        });
        endTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,listener,schedule.getEndTime().getHour(), schedule.getEndTime().getMinute(), false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    String[] str = startTv.getText().toString().split(":");

                    if(minute < 10) {

                        // 2022-01-17 황우진
                        if(Integer.parseInt(str[0]) > hourOfDay ) {
                            if(hourOfDay == 0) {
                                startTv.setText(23 + ":0" + minute);
                            } else {
                                startTv.setText(hourOfDay-1 + ":0" + minute);
                            }
                            schedule.getStartTime().setHour(hourOfDay-1);
                            schedule.getStartTime().setMinute(minute);
                        }

                        endTv.setText(hourOfDay + ":0" + minute);
                    } else {
                        // 2022-01-17 황우진
                        if(Integer.parseInt(str[0]) > hourOfDay ) {
                            if(hourOfDay == 0) {
                                startTv.setText(23 + ":" + minute);
                            } else {
                                startTv.setText(hourOfDay-1 + ":" + minute);
                            }
                            schedule.getStartTime().setHour(hourOfDay-1);
                            schedule.getStartTime().setMinute(minute);
                        }

                        endTv.setText(hourOfDay + ":" + minute);
                    }

                    schedule.getEndTime().setHour(hourOfDay);
                    schedule.getEndTime().setMinute(minute);
                }
            };
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_btn:
                if(mode == TimeTableActivity.REQUEST_ADD){
                    if((subjectEdit.getText().toString().equals("") || subjectEdit.getText().toString() == null)) {
                        Toast.makeText(EditActivity.this, "수업명을 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    } else if((classroomEdit.getText().toString().equals("") || classroomEdit.getText().toString() == null)) {
                        Toast.makeText(EditActivity.this, " 강의실명을 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                        inputDataProcessing();
                    Intent i = new Intent();
                    ArrayList<Schedule> schedules = new ArrayList<Schedule>();
                    //you can add more schedules to ArrayList
                    schedules.add(schedule);
                    i.putExtra("schedules",schedules);
                    setResult(RESULT_OK_ADD,i);
                    finish();
                }
                else if(mode == TimeTableActivity.REQUEST_EDIT){
                    if((subjectEdit.getText().toString().equals("") || subjectEdit.getText().toString() == null)) {
                        Toast.makeText(EditActivity.this, "수업명을 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    } else if((classroomEdit.getText().toString().equals("") || classroomEdit.getText().toString() == null)) {
                        Toast.makeText(EditActivity.this, "강의실명을 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    inputDataProcessing();
                    Intent i = new Intent();
                    ArrayList<Schedule> schedules = new ArrayList<Schedule>();
                    schedules.add(schedule);
                    i.putExtra("idx",editIdx);
                    i.putExtra("schedules",schedules);
                    setResult(RESULT_OK_EDIT,i);
                    finish();
                }
                break;
            case R.id.delete_btn:
                Intent i = new Intent();
                i.putExtra("idx",editIdx);
                setResult(RESULT_OK_DELETE, i);
                finish();
                break;
        }
    }

    // 2022-01-18 황우진
    // 시간표 데이터 불러오기
    private void loadTimetableData(){
        try {
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
            String savedData = mPref.getString("timetable_demo","");
            if(savedData == null || savedData.equals("")) {
                return;
            }
            timetable.load(savedData);
        }
        catch (NullPointerException e) {
        }
    }

    private void loadScheduleData(){
        Intent i = getIntent();
        editIdx = i.getIntExtra("idx",-1);
        ArrayList<Schedule> schedules = (ArrayList<Schedule>)i.getSerializableExtra("schedules");
        schedule = schedules.get(0);
        subjectEdit.setText(schedule.getClassTitle());
        classroomEdit.setText(schedule.getClassPlace());
        professorEdit.setText(schedule.getProfessorName());

        // 2022-01-18 황우진
        // 시작 시간 데이터 불러오기
        if(schedule.getStartTime().getMinute() < 10) {
            startTv.setText(schedule.getStartTime().getHour() + ":0" + schedule.getStartTime().getMinute());
        } else {
            startTv.setText(schedule.getStartTime().getHour() + ":" + schedule.getStartTime().getMinute());
        }

        // 2022-01-18 황우진
        // 끝나는 시간 데이터 불러오기
        if(schedule.getEndTime().getMinute() < 10) {
            endTv.setText(schedule.getEndTime().getHour() + ":0" + schedule.getEndTime().getMinute());
        } else {
            endTv.setText(schedule.getEndTime().getHour() + ":" + schedule.getEndTime().getMinute());
        }


        daySpinner.setSelection(schedule.getDay());
    }

    private void inputDataProcessing(){
        schedule.setClassTitle(subjectEdit.getText().toString());
        schedule.setClassPlace(classroomEdit.getText().toString());
        schedule.setProfessorName(professorEdit.getText().toString());
    }
}