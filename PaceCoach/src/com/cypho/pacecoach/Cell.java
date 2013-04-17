package com.cypho.pacecoach;

import java.util.List;
import wgheaton.pacecoach.R;

import android.os.Handler.Callback;
import android.os.Bundle;
import android.os.Message;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.cypho.pacecoach.PC.Graph;
import com.cypho.pacecoach.PC.MyOverlay;
import com.cypho.pacecoach.PC.DataView;
import com.cypho.pacecoach.PC.TimeView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class Cell {
	public int template;
	public int cellid;
	public int datatype;
	public int units;
	public ViewGroup view;
	private Activity ctx;
	private int page;
	private MapController myMapController;
	private MyLocationOverlay myLocationOverlay;
	private boolean configure_mode;
	private boolean lockscreens;
	private int mode;
	private int runid;
	private String autosplitD;
	private int autosplitU;
	private float splitDistance;
	private boolean autosplit;
	private boolean manusplit;
	private int mapmode;
	private boolean maptype;
	private int fragid;
	private int profile;

	private OnLongClickListener longClick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			PC.settings_select_cell(ctx,new Callback(){
				@Override
				public boolean handleMessage(Message msg) {

					int d = msg.arg1;
					int u = msg.arg2;
					changeCellType(d,u);
					return true;
				}});

			return false;
		}
	};
	public Cell(Activity c,int pr, int p, int t, int cid, int d, int u, ViewGroup v, boolean cm,int m,int r) {
		ctx = c;
		template = t;
		page = p;
		cellid = cid;
		units = u;
		datatype = d;
		view = v;
		configure_mode = cm;
		mode = m;
		runid = r;
		profile = pr;

		DbAdapter db = new DbAdapter(ctx.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
		lockscreens = 	prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_LOCK))==1;
		autosplit  = 	prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT))==1;
		manusplit  = 	prefs.getInt(prefs.getColumnIndex(PC.PREF_MANUAL_SPLITS))==1;
		autosplitU = 	prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT_DISTANCE_UNIT));
		splitDistance = prefs.getFloat(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT_DISTANCE_VALU));

		autosplitD = 	String.valueOf( prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT_DISTANCE_INT1))) + "." + 
				String.valueOf( prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT_DISTANCE_INT2))); 

		mapmode =	 	prefs.getInt(prefs.getColumnIndex(PC.PREF_MAP_MODE));
		maptype =	 	prefs.getInt(prefs.getColumnIndex(PC.PREF_MAP_TYPE))==1;

		prefs.close();
		db.close();

		inflateCell();		
	}
	private void changeCellType(int d, int u){
		DbAdapter cellsDB = new DbAdapter(ctx);
		cellsDB.open();
		cellsDB.updateCell(profile,page,cellid,d,u);
		cellsDB.close();

		if(!configure_mode && d == PC.CELL_TYPE_MAP){
			((Running) ctx).recreate();
			return;
		}else{
			datatype = d;
			units = u;
			if(configure_mode){
				((SettingsConfigPages) ctx).bubbleUp(cellid);	
			}
			inflateCell();
		}

	}
	private void inflateBlank(){
	}
	private void inflateCell(){
		int d = datatype;

		view.removeAllViews();

		if		( d == PC.CELL_TYPE_LOCATION		) 	inflateLocation();
		else if ( d == PC.CELL_TYPE_MAP				) 	inflateMap();
		else if ( d == PC.CELL_TYPE_TIME			) 	inflateTime();
		else if ( d == PC.CELL_TYPE_SPLIT_TIME		)	inflateTime();
		else if ( d == PC.CELL_TYPE_BLANK			) 	inflateBlank();
		else if ( d == PC.CELL_TYPE_SPLITS			) 	inflateSplits();
		else if ( d == PC.CELL_TYPE_GRAPH_ACCURACY	) 	inflateGraph();
		else if ( d == PC.CELL_TYPE_GRAPH_ALTITUDE	) 	inflateGraph();
		else if ( d == PC.CELL_TYPE_GRAPH_DISTANCE	) 	inflateGraph();
		else if ( d == PC.CELL_TYPE_GRAPH_SPEEDGPS	) 	inflateGraph();
		else if ( d == PC.CELL_TYPE_GRAPH_SPEEDCAL	) 	inflateGraph();
		else if ( d == PC.CELL_TYPE_GRAPH_SPEEDAVG	) 	inflateGraph();
		else											inflateNormal();


		if( d == PC.CELL_TYPE_SPLIT_AUTOSPLIT	) 	updateAutoSplitDist();

		if ( ( d != PC.CELL_TYPE_MAP && !lockscreens ) || configure_mode )
			view.setOnLongClickListener(longClick);

		recieveSignal(mode);

	}
	private void inflateGraph() {
		if(configure_mode){
			inflateNormal();
			return;
		}

		//LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		//View v = inflater.inflate(R.layout.cell_empty, view,false);

		fragid = PC.uniqueId(ctx);
		int t = 0;

		if      ( datatype == PC.CELL_TYPE_GRAPH_ACCURACY	) 	t = PC.CELL_TYPE_ACCURACY;
		else if ( datatype == PC.CELL_TYPE_GRAPH_ALTITUDE	) 	t = PC.CELL_TYPE_ALTITUDE;
		else if ( datatype == PC.CELL_TYPE_GRAPH_DISTANCE	) 	t = PC.CELL_TYPE_DISTANCE;
		else if ( datatype == PC.CELL_TYPE_GRAPH_SPEEDGPS	) 	t = PC.CELL_TYPE_SPEEDGPS;
		else if ( datatype == PC.CELL_TYPE_GRAPH_SPEEDCAL	) 	t = PC.CELL_TYPE_SPEEDCAL;
		else if ( datatype == PC.CELL_TYPE_GRAPH_SPEEDAVG	) 	t = PC.CELL_TYPE_SPEEDAVG;

		Graph g = new Graph(ctx,runid,t,units);
		g.setId(fragid);
		view.addView(g);
	}
	private void inflateLocation(){
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View v = inflater.inflate(R.layout.cell_normal, view,false);
		view.addView(v);
		((TextView) v.findViewById(R.id.label)).setText(PC.getText(ctx,PC.CELLS+datatype));
		LinearLayout ct = ((LinearLayout) v.findViewById(R.id.dataContainer));

		DataView d1 = (DataView) new DataView(ctx);
		DataView d2 = (DataView) new DataView(ctx);
		d1.setId(R.id.dataView1);
		d2.setId(R.id.dataView2);
		d1.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
		d2.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));

		d1.setTypeface(Typeface.MONOSPACE);
		d2.setTypeface(Typeface.MONOSPACE);

		d1.setAlign(DataView.BOTTOM);
		d2.setAlign(DataView.TOP);

		d1.setText("N 00°%00'00.000\"");
		d1.setText("W 00°%00'00.000\"");
		ct.addView(d1);
		ct.addView(d2);


	}
	private void inflateMap(){
		if(configure_mode){
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			View v = inflater.inflate(R.layout.cell_map_preview, view,false);
			view.addView(v);
			return;
		}
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View v = inflater.inflate(R.layout.cell_map, view,false);
		view.addView(v);

		MapView mapView = (MapView) v.findViewById(R.id.mapview);
		if(mapmode == PC.MAP_MODE_ZOOM){
			v.findViewById(R.id.zoomcontainer).setVisibility(View.VISIBLE);

			((ImageView) v.findViewById(R.id.zoomin)).setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				myMapController.zoomIn();
			}});
			((ImageView) v.findViewById(R.id.zoomout)).setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				myMapController.zoomOut();
			}});
		}
		mapView.setSatellite(maptype);
		mapView.postInvalidate();

		myMapController = mapView.getController();
		myMapController.setZoom(20);
		initMap();
		updateMap(((LocationManager)ctx.getSystemService( Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER));

	}
	private void inflateNormal(){
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View v = inflater.inflate(R.layout.cell_normal, view,false);
		view.addView(v);
		((TextView) v.findViewById(R.id.label)).setText(PC.getText(ctx,PC.CELLS+datatype));
		((TextView) v.findViewById(R.id.units)).setText(PC.getText(ctx,PC.UNITS+units));

		LinearLayout ct = ((LinearLayout) v.findViewById(R.id.dataContainer));

		DataView d = (DataView) new DataView(ctx);
		d.setId(R.id.dataView);
		d.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
		if(!configure_mode){
			d.setText(PC.formatUnits(0, units));
		}
		ct.addView(d);

	}
	private void inflateSplits() {

		if(configure_mode){
			inflateNormal();
			return;
		}

		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View v = inflater.inflate(R.layout.cell_empty, view,false);

		fragid = PC.uniqueId(ctx);
		v.setId(fragid);
		v.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
		view.addView(v);
		updateSplits();
	}

	private void inflateTime(){
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View v = inflater.inflate(R.layout.cell_normal, view,false);
		view.addView(v);
		((TextView) v.findViewById(R.id.label)).setText(PC.getText(ctx,PC.CELLS+datatype));

		LinearLayout ct = ((LinearLayout) v.findViewById(R.id.dataContainer));

		TimeView d = (TimeView) new TimeView(ctx);
		d.setId(R.id.dataView);
		d.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
		ct.addView(d);
	}
	public void initMap(){


		MapView mapView = (MapView) view.findViewById(R.id.mapview);

		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.clear();
		System.out.println("initmap");
		if(runid!=0){
			System.out.println("add location overlay");
			MyOverlay myoverlay = new MyOverlay(ctx,runid);
			mapOverlays.add(myoverlay);
		}
		if(myLocationOverlay == null)
			myLocationOverlay = new MyLocationOverlay(ctx, mapView);
		mapOverlays.add(myLocationOverlay);
		mapView.invalidate();

	}

	public void newLocation(Location argLocation){
		if(     datatype == PC.CELL_TYPE_MAP	 )		updateMap(argLocation);
		else if(datatype == PC.CELL_TYPE_ALTITUDE)		updateAltitude(argLocation.getAltitude());
		else if(datatype == PC.CELL_TYPE_LOCATION)		updateLocation(argLocation.getLatitude(),argLocation.getLongitude());
		else if(datatype == PC.CELL_TYPE_ACCURACY)		updateAccuracy(argLocation.getAccuracy());
		else if(datatype == PC.CELL_TYPE_SPEEDGPS)		updateSpeedGPS(argLocation.getSpeed());
	}
	public void recieveSignal(int m) {
		switch(m){
		case PC.SIGNAL_WARMUP:
		case PC.SIGNAL_WORKOUT:
		case PC.SIGNAL_COOLDOWN:
		case PC.SIGNAL_RESUME_WARMUP:
		case PC.SIGNAL_RESUME_WORKOUT:
		case PC.SIGNAL_RESUME_COOLDOWN:
			if( datatype == PC.CELL_TYPE_TIME) 			updateTime(1);
			if( datatype == PC.CELL_TYPE_SPLIT_TIME)	updateTimeSinceSplit(1);
			mode = m;
			break;
		case PC.SIGNAL_STOP:
			runid = 0;
		case PC.SIGNAL_PAUSE_WARMUP:
		case PC.SIGNAL_PAUSE_WORKOUT:
		case PC.SIGNAL_PAUSE_COOLDOWN:
			if( datatype == PC.CELL_TYPE_TIME) 			updateTime(2);
			if( datatype == PC.CELL_TYPE_SPLIT_TIME)	updateTimeSinceSplit(2);
			mode = m;
			break;
		case PC.SIGNAL_SPLIT:
			if( datatype == PC.CELL_TYPE_SPLIT_NO)		updateSplitNo();
			if( datatype == PC.CELL_TYPE_SPLIT_TIME)	updateTimeSinceSplit(1);
			if( datatype == PC.CELL_TYPE_SPLITS)		updateSplits();
			break;
		case PC.SIGNAL_WAKEUP:
			if( datatype == PC.CELL_TYPE_MAP)	 		startMap();
			break;
		case PC.SIGNAL_SLEEP:
			if( datatype == PC.CELL_TYPE_MAP) 			stopMap();
			break;
		case PC.SIGNAL_REFRESH:
			refreshDetails();
			break;
		}

		switch(m){
		case PC.SIGNAL_WARMUP:
		case PC.SIGNAL_WORKOUT:
		case PC.SIGNAL_COOLDOWN:
		case PC.SIGNAL_PAUSE_WARMUP:
		case PC.SIGNAL_PAUSE_WORKOUT:
		case PC.SIGNAL_PAUSE_COOLDOWN:
		case PC.SIGNAL_RESUME_WARMUP:
		case PC.SIGNAL_RESUME_WORKOUT:
		case PC.SIGNAL_RESUME_COOLDOWN:
		case PC.SIGNAL_UPDATE:
			if( 	 datatype == PC.CELL_TYPE_DISTANCE)		 	updateDistance();
			else if( datatype == PC.CELL_TYPE_SPEEDCAL)		 	updateSpeedCal();
			else if( datatype == PC.CELL_TYPE_SPEEDAVG)			updateSpeedAvg();
			else if( datatype == PC.CELL_TYPE_SPLIT_SPEED)		updateSpeedSinceSplit();
			else if( datatype == PC.CELL_TYPE_SPLIT_DISTANCE)	updateDistanceSinceSplit();
			else if( datatype == PC.CELL_TYPE_SPLIT_DIST_LEFT) 	updateDistanceUntilSplit();
			else if( datatype == PC.CELL_TYPE_SPLIT_NO)			updateSplitNo();
			else if( datatype == PC.CELL_TYPE_SPLITS)			updateSplits();
			else if( datatype == PC.CELL_TYPE_GRAPH_ACCURACY) 	updateGraph();
			else if( datatype == PC.CELL_TYPE_GRAPH_ALTITUDE) 	updateGraph();
			else if( datatype == PC.CELL_TYPE_GRAPH_DISTANCE) 	updateGraph();
			else if( datatype == PC.CELL_TYPE_GRAPH_SPEEDGPS) 	updateGraph();
			else if( datatype == PC.CELL_TYPE_GRAPH_SPEEDCAL) 	updateGraph();
			else if( datatype == PC.CELL_TYPE_GRAPH_SPEEDAVG) 	updateGraph();

			break;
		}
	}
	private void refreshDetails(){
		DbAdapter cellsDB = new DbAdapter(ctx);
		cellsDB.open();
		Cursor p = cellsDB.getCell(profile,page,cellid);
		int t = datatype;
		int u = units;
		if(p.moveToFirst()){
			t = p.getInt(p.getColumnIndex(PC.DB_DATATYPE));
			u = p.getInt(p.getColumnIndex(PC.DB_UNITS));

		}
		p.close();
		cellsDB.close();

		if(t != datatype || u != units){
			datatype = t;
			units = u;
			inflateCell();
		}
	}

	public void setRunid(int r) {
		runid = r;

		if( datatype == PC.CELL_TYPE_SPLIT_NO)		updateSplitNo();
		if( datatype == PC.CELL_TYPE_MAP) 			initMap();
	}


	private void startMap(){
		myLocationOverlay.enableMyLocation();
	}

	private void stopMap(){
		myLocationOverlay.disableMyLocation();
	}
	public void updateAccuracy(Float a){
		((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits((double) a,units));
	}
	public void updateAltitude(Double a){
		((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits((double) a,units));
	}
	private void updateAutoSplitDist() {

		if(!autosplit && !manusplit)
			return;

		((DataView) view.findViewById(R.id.dataView)).setText(autosplitD);
		((TextView) view.findViewById(R.id.units)).setText(PC.getText(ctx, PC.UNITS+autosplitU));
	}
	public void updateDistance(){
		if(runid == 0) return;
		DbAdapter runDB = new DbAdapter(ctx);
		runDB.open();
		Cursor data = runDB.getLastDatapoints(runid,1);
		if(data.moveToFirst()){
			double d = data.getDouble(data.getColumnIndex(PC.DB_DIST_ELAP));
			((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits((double) d,units));
		}
		data.close();
		runDB.close();
	}

	private void updateDistanceSinceSplit(){
		if(runid == 0) return;
		if(!autosplit && !manusplit) return;

		double splitDist = 0;
		double distatnce = 0;

		DbAdapter db = new DbAdapter(ctx);
		db.open();

		Cursor spli	  = db.getSplits(runid);
		Cursor data   = db.getLastDatapoints(runid,1);

		if(spli.moveToLast())  splitDist = spli.getDouble(spli.getColumnIndex(PC.DB_DIST_ELAP));
		if(data.moveToFirst()) distatnce = data.getDouble(data.getColumnIndex(PC.DB_DIST_ELAP));

		((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits(distatnce-splitDist,units));

		data.close();
		spli.close();
		db.close();
	}
	private void updateDistanceUntilSplit(){
		if(runid == 0) return;
		if(!autosplit ) return;

		double splitDist = 0;
		double distatnce = 0;

		DbAdapter db = new DbAdapter(ctx);
		db.open();

		Cursor spli	  = db.getSplits(runid);
		Cursor data   = db.getLastDatapoints(runid,1);

		if(spli.moveToLast())  splitDist   = spli.getDouble(spli.getColumnIndex(PC.DB_DIST_ELAP));
		if(data.moveToFirst()) distatnce   = data.getDouble(data.getColumnIndex(PC.DB_DIST_ELAP));

		((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits(splitDist+splitDistance - distatnce,units));

		data.close();
		spli.close();
		db.close();
	}
	private void updateGraph(){
		((Graph)view.findViewById(fragid)).setRunId(runid);
	}
	public void updateLocation(Double lat,Double lon){
		((DataView) view.findViewById(R.id.dataView1)).setText(PC.formatGPS(Location.convert(lat,Location.FORMAT_SECONDS),PC.LATITUDE));
		((DataView) view.findViewById(R.id.dataView2)).setText(PC.formatGPS(Location.convert(lon,Location.FORMAT_SECONDS),PC.LONGITUDE));
	}
	public void updateMap(Location argLocation) {
		if (argLocation != null) {

			if(mapmode == PC.MAP_MODE_AUTO && runid!=0){


				DbAdapter db = new DbAdapter(ctx);
				db.open();
				Cursor p = db.getMinMaxLatLon(runid);
				p.moveToFirst();
				double minlon = p.getDouble(p.getColumnIndex("minlon"));
				double minlat = p.getDouble(p.getColumnIndex("minlat"));
				double maxlon = p.getDouble(p.getColumnIndex("maxlon"));
				double maxlat = p.getDouble(p.getColumnIndex("maxlat"));
				db.close();

				GeoPoint myGeoPoint = new GeoPoint(
						(int) ((minlat+maxlat) / 2 * 1000000),
						(int) ((minlon+maxlon) / 2 * 1000000));
				myMapController.setCenter(myGeoPoint);

				myMapController.zoomToSpan((int)((maxlat-minlat)*1000000*1.2), (int)((maxlon-minlon)*1000000*1.2));
			}else {

				GeoPoint myGeoPoint = new GeoPoint(
						(int) (argLocation.getLatitude() * 1000000),
						(int) (argLocation.getLongitude() * 1000000));

				myMapController.animateTo(myGeoPoint);

			}


		}
	}

	public void updateSpeedAvg(){
		if(runid == 0) return;
		((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits(PC.getSpeedAvg(ctx,runid),units));
	}
	public void updateSpeedCal(){
		if(runid == 0) return;

		((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits(PC.getSpeedCal(ctx,runid),units));
	}
	public void updateSpeedGPS(Float a){
		((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits(a,units));
	}
	private void updateSpeedSinceSplit(){
		if(runid == 0) return;
		if(!autosplit && !manusplit) return;

		((DataView) view.findViewById(R.id.dataView)).setText(PC.formatUnits(PC.getSpeedSpl(ctx,runid),units));
	}
	private void updateSplitNo() {
		if(runid == 0) return;
		((DataView) view.findViewById(R.id.dataView)).setText(String.valueOf(PC.getSplitNum(ctx, runid)));
	}
	private void updateSplits(){
		Bundle b = new Bundle();
		b.putInt("runid", runid);
		b.putBoolean("invert", true);
		Splits newFragment = new Splits();
		newFragment.setArguments(b);
		FragmentTransaction ft = ctx.getFragmentManager().beginTransaction();
		ft.replace(fragid, newFragment).commit();
	}
	public void updateTime(int m){

		if(runid == 0) return;

		DbAdapter runDB = new DbAdapter(ctx);
		runDB.open();
		Cursor data = runDB.getLastDatapoints(runid,1);
		if(data.moveToFirst()){
			long timestamp = data.getLong(data.getColumnIndex(PC.DB_TIMESTAMP));
			long elapsedtime = data.getLong(data.getColumnIndex(PC.DB_TIME_ELAP));

			TimeView mChronometer = (TimeView) view.findViewById(R.id.dataView);

			mChronometer.setBase(SystemClock.elapsedRealtime() - (SystemClock.elapsedRealtime()-timestamp)- elapsedtime);

			switch (m){
			case 1:
				mChronometer.start();
				break;
			case 2:
				mChronometer.stop();
			}
		}
		data.close();
		runDB.close();
	}
	public void updateTimeSinceSplit(int m){
		
		System.out.println(m);
		
		if(runid == 0) return;

		DbAdapter runDB = new DbAdapter(ctx);
		runDB.open();
		
		long splitime = 0;

		Cursor data2 = runDB.getSplits(runid);
		if(data2.moveToLast()){
			splitime = data2.getLong(data2.getColumnIndex(PC.DB_TIME_ELAP));
		}
		data2.close();
		
		Cursor data = runDB.getLastDatapoints(runid,1);
		if(data.moveToFirst()){
			long timestamp = data.getLong(data.getColumnIndex(PC.DB_TIMESTAMP));
			long elapsedtime = data.getLong(data.getColumnIndex(PC.DB_TIME_ELAP));

			TimeView mChronometer = (TimeView) view.findViewById(R.id.dataView);

			mChronometer.setBase(SystemClock.elapsedRealtime() - (SystemClock.elapsedRealtime()-timestamp)- elapsedtime+splitime);

			switch (m){
			case 1:
				mChronometer.start();
				break;
			case 2:
				mChronometer.stop();
			}
		}
		data.close();
		runDB.close();
	}
}