package com.github.tlaabs.chatbot;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

// 2022-01-12 황우진
// 채팅 화면에 대한 자바소스코드. + 서버 연결 코드.
public class ChattingActivity extends AppCompatActivity {

    // 리사이클러뷰에 필요한 변수들
    private ArrayList<Chat> chatArrayList;
    private ChatAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView chatView;

    // 뷰홀더 타입별로 생성
    private final String USER_KEY = "user";
    private final String BOTTEXT_KEY = "bottext";
    private final String BOTSTART_KEY = "botstart";
    private final String BOTBUTTON_KEY = "botbutton";
    private final String BOTWEB_KEY = "botweb";
    private final String BOTMAP_KEY = "botmap";
    private final String BOTIMAGE_KEY = "botimage";

    private TextToSpeech mTTS;

    EditText editMessage;
    ImageButton btnsend;
    FloatingActionButton info, speak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        editMessage =(EditText)findViewById(R.id.editMessage);
        btnsend = (ImageButton) findViewById(R.id.btnSend);
        info = (FloatingActionButton)findViewById(R.id.info);
        speak = (FloatingActionButton)findViewById(R.id.speak);
        chatView = (RecyclerView)findViewById(R.id.chatView);

        // TTS
        mTTS = new TextToSpeech(ChattingActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // TTS 언어 설정
                    int result = mTTS.setLanguage(Locale.KOREAN);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                }
                else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        // 리사이클러뷰 만드는 함수 (길어서 따로 분리..)
        CreateRecyclerview();

