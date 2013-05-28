package net.openwatch.reporter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import net.openwatch.reporter.constants.Constants;
import net.openwatch.reporter.model.OWServerObject;
import net.openwatch.reporter.model.OWVideoRecording;

public class MapFragment extends SupportMapFragment implements OWMediaObjectBackedEntity{
	private static final String TAG = "MapFragment";
	
    private GoogleMap mMap;
    private LatLng mStartLocation;
    private LatLng mStopLocation;

    public MapFragment() {
        super();
    }

    public static MapFragment newInstance(LatLng start, LatLng stop) {
        MapFragment frag = new MapFragment();
        frag.mStartLocation = start;
        frag.mStopLocation = stop;
        return frag;
    }

    @Override
    public void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
    }
    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstance) {
    View layout = super.onCreateView(inflater, view, savedInstance);

    FrameLayout frameLayout = new FrameLayout(getActivity());
    frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    ((ViewGroup) layout).addView(frameLayout,
        new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    return layout;
}
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(OWMediaObjectViewActivity.model_id != -1){
        	OWServerObject server_object = OWServerObject.objects(getActivity().getApplicationContext(), OWServerObject.class).get(OWMediaObjectViewActivity.model_id);

        	if(server_object != null)
        		mapOWServerObjectInterface(server_object);
        	
        }
        // Hack to fix MapFragment causing drawing errors
        // see http://stackoverflow.com/questions/13837697/viewpager-with-google-maps-api-v2-mysterious-black-view/13910364#13910364
        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ((ViewGroup) view).addView(frameLayout,
            new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        return view;
    }
    
    private void mapOWServerObjectInterface(OWServerObject server_object){
        Context app_context = getActivity().getApplicationContext();
        if(server_object.getMediaType(app_context) == Constants.MEDIA_TYPE.VIDEO){
            OWVideoRecording video_object = (OWVideoRecording) server_object.getChildObject(app_context);
            if(video_object.begin_lat.get() != null && video_object.end_lat.get() != null)
                Log.i(TAG, "recording begin point: " + String.valueOf(video_object.begin_lat.get()) + ", " + String.valueOf(video_object.begin_lon.get()));
            mStartLocation = new LatLng(video_object.begin_lat.get(), video_object.begin_lon.get());
        }
        if(server_object.getLat(app_context) != 0.0 && server_object.getLon(app_context) != 0.0){
            Log.i(TAG, "recording end point: " + String.valueOf(server_object.getLat(app_context)) + ", " + String.valueOf(server_object.getLon(app_context)));
    	    mStopLocation = new LatLng(server_object.getLat(app_context), server_object.getLon(app_context));
        }
    	initMap();
    }

    private void initMap() {
    	if(this.getMap() == null)
    		return;
    	
        UiSettings settings = getMap().getUiSettings();
        //settings.setAllGesturesEnabled(false);
        settings.setMyLocationButtonEnabled(false);
        getMap().clear();
        if(mStartLocation != null){
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(mStartLocation, 10));
            getMap().addMarker(
                    new MarkerOptions().position(mStartLocation)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_start)));
        }
        if(mStopLocation != null){
            getMap().addMarker(
                    new MarkerOptions().position(mStopLocation)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_stop)));
            if(mStartLocation == null)
                getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(mStopLocation, 10));
        }

        if(mStartLocation != null )
            getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(mStartLocation, 18));
        else if(mStopLocation != null)
            getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(mStopLocation, 18));
    }

	@Override
	public void populateViews(OWServerObject server_object, Context c) {
		mapOWServerObjectInterface(server_object);
	}

}