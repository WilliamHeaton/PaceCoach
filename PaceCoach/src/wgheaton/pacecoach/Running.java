package wgheaton.pacecoach;

import java.util.ArrayList;
import wgheaton.pacecoach.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.google.android.maps.MapActivity;

public class Running extends MapActivity {
	int width;
	int numpages;
	int onpage;
	int mode;
	int profile;
	Activity ctx;
	GestureDetector mGestureDetector;
	boolean lockscreens;
	int delay = 1000; 
	int period = 10000; 
	int runid; 
	private LocationListener myLocationListener;
	private LocationManager myLocationManager;
	ArrayList<Page> pageList;
	@Override
	protected void onResume() {
		super.onResume();

		myLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0,0, myLocationListener);

		showRightButton(mode);
		sendSignal(PC.SIGNAL_WAKEUP);
		signalPositionLogger(PC.SIGNAL_WAKEUP);
		if(mode != PC.SIGNAL_SEARCHING && mode!= PC.SIGNAL_STOP )
			sendLocation(myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
	}
	@Override
	protected void onPause() {
		super.onPause();
		myLocationManager.removeUpdates(myLocationListener);
		signalPositionLogger(PC.SIGNAL_SLEEP);
		sendSignal(PC.SIGNAL_SLEEP);
	}