        // 처음 앱 실행하자마자 보이는 말풍선을 위하여 서버로부터 데이터를 요청한다.
        ServerRequest.postRequest("SEARCH", "null", new Callback() {

            // 실패했을경우
            @Override
            public void onFailure(Call call, IOException e) {
                ChattingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 기본값으로 출력한다.
                        chatArrayList.add(new Chat(BOTSTART_KEY, null));
                        adapter.notifyItemInserted(chatArrayList.size()-1);
                        // 스크롤 위치 업데이트
                        chatView.scrollToPosition(chatArrayList.size()-1);
                    }
                });
            }

            // 성공했을경우
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                ChattingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String message = response.body().string();
                            JSONObject jObject = new JSONObject(message);

                            if (jObject.getString("TrueOrFalse").equals("True")) {

                                String str = jObject.getString("Top1") + "#";
                                str += jObject.getString("Top2") + "#";
                                str += jObject.getString("Top3") + "#";
                                str += jObject.getString("Top4");

                                chatArrayList.add(new Chat(BOTSTART_KEY, str));
                                adapter.notifyItemInserted(chatArrayList.size()-1);

                            } else {
                                chatArrayList.add(new Chat(BOTSTART_KEY, null));
                                adapter.notifyItemInserted(chatArrayList.size()-1);
                            }
                        } catch (IOException | JSONException | NetworkOnMainThreadException
                                | NullPointerException e) {
                            chatArrayList.add(new Chat(BOTSTART_KEY, null));
                            adapter.notifyItemInserted(chatArrayList.size()-1);
                        }
                    }
                });
            }
        });


        // 마이크 버튼 눌렀을때
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                // 마이크에 관한 권한 승인 여부를 확인한다.
                if(!TedPermission.isGranted(ChattingActivity.this,
                        Manifest.permission.RECORD_AUDIO)) {
                    // 권한 승인 여부에 관한 리스너 함수이다.
                    PermissionListener permissionlistener = new PermissionListener() {
                        // 권한 승인을 하였을 경우는 정상적으로 음성인식을 한다.
                        @Override
                        public void onPermissionGranted() {
                            Snackbar.make(view, "음성인식", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            VoiceTask voiceTask = new VoiceTask();
                            voiceTask.execute();
                        }

                        // 권한 승인을 하지 않았을 경우에는 아무런 행동을 하지 않는다.
                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {

                        }
                    };

                    TedPermission.with(ChattingActivity.this)
                            .setPermissionListener(permissionlistener) // 리스너 등록
                            // 권한 승인을 하지 않았을 경우의 뜨는 다이얼로그의 메세지를 다음과 같이 설정한다.
                            .setDeniedMessage("접근 거부하셨습니다." +
                                    "\n[설정] - [권한]에서 권한을 허용해주세요.")
                            // 어떤 퍼미션을 확인할 것인지 설정한다.
                            .setPermissions(Manifest.permission.RECORD_AUDIO)
                            .check();
                }
                // 만약 이미 권한 승인을 하였을 경우, 아래와 같은 구문을 실행한다.
                else {
                    // Snackbar 를 일시적으로 만든다.
                    Snackbar.make(view, "음성인식", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    // 음성인식을 하는 객체를 새로 생성한다.
                    VoiceTask voiceTask = new VoiceTask();
                    // 음성인식을 동기로 실행한다.
                    voiceTask.execute();
                }
            }
        });


        // 보내기 버튼 눌렀을때
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 만약 editMessage 에 아무런 문자열도 들어가있지 않으면 null값을 리턴한다.
                if(editMessage.getText().toString().equals("")
                        || editMessage.getText().toString() == null) {
                    return;
                }

                // 유저가 보낸 메세지를 ArrayList 에 넣는다.
                String message = editMessage.getText().toString();
                Chat chat = new Chat(USER_KEY, message);
                chatArrayList.add(chat);
                // 해당하는 위치에 아이템 추가 업데이트 한다.
                adapter.notifyItemInserted(chatArrayList.size()-1);

                // 만약 유저가 보낸 메세지에 시간표 단어가 포함되어 있으면 안드로이드 내부에서 자체적으로 처리한다.
                if(message.contains("시간표")) {
                    chatArrayList.add(new Chat(BOTBUTTON_KEY, "시간표"));
                    adapter.notifyItemInserted(chatArrayList.size()-1);
                }
                else {
                    // 서버에 전송하여 봇의 대답을 받는다.
                    ServerRequest.postRequest("TEST", message, new Callback() {
                        // 만약 에라가 일어난 경우
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // .runOnUiThread 메소드를 이용하여 현재 스레드가 UI 스레드라면 UI 자원을 즉시 실행
                            // 현재 스레드가 UI 스레드가 아닐 경우,
                            // UI 스레드의 자원 사용 이벤트 큐에 들어가도록 한다.
                            ChattingActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    call.cancel();
                                    try {
                                        ParsingAnswer(null);
                                    }
                                    catch (IOException | JSONException | NetworkOnMainThreadException
                                            | NullPointerException e) {
                                        chatArrayList.add(new Chat(BOTTEXT_KEY, "서비스가 원활하지 않습니다."));
                                        adapter.notifyItemInserted(chatArrayList.size()-1);
                                        // 만약
                                        if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                                            mTTS.speak("서비스가 원활하지 않습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                        }
                                    }

                                    /*// 2022-01-17 황우진 --> 오류를 출력하는 코드.
                                    chatArrayList.add(new Chat(BOTTEXT_KEY, e.toString()));
                                    adapter.notifyItemInserted(chatArrayList.size()-1);
                                    if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                                        mTTS.speak(e.toString(), TextToSpeech.QUEUE_FLUSH, null);
                                    }*/
                                }
                            });
                        }

                        // 만약 서버로부터 정상적으로 대답을 받은 경우
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ChattingActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // 서버로부터 온 대답을 분석하여 Recyclerview 에 아이템을 추가시킨다.
                                        ParsingAnswer(response);
                                    }
                                    catch (IOException | JSONException | NetworkOnMainThreadException | NullPointerException e) {
                                        chatArrayList.add(new Chat(BOTTEXT_KEY, "서비스가 원활하지 않습니다."));
                                        adapter.notifyItemInserted(chatArrayList.size()-1);
                                        if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                                            mTTS.speak("서비스가 원활하지 않습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                        }
                                    }
                                }
                            });

                        }
                    });
                }
                // 스크롤 위치 업데이트
                chatView.scrollToPosition(chatArrayList.size()-1);
                // 입력창 초기화
                editMessage.setText("");
            }
        });

        // 도움말 버튼 눌렀을때의 리스너 함수
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다이얼로그 객체 생성
                AlertDialog.Builder ad = new AlertDialog.Builder(ChattingActivity.this);
                // 다이얼로그 창의 제목 셋팅
                ad.setTitle("우송이에게 물어보세요!");
                // 다이얼로그 창의 메세지 셋팅
                ad.setMessage("우송대 학사일정 알려줘.\n" +
                        "우송대 건물위치 알려줘.\n" +
                                "우송대 졸업요건 알려줘.\n" +
                                "학과별 홈페이지 주소알려줘.\n" +
                                "도서관 위치 알려줘.\n" +
                                "우송대 등록금 알려줘.\n" +
                                "우송대 장학제도 알려줘.\n" +
                                "학생식당 알려줘.\n" +
                                "기숙사 안내.\n" +
                                "입학처 알려줘.\n" +
                                "휴학 알려줘.\n" +
                                "수강신청 어떻게해.\n" +
                                "\n" +
                                "\n" +
                        "우송대학교에 대해 궁금한것을 물어보세요!");

                // 긍정적 답안의 버튼 만들기.
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 다이얼로그 확인 버튼 클릭 시 다이얼로그 창 닫기
                        dialog.dismiss();
                    }
                });
                // 만든 다이얼로그 창 모여주기
                ad.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 만약 TTS 기능이 작동중 일때 TTS 기능 정지
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    // 음성인식 하는 클래스. AsyncTask로 음성인식을 한 결과를 돌려준다.
    public class VoiceTask extends AsyncTask<String, Integer, String> {
        String str = null;
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                getVoice();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return str;
        }
        @Override
        protected void onPostExecute(String result) {
            try {

            } catch (Exception e) {
                Log.d("onActivityResult", "getImageURL exception");
            }
        }
    }

    // 구글 음성인식 창 보여주는 함수.
    private void getVoice() {
        // 새로운 인텐트 생성
        Intent intent = new Intent();
        // 인텐트에서 할 작업 선택
        intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // 인텐트에 데이터 전달
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        String language = "ko-KR";
        // 인텐트에 셋팅할 언어 데이터 전달
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        startActivityForResult(intent, 2);

    }

    // 음성인식한 것의 결과를 받는 함수.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // 만약 정상적인 종료를 하였을 경우
        if (resultCode == RESULT_OK) {
            // String 결과 데이터 받기
            ArrayList<String> results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String str = results.get(0);

            // UI에 출력(editMessage 에 출력)
            TextView tv = findViewById(R.id.editMessage);
            tv.setText(str);
        }
    }

    // 리사이클러뷰 초기화하는 함수. 처음 액티비티 실행때만 실행되는 것이다.
    private void CreateRecyclerview(){
        chatArrayList = new ArrayList<>();
        // Adapter 생성
        adapter = new ChatAdapter(chatArrayList, ChattingActivity.this);
        // LayoutManager 생성 --> 아이템 뷰가 나열되는 형태를 관리하기 위한 요소이다. 여러가지가 있지만 그중에서 LinearLayoutManager를 사용함.
        manager = new LinearLayoutManager(ChattingActivity.this);
        // Recycler 에 LayoutManager 등록하기
        chatView.setLayoutManager(manager);
        // Recycler 에 Adapter 등록하기
        chatView.setAdapter(adapter);
        // Recycler 에 함수 인자만큼의 Item 데이터 저장
        chatView.setItemViewCacheSize(100);
    }

    // 정상적으로 서버로부터 받은 대답을 분석하는 함수이다.
    private void ParsingAnswer(Response response)
            throws JSONException, IOException, NetworkOnMainThreadException, NullPointerException {

        // 만약 서버로부터 온 대답이 null일 경우
        if(response == null) {
            // BOTTEXT 형태의 뷰홀더를 출력한다.
           chatArrayList.add(new Chat(BOTTEXT_KEY, "서비스가 원활하지 않습니다."));
           // ArrayList 형태의 데이터에서 새롭게 데이터가 추가되었다고 알려주는 함수이다. 인자는 새롭게 추가된 데이터 위치이다.
           adapter.notifyItemInserted(chatArrayList.size()-1);
           // 만약 사용자가 설정창에서 TTS기능을 켜놓았을 경우에만 text를 말하게 한다.
            if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                mTTS.speak("서비스가 원활하지 않습니다.", TextToSpeech.QUEUE_FLUSH, null);
            }
            return;
        }

        // 데이터를 JSON 데이터 형식으로 받는다.
        String message = response.body().string();
        JSONObject jObject = new JSONObject(message);

        String intent = null;
        String Message = null;
        String image = null;


        // JSON 데이터 파싱
        if(!jObject.isNull("Answer")) {
            // Answer 부분의 String 데이터를 가져온다.
            Message = jObject.getString("Answer");
        }
        if(!jObject.isNull("Intent")) {
            // Intent 부분의 String 데이터를 가져온다.
            intent = jObject.getString("Intent");
        }
        if(!jObject.isNull("AnswerImageUrl")) {
            // AnswerImageUrl 부분의 String 데이터를 가져온다.
            image = jObject.getString("AnswerImageUrl");
        }


        // 길찾기에 관한 내용일때만 빼고 item_bottext 레이아웃으로 Message 출력. 또한 답변이 null값이 아닐 경우에만 출력
        if(!jObject.isNull("Answer")) {
            if(!Message.contains("http://kko.to/")) {
                // BOTTEXT 형태의 뷰홀더를 출력한다.
                chatArrayList.add(new Chat(BOTTEXT_KEY, Message));
                // ArrayList 형태의 데이터에서 새롭게 데이터가 추가되었다고 알려주는 함수이다. 인자는 새롭게 추가된 데이터 위치이다.
                adapter.notifyItemInserted(chatArrayList.size()-1);
                // 만약 사용자가 설정창에서 TTS 기능을 켜놓았을 경우에만 Message 를 말하게 한다.
                if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                    mTTS.speak(Message, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }


        // 이후에 AddExtraItem()를 실행하여 추가적인 것을 실행하도록 한다.
        AddExtraItem(Message, intent, image);
        // 스크롤 위치 업데이트
        chatView.scrollToPosition(chatArrayList.size()-1);
    }

    // 답변을 분석하여 연락처 / URL / MAP 구분한다.
    private void AddExtraItem(String botAnswer, String intent, String image) {

        // 만약 이미지 출력을 요구하는 경우
        if(image != null) {
            chatArrayList.add(new Chat(BOTIMAGE_KEY, image));
            adapter.notifyItemInserted(chatArrayList.size()-1);
        }

        // 만약 String 안에 URL 이 있을때
        if(DistinguishAnswer.containsLink(botAnswer)) {
            // 서버로부터 온 대답 중 url 만을 추출한다.
            List<String> urls =  DistinguishAnswer.extractUrls(botAnswer);
            // List 형태를 String 형태로 변환한다. 첫번째 인사에 들어가있는 문자를 두번째 인자 에 들어가있는 각각의 데이터들의 구분 문자로 사용한다.
            String url = TextUtils.join("", urls);

            // 만약 botAnswer 가 길찾기일때 아래에 있는 구문을 실행한다.
            if (botAnswer.contains("http://kko.to/")) {
                // 서버로부터 온 데이터를 ,를 기준으로 나눈다.
                String[] answer = botAnswer.split(",");
                // 데이터를 분석하여 웹 URL Scheme 기능을 이용하기 위한 밑작업을 한다.
                String text = answer[0] + "," + answer[1] + "," + answer[2];

                Chat chat = new Chat(BOTMAP_KEY, text);
                // 데이터에 ImageUrl를 셋팅한다.
                chat.setImageUrl(answer[3]);
                chatArrayList.add(chat);
            }
            // 길찾기가 아니면 Web 미리보기 창으로
            else {
                chatArrayList.add(new Chat(BOTWEB_KEY, url));
            }
            adapter.notifyItemInserted(chatArrayList.size()-1);
        }
        // 만약 String 안에 전화번호가 있을때 --> 앞의 세글자가 042 이고 총 전화번호 개수가 9~12 사이여야 됨.(9, 12 포함) ==> intent 추가
        else if(DistinguishAnswer.getPhoneNumber(botAnswer) != null) {
            chatArrayList.add(new Chat(BOTBUTTON_KEY,  DistinguishAnswer.getPhoneNumber(botAnswer)));
            adapter.notifyItemInserted(chatArrayList.size()-1);
        }
    }
}