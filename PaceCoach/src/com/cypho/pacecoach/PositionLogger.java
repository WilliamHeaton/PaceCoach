package com.cypho.pacecoach;

import java.util.TimeZone;
import wgheaton.pacecoach.R;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;

public class PositionLogger extends Service implements OnInitListener {
	  public static final String EXTRA_MESSENGER="com.cypho.pacecoach.EXTRA_MESSENGER";

	private LocationListener myLocationListener;
	private LocationManager myLocationManager;
	private int runid = 0;
	private int mode = PC.SIGNAL_SEARCHING;
	private int tooSlowUnit;
	private int tooFastUnit;
	private int tooSlowData;
	private int tooFastData;
	private int splitNo;
	private int profile;
	private int warmup_unit;
	private int workout_unit;
	private int cooldown_unit;
	private int warmup_end;
	private int workout_end;
	private int cooldown_end;
	private long startime;
	private long prevtime;
	private long oldtime;
	private long pausedAt;
	private long timeOfLastSplit;
	private long speedcaltime;
	private long previd;
	private long speedfreq;
	private long timeOfLastSpeedAlert;
	private boolean speak;
	private boolean speakStart;
	private boolean speakStop;
	private boolean speakPause;
	private boolean speakResume;
	private boolean speakSplits;
	private boolean speakTooSlow;
	private boolean speakTooFast;
	private boolean autosplit;
	private boolean manualsplit;
	private boolean sleaping;
	private boolean tracking;
	private boolean warmup_incl;
	private boolean cooldown_incl;
	private boolean warmup_splits;
	private boolean cooldown_splits;
	private double warmup_valu;
	private double workout_valu;
	private double cooldown_valu;
	private double distance;
	private double olddistance;
	private double splitDistance;
	private double distanceSinceSplit;
	private double tooFast;
	private double tooSlow;

	private TextToSpeech tts;
	private Location prevlocation;
	private Location oldlocation;
	private Location curlocation;
	Messenger messageToRunning;
	private void sendToRunning(int m){
		if(sleaping)return;
		
		Message msg = new Message();
		msg.arg1 = m;
		try { messageToRunning.send(msg); }catch (android.os.RemoteException e1) {System.out.println("It did not work"); }		
	}
	private void queryRespond(){

		Message msg = new Message();
		msg.arg1 = PC.SIGNAL_QUERY;
		msg.obj = new int[] {profile,runid,mode};
		try { messageToRunning.send(msg); }catch (android.os.RemoteException e1) {System.out.println("It did not work"); }		
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		if(tts!=null)
			tts.shutdown();
		speak = false;
		myLocationManager.removeUpdates(myLocationListener);
		if(tracking)
			stopTracking();
		stopForeground(true);
		super.onDestroy();

	}

