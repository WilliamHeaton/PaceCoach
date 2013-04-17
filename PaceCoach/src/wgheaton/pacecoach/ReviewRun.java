package wgheaton.pacecoach;

import java.util.List;
import wgheaton.pacecoach.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import wgheaton.pacecoach.PC.DataView;
import wgheaton.pacecoach.PC.MyOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ReviewRun extends MapActivity {
	Activity ctx;
	int runid;
	int splits;
	int showing = -1;
	int profile;
	final int SHOWING_MAPS = 1;
	final int SHOWING_SPLITS = 2;
	final int SHOWING_GRAPH = 3;
	int mv;
	@Override
	public void onBackPressed(){
		Intent i = new Intent(ctx, History.class);
		i.putExtra(PC.PREF_PROFILE, profile);
		startActivity(i);
		finish();
	}

    @Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("reviewmode",showing);
		super.onSaveInstanceState(outState);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		
		Bundle extras = getIntent().getExtras();
		runid =  (extras != null) ? extras.getInt("runid") : 0;  
		profile = (extras != null) ? extras.getInt(PC.PREF_PROFILE) : 0;
		
		
		if(runid==0)
			finish();
		
		if(savedInstanceState!=null && savedInstanceState.getInt("reviewmode")!=0){
			showing =  savedInstanceState.getInt("reviewmode");
        }else{
        	showing = SHOWING_MAPS;
        }
		
		splits = PC.getSplitNum(ctx, runid);
		

		MyCustomView mcv = (MyCustomView) new MyCustomView(ctx);
		mcv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mcv.setId(R.id.container);
		setContentView(mcv);
		
		
	}
	public void fill(boolean r){
		
		LinearLayout container = (LinearLayout)findViewById(R.id.container);
		container.removeAllViews();
        

		DbAdapter db = new DbAdapter(this.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
		
		int v = r?R.layout.review_horizontal:R.layout.review_vertical;
        
        container.addView(getLayoutInflater().inflate(v, container,false));

        FrameLayout f;
        DataView d;
        
        f = ((FrameLayout) findViewById(R.id.time));
        f.addView(getLayoutInflater().inflate(R.layout.cell_normal, container,false));
        ((TextView) f.findViewById(R.id.label)).setText(PC.getText(ctx, PC.CELLS+PC.CELL_TYPE_TIME));
        d = (DataView) new DataView(ctx); d.setId(R.id.dataView); d.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
        ((LinearLayout) f.findViewById(R.id.dataContainer)).addView(d);
        d.setText(PC.formatTime(PC.getTime(ctx, runid)));

        f = ((FrameLayout) findViewById(R.id.distance));
        f.addView(getLayoutInflater().inflate(R.layout.cell_normal, container,false));
        ((TextView) f.findViewById(R.id.label)).setText(PC.getText(ctx, PC.CELLS+PC.CELL_TYPE_DISTANCE));
        ((TextView) f.findViewById(R.id.units)).setText(PC.getText(ctx, PC.UNITS+PC.UNITS_MILES));
        d = (DataView) new DataView(ctx); d.setId(R.id.dataView); d.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
        ((LinearLayout) f.findViewById(R.id.dataContainer)).addView(d);
        d.setText(PC.formatUnits(PC.getDistance(ctx, runid), PC.UNITS_MILES));


        f = ((FrameLayout) findViewById(R.id.speed));
        f.addView(getLayoutInflater().inflate(R.layout.cell_normal, container,false));
        ((TextView) f.findViewById(R.id.label)).setText(PC.getText(ctx, PC.CELLS+PC.CELL_TYPE_SPEEDAVG));
        ((TextView) f.findViewById(R.id.units)).setText(PC.getText(ctx, PC.UNITS+PC.UNITS_MINMILE));
        d = (DataView) new DataView(ctx); d.setId(R.id.dataView); d.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
        ((LinearLayout) f.findViewById(R.id.dataContainer)).addView(d);
        d.setText(PC.formatUnits(PC.getSpeedAvg(ctx, runid), PC.UNITS_MINMILE));

        f = ((FrameLayout) findViewById(R.id.altitude));
        f.addView(getLayoutInflater().inflate(R.layout.cell_normal, container,false));
        ((TextView) f.findViewById(R.id.label)).setText(PC.getText(ctx, PC.CELLS+PC.CELL_TYPE_ALTITUDE));
        ((TextView) f.findViewById(R.id.units)).setText(PC.getText(ctx, PC.UNITS+PC.UNITS_FEET));
        d = (DataView) new DataView(ctx); d.setId(R.id.dataView); d.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
        ((LinearLayout) f.findViewById(R.id.dataContainer)).addView(d);
        d.setText(PC.formatUnits(PC.getAltitudeAvg(ctx, runid), PC.UNITS_FEET));


        f = ((FrameLayout) findViewById(R.id.splits));
        f.addView(getLayoutInflater().inflate(R.layout.cell_normal, container,false));
        ((TextView) f.findViewById(R.id.label)).setText(PC.getText(ctx, PC.CELLS+PC.CELL_TYPE_SPLITS));
        d = (DataView) new DataView(ctx); d.setId(R.id.dataView); d.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
        ((LinearLayout) f.findViewById(R.id.dataContainer)).addView(d);
        d.setText(String.valueOf(splits));
        
    	((FrameLayout) findViewById(R.id.map)).addView(getLayoutInflater().inflate(R.layout.cell_map_preview, container,false));
    	((FrameLayout) findViewById(R.id.graph)).addView(getLayoutInflater().inflate(R.layout.cell_chart_preview, container,false));
    	
    	
        ((FrameLayout)findViewById(R.id.graph)).setOnClickListener(new OnClickListener(){ @Override public void onClick(View v) {
    		showGraph();
		}});
        ((FrameLayout)findViewById(R.id.map)).setOnClickListener(new OnClickListener(){ @Override public void onClick(View v) {
    		showMap();
		}});
        ((FrameLayout)findViewById(R.id.splits)).setOnClickListener(new OnClickListener(){ @Override public void onClick(View v) {
    		showSplits();
		}});
    
        
        ((FrameLayout) findViewById(R.id.big)).addView(getLayoutInflater().inflate(R.layout.cell_map,      null,false));
        
        View chart = getLayoutInflater().inflate(R.layout.cell_empty,    null,false);
        chart.setId(R.id.frag);
        ((FrameLayout) findViewById(R.id.big)).addView(chart);
        
        findViewById(R.id.big).findViewById(R.id.frag).setVisibility(View.GONE);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
	    mapView.postInvalidate();
	    mapView.setSatellite(prefs.getInt(prefs.getColumnIndex(PC.PREF_MAP_TYPE))==1);
	    MapController myMapController = mapView.getController();
		
		
        Cursor p = db.getMinMaxLatLon(runid);
        p.moveToFirst();
        double minlon = p.getDouble(p.getColumnIndex("minlon"));
		double minlat = p.getDouble(p.getColumnIndex("minlat"));
        double maxlon = p.getDouble(p.getColumnIndex("maxlon"));
		double maxlat = p.getDouble(p.getColumnIndex("maxlat"));
        p.close();
	    MyOverlay myoverlay = new MyOverlay(ctx,runid);
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add(myoverlay);
		
		GeoPoint myGeoPoint = new GeoPoint(
				(int) ((minlat+maxlat) / 2 * 1000000),
				(int) ((minlon+maxlon) / 2 * 1000000));
		myMapController.setCenter(myGeoPoint);
        
		myMapController.zoomToSpan((int)((maxlat-minlat)*1000000*1.2), (int)((maxlon-minlon)*1000000*1.2));
		

		
		switch(showing){
		case SHOWING_GRAPH:
			showing=-1;
			showGraph();
			break;
		case SHOWING_SPLITS:
			showing=-1;
			showSplits();
			break;
		case SHOWING_MAPS:
			showing=-1;
			showMap();
		}
		prefs.close();
		db.close();
	}
	private void showGraph(){
		if(showing == SHOWING_GRAPH)
			return;
		showing = SHOWING_GRAPH;

    	
		Bundle b = new Bundle();
		b.putInt("runid", runid);
		
		GraphViewer newFragment = new GraphViewer();
		newFragment.setArguments(b);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frag, newFragment).commit();
        

		findViewById(R.id.frag).setVisibility(View.VISIBLE);
    	findViewById(R.id.mapview).setVisibility(View.GONE);
		
		findViewById(R.id.splits).setVisibility(View.VISIBLE);
    	findViewById(R.id.map).setVisibility(View.VISIBLE);
    	findViewById(R.id.graph).setVisibility(View.GONE);
    	
	}
	private void showSplits(){
		if(showing == SHOWING_SPLITS)
			return;
		showing = SHOWING_SPLITS;
    	
		Bundle b = new Bundle();
		b.putInt("runid", runid);
		
		Splits newFragment = new Splits();
		newFragment.setArguments(b);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frag, newFragment).commit();
        
		findViewById(R.id.frag).setVisibility(View.VISIBLE);
    	findViewById(R.id.mapview).setVisibility(View.GONE);
		
		findViewById(R.id.splits).setVisibility(View.GONE);
    	findViewById(R.id.map).setVisibility(View.VISIBLE);
    	findViewById(R.id.graph).setVisibility(View.VISIBLE);
	}
	private void showMap(){
		if(showing == SHOWING_MAPS)
			return;
		showing = SHOWING_MAPS;

		findViewById(R.id.frag).setVisibility(View.GONE);
    	findViewById(R.id.mapview).setVisibility(View.VISIBLE);
		
		findViewById(R.id.splits).setVisibility(View.VISIBLE);
    	findViewById(R.id.map).setVisibility(View.GONE);
    	findViewById(R.id.graph).setVisibility(View.VISIBLE);
		
	}
	
	class MyCustomView extends LinearLayout{
		public MyCustomView(Context context) {
			super(context);
		}
		@Override
		protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
			super.onSizeChanged(xNew, yNew, xOld, yOld);
			
			if(xNew>yNew)
				fill(true);
			else
				fill(false);
			
		}
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
		}
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
