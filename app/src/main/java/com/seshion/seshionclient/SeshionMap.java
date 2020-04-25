package com.seshion.seshionclient;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeshionMap extends Fragment implements OnMapReadyCallback {

    private String PARCEL_KEY = "data";
    List<UserSession> seshions;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    SupportMapFragment mapFragment;
    public SeshionMap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_seshion_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        /* get the parcel from dashboard activity */
        Bundle bundle = getArguments();
        StateParcel stateParcel = bundle.getParcelable(PARCEL_KEY);
        seshions = stateParcel.getAllOpenSessions();

        List<UserAccount> friends = stateParcel.getFriends();

        if(mapFragment == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();

        }

        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        for(int i = 0; i < seshions.size(); i++){


            /* make a polygon around all of the seshions
            ( needs 5 coordinates to connect the dots )*/
            Polyline seshionOutline = googleMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add(
                            new LatLng(seshions.get(i).getLatitudeTopLeft(), seshions.get(i).getLongitudeTopLeft()),
                            new LatLng(seshions.get(i).getLatitudeTopRight(), seshions.get(i).getLongitudeTopRight()),
                            new LatLng(seshions.get(i).getLatitudeBottomRight(), seshions.get(i).getLongitudeBottomRight()),
                            new LatLng(seshions.get(i).getLatitudeBottomLeft(), seshions.get(i).getLongitudeBottomLeft()),
                            new LatLng(seshions.get(i).getLatitudeTopLeft(), seshions.get(i).getLongitudeTopLeft())  ));
            // Store a data object with the polyline, used here to indicate an arbitrary type.
            seshionOutline.setTag(seshions.get(i).getName());
            // Style the polyline.
            stylePolyline(seshionOutline);

            /* Add polygons to indicate areas on the map.
                needs 4 points to draw the quadrilateral */
            Polygon seshionPolygon = googleMap.addPolygon(new PolygonOptions()
                    .clickable(true)
                    .add(
                            new LatLng(seshions.get(i).getLatitudeTopLeft(), seshions.get(i).getLongitudeTopLeft()),
                            new LatLng(seshions.get(i).getLatitudeTopRight(), seshions.get(i).getLongitudeTopRight()),
                            new LatLng(seshions.get(i).getLatitudeBottomRight(), seshions.get(i).getLongitudeBottomRight()),
                            new LatLng(seshions.get(i).getLatitudeBottomLeft(), seshions.get(i).getLongitudeBottomLeft())  ));
            // Store a data object with the polygon, used here to indicate an arbitrary type.
            seshionPolygon.setTag(seshions.get(i).getName());
            // Style the polygon.
            stylePolygon(seshionPolygon);

            // Add marker for this session boundary
            googleMap.addMarker(new MarkerOptions().position(new LatLng(seshions.get(i).getLatitudeTopLeft(), seshions.get(i).getLongitudeTopLeft())).title(seshions.get(i).getName()));

            // Position the map's camera near the seshion,
            // and set the zoom factor so most of Australia shows on the screen.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(seshions.get(i).getLatitudeTopLeft(), seshions.get(i).getLongitudeTopLeft()), 16.5f));

        }

        // Set listeners for click events.
        //googleMap.setOnPolylineClickListener(this);
        //googleMap.setOnPolygonClickListener(this);

    }


    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
//                polyline.setStartCap(
//                        new CustomCap(
//                                BitmapDescriptorFactory.fromResource(R.drawable.arrow_down_float), 10));//R.drawable.ic_arrow
                polyline.setStartCap(new RoundCap());

                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
            case "scaziSkatePark":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
            case "scaziSkatePark":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_WHITE_ARGB;
                fillColor = COLOR_WHITE_ARGB;
                break;
            case "scaziTennisCourt":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_BLUE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }
}