	@Override
	public void onCreate() {
		super.onCreate();
		tracking = false;
		runid = 0;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Bundle extras = intent.getExtras();
		
		if(extras!=null && extras.get(EXTRA_MESSENGER) != null)
			messageToRunning = (Messenger) extras.get(EXTRA_MESSENGER);
		
		if(runid == 0 && extras != null && extras.getInt("runid")!=0)
			runid = extras.getInt("runid");
		
		
		if(profile==0 && extras != null && extras.getInt(PC.PREF_PROFILE)!=0){
			profile = extras.getInt(PC.PREF_PROFILE);
			
			DbAdapter db = new DbAdapter(this.getApplicationContext());
			db.open();
			Cursor prefs = db.getPrefs(profile);
			prefs.moveToFirst();
			
			mode = PC.SIGNAL_SEARCHING;
			notifcation(mode);

			speakStart 		= prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_START))==1;
			speakStop  		= prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_STOP))==1;
			speakPause 		= prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_PAUSE))==1;
			speakResume 	= prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_RESUME))==1;
			speakSplits 	= prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPLITS))==1;
			
			autosplit 		= prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT))==1;
			manualsplit 	= prefs.getInt(prefs.getColumnIndex(PC.PREF_MANUAL_SPLITS))==1;
			splitDistance 	= prefs.getDouble(prefs.getColumnIndex(  PC.PREF_AUTO_SPLIT_DISTANCE_VALU));

			speedcaltime	= prefs.getLong(prefs.getColumnIndex(PC.PREF_SPEEDCAL_TIME))*1000;
			
			warmup_unit 	= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_WARMUP_UNIT));
			warmup_valu		= prefs.getDouble(prefs.getColumnIndex(PC.PREF_WARMUP_VALU));
			warmup_incl		= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_WARMUP_INCL))==1;
			warmup_splits	= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_WARMUP_SPLITS))==1;
			warmup_end		= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_WARMUP_END_ACTION));
			workout_unit 	= prefs.getInt(   prefs.getColumnIndex(PC.PREF_WORKOUT_UNIT));
			workout_valu	= prefs.getDouble(prefs.getColumnIndex(PC.PREF_WORKOUT_VALU));
			workout_end		= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_WORKOUT_END_ACTION));
			cooldown_unit 	= prefs.getInt(   prefs.getColumnIndex(PC.PREF_COOLDOWN_UNIT));
			cooldown_valu	= prefs.getDouble(prefs.getColumnIndex(PC.PREF_COOLDOWN_VALU));
			cooldown_incl	= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_COOLDOWN_INCL))==1;
			cooldown_splits	= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_COOLDOWN_SPLITS))==1;
			cooldown_end	= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_COOLDOWN_END_ACTION));
			speedfreq		= prefs.getInt(	  prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_FREQ))*1000;
			
			
			myLocationListener = new MyLocationListener();
			myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
			speak = false;
			if (prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK))==1)
				tts = new TextToSpeech(getApplicationContext(), this);
			
			prefs.close();
			db.close();
			
		}
		
		int action = (extras != null) ? extras.getInt(PC.SIGNAL) : 0;
		switch (action) {
		case PC.SIGNAL_SLEEP:
			sleaping = true;
			break;
		case PC.SIGNAL_WAKEUP:
			sleaping = false;
			break;
		case PC.SIGNAL_QUERY:
			sleaping = false;
			queryRespond();
			break;
		case PC.SIGNAL_WARMUP:
		case PC.SIGNAL_WORKOUT:
		case PC.SIGNAL_COOLDOWN:
			if(mode==action)break;
			modeChanged(action);
			break;
		case PC.SIGNAL_PAUSE_WARMUP:
		case PC.SIGNAL_PAUSE_WORKOUT:
		case PC.SIGNAL_PAUSE_COOLDOWN:
			if(mode==action)break;
			pauseTracking(action);
			break;
		case PC.SIGNAL_RESUME_WARMUP:
		case PC.SIGNAL_RESUME_WORKOUT:
		case PC.SIGNAL_RESUME_COOLDOWN:
			if(mode==action-6)break;
			resumeTracking(action-6);
			break;
		case PC.SIGNAL_STOP:
			if(mode==action)break;
			stopTracking();
			break;
		case PC.SIGNAL_SPLIT:
			split( SystemClock.elapsedRealtime());
			break;
		}
		
		return START_STICKY;
	}
	private void notifcation(int mode){

		Notification.Builder mBuilder = new Notification.Builder(this)
		.setContentTitle(this.getString(R.string.app_name))
		.setSmallIcon(R.drawable.android_whistle)
		.setContentIntent(
				PendingIntent.getActivity(this, 0, new Intent(this,
						Running.class),
						PendingIntent.FLAG_UPDATE_CURRENT));

		switch (mode) {
		case PC.SIGNAL_WARMUP:
		case PC.SIGNAL_WORKOUT:
		case PC.SIGNAL_COOLDOWN:
		case PC.SIGNAL_RESUME_WARMUP:
		case PC.SIGNAL_RESUME_WORKOUT:
		case PC.SIGNAL_RESUME_COOLDOWN:
			mBuilder.setContentText("Recording Workout");
			break;
		case PC.SIGNAL_PAUSE_WARMUP:
		case PC.SIGNAL_PAUSE_WORKOUT:
		case PC.SIGNAL_PAUSE_COOLDOWN:
			mBuilder.setContentText("Paused");
			break;
		case PC.SIGNAL_STOP:
			mBuilder.setContentText("Ready to begin Recording Workout");
			break;
		case PC.SIGNAL_SEARCHING:
			mBuilder.setContentText(this.getString(R.string.searching));
			break;
		}
		Notification n = mBuilder.getNotification();
		startForeground(1, n);
		
	}
	private void modeChanged(int m){
		mode = m;
		DbAdapter db = new DbAdapter(this.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
		switch (mode) {
			case PC.SIGNAL_WARMUP:
				speakTooSlow	= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L))==1;
				speakTooFast	= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H))==1;
				tooSlowData		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L_DATA));
				tooFastData		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H_DATA));
				tooSlow			= prefs.getFloat(	prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L_VALU));
				tooFast			= prefs.getFloat(	prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H_VALU));
				tooSlowUnit		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L_UNIT));
				tooFastUnit		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H_UNIT));
				break;
			case PC.SIGNAL_WORKOUT:
				speakTooSlow	= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L))==1;
				speakTooFast	= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H))==1;
				tooSlowData		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L_DATA));
				tooFastData		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H_DATA));
				tooSlow			= prefs.getFloat(	prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L_VALU));
				tooFast			= prefs.getFloat(	prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H_VALU));
				tooSlowUnit		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L_UNIT));
				tooFastUnit		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H_UNIT));
				break;
			case PC.SIGNAL_COOLDOWN:
				speakTooSlow	= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L))==1;
				speakTooFast	= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H))==1;
				tooSlowData		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L_DATA));
				tooFastData		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H_DATA));
				tooSlow			= prefs.getFloat(	prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L_VALU));
				tooFast			= prefs.getFloat(	prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H_VALU));
				tooSlowUnit		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L_UNIT));
				tooFastUnit		= prefs.getInt(		prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H_UNIT));
				break;
		}
		prefs.close();
		db.close();
		
		startTracking();
		notifcation(mode);
		sendToRunning(mode);
	}
	private void startTracking() {
		
		if( distance == 0 || (
				( mode==PC.SIGNAL_WARMUP ) ||
				( mode==PC.SIGNAL_WORKOUT  && !warmup_incl ) ||
				( mode==PC.SIGNAL_COOLDOWN && !cooldown_incl )
			)){

			if((autosplit || manualsplit) && distance !=0 && (
					(mode==PC.SIGNAL_WORKOUT   && warmup_splits) ||
					(mode==PC.SIGNAL_COOLDOWN)
					
				)) splitLast(SystemClock.elapsedRealtime());
			
			
			startime = SystemClock.elapsedRealtime();
			prevtime = startime;
			prevlocation = curlocation;
			distance  = 0;
			distanceSinceSplit = 0;
			timeOfLastSplit = startime;
			splitNo = 0;
			timeOfLastSpeedAlert = startime;
		}else{
			if(mode==PC.SIGNAL_COOLDOWN && !cooldown_splits && (autosplit || manualsplit)) splitLast(SystemClock.elapsedRealtime());
			
			// THIS SHOULD NOT HAPPEN BECAUSE IT DOES NOT MAKE ANY SENSE, But just in case
			if(mode==PC.SIGNAL_WORKOUT  && !warmup_splits && (autosplit || manualsplit))   splitLast(SystemClock.elapsedRealtime());
			
			if(mode==PC.SIGNAL_WORKOUT && workout_unit!=PC.UNITS_NODURATION){
				workout_valu = workout_valu + (workout_unit==PC.UNITS_MIN?SystemClock.elapsedRealtime()-startime:distance);
			}
			if(mode==PC.SIGNAL_COOLDOWN && cooldown_unit!=PC.UNITS_NODURATION){
				cooldown_valu = workout_valu + (cooldown_unit==PC.UNITS_MIN?SystemClock.elapsedRealtime()-startime:distance);	
			} 
		}
		

		tracking = true;
		logNewPoint(prevlocation, startime,mode+9,false);

		if (speak && speakStart)
			talk(this.getString(R.string.beginSpeech));
	}
	private void resumeTracking(int m) {

		mode = m;
		long now = SystemClock.elapsedRealtime();

		startime = now - (pausedAt - startime);
		prevtime = now - (pausedAt - prevtime);

		timeOfLastSplit = now - (pausedAt - timeOfLastSplit);
		prevlocation = curlocation;
		tracking = true;
		logNewPoint(prevlocation, now, mode+6,false);

		if (speak && speakResume)
			talk(this.getString(R.string.resumeSpeech));
		

		notifcation(mode);
		sendToRunning(mode);
	}

	private void stopTracking() {
		tracking = false;
		mode = PC.SIGNAL_STOP;
		
		splitLast(pausedAt);
		
		DbAdapter mDbHelper = new DbAdapter(this.getApplicationContext());
		mDbHelper.open();

		mDbHelper.logRun(runid, profile, System.currentTimeMillis()
				+ TimeZone.getDefault().getOffset(System.currentTimeMillis()),
				pausedAt - startime, distance);
		mDbHelper.close();
		if (speak && speakStop)
			talk(this.getString(R.string.stopSpeech));
		
		runid = 0;

		sendToRunning(mode);
	}

	private void pauseTracking(int m) {
		mode = m;
		pausedAt = SystemClock.elapsedRealtime();
		logNewPoint(curlocation, pausedAt,mode,false);
		tracking = false;

		if (speak && speakPause)
			talk(this.getString(R.string.pauseSpeech));

		notifcation(mode);
		sendToRunning(mode);
	}

	private void speakTooSlow(long now, Location argLocation){
		
		double speed = 0;
		if(		tooSlowData == PC.CELL_TYPE_SPEEDGPS)	speed = argLocation.getSpeed();
		else if(tooSlowData == PC.CELL_TYPE_SPEEDCAL)	speed = PC.getSpeedCal(this.getApplicationContext(),runid);
		else if(tooSlowData == PC.CELL_TYPE_SPEEDAVG)	speed = PC.getSpeedAvg(this.getApplicationContext(),runid);
		else if(tooSlowData == PC.CELL_TYPE_SPLIT_SPEED
				&& autosplit || manualsplit)				speed = PC.getSpeedSpl(this.getApplicationContext(),runid);
		
		if(speed > 0 && speed < tooSlow && now-timeOfLastSpeedAlert>speedfreq){
			talk("Speed Up");
			timeOfLastSpeedAlert = now;
		}
	}
	private void speakTooFast(long now, Location argLocation){
		
		double speed = 0;
		if(		tooFastData == PC.CELL_TYPE_SPEEDGPS)	speed = argLocation.getSpeed();
		else if(tooFastData == PC.CELL_TYPE_SPEEDCAL)	speed = PC.getSpeedCal(this.getApplicationContext(),runid);
		else if(tooFastData == PC.CELL_TYPE_SPEEDAVG)	speed = PC.getSpeedAvg(this.getApplicationContext(),runid);
		else if(tooFastData == PC.CELL_TYPE_SPLIT_SPEED 
				&& autosplit || manualsplit)				speed = PC.getSpeedSpl(this.getApplicationContext(),runid);
		
		if(speed > tooFast && now-timeOfLastSpeedAlert>speedfreq){
			talk("Slow Down");
			timeOfLastSpeedAlert = now;
		}
	}
	private void talk(String o){
		tts.speak(o, TextToSpeech.QUEUE_ADD, null);
	}
	private long insert(Location argLocation, long now, int signal,long elapsedtime, long difftime, double distance2, double d, float speedcal,long splittime,double distanceSinceSplit2){


		DbAdapter mDbHelper = new DbAdapter(this.getApplicationContext());
		mDbHelper.open();

		long p = mDbHelper.addPoint(
				runid, 
				now, 
				signal,
				argLocation.getLongitude(), 
				argLocation.getLatitude(),
				argLocation.getAccuracy(), 
				argLocation.getAltitude(),
				argLocation.getSpeed(), 
				argLocation.getBearing(),
				argLocation.getTime(), 
				elapsedtime, 
				difftime,
				distance2, 
				d,
				speedcal,
				splittime,
				distanceSinceSplit2);
		mDbHelper.close();
		return p;
	}
	private void splitLast(long now){
		

		long splitTime = now - timeOfLastSplit; 
		splitNo++;

		float curspeed = (float) PC.getSpeedCal(this.getApplicationContext(),runid,now,speedcaltime,0,0);
		
		long id = insert(prevlocation,now,PC.SPLIT_EXTRA,now-startime,now-prevtime,distance,0,curspeed,splitTime,distanceSinceSplit);
		
		DbAdapter db = new DbAdapter(this.getApplicationContext());
		db.open();
		db.addSplit(splitNo,(autosplit?PC.SPLIT_TYPE_AUTO:PC.SPLIT_TYPE_MANUAL),id,splitTime,distanceSinceSplit);
		db.close();
		
		distanceSinceSplit = 0;
		timeOfLastSplit = now;
	}
	private void split(long now) {

		if (!tracking)
			return;
		
		if(!(mode == PC.SIGNAL_WORKOUT || (mode==PC.SIGNAL_WARMUP && warmup_splits ) || mode==PC.SIGNAL_COOLDOWN && cooldown_splits))
			return;
		
		long id = 0;
		double over = 0;
		long t = now;
		if(autosplit){
			over = distanceSinceSplit - splitDistance;
			double deltad = distance - olddistance; 
			long  deltat = now - oldtime;
			double x1 = oldlocation.getLongitude();
			double y1 = oldlocation.getLatitude();
			double a1 = oldlocation.getAltitude();

			double x2 = prevlocation.getLongitude();
			double y2 = prevlocation.getLatitude();
			double a2 = prevlocation.getAltitude();

			t = (long) (now-((over/deltad)*deltat));

			Location l = new Location(prevlocation);
			l.setLongitude( x2-(over/deltad*(x2-x1)));
			l.setLatitude(y2-(over/deltad*(y2-y1)));
			l.setAltitude( a2-(over/deltad*(a2-a1)));
			l.setBearing(oldlocation.bearingTo(prevlocation));
			l.setSpeed((float) (deltad/deltat));
			l.setTime(t);

			float curspeed = (float) PC.getSpeedCal(this.getApplicationContext(),runid,t,speedcaltime,distance-over,t - prevtime);
			
			id = insert(l,t, PC.SIGNAL_SPLIT,t - startime, t - prevtime, distance-over,deltad-over,curspeed,t - timeOfLastSplit,distanceSinceSplit-over);
			oldlocation = new Location(l);
			olddistance = distance-over;
			oldtime = t;

		}else{
			float curspeed = (float) PC.getSpeedCal(this.getApplicationContext(),runid,t,speedcaltime,0,0);
			id = insert(prevlocation,now,PC.SIGNAL_SPLIT,now-startime,now-prevtime,distance,0,curspeed,t - timeOfLastSplit,distanceSinceSplit);
		}

		long splitTime = t - timeOfLastSplit; 
		timeOfLastSplit = t;
		splitNo++;

		DbAdapter db = new DbAdapter(this.getApplicationContext());
		db.open();
		db.addSplit(splitNo,(autosplit?PC.SPLIT_TYPE_AUTO:PC.SPLIT_TYPE_MANUAL),id,splitTime,distanceSinceSplit-over);
		db.close();

		distanceSinceSplit = over;

		sendToRunning(PC.SIGNAL_SPLIT);
		
		if (speak && speakSplits) {

			double tt = (double) splitTime;
			int hours = (int) Math.floor(tt / 1000 / 60 / 60); tt = tt - (hours * 60 * 60 * 1000);
			int minutes = (int) Math.floor(tt / 1000 / 60);    tt = tt - (minutes * 60 * 1000);
			int seconds = (int) Math.floor(tt / 1000);

			String output = this.getString(R.string.splitSpeech);
			if (hours > 0)
				output = output
				+ " "
				+ hours
				+ " "
				+ (hours == 1 ? this.getString(R.string.hour) : this
						.getString(R.string.hours));
			if (minutes > 0)
				output = output
				+ " "
				+ minutes
				+ " "
				+ (minutes == 1 ? this.getString(R.string.minute)
						: this.getString(R.string.minutes));
			if (seconds > 0)
				output = 
				output
				+ " "
				+ seconds
				+ " "
				+ (seconds == 1 ? this.getString(R.string.second)
						: this.getString(R.string.seconds));
			talk(output);

		}


		if (autosplit && distanceSinceSplit >= splitDistance)
			split( now);
	}

	private long logNewPoint(Location argLocation, long now, int signal, boolean update) {

		if (!tracking)
			return 0;
		
		if(update)
			distanceSinceSplit = distanceSinceSplit - (distance - olddistance);
		float diffdist;
		if(update){
			diffdist = oldlocation.distanceTo(argLocation);
			distance = olddistance + diffdist;
		}else{
			diffdist = prevlocation.distanceTo(argLocation);
			olddistance = distance;
			distance = distance + diffdist;
		}
		
		if(olddistance == 0){
			double d = argLocation.getSpeed()*(now - prevtime)/1000;
			if(d<diffdist) 
				distance = diffdist;
		}
			
			
		float curspeed;
		if(update){ curspeed = (float) PC.getSpeedCal(this.getApplicationContext(),runid,oldtime,speedcaltime,diffdist,now - oldtime);}
		else{ 		curspeed = (float) PC.getSpeedCal(this.getApplicationContext(),runid,now,    speedcaltime,diffdist,now - prevtime);}
		
		if(diffdist<1 && now - prevtime > 1000)curspeed=0;
		
			
		
		distanceSinceSplit = distanceSinceSplit + diffdist;
		
		if(update){
			DbAdapter mDbHelper = new DbAdapter(this.getApplicationContext());
			mDbHelper.open();
			mDbHelper.updateLastPoint(previd, runid, now,
					argLocation.getLongitude(), argLocation.getLatitude(),
					argLocation.getAccuracy(), argLocation.getAltitude(),
					argLocation.getSpeed(), argLocation.getBearing(),
					argLocation.getTime(), now - startime, now - oldtime, distance,
					diffdist,
					curspeed,
					now - timeOfLastSplit,
					distanceSinceSplit);

			mDbHelper.close();
		}else{
			previd = insert(argLocation,now,signal,now - startime, now - prevtime, distance, diffdist,curspeed,now - timeOfLastSplit,distanceSinceSplit);
		}
		if(!update){
			oldtime = prevtime;
			oldlocation = new Location(prevlocation);
		}
		prevtime = now;
		prevlocation = new Location(argLocation);
		
		newlocation(now,argLocation);
		
		return previd;
	}
	
	private void newlocation(long now, Location argLocation){

		sendToRunning(PC.SIGNAL_UPDATE);
		
		if (autosplit && distanceSinceSplit >= splitDistance) split( now);

		
		if(mode==PC.SIGNAL_WARMUP && (
				warmup_unit != PC.UNITS_MIN && distance       > warmup_valu && (warmup_unit != PC.UNITS_NODURATION ) ||
				warmup_unit == PC.UNITS_MIN && now - startime > warmup_valu)){
			switch(warmup_end){
			case PC.END_MOVE_ON_WORKOUT:
				modeChanged(PC.SIGNAL_WORKOUT);
				break;
			case PC.END_PAUSE:
				pauseTracking(PC.SIGNAL_PAUSE_WORKOUT);
				break;
			}
			
		}else if(mode==PC.SIGNAL_WORKOUT && (
				(workout_unit != PC.UNITS_MIN && distance       > workout_valu && workout_unit != PC.UNITS_NODURATION) ||
				(workout_unit == PC.UNITS_MIN && now - startime > workout_valu))){
			
			switch(workout_end){
			case PC.END_MOVE_ON_COOLDOWN:
				modeChanged(PC.SIGNAL_COOLDOWN);
				break;
			case PC.END_PAUSE:
				pauseTracking(PC.SIGNAL_PAUSE_COOLDOWN);
				break;
			}
			
		}else if(mode==PC.SIGNAL_COOLDOWN && (
				cooldown_unit != PC.UNITS_MIN && distance       > cooldown_valu && (cooldown_unit != PC.UNITS_NODURATION ) ||
				cooldown_unit == PC.UNITS_MIN && now - startime > cooldown_valu)){
			
			if(cooldown_end==PC.END_PAUSE)
				pauseTracking(PC.SIGNAL_PAUSE_COOLDOWN);
		}
		

		if (speak && speakTooSlow) speakTooSlow(now,argLocation);
		if (speak && speakTooFast) speakTooFast(now,argLocation);
	}
	private void updatelocation(Location argLocation) {

		float diffdist = prevlocation.distanceTo(argLocation);
		float newerror = argLocation.getAccuracy();
		float olderror = prevlocation.getAccuracy();

		// if within the old circle of error, but new accuracy better update old
		// point.
		if (diffdist < olderror && newerror < olderror) {
			logNewPoint(argLocation, SystemClock.elapsedRealtime(),mode,true);
		}
		// if outside of old circle of error, and old location outside new
		// circle of error save new point
		else if (diffdist >= olderror && diffdist >= newerror) {
			logNewPoint(argLocation, SystemClock.elapsedRealtime(),mode,false);
		}

		// if within the old circle of error, but accuracy is good, gps velocity
		// is near 0, and most recent update has been a while log 0 velocity
		// point
		else if (diffdist < olderror && newerror < 15 && olderror < 15
				&& argLocation.getSpeed() <= 0.1667
				&& prevtime + 10000 < SystemClock.elapsedRealtime()) {
			prevlocation.setSpeed(argLocation.getSpeed());
			logNewPoint(prevlocation, SystemClock.elapsedRealtime(),mode,false);
		}
	}

	private class MyLocationListener implements LocationListener { 	
		@Override 
		public void onLocationChanged(Location argLocation) {
			curlocation = new Location(argLocation);
			if (tracking) updatelocation(argLocation);
		}
		@Override public void onProviderDisabled(String provider) {}
		@Override public void onProviderEnabled(String provider) {}
		@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
	}

	@Override
	public void onInit(int status) {
		
		if (status == TextToSpeech.SUCCESS)
			speak = true;

	}
}
