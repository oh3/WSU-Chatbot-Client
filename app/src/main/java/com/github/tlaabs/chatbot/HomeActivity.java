package com.github.tlaabs.chatbot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    Button btnSetting, btnChatting, btnTimetable, btnMap;
    private long backpressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getHashKey(this);

        // 최초 실행 여부를 판단 ->>>
        SharedPreferences pref = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
        boolean checkFirst = pref.getBoolean("checkFirst", false);

        // false일 경우 최초 실행
        if(!checkFirst){
            // 앱 최초 실행시 하고 싶은 작업
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("checkFirst",true);
            editor.apply();
            finish();

            Intent intent = new Intent(HomeActivity.this, TutorialActivity.class);
            startActivity(intent);

        }

        btnSetting = findViewById(R.id.btnSetting);
        btnChatting = findViewById(R.id.btnChatting);
        btnTimetable = findViewById(R.id.btnTimetable);
        btnMap = findViewById(R.id.btnMap);



        // 2022-01-14 황우진 퍼미션 if문 추가
        if(!TedPermission.isGranted(HomeActivity.this, Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION)) { // 2022-01-19 황우진 --> 위치 액세스 삭제
            // 2022-01-13 퍼미션 체크 추가
            PermissionListener permissionListener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    // 접근 허용할시 실행 코드
                }

                @Override
                public void onPermissionDenied(List<String> list) {
                    // 접근 거부시 실행할 코드
                }
            };
            TedPermission.with(this)
                    .setPermissionListener(permissionListener)
                    .setPermissions(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION) // 2022-01-19 황우진 --> 위치 액세스 삭제
                    .check();
        }


        // 2022-01-05 황우진
        // 설정 버튼 --> 설정 액티비티로 전환
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSetting.startAnimation(clickAnimation());
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        // 2022-01-05 황우진
        // 채팅 버튼 --> 채팅 액티비티로 전환
        btnChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChattingActivity.class);
                startActivity(intent);
            }
        });

        // 2022-01-05 황우진
        // 시간표 버튼 --> 시간표 액티비티로 전환
        btnTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TimeTableActivity.class);
                startActivity(intent);
            }
        });

        // 2022-01-05 황우진
        // 캠퍼스맵 버튼 --> 캠퍼스맵 액티비티로 전환
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    // 2022-01-05 황우진
    // background가 img인 버튼(설정버튼) 클릭 모션.
    public AlphaAnimation clickAnimation() {
        return new AlphaAnimation(1F, 0.4F);
    }

    // 2022-01-05 황우진
    // 두번 연속으로 뒤로가기 버튼을 누를 시에만 종료
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }
    }

    // 2022-01-05 황우진
    //키 해쉬값 얻는 함수
    //Logcat에 keyhash 검색하면 나옴.
    private void getHashKey(Context context){
        PackageManager pm = context.getPackageManager();
        try{
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for(int i = 0; i < packageInfo.signatures.length; i++){
                Signature signature = packageInfo.signatures[i];
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("<클래스명>","keyhash="+ Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
    }
}