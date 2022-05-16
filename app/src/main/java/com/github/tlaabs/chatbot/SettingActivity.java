package com.github.tlaabs.chatbot;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    Switch switchTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //2022-01-19 오상민
        //튜토리얼 다시보기 버튼
        Button moveButton;
        moveButton = findViewById(R.id.moveButton);
        moveButton.setOnClickListener(onClickListener);

        switchTTS = (Switch)findViewById(R.id.switchTTS);

        // TTS 기능을 사전에 미리 설정을 하였다면, 그 설정대로 UI를 설정한다.
        Boolean IsTTS = PreferenceManager.getBoolean(SettingActivity.this, "IsTTS");
        if(IsTTS != null){
            switchTTS.setChecked(IsTTS);
        }

        // TTS 스위치 버튼 눌렀을 때, 휴대폰에 해당하는 설정으로 수정하여 저장한다.
        switchTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.setBoolean(SettingActivity.this, "IsTTS", switchTTS.isChecked());
            }
        });
    }

    //2022-01-19 오상민
    //튜토리얼 다시보기 버튼 기능구현
    Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SettingActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish();
        }
    };
}