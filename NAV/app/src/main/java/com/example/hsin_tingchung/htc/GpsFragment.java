package com.example.hsin_tingchung.htc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class GpsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    //private LocationManager status;
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;    //最佳資訊提供者

    private final String TAG = "GPS";

    private ProgressDialog progressDialog;
    private double lng, lat;

    public GpsFragment() {
    }

    public static GpsFragment newInstance(String param1, String param2) {
        GpsFragment fragment = new GpsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        testLocationProvider();        //檢查定位服務
    }


    private void testLocationProvider() {

        //取得系統定位服務
        lms = (LocationManager) (getActivity().getSystemService(Context.LOCATION_SERVICE));


        if (lms.isProviderEnabled(LocationManager.GPS_PROVIDER) || lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();

        } else {
            Toast.makeText(getActivity(), "請開啟定位服務", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面

        }
    }

    //handler!!!

    private void locationServiceInitial() {

        Toast.makeText(getActivity(), "開始囉", Toast.LENGTH_SHORT).show();
        Criteria criteria = new Criteria();    //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者

        Location location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);//取得上次已知的位置
        updateWithNewLocation(location);

        //Location Listener
        long minTime = 5000;//ms
        float minDist = 1;//meter
        lms.requestLocationUpdates(bestProvider, minTime, minDist, locationListener);

        //getLocation(location);
    }

    private void updateWithNewLocation(Location location) {
        String where = "";
        if (location != null) {
            lng = location.getLongitude();   //經度
            lat = location.getLatitude();    //緯度
            float speed = location.getSpeed();      //速度
            long time = location.getTime();         //時間
            String timeString = getTimeString(time);

            where = "經度: " + lng +
                    "\n緯度: " + lat +
                    "\n速度: " + speed +
                    "\n時間: " + timeString +
                    "\nProvider: " + bestProvider;

        } else {
            where = "無法取得位置";
        }

        //位置改變顯示
        Toast.makeText(getActivity(), where, Toast.LENGTH_SHORT).show();
    }

    public String getLocation(){ //經度
        NumberFormat nf =  NumberFormat.getInstance();
        nf.setMaximumFractionDigits(6);
        return nf.format(lng)+" , "+nf.format(lat);
    }


    LocationListener locationListener = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.v(TAG, "Status Changed: Out of Service");
                    //Toast.makeText(getActivity(), "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.v(TAG, "Status Changed: Temporarily Unavailable");
                    //Toast.makeText(getActivity(), "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Log.v(TAG, "Status Changed: Available");
                    //Toast.makeText(getActivity(), "Status Changed: Available", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gps, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    private String getTimeString(long timeInMilliseconds){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timeInMilliseconds);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