    @Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(PC.PREF_PROFILE,profile);
		outState.putInt("onpage",onpage);
		outState.putInt("runid",runid);
		outState.putInt(PC.MODE, mode);
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onBackPressed(){

		if( mode==PC.SIGNAL_SEARCHING || mode==PC.SIGNAL_STOP ){
			Intent i = new Intent(ctx, History.class);
			i.putExtra(PC.PREF_PROFILE, profile);
			startActivity(i);
			finish();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(isFinishing()){
			if( mode==PC.SIGNAL_SEARCHING || mode==PC.SIGNAL_STOP ){
				Intent service = new Intent(this, PositionLogger.class);
				this.stopService(service);
			}
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ctx = this;
		width = 400;
		numpages = 0;
		pageList = new ArrayList<Page>();
		myLocationListener = new MyLocationListener();
		myLocationManager = (LocationManager)getSystemService( Context.LOCATION_SERVICE);
		

		MyCustomView mcv = (MyCustomView) new MyCustomView(ctx);
		mcv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mcv.addView(getLayoutInflater().inflate(R.layout.running_container, mcv,false));
		setContentView(mcv);
		
    	
        if(savedInstanceState!=null && savedInstanceState.getInt(PC.PREF_PROFILE)!=0){
        	profile = savedInstanceState.getInt(PC.PREF_PROFILE);
        	onpage = savedInstanceState.getInt("onpage");
        	mode = savedInstanceState.getInt(PC.MODE);
        	runid = savedInstanceState.getInt("runid");

    		Intent service = new Intent(ctx, PositionLogger.class);
    		service.putExtra(PC.PREF_PROFILE, profile);
    	    service.putExtra(PositionLogger.EXTRA_MESSENGER, new Messenger(fromPositionLogger));
    		startService(service);
    		setupPage();
    	    
        }else if(getIntent().getExtras()!= null && getIntent().getExtras().getInt(PC.PREF_PROFILE)!=0){
			profile = getIntent().getExtras().getInt(PC.PREF_PROFILE);
			onpage = PC.getPrefInt(ctx,profile,PC.PREF_PAGE_DEFAULT)-1;
			mode   = PC.SIGNAL_SEARCHING;
	    	runid = 0;

			Intent service = new Intent(ctx, PositionLogger.class);
			service.putExtra(PC.PREF_PROFILE, profile);
		    service.putExtra(PositionLogger.EXTRA_MESSENGER, new Messenger(fromPositionLogger));
			startService(service);
			setupPage();
		    
        }else{
        	
    		Intent service = new Intent(ctx, PositionLogger.class);
    		service.putExtra(PC.SIGNAL, PC.SIGNAL_QUERY);
    	    service.putExtra(PositionLogger.EXTRA_MESSENGER, new Messenger(fromPositionLogger));
    		startService(service);
    	    
        }

		
		
	}
	public void setupPage(){

		View v;
		int pageid;
		int template;
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
        DbAdapter db = new DbAdapter(this.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
		
		lockscreens = prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_LOCK))==1;

		if(prefs.getInt(prefs.getColumnIndex(PC.PREF_MANUAL_SPLITS))!=1)
			((Button)findViewById(R.id.split		)).setVisibility(View.GONE);
		
		switch(prefs.getInt(prefs.getColumnIndex(PC.PREF_ROTATE))){
		case PC.ROTATE_AUTO:
			break;
		case PC.ROTATE_LANDSCAPE:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case PC.ROTATE_PORTRAIT:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
		prefs.close();

		
		// Setup Pages

		LinearLayout pageholder = (LinearLayout) findViewById(R.id.pages);
		pageholder.setDescendantFocusability(LinearLayout.FOCUS_BLOCK_DESCENDANTS);
		
		Cursor pages = db.getPages(profile);
		while (pages.moveToNext()) {
			numpages++;
			pageid = pages.getInt(pages.getColumnIndex(PC.DB_PAGE));
			template = PC.page_link(pages.getInt(pages.getColumnIndex(PC.DB_TEMPLATE)));
			v = getLayoutInflater().inflate(template, pageholder,false);
			p = v.getLayoutParams();
			p.width = width;
			v.setLayoutParams(p);
			pageholder.addView(v);
			pageList.add((Page) new Page(ctx,profile,pages.getInt(pages.getColumnIndex(PC.DB_TEMPLATE)),pageid,v,false,mode,runid));
		}
		pages.close();
		db.close();

		// Page swiping functionality
		mGestureDetector = new GestureDetector(this,new MyGestureDetector());
		HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);
		scroller.setOnTouchListener(touchListener);
		scroller.setDescendantFocusability(LinearLayout.FOCUS_BLOCK_DESCENDANTS);


		((Button)findViewById(R.id.warmup 			)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.workout 			)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.cooldown			)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.pausewarmup		)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.pauseworkout		)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.pausecooldown	)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.resumewarmup		)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.resumeworkout	)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.resumecooldown	)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.stop 			)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.searching		)).setOnClickListener(buttonListner);
		((Button)findViewById(R.id.split			)).setOnClickListener(buttonListner);

		
		
	}
	
	OnClickListener buttonListner = new OnClickListener() {
		public void onClick(View view) {
			int m = mode;
			switch (view.getId()) {
			case R.id.warmup:
				if(runid==0) getrunid();
				m = PC.SIGNAL_WARMUP;
				break;
			case R.id.workout:
				if(runid==0) getrunid();
				m = PC.SIGNAL_WORKOUT;
				break;
			case R.id.cooldown:
				m = PC.SIGNAL_COOLDOWN;
				break;
			case R.id.pausewarmup:
				m = PC.SIGNAL_PAUSE_WARMUP;
				break;
			case R.id.pauseworkout:
				m = PC.SIGNAL_PAUSE_WORKOUT;
				break;
			case R.id.pausecooldown:
				m =  PC.SIGNAL_PAUSE_COOLDOWN;
				break;
			case R.id.resumewarmup:
				m = PC.SIGNAL_RESUME_WARMUP;
				break;
			case R.id.resumeworkout:
				m = PC.SIGNAL_RESUME_WORKOUT;
				break;
			case R.id.resumecooldown:
				m = PC.SIGNAL_RESUME_COOLDOWN;
				break;
			case R.id.stop:
				m = PC.SIGNAL_STOP;
				break;
			case R.id.searching:
				startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
				return;
			case R.id.split:
				signalPositionLogger(PC.SIGNAL_SPLIT);
				return;
			}
			showRightButton(m);
			signalPositionLogger(m);
		}
	};
	void signalPositionLogger(int s){

		Intent i = new Intent(ctx, PositionLogger.class);
		i.putExtra(PC.SIGNAL,s);
		i.putExtra("runid",runid);
		startService(i);
		
	}
	
	private Handler fromPositionLogger = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.arg1){
			case PC.SIGNAL_QUERY:
				int[] m = (int[]) msg.obj;
				profile = m[0];
				runid = m[1];
				mode = m[2];
				setupPage();
				showRightButton(mode);
				break;
			case PC.SIGNAL_SPLIT:
			case PC.SIGNAL_UPDATE:
				sendSignal(msg.arg1);
				break;

			case PC.SIGNAL_WARMUP:
			case PC.SIGNAL_WORKOUT:
			case PC.SIGNAL_COOLDOWN:
			case PC.SIGNAL_RESUME_WARMUP:
			case PC.SIGNAL_RESUME_WORKOUT:
			case PC.SIGNAL_RESUME_COOLDOWN:
			case PC.SIGNAL_PAUSE_WARMUP:
			case PC.SIGNAL_PAUSE_WORKOUT:
			case PC.SIGNAL_PAUSE_COOLDOWN:
				mode = msg.arg1;
				showRightButton(msg.arg1);
				sendSignal(msg.arg1);
				break;
			case PC.SIGNAL_STOP:

				Intent i = new Intent(ctx, ReviewRun.class);
				i.putExtra("runid", runid);
				i.putExtra(PC.PREF_PROFILE, profile);
				ctx.startActivity(i);
				mode=msg.arg1;
				finish();
			}
		}
	};

	private void sendSignal(int signal){
		for ( Page page : pageList){  
			page.signalCells(signal);
		}
	}
	private void sendLocation(Location l){
		for ( Page page : pageList){  
			page.newLocation(l);
		}    	
	}

	void showRightButton(int m){
		
		
		DbAdapter db = new DbAdapter(this.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
		hideAllButtons();
		switch (m) {
		case PC.SIGNAL_SEARCHING:
			((Button)findViewById(R.id.searching)		).setVisibility(View.VISIBLE);
			break;
		case PC.SIGNAL_RESUME_WARMUP:
		case PC.SIGNAL_WARMUP:
			((Button)findViewById(R.id.workout) 		).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.pausewarmup)		).setVisibility(View.VISIBLE);
			break;
		case PC.SIGNAL_RESUME_WORKOUT:
		case PC.SIGNAL_WORKOUT:
			if(prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN))==1)
				((Button)findViewById(R.id.cooldown)	).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.pauseworkout)	).setVisibility(View.VISIBLE);
			break;
		case PC.SIGNAL_RESUME_COOLDOWN:
		case PC.SIGNAL_COOLDOWN:
			((Button)findViewById(R.id.pausecooldown)	).setVisibility(View.VISIBLE);
			break;
		case PC.SIGNAL_PAUSE_WARMUP:
			((Button)findViewById(R.id.resumewarmup)	).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.stop)  			).setVisibility(View.VISIBLE);
			break;
		case PC.SIGNAL_PAUSE_WORKOUT:
			((Button)findViewById(R.id.resumeworkout)	).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.stop)  			).setVisibility(View.VISIBLE);
			break;
		case PC.SIGNAL_PAUSE_COOLDOWN:
			((Button)findViewById(R.id.resumecooldown)	).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.stop)  			).setVisibility(View.VISIBLE);
			break;
		case PC.SIGNAL_STOP:
			if(prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP))==1){
				((Button)findViewById(R.id.warmup)		).setVisibility(View.VISIBLE);
			}else{
				((Button)findViewById(R.id.workout)		).setVisibility(View.VISIBLE);
			}
			break;
		}
		prefs.close();
		db.close();
	};
	
	private void getrunid(){
		DbAdapter db = new DbAdapter(ctx);
		db.open();
		runid = db.getNewRunID();
		db.close();
		for ( Page page : pageList){  
			page.setRunid(runid);
		}
	}
	void hideAllButtons(){
		((Button)findViewById(R.id.searching)		).setVisibility(View.GONE);
		((Button)findViewById(R.id.warmup) 			).setVisibility(View.GONE);
		((Button)findViewById(R.id.workout) 		).setVisibility(View.GONE);
		((Button)findViewById(R.id.cooldown)		).setVisibility(View.GONE);
		((Button)findViewById(R.id.resumewarmup)	).setVisibility(View.GONE);
		((Button)findViewById(R.id.resumeworkout)	).setVisibility(View.GONE);
		((Button)findViewById(R.id.resumecooldown)	).setVisibility(View.GONE);
		((Button)findViewById(R.id.stop)  			).setVisibility(View.GONE);
		((Button)findViewById(R.id.pausewarmup)		).setVisibility(View.GONE);
		((Button)findViewById(R.id.pauseworkout)	).setVisibility(View.GONE);
		((Button)findViewById(R.id.pausecooldown)	).setVisibility(View.GONE);
		
		
	}

	OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mGestureDetector.onTouchEvent(event)) {
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL ){
				HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);
				onpage = (int) Math.round((float)scroller.getScrollX()/width);
				scroller.smoothScrollTo(onpage*width,0);
				return true;
			}else{
				return false;
			}
		}
	};
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);

			if(e1 == null || e2==null )
				return false;

			//right to left
			if( e1.getRawX() > e2.getRawX() ){
				onpage = (int) Math.ceil((float)scroller.getScrollX()/width);
				scroller.smoothScrollTo(onpage*width, 0);
				return true;
			}
			//left to right
			else if ( e1.getRawX() < e2.getRawX() ){

				onpage = (int) Math.floor((float)scroller.getScrollX()/width);
				scroller.smoothScrollTo(onpage*width, 0);
				return true;
			}
			return false;
		}
	}
	private class MyLocationListener implements LocationListener{
		@Override
		public void onLocationChanged(Location argLocation) {

			if(mode==PC.SIGNAL_SEARCHING){
				showRightButton(PC.SIGNAL_STOP);
				Intent i = new Intent(ctx, PositionLogger.class);
				startService(i);
			}
			sendLocation(argLocation);
		}
		@Override public void onProviderDisabled(String provider) {}
		@Override public void onProviderEnabled(String provider) {}
		@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
	class MyCustomView extends LinearLayout{
		public MyCustomView(Context context) {
			super(context);
		}
		@Override
		protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
			super.onSizeChanged(xNew, yNew, xOld, yOld);
			boolean rotate = false;			
			int por = PC.getPrefInt(ctx, profile, PC.PREF_PORTRAIT);
			
			if(xNew>yNew){
				if(por == PC.LANDSCAPE_DOUBLE)
					rotate = true;
				if(por == PC.LANDSCAPE_DOUBLE)
					xNew = xNew/2;
			}
			width = xNew;
			LayoutParams p;
			for ( Page page : pageList){
				p = (LayoutParams) page.view.getLayoutParams();
				p.width = xNew;
				//p.height = LayoutParams.FILL_PARENT;
				page.view.setLayoutParams(p);
				if(rotate)
					page.rotate();
			}

			HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);

			scroller.postDelayed(new Runnable() {
				@Override
				public void run() {
					HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);
					scroller.scrollTo(onpage*width, 0);
				} 
			}, 100);

		}
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
		}
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
	    if (mGestureDetector != null) {
			super.dispatchTouchEvent(ev);
	    	return mGestureDetector.onTouchEvent(ev);
	    }
	    return super.dispatchTouchEvent(ev);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
