package com.github.tlaabs.chatbot;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

// 2022-01-06 황우진
// 채팅창 구현을 위한 ChatAdapter 제작
// 대답 형식에 따라 각각의 UI 뷰홀더를 갖추고 있음.
public class ChatAdapter extends RecyclerView.Adapter{

    private static Context context;
    ArrayList<Chat> chatArrayList;


    // 생성자에서 데이터 리스트 객체를 전달받음.
    public ChatAdapter(ArrayList<Chat> chatArrayList, Context context) {
        this.chatArrayList = chatArrayList;
        this.context = context;
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 해당하는 뷰홀더 객체를 리턴한다.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (i){
            case 0:
                // LayoutInflater 는 XML 에 정의된 Resource 를 View 객체로 반환해주는 역할
                // .from() : 주어진 Context 에서 LayoutInflater 를 가져온다.
                // .inflate() : 사전에 미리 선언해뒀던 레이아웃에 작성했던 xml 의 메모리객체가 삽입되게 된다.
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
                return new userViewHolder(view);
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bottext, viewGroup, false);
                return new bottextViewHolder(view);
            case 2:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botstart, viewGroup, false);
                return new botStartViewHolder(view);
            case 3:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botbutton, viewGroup, false);
                return new botButtonViewHolder(view);
            case 4:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botweb, viewGroup, false);
                return new botWebViewHolder(view);
            case 5:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botmap, viewGroup, false);
                return new botMapViewHolder(view);
            case 6:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botimage, viewGroup, false);
                return new botImageViewHolder(view);
        }
        return null;
    }



    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시한다.
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        Chat chat = chatArrayList.get(i);
        final String message = chatArrayList.get(i).getMessasge();
        // ArrayList 데이터의 i 위치에 있는 것들 중, 무슨 뷰홀더로 UI를 업데이트를 할지 switch 문을 이용하여 정한다.
        switch (chatArrayList.get(i).getWho()) {
            // user 텍스트 일 경우
            case "user": {
                ((userViewHolder)viewHolder).userMsg.setText(message);
                break;
            }

            // bot 텍스트 ui 일경우
            case "bottext":{
                ((bottextViewHolder)viewHolder).bottextMsg.setText(message);

                break;
            }

            // bot 처음 시작할 때의 UI 일경우
            case "botstart": {
                // 만약 서버로부터 받은 데이터가 있을 경우에는 버튼의 텍스트를 수정한다.
                if(message != null) {
                    String[] str = message.split("#");
                    ((botStartViewHolder)viewHolder).btnQnA1.setText(str[0]);
                    ((botStartViewHolder)viewHolder).btnQnA2.setText(str[1]);
                    ((botStartViewHolder)viewHolder).btnQnA3.setText(str[2]);
                    ((botStartViewHolder)viewHolder).btnQnA4.setText(str[3]);
                }

                // 첫번째 질문 edittext에..
                ((botStartViewHolder)viewHolder).btnQnA1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et = ((Activity)context).findViewById(R.id.editMessage);
                        et.setText(((botStartViewHolder)viewHolder).btnQnA1.getText());
                    }
                });

                // 두번째 질문 edittext에..
                ((botStartViewHolder)viewHolder).btnQnA2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et = ((Activity)context).findViewById(R.id.editMessage);
                        et.setText(((botStartViewHolder)viewHolder).btnQnA2.getText());
                    }
                });

                // 세번째 질문 edittext에..
                ((botStartViewHolder)viewHolder).btnQnA3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et = ((Activity)context).findViewById(R.id.editMessage);
                        et.setText(((botStartViewHolder)viewHolder).btnQnA3.getText());
                    }
                });

                // 네번째 질문 edittext에..
                ((botStartViewHolder)viewHolder).btnQnA4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et = ((Activity)context).findViewById(R.id.editMessage);
                        et.setText(((botStartViewHolder)viewHolder).btnQnA4.getText());
                    }
                });
                break;
            }

            // bot 버튼 형식의 UI일경우
            case "botbutton": {

                // 만약 메세지가 시간표일 경우, 버튼 텍스트를 시간표 바로가기로 수정한다.
                if(message.equals("시간표")) {
                    ((botButtonViewHolder)viewHolder).btnBotbutton.setText("시간표 바로가기");
                    ((botButtonViewHolder)viewHolder).btnBotbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 시간표 인텐트로
                            Intent intent =
                                    new Intent(context.getApplicationContext(),
                                            TimeTableActivity.class);
                            context.startActivity(intent);
                        }
                    });
                }
                // 연락처
                else {
                    ((botButtonViewHolder)viewHolder).btnBotbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 전화 걸기
                            Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + message));
                            context.startActivity(mIntent);
                        }
                    });
                }
                break;
            }

            // bot 웹 미리보기 및 바로가기 UI 일경우
            case "botweb": {
                // 웹 미리보기를 제공하는 라이브러리를 사용한다.
                // URL을 읽어서 URL의 metadata를 얻어 사이트명, url, 설명, 이미지를 얻는다.
                // 아래의 코드는 userHolder에 차례대로 적용시키는 중이다.
                RichPreview richPreview = new RichPreview(new ResponseListener() {
                    @Override
                    public void onData(MetaData metaData) {

                       if (metaData != null) {
                           if(metaData.getTitle() != null){
                               // 제목 텍스트뷰 변경
                               ((botWebViewHolder)viewHolder).preview_title.setText(metaData.getTitle());
                           }
                           if(metaData.getUrl() != null){
                               // 링크 텍스트뷰 변경
                               ((botWebViewHolder)viewHolder).preview_link.setText(metaData.getUrl());
                           }
                           if(metaData.getDescription() != null){
                               // 설명 텍스트뷰 변경
                               ((botWebViewHolder)viewHolder).preview_description.setText(metaData.getDescription());
                           }
                           if(metaData.getImageurl() != null){
                               // 웹 미리보기 이미지 셋팅
                               Glide.with(context).load(metaData.getImageurl()).into(((botWebViewHolder)viewHolder).img_preview);
                           }
                       }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(e.getMessage(), e.getMessage());

                    }
                });

                // 사전에 미리 분석하여 얻은 url을 리치프리뷰에게 넘겨 메타데이터를 얻은 뒤 앞에서 설정한 UI대로 자동으로 만들어준다.
                richPreview.getPreview(message);

                // 웹 미리보기를 클릭할 경우
                ((botWebViewHolder)viewHolder).layout_preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 인터넷으로 이동
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
                        context.startActivity(intent);
                    }
                });
                break;
            }

            // bot 카카오맵 미리보기 및 바로가기 UI일 경우
            case "botmap": {
                // 미리 파싱해둔 메세지를 링크 뒤에 붙힌다
                final String mapLink = "https://map.kakao.com/link/to/" + message;

                // 카카오맵 이미지 미리보기
                RichPreview richPreview = new RichPreview(new ResponseListener() {
                    @Override
                    public void onData(MetaData metaData) {
                        if (metaData != null) {
                            if(metaData.getImageurl() != null){
                                Glide.with(context).load(metaData.getImageurl()).into(((botMapViewHolder)viewHolder).botMapImage);
                            }
                        }
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.d(e.getMessage(), e.getMessage());
                    }
                });


                richPreview.getPreview(chat.getImageUrl());

                ((botMapViewHolder)viewHolder).botMapImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapLink));
                        context.startActivity(intent);
                    }
                });

                ((botMapViewHolder)viewHolder).btnMoveMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapLink));
                        context.startActivity(intent);
                    }
                });
                break;
            }

            // 이미지 뷰홀더 Key 일 경우
            case "botimage": {
                //
                Glide.with(context).load(message).into(((botImageViewHolder)viewHolder).botImage);

                // 이미지 누를시 액티비티 이동
                ((botImageViewHolder) viewHolder).botImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // 비트맵으로 전환시.. 이쪽이 더 효율적이면 이쪽으로 코드 바꾸기
                        Intent intent = new Intent(context, ImageActivity.class);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap bitmap =
                                ((BitmapDrawable)((botImageViewHolder) viewHolder)
                                        .botImage.getDrawable()).getBitmap();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        intent.putExtra("image", byteArray);
                        context.startActivity(intent);

                        // Intent intent = new Intent(context, ImageActivity.class);
                        //intent.putExtra("image", message);
                        // context.startActivity(intent);
                    }
                });



                break;
            }
        }
    }

    // 해당하는 위치의 아이템 ID를 반환한다.
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // position에 해당하는 아이템 항목에 따른 뷰타입을 리턴한다. Chat 이라는 데이터 클래스를 따로 정의한다.
    @Override
    public int getItemViewType(int position) {
        switch (chatArrayList.get(position).getWho()){
            case "user": // user 뷰홀더
                return 0;
            case "bottext": // bot text 뷰홀더
                return 1;
            case "botstart": // bot 시작 뷰홀더
                return 2;
            case "botbutton": // bot Button 대답 뷰홀더
                return 3;
            case "botweb": // bot web 미리보기 대답 뷰홀더
                return 4;
            case "botmap": // bot map 미리보기 및 앱 바로가기 대답 뷰홀더
                return 5;
            case "botimage":    // bot 이미지 UI 및 이미지 확대 액티비티 바로가기 대답 뷰홀더
                return 6;
            default:            // 오류
                return -1;
        }
    }


    // 데이터 개수 반환
    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }



    // ---------------------------------------아이템 뷰를 저장하는 뷰홀더 클래스-----------------------------------------------


    // 유저 텍스트 뷰홀더 클래스
    public static class userViewHolder extends RecyclerView.ViewHolder {
        TextView userMsg;

        public userViewHolder(@NonNull View itemView) {
            super(itemView);
            userMsg = (TextView)itemView.findViewById(R.id.userTextMsg);
        }
    }

    // 챗봇 텍스트 뷰홀더 클래스
    public static class bottextViewHolder extends RecyclerView.ViewHolder {
        TextView bottextMsg;

        public bottextViewHolder(@NonNull View itemView) {
            super(itemView);
            bottextMsg =  (TextView)itemView.findViewById(R.id.botTextMsg);
        }
    }

    // 챗봇 시작 UI 뷰홀더 클래스
    public static class botStartViewHolder extends RecyclerView.ViewHolder {
        TextView botStartMsg;
        Button btnQnA1, btnQnA2, btnQnA3, btnQnA4;

        public botStartViewHolder(@NonNull View itemView) {
            super(itemView);
            botStartMsg = (TextView)itemView.findViewById(R.id.botStartMsg);
            btnQnA1 =  (Button)itemView.findViewById(R.id.btnQnA1);
            btnQnA2 =  (Button)itemView.findViewById(R.id.btnQnA2);
            btnQnA3 =  (Button)itemView.findViewById(R.id.btnQnA3);
            btnQnA4 =  (Button)itemView.findViewById(R.id.btnQnA4);
        }
    }

    // 챗봇 버튼 UI 뷰홀더 클래스
    public static class botButtonViewHolder extends  RecyclerView.ViewHolder {
        Button btnBotbutton;

        public botButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            btnBotbutton = (Button) itemView.findViewById(R.id.btnBotbutton);
        }
    }

    // 챗봇 웹 미리보기 및 바로가기 UI 뷰홀더 클래스
    public static class botWebViewHolder extends  RecyclerView.ViewHolder {
        LinearLayout layout_preview;
        ImageView img_preview;
        TextView preview_link,preview_title,preview_description;

        public botWebViewHolder(@NonNull View itemView) {
            super(itemView);
            img_preview = (ImageView) itemView.findViewById(R.id.img_preview);
            preview_link = (TextView) itemView.findViewById(R.id.preview_link);
            preview_title = (TextView)itemView.findViewById(R.id.preview_title);
            preview_description = (TextView)itemView.findViewById(R.id.preview_description);
            layout_preview = (LinearLayout)itemView.findViewById(R.id.layout_preview);
        }
    }

    // 챗봇 카카오맵 미리보기 및 바로가기 UI 뷰홀더 클래스
    public static class botMapViewHolder extends  RecyclerView.ViewHolder {
        ImageView botMapImage;
        Button btnMoveMap;

        public botMapViewHolder(@NonNull View itemView) {
            super(itemView);
            botMapImage = (ImageView)itemView.findViewById(R.id.botMapImage);
            btnMoveMap = (Button)itemView.findViewById(R.id.btnMoveMap);
        }
    }

    // 챗봇 이미지 UI 뷰홀더 클래스
    public static class botImageViewHolder extends  RecyclerView.ViewHolder {
        ImageView botImage;

        public botImageViewHolder(@NonNull View itemView) {
            super(itemView);
            botImage = (ImageView)itemView.findViewById(R.id.botImage);
        }
    }

}