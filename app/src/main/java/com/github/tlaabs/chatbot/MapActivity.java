package com.github.tlaabs.chatbot;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;

public class MapActivity extends AppCompatActivity {
    FloatingActionButton btnGps;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnGps = (FloatingActionButton) findViewById(R.id.btnGps);

        mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);


        // 2022-01-25 황우진
        // 우송대 서캠퍼스로 중심점 변경하는 코드 삭제
        // 앱 최초 실행시 중심점 변경 ( 현위치 우송대 서캠퍼스)
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.3380469589644, 127.44600026824709), true);

        //마커
        MapPOIItem marker = new MapPOIItem();

        //배열좌표
        double[] x = {36.33804696, 36.33956397, 36.33963272, 36.33946041, 36.33805587, 36.33816246, 36.33756181, 36.33715129, 36.33752291, 36.33711672, 36.33670245, 36.33582814, 36.33552471, 36.33564355, 36.33543785, 36.33547242, 36.33598804, 36.33594444, 36.33638422, 36.33672776, 36.33609812, 36.33651703, 36.33697709, 36.33732493, 36.33887392, 36.33854551, 36.33506409, 36.33476293, 36.33493048, 36.33788532, 36.3384468, 36.32900924, 36.33862182};
        double[] y = {127.4464182, 127.4472533, 127.4477692, 127.4464404, 127.44511, 127.4451695, 127.4457167, 127.4450944, 127.4443702, 127.4440752, 127.4438424, 127.4433085, 127.4437639, 127.4444425, 127.4446611, 127.4452619, 127.4449776, 127.4454512, 127.4461524, 127.4457214, 127.453864, 127.4540126, 127.4540683, 127.453748, 127.4496652, 127.4497108, 127.44621, 127.4459884, 127.4466725, 127.4475792, 127.4477128, 127.4387044, 127.4321894};


        //배열의 크기
        double[] array = new double[33];

        String Name[] = {"W1 도서관", "W2 국제교육센터", "W2-1 학군단", "W3 유학생기숙사", "W4 철도물류관", "W5 보건의료과학관", "W6 교양교육관", "W7 우송관", "W8 우송유치원", "W9 정례원", "W10 사회복지융합관", "W11 체육관", "W12 SICA", "W13 우송타워", "W14 Culinary Center", "W15 식품건축관", "W16 학생회관", "W17 미디어융합관", "W18 우송예술회관", "W19 Endicott Building", "E7 자립관(솔지오1)", "E8 청솔관(솔지오2)", "E9 단정관(솔지오3)", "E10 독행관(솔지오4)", "S1 우송어학센터", "S2 우송IT교육센터", "S3 우송뷰티센터", "S4 우송애견센터", "S5 우송오토센터", "청운 1숙(남)", "청운 2숙(여)", "대전화병원", "솔브릿지 국제경영대학"};

        //맵 포인트 위도경도 설정
        for (int i = 0; i < array.length; i++) {
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x[i], y[i]);
            marker.setItemName((Name[i]));

            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

            mapView.addPOIItem(marker);
        }

        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TedPermission.isGranted(MapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    PermissionListener permissionlistener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            SetTrackingMode(mapView);
                        }

                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {

                        }
                    };
                    TedPermission.with(MapActivity.this)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("접근 거부하셨습니다.\n[설정] - [권한]에서 권한을 허용해주세요.")
                            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                            .check();
                } else {
                    SetTrackingMode(mapView);
                }
            }
        });


        if (!TedPermission.isGranted(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    if (!checkLocationServicesStatus()) {
                        mapView.setCurrentLocationTrackingMode
                                (MapView.CurrentLocationTrackingMode.TrackingModeOff);
                        mapView.setShowCurrentLocationMarker(false);
                        btnGps.setImageResource(R.drawable.ic_baseline_gps_not_fixed_24);
                        return;
                    }
                    SetTrackingMode(mapView);
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                }
            };
            TedPermission.with(MapActivity.this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("접근 거부하셨습니다.\n[설정] - [권한]에서 권한을 허용해주세요.")
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                    .check();
        } else {
            if (!checkLocationServicesStatus()) {
                mapView.setCurrentLocationTrackingMode(
                        MapView.CurrentLocationTrackingMode.TrackingModeOff);
                mapView.setShowCurrentLocationMarker(false);
                btnGps.setImageResource(R.drawable.ic_baseline_gps_not_fixed_24);
                return;
            }
            SetTrackingMode(mapView);
        }

    }

    private void SetTrackingMode(MapView mapView) {

        if (!checkLocationServicesStatus()) {
            if (mapView.getCurrentLocationTrackingMode() == MapView.CurrentLocationTrackingMode.TrackingModeOff){
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                mapView.setShowCurrentLocationMarker(false);
                btnGps.setImageResource(R.drawable.ic_baseline_gps_not_fixed_24);
                showDialogForLocationServiceSetting();
            } else {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                mapView.setShowCurrentLocationMarker(false);
                btnGps.setImageResource(R.drawable.ic_baseline_gps_not_fixed_24);
            }
            return;
        }

        if (mapView.getCurrentLocationTrackingMode() == MapView.CurrentLocationTrackingMode.TrackingModeOff) {
            mapView.setZoomLevel(0, true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setShowCurrentLocationMarker(true);
            btnGps.setImageResource(R.drawable.ic_baseline_gps_fixed_24);
        }
        else if (mapView.getCurrentLocationTrackingMode() == MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading) {
            mapView.setZoomLevel(0, true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            mapView.setShowCurrentLocationMarker(true);
            btnGps.setImageResource(R.drawable.ic_baseline_compass_calibration_24);
        }
        else if (mapView.getCurrentLocationTrackingMode() == MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading) {
            mapView.setZoomLevel(0, true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setShowCurrentLocationMarker(true);
            btnGps.setImageResource(R.drawable.ic_baseline_gps_fixed_24);
        }

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("현위치 기능을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, 2000);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 2000:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        SetTrackingMode(mapView);
                        return;
                    }
                }
                break;
        }
    }
}