package com.example.posted.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.posted.R;
import com.example.posted.constants.ConstantsHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Context mContext;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        TextView mInfoView = (TextView) view.findViewById(R.id.info);
        mInfoView.setText(R.string.about_us);

        this.mMapView = (MapView) view.findViewById(R.id.mapView);


        this.mMapView.onCreate(savedInstanceState);

        this.mMapView.onResume();

        try {
            MapsInitializer.initialize(this.mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                MainFragment.this.mGoogleMap = mMap;

                // For dropping a marker at a point on the Map
                LatLng softUniLocation = new LatLng(ConstantsHelper.LATITUDE, ConstantsHelper.LONGITUDE);
                MainFragment.this.mGoogleMap.addMarker(new MarkerOptions()
                        .position(softUniLocation)
                        .title("PosTED")
                        .snippet(MainFragment.this.getResources().getString(R.string.posted_office)));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(softUniLocation).zoom(15).build();
                MainFragment.this.mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }


        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}



