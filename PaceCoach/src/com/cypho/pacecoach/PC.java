package com.cypho.pacecoach;

import java.util.Locale;
import java.util.Random;
import wgheaton.pacecoach.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class PC {
	public static final String UNITS					= "u";
	public static final int UNITS_NODURATION			= 14;
	public static final int UNITS_MILES 				= 1;
	public static final int UNITS_KILOMETERS			= 2;
	public static final int UNITS_FEET 					= 3;
	public static final int UNITS_YARDS 				= 4;
	public static final int UNITS_METERS 				= 5;
	public static final int UNITS_MINMILE 				= 6;
	public static final int UNITS_MINKILO 				= 7;
	public static final int UNITS_MIN	 				= 8;
	public static final int UNITS_MPH 					= 9;
	public static final int UNITS_KPH 					= 10;
	public static final int UNITS_KNOTS 				= 11;
	public static final int UNITS_FPS 					= 12;
	public static final int UNITS_MPS 					= 13;

	public static final String CELLS					= "c";
	public static final int CELL_TYPE_BLANK 			= 1;
	public static final int CELL_TYPE_TIME 				= 2;
	public static final int CELL_TYPE_DISTANCE 			= 3;
	public static final int CELL_TYPE_SPEEDGPS 			= 4;
	public static final int CELL_TYPE_SPEEDCAL 			= 5;
	public static final int CELL_TYPE_SPEEDAVG 			= 6;
	public static final int CELL_TYPE_LOCATION 			= 7;
	public static final int CELL_TYPE_ACCURACY 			= 8;
	public static final int CELL_TYPE_ALTITUDE 			= 9;
	public static final int CELL_TYPE_MAP 				= 10;
	public static final int CELL_TYPE_SPLITS			= 11;
	public static final int CELL_TYPE_SPLIT_NO			= 12;
	public static final int CELL_TYPE_SPLIT_SPEED		= 13;
	public static final int CELL_TYPE_SPLIT_DISTANCE	= 14;
	public static final int CELL_TYPE_SPLIT_TIME		= 15;
	public static final int CELL_TYPE_SPLIT_AUTOSPLIT	= 16;
	public static final int CELL_TYPE_SPLIT_DIST_LEFT	= 17;
	public static final int CELL_TYPE_SPLIT_TIME_LEFT	= 18;
	public static final int CELL_TYPE_GRAPH_DISTANCE	= 19;
	public static final int CELL_TYPE_GRAPH_ACCURACY	= 20;
	public static final int CELL_TYPE_GRAPH_ALTITUDE	= 21;
	public static final int CELL_TYPE_GRAPH_SPEEDGPS	= 22;
	public static final int CELL_TYPE_GRAPH_SPEEDAVG	= 23;
	public static final int CELL_TYPE_GRAPH_SPEEDCAL	= 24;

	public static final String MAP_MODES				= "mm";
	public static final int MAP_MODE_AUTO				= 1;
	public static final int MAP_MODE_FIXED				= 2;
	public static final int MAP_MODE_ZOOM				= 3;
	public static final String MAP_TYPES				= "mt";
	public static final int MAP_TYPE_SATALITE			= 1;
	public static final int MAP_TYPE_NORMAL				= 0;

	public static final String SREEN_ROTATIONS			= "sr";
	public static final int ROTATE_AUTO					= 1;
	public static final int ROTATE_PORTRAIT				= 2;
	public static final int ROTATE_LANDSCAPE			= 3;
	
	public static final String LANDSCAPE_MODE			= "lm";
	public static final int LANDSCAPE_ROTATE			= 1;
	public static final int LANDSCAPE_DOUBLE			= 2;
	public static final int LANDSCAPE_STRETCH			= 3;
	
	public static final String FINISH_MODE				= "fm";
	public static final int END_KEEP_GOING				= 1;
	public static final int END_PAUSE					= 2;
	public static final int END_MOVE_ON_WORKOUT			= 3;
	public static final int END_MOVE_ON_COOLDOWN		= 4;
	
	
	
	public static final int SIGNAL_SEARCHING			= 0;
	public static final int SIGNAL_WARMUP 				= 1;
	public static final int SIGNAL_WORKOUT 				= 2;
	public static final int SIGNAL_COOLDOWN				= 3;
	public static final int SIGNAL_PAUSE_WARMUP 		= 4;
	public static final int SIGNAL_PAUSE_WORKOUT 		= 5;
	public static final int SIGNAL_PAUSE_COOLDOWN		= 6;
	public static final int SIGNAL_RESUME_WARMUP 		= 7;
	public static final int SIGNAL_RESUME_WORKOUT 		= 8;
	public static final int SIGNAL_RESUME_COOLDOWN		= 9;
	public static final int SIGNAL_BEGIN_WARMUP 		= 10;
	public static final int SIGNAL_BEGIN_WORKOUT 		= 11;
	public static final int SIGNAL_BEGIN_COOLDOWN		= 12;
	public static final int SIGNAL_STOP 				= 13;
	public static final int SIGNAL_SPLIT 				= 14;
	public static final int SPLIT_EXTRA 				= 15;
	public static final int SIGNAL_WAKEUP				= 16;
	public static final int SIGNAL_SLEEP				= 17;
	public static final int SIGNAL_UPDATE				= 18;
	public static final int SIGNAL_REFRESH				= 19;
	public static final int SIGNAL_QUERY				= 20;
	
	public static final int SPLIT_TYPE_AUTO 			= 1;
	public static final int SPLIT_TYPE_MANUAL			= 2;
	
	public static final int     DEFAULT_PREF_PROFILE								= 1;
	public static final boolean DEFAULT_PREF_REMEMBER								= false;
	public static final boolean DEFAULT_PREF_SPEAK		 							= true;
	public static final boolean DEFAULT_PREF_SPEAK_START 							= true;
	public static final boolean DEFAULT_PREF_SPEAK_STOP 							= true;
	public static final boolean DEFAULT_PREF_SPEAK_RESUME 							= true;
	public static final boolean DEFAULT_PREF_SPEAK_PAUSE 							= true;
	public static final boolean DEFAULT_PREF_SPEAK_SPLITS 							= true;
	public static final boolean DEFAULT_PREF_SPEAK_SPEED_L							= false;
	public static final boolean DEFAULT_PREF_SPEAK_SPEED_H							= false;
	public static final boolean DEFAULT_PREF_SPEAK_WARMUP_SPEED_L					= false;
	public static final boolean DEFAULT_PREF_SPEAK_WARMUP_SPEED_H					= false;
	public static final boolean DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L					= false;
	public static final boolean DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H					= false;
	public static final boolean DEFAULT_PREF_COOLDOWN								= false;
	public static final boolean DEFAULT_PREF_WARMUP									= false;
	public static final boolean DEFAULT_PREF_WARMUP_SPLITS							= false;
	public static final boolean DEFAULT_PREF_COOLDOWN_SPLITS						= false;

	public static final int 	DEFAULT_PREF_SPEAK_SPEED_FREQ						= 15;
	public static final int 	DEFAULT_PREF_SPEAK_SPEED_L_DATA						= CELL_TYPE_SPEEDCAL;
	public static final int 	DEFAULT_PREF_SPEAK_SPEED_L_INT1						= 8;
	public static final int 	DEFAULT_PREF_SPEAK_SPEED_L_INT2						= 0;
	public static final int 	DEFAULT_PREF_SPEAK_SPEED_L_UNIT						= UNITS_MINMILE;
	public static final String 	DEFAULT_PREF_SPEAK_SPEED_L_FORM						= "During Workout, Alert when Speed (Calc) < 8:00 Minutes/Mile";
	public static final float 	DEFAULT_PREF_SPEAK_SPEED_L_VALU						= (float) 3.3528;
	public static final int 	DEFAULT_PREF_SPEAK_SPEED_H_DATA						= CELL_TYPE_SPEEDCAL;
	public static final int 	DEFAULT_PREF_SPEAK_SPEED_H_INT1						= 8;
	public static final int 	DEFAULT_PREF_SPEAK_SPEED_H_INT2						= 0;
	public static final int 	DEFAULT_PREF_SPEAK_SPEED_H_UNIT						= UNITS_MINMILE;
	public static final String 	DEFAULT_PREF_SPEAK_SPEED_H_FORM						= "During Workout, Alert when Speed (Calc) > 8:00 Minutes/Mile";
	public static final float 	DEFAULT_PREF_SPEAK_SPEED_H_VALU						= (float) 3.3528;
	

	public static final int 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_DATA			= CELL_TYPE_SPEEDCAL;
	public static final int 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_INT1			= 8;
	public static final int 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_INT2			= 0;
	public static final int 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_UNIT			= UNITS_MINMILE;
	public static final String 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_FORM			= "During Cooldown, Alert when Speed (Calc) < 8:00 Minutes/Mile";
	public static final float 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_VALU			= (float) 3.3528;
	public static final int 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_DATA			= CELL_TYPE_SPEEDCAL;
	public static final int 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_INT1			= 8;
	public static final int 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_INT2			= 0;
	public static final int 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_UNIT			= UNITS_MINMILE;
	public static final String 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_FORM			= "During Cooldown, Alert when Speed (Calc) > 8:00 Minutes/Mile";
	public static final float 	DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_VALU			= (float) 3.3528;

	public static final int 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_DATA				= CELL_TYPE_SPEEDCAL;
	public static final int 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_INT1				= 8;
	public static final int 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_INT2				= 0;
	public static final int 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_UNIT				= UNITS_MINMILE;
	public static final String 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_FORM				= "During Warmup, Alert when Speed (Calc) < 8:00 Minutes/Mile";
	public static final float 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_VALU				= (float) 3.3528;
	public static final int 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_DATA				= CELL_TYPE_SPEEDCAL;
	public static final int 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_INT1				= 8;
	public static final int 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_INT2				= 0;
	public static final int 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_UNIT				= UNITS_MINMILE;
	public static final String 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_FORM				= "During Warmup, Alert when Speed (Calc) > 8:00 Minutes/Mile";
	public static final float 	DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_VALU				= (float) 3.3528;
	
	public static final float   DEFAULT_PREF_AUTO_SPLIT_DISTANCE_VALU 				=  (float) 1609.34;
	public static final int 	DEFAULT_PREF_AUTO_SPLIT_DISTANCE_INT1				= 1;
	public static final int 	DEFAULT_PREF_AUTO_SPLIT_DISTANCE_INT2				= 0;
	public static final int 	DEFAULT_PREF_AUTO_SPLIT_DISTANCE_UNIT				= UNITS_MILES;
	public static final String 	DEFAULT_PREF_AUTO_SPLIT_DISTANCE_FORM				= "1.0 Miles";
	public static final boolean DEFAULT_PREF_AUTO_SPLIT 							= false;	
	public static final boolean DEFAULT_PREF_MANUAL_SPLITS 							= false;
	
	public static final int 	DEFAULT_PREF_PAGE_NUMBER 							= 6;
	public static final int 	DEFAULT_PREF_PAGE_DEFAULT 							= 1;
	public static final boolean DEFAULT_PREF_PAGE_LOCK 								= true;
	public static final int 	DEFAULT_PREF_MIN_PAGES 								= 2;
	public static final int 	DEFAULT_PREF_MAX_PAGES 								= 10;
	public static final int 	DEFAULT_PREF_MAP_MODE 								= MAP_MODE_AUTO;
	public static final int 	DEFAULT_PREF_MAP_TYPE 								= MAP_TYPE_NORMAL;
	public static final int		DEFAULT_PREF_ROTATE									= ROTATE_AUTO;
	public static final int 	DEFAULT_PREF_PORTRAIT								= LANDSCAPE_ROTATE;
	public static final long	DEFAULT_PREF_SPEEDCAL_TIME							= 30;
	
	public static final int 	DEFAULT_PREF_WARMUP_INT1							= 3;
	public static final int		DEFAULT_PREF_WARMUP_INT2							= 0;
	public static final int 	DEFAULT_PREF_WARMUP_UNIT							= UNITS_MIN;
	public static final String 	DEFAULT_PREF_WARMUP_FORM							= "3:00 Minutes";
	public static final double 	DEFAULT_PREF_WARMUP_VALU							= 180000;
	public static final boolean	DEFAULT_PREF_WARMUP_INCL							= false;
	public static final int		DEFAULT_PREF_WARMUP_END_ACTION						= END_MOVE_ON_WORKOUT;
	
	public static final int 	DEFAULT_PREF_WORKOUT_INT1							= 0;
	public static final int 	DEFAULT_PREF_WORKOUT_INT2							= 0;
	public static final int 	DEFAULT_PREF_WORKOUT_UNIT							= UNITS_NODURATION;
	public static final String 	DEFAULT_PREF_WORKOUT_FORM							= "No Duration Set";
	public static final double 	DEFAULT_PREF_WORKOUT_VALU							= 0;
	public static final int		DEFAULT_PREF_WORKOUT_END_ACTION						= END_KEEP_GOING;
	
	public static final int 	DEFAULT_PREF_COOLDOWN_INT1							= 3;
	public static final int 	DEFAULT_PREF_COOLDOWN_INT2							= 0;
	public static final int 	DEFAULT_PREF_COOLDOWN_UNIT							= UNITS_MIN;
	public static final String 	DEFAULT_PREF_COOLDOWN_FORM							= "3:00 Minutes";
	public static final double 	DEFAULT_PREF_COOLDOWN_VALU							= 180000;
	public static final boolean	DEFAULT_PREF_COOLDOWN_INCL							= false;
	public static final int		DEFAULT_PREF_COOLDOWN_END_ACTION					= END_PAUSE;
	
	
	
	public static final String PREF_PROFILE					= "profile";
	public static final String PREF_REMEMBER				= "remember";
	public static final String PREF_MAP_MODE				= "mapmode";
	public static final String PREF_MAP_TYPE				= "maptype";
	public static final String PREF_ROTATE					= "rotate";
	public static final String PREF_PORTRAIT				= "landscape";
	
	public static final String PREF_SPEAK		 			= "speak";
	public static final String PREF_SPEAK_START 			= "speakStart";
	public static final String PREF_SPEAK_STOP 				= "speakStop";
	public static final String PREF_SPEAK_RESUME 			= "speakResume";
	public static final String PREF_SPEAK_PAUSE 			= "speakPause";
	public static final String PREF_SPEAK_SPLITS 			= "speakSplits";

	public static final String PREF_SPEAK_SPEED_FREQ		= "speakSpeedFreq";
	public static final String PREF_SPEAK_SPEED_L 			= "speakSpeed_L";
	public static final String PREF_SPEAK_SPEED_L_DATA		= "speakSpeedData_L";
	public static final String PREF_SPEAK_SPEED_L_INT1		= "speakSpeedInt1_L";
	public static final String PREF_SPEAK_SPEED_L_INT2		= "speakSpeedInt2_L";
	public static final String PREF_SPEAK_SPEED_L_UNIT		= "speakSpeedUnit_L";
	public static final String PREF_SPEAK_SPEED_L_FORM		= "speakSpeedFormatted_L";
	public static final String PREF_SPEAK_SPEED_L_VALU		= "speakSpeedValue_L";
	public static final String PREF_SPEAK_SPEED_H 			= "speakSpeed_H";
	public static final String PREF_SPEAK_SPEED_H_DATA		= "speakSpeedData_H";
	public static final String PREF_SPEAK_SPEED_H_INT1		= "speakSpeedInt1_H";
	public static final String PREF_SPEAK_SPEED_H_INT2		= "speakSpeedInt2_H";
	public static final String PREF_SPEAK_SPEED_H_UNIT		= "speakSpeedUnit_H";
	public static final String PREF_SPEAK_SPEED_H_FORM		= "speakSpeedFormatted_H";
	public static final String PREF_SPEAK_SPEED_H_VALU		= "speakSpeedValue_H";
	
	public static final String PREF_SPEAK_WARMUP_SPEED_L 			= "speakWarmupSpeed_L";
	public static final String PREF_SPEAK_WARMUP_SPEED_L_DATA		= "speakWarmupSpeedData_L";
	public static final String PREF_SPEAK_WARMUP_SPEED_L_INT1		= "speakWarmupSpeedInt1_L";
	public static final String PREF_SPEAK_WARMUP_SPEED_L_INT2		= "speakWarmupSpeedInt2_L";
	public static final String PREF_SPEAK_WARMUP_SPEED_L_UNIT		= "speakWarmupSpeedUnit_L";
	public static final String PREF_SPEAK_WARMUP_SPEED_L_FORM		= "speakWarmupSpeedFormatted_L";
	public static final String PREF_SPEAK_WARMUP_SPEED_L_VALU		= "speakWarmupSpeedValue_L";
	public static final String PREF_SPEAK_WARMUP_SPEED_H 			= "speakWarmupSpeed_H";
	public static final String PREF_SPEAK_WARMUP_SPEED_H_DATA		= "speakWarmupSpeedData_H";
	public static final String PREF_SPEAK_WARMUP_SPEED_H_INT1		= "speakWarmupSpeedInt1_H";
	public static final String PREF_SPEAK_WARMUP_SPEED_H_INT2		= "speakWarmupSpeedInt2_H";
	public static final String PREF_SPEAK_WARMUP_SPEED_H_UNIT		= "speakWarmupSpeedUnit_H";
	public static final String PREF_SPEAK_WARMUP_SPEED_H_FORM		= "speakWarmupSpeedFormatted_H";
	public static final String PREF_SPEAK_WARMUP_SPEED_H_VALU		= "speakWarmupSpeedValue_H";

	public static final String PREF_SPEAK_COOLDOWN_SPEED_L 			= "speakCooldownSpeed_L";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_L_DATA		= "speakCooldownSpeedData_L";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_L_INT1		= "speakCooldownSpeedInt1_L";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_L_INT2		= "speakCooldownSpeedInt2_L";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_L_UNIT		= "speakCooldownSpeedUnit_L";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_L_FORM		= "speakCooldownSpeedFormatted_L";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_L_VALU		= "speakCooldownSpeedValue_L";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_H 			= "speakCooldownSpeed_H";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_H_DATA		= "speakCooldownSpeedData_H";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_H_INT1		= "speakCooldownSpeedInt1_H";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_H_INT2		= "speakCooldownSpeedInt2_H";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_H_UNIT		= "speakCooldownSpeedUnit_H";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_H_FORM		= "speakCooldownSpeedFormatted_H";
	public static final String PREF_SPEAK_COOLDOWN_SPEED_H_VALU		= "speakCooldownSpeedValue_H";
	
	public static final String PREF_AUTO_SPLIT 						= "autosplit";
	public static final String PREF_AUTO_SPLIT_DISTANCE_VALU		= "autosplitdist";
	public static final String PREF_AUTO_SPLIT_DISTANCE_INT1		= "autosplitdistint1";
	public static final String PREF_AUTO_SPLIT_DISTANCE_INT2		= "autosplitdistint2";
	public static final String PREF_AUTO_SPLIT_DISTANCE_UNIT		= "autosplitdistunits";
	public static final String PREF_AUTO_SPLIT_DISTANCE_FORM		= "autosplitdistformatted";
	public static final String PREF_MANUAL_SPLITS 					= "manualsplits";
	public static final String PREF_PAGE_NUMBER 					= "numberofpages";
	public static final String PREF_PAGE_DEFAULT 					= "defaultpage";
	public static final String PREF_PAGE_LOCK 						= "lockpages";
	public static final String PREF_SPEEDCAL_TIME					= "speedcaltime";
	

	public static final String PREF_WARMUP				= "warmup";
	public static final String PREF_WARMUP_INT1			= "warmupInt1";
	public static final String PREF_WARMUP_INT2			= "warmupInt2";
	public static final String PREF_WARMUP_UNIT			= "warmupUnit";
	public static final String PREF_WARMUP_FORM			= "warmupFormatted";
	public static final String PREF_WARMUP_VALU			= "warmupValue";
	public static final String PREF_WARMUP_INCL			= "warmupInclude";
	public static final String PREF_WARMUP_SPLITS		= "warmupSplits";
	public static final String PREF_WARMUP_END_ACTION	= "warmupEndAction";
	
	public static final String PREF_WORKOUT_INT1		= "workoutInt1";
	public static final String PREF_WORKOUT_INT2		= "workoutInt2";
	public static final String PREF_WORKOUT_UNIT		= "workoutUnit";
	public static final String PREF_WORKOUT_FORM		= "workoutFormatted";
	public static final String PREF_WORKOUT_VALU		= "workoutValue";
	public static final String PREF_WORKOUT_END_ACTION	= "wokroutEndAction";

	public static final String PREF_COOLDOWN			= "cooldown";
	public static final String PREF_COOLDOWN_INT1		= "cooldownInt1";
	public static final String PREF_COOLDOWN_INT2		= "cooldownInt2";
	public static final String PREF_COOLDOWN_UNIT		= "cooldownUnit";
	public static final String PREF_COOLDOWN_FORM		= "cooldownFormatted";
	public static final String PREF_COOLDOWN_VALU		= "cooldownValue";
	public static final String PREF_COOLDOWN_INCL		= "cooldownInclude";
	public static final String PREF_COOLDOWN_SPLITS		= "cooldownSplits";
	public static final String PREF_COOLDOWN_END_ACTION	= "cooldownEndAction";
	
	
	public static final String PREFS 						= "prefs";
	public static final String MODE							= "runningstatus";
	public static final String SIGNAL						= "signal";

	public static final String DB_RUNDATA					= "rundata";
	public static final String DB_RUNSUMMARY				= "runsummary";
	public static final String DB_PREFS						= "prefs";
	public static final String DB_SPLITS					= "splits";
	public static final String DB_PAGE						= "page";
	public static final String DB_TEMPLATE					= "template";
	public static final String DB_CELL						= "cell";
	public static final String DB_DATATYPE					= "datatype";
	public static final String DB_UNITS						= "units";
	public static final String DB_PROFILE_LABEL				= "label";
	
	public static final String DB_ROWID						= "_id";
	public static final String DB_RUNID						= "runid";
	public static final String DB_TIMESTAMP					= "timestampe";
	public static final String DB_MODE						= "mode";
	public static final String DB_LONGITUDE					= "longitude";
	public static final String DB_LATITUDE					= "latitude";
	public static final String DB_ACCURACY					= "accuracy";
	public static final String DB_ALTITUDE					= "altitude";
	public static final String DB_SPEED_GPS					= "speed";
	public static final String DB_BEARING					= "bearing";
	public static final String DB_THETIME					= "thetime";

	public static final String DB_TIME_ELAP					= "elapsedtime";
	public static final String DB_DIST_ELAP					= "elapseddist";
	public static final String DB_TIME_DIFF					= "difftime";
	public static final String DB_DIST_DIFF					= "diffdist";
	public static final String DB_DIST_SINCESPLIT			= "distsincesplit";
	public static final String DB_TIME_SINCESPLIT			= "timesincesplit";
	public static final String DB_SPEED_CAL					= "speedcal";

	public static final String DB_SPLIT_TYPE				= "splitType";
	public static final String DB_SPLIT_NO					= "splitNo";
	public static final String DB_SPLIT_TIME				= "splitTime";
	public static final String DB_SPLIT_DIST				= "splitDist";
	

	public static final int[] map_mode_list = new int[]{
		MAP_MODE_AUTO,
		MAP_MODE_FIXED,
		MAP_MODE_ZOOM}; 
	public static final int[] map_type_list = new int[]{
		MAP_TYPE_NORMAL,
		MAP_TYPE_SATALITE};

	public static final int[] rotation_list = new int[]{
		ROTATE_AUTO,
		ROTATE_PORTRAIT,
		ROTATE_LANDSCAPE};

	public static final int[] portrait_list = new int[]{
		LANDSCAPE_ROTATE,
		LANDSCAPE_DOUBLE,
		LANDSCAPE_STRETCH};

	public static final int[] end_warmup_list = new int[]{
		PC.END_MOVE_ON_WORKOUT,
		PC.END_KEEP_GOING,
		PC.END_PAUSE};
	public static final int[] end_workout_list1 = new int[]{
		PC.END_KEEP_GOING,
		PC.END_PAUSE};
	public static final int[] end_workout_list2 = new int[]{
		PC.END_MOVE_ON_COOLDOWN,
		PC.END_KEEP_GOING,
		PC.END_PAUSE};
	public static final int[] end_cooldown_list = new int[]{
		PC.END_KEEP_GOING,
		PC.END_PAUSE};
	
	
	public static final int[] cell_types = new int[]{
		CELL_TYPE_BLANK,
		CELL_TYPE_TIME,
		CELL_TYPE_DISTANCE,
		CELL_TYPE_SPEEDGPS,
		CELL_TYPE_SPEEDCAL,
		CELL_TYPE_SPEEDAVG,
		CELL_TYPE_LOCATION,
		CELL_TYPE_ACCURACY,
		CELL_TYPE_ALTITUDE,
		CELL_TYPE_MAP,
		CELL_TYPE_SPLITS,
		CELL_TYPE_SPLIT_NO,
		CELL_TYPE_SPLIT_SPEED,
		CELL_TYPE_SPLIT_TIME,
		CELL_TYPE_SPLIT_DISTANCE,
		CELL_TYPE_SPLIT_AUTOSPLIT,
		CELL_TYPE_SPLIT_DIST_LEFT,
		CELL_TYPE_SPLIT_TIME_LEFT,
		CELL_TYPE_GRAPH_DISTANCE,
		CELL_TYPE_GRAPH_ACCURACY,
		CELL_TYPE_GRAPH_ALTITUDE,
		CELL_TYPE_GRAPH_SPEEDGPS,
		CELL_TYPE_GRAPH_SPEEDAVG,
		CELL_TYPE_GRAPH_SPEEDCAL};
	
	public static final int[] cell_speed  = new int[]{
		CELL_TYPE_SPEEDGPS,
		CELL_TYPE_SPEEDCAL,
		CELL_TYPE_SPEEDAVG,
		CELL_TYPE_SPLIT_SPEED};

	public static final int[] units_distance_full = new int[]{
		UNITS_MILES,
		UNITS_KILOMETERS,
		UNITS_FEET,
		UNITS_YARDS,
		UNITS_METERS};
	public static final int[] units_duration= new int[]{
		UNITS_NODURATION,
		UNITS_FEET,
		UNITS_YARDS,
		UNITS_METERS,
		UNITS_MILES,
		UNITS_KILOMETERS,
		UNITS_MIN};
	public static final int[] units_distance_small = new int[]{
		UNITS_FEET,
		UNITS_YARDS,
		UNITS_METERS};
	public static final int[] units_speed = new int[]{
		UNITS_MINMILE,
		UNITS_MINKILO,
		UNITS_MPH,
		UNITS_KPH,
		UNITS_KNOTS,
		UNITS_FPS,
		UNITS_MPS};
	public static final int[] units_split = new int[]{
		UNITS_FEET,
		UNITS_YARDS,
		UNITS_METERS,
		UNITS_MILES,
		UNITS_KILOMETERS};
	
	
	public static int cell_count(int template){
		int n = template;
		int sum = 0;
		while (n > 0) {
			int p = n % 10;
			sum = sum + p;
			n = n / 10;
		}
		return sum;
	}
	public static int cell_link(int cellid){
		int ret = 0;
		switch(cellid){
		case 1:
			ret = R.id.cell1;
			break;
		case 2:
			ret = R.id.cell2;
			break;
		case 3:
			ret = R.id.cell3;
			break;
		case 4:
			ret = R.id.cell4;
			break;
		case 5:
			ret = R.id.cell5;
			break;
		case 6:
			ret = R.id.cell6;
			break;
		case 7:
			ret = R.id.cell7;
			break;
		case 8:
			ret = R.id.cell8;
			break;
		case 9:
			ret = R.id.cell9;
			break;
		case 10:
			ret = R.id.cell10;
			break;
		case 11:
			ret = R.id.cell11;
			break;
		case 12:
			ret = R.id.cell12;
			break;
		}
		return ret;
	}
	public static int uniqueId(Activity a){
		Random r = new Random();
		int n;
		do{
			n = Math.abs(r.nextInt());
		}while(a.findViewById(n)!=null);
		
		return n;
	}
	public static final int[] page_list = {	   1,
		11,
		21,
		22,
		31,
		32,
		41,
		211,
		221,
		222,
		311,
		321,
		322,
		331,
		332,
		333,
		1111,
		2222,
		3222,
		3322,
		3331,
		3332,
		3333};

	public static int page_link(int template){
		int out = R.layout.page_0001;
		switch(template){
		case 1:
			out = R.layout.page_0001;
			break;
		case 11:
			out = R.layout.page_0011;
			break;
		case 21:
			out = R.layout.page_0021;
			break;
		case 22:
			out = R.layout.page_0022;
			break;
		case 31:
			out = R.layout.page_0031;
			break;
		case 32:
			out = R.layout.page_0032;
			break;
		case 41:
			out = R.layout.page_0041;
			break;
		case 211:
			out = R.layout.page_0211;
			break;
		case 221:
			out = R.layout.page_0221;
			break;
		case 222:
			out = R.layout.page_0222;
			break;
		case 311:
			out = R.layout.page_0311;
			break;
		case 321:
			out = R.layout.page_0321;
			break;
		case 322:
			out = R.layout.page_0322;
			break;
		case 331:
			out = R.layout.page_0331;
			break;
		case 332:
			out = R.layout.page_0332;
			break;
		case 333:
			out = R.layout.page_0333;
			break;
		case 1111:
			out = R.layout.page_1111;
		case 2222:
			out = R.layout.page_2222;
			break;
		case 3222:
			out = R.layout.page_3222;
			break;
		case 3322:
			out = R.layout.page_3322;
			break;
		case 3331:
			out = R.layout.page_3331;
			break;
		case 3332:
			out = R.layout.page_3332;
			break;
		case 3333:
			out = R.layout.page_3333;
			break;
		}
		return out;

	}

	public static double unconvertUnits(int i1, int i2, int u){
		double b = (((double)i1)+(((double)i2)/10));

		if(		u == UNITS_FEET) 		b = b/3.28;
		else if(u == UNITS_YARDS) 		b = b/3.28*3;
		else if(u == UNITS_MILES) 		b = b*1609.34;
		else if(u == UNITS_KILOMETERS) 	b = b*1000;
		else if(u == UNITS_MINMILE) 	b = 26.8224 / (i1*60+i2);
		else if(u == UNITS_MINKILO)		b = 16.6666 / (i1*60+i2);
		else if(u == UNITS_MIN) 		b = i1*60+i2;
		else if(u == UNITS_MPH) 		b = b/2.23694;
		else if(u == UNITS_KPH) 		b = b/3.6;
		else if(u == UNITS_KNOTS) 		b = b/1.94384;
		else if(u == UNITS_FPS)			b = b/3.28084;
		else if(u == UNITS_NODURATION)	b = 0;
		
		return b;
	}
	public static double convertUnits(double a, int u){
		
		if     (u == UNITS_MINMILE)		a = (a<0.2683)?100:26.8224/a;
		else if(u == UNITS_MINKILO)		a = (a>0.1667)?100:16.6666/a;
		else if(u == UNITS_MPH) 		a = a*2.23694;
		else if(u == UNITS_KPH) 		a = a*3.6;
		else if(u == UNITS_KNOTS) 		a = a*1.94384;
		else if(u == UNITS_FPS) 		a = a*3.28084;
		else if(u == UNITS_FEET) 		a = a*3.28;
		else if(u == UNITS_YARDS) 		a = a*3.28/3;
		else if(u == UNITS_MILES) 		a = a/1609.34;
		else if(u == UNITS_KILOMETERS) 	a = a/1000;
		else if(u == UNITS_NODURATION)	a = 0;
		
		return a;
	}
	public static String partsFormat(Context ctx, int i1, int i2, int u){
		String formatted = "";

		if(u == PC.UNITS_NODURATION){
			
		}else if(u == PC.UNITS_FEET || u == PC.UNITS_YARDS || u == PC.UNITS_METERS){
			
			formatted = String.valueOf((i1+1)*50) + " ";
		}else if(u == PC.UNITS_MINKILO || u == PC.UNITS_MINMILE || u == PC.UNITS_MIN){
			formatted = String.format(Locale.getDefault(),"%02d:%02d ", i1,i2);
			
		}else{
			formatted = String.format(Locale.getDefault(),"%d.%d ", i1,i2);
		}
		formatted = formatted + PC.getText(ctx,PC.UNITS+u);
		
		return formatted;
	}
	public static float partsVal(Context ctx, int i1, int i2, int u){
		float val;
		
		if(u == PC.UNITS_FEET || u == PC.UNITS_YARDS || u == PC.UNITS_METERS){
			val = (float) PC.unconvertUnits((i1+1)*50,i2,u);
		}else{
			val = (float) PC.unconvertUnits(i1,i2,u);
		}
		
		return val;
	}

	
	public static String formatUnits(double a,int u){
		a = convertUnits(a,u);
		
		String s = "";
		String min = "";
		String sec = "";
		int seci = 0;
		if(u == UNITS_MINMILE || u == UNITS_MINKILO){
			if(a<99){
				min = String.valueOf((int)Math.floor(a));
				seci = (int) Math.floor((a - Math.floor(a))*60);
				sec = String.valueOf(seci);
				if(min.length()==1) min = "0"+min;
				if(sec.length()==1) sec = "0"+sec;
				s = min + ":" + sec;
			}else{
				s = "";
			}
		}else if(u == UNITS_MILES || u == UNITS_KILOMETERS){
			s = String.format(Locale.getDefault(),"%.2f", a);
		}else if(u == UNITS_FEET || u == UNITS_YARDS){
			s = String.format(Locale.getDefault(),"%.0f", a);
		}else if(u == UNITS_NODURATION){
			s = "";
		}else if(u == 0){
			s = String.format(Locale.getDefault(),"%.0f", a);
		}else{
			s = String.format(Locale.getDefault(),"%.1f", a);
		}
		
		return s;
	}

	
	public static final boolean LATITUDE  = true;
	public static final boolean LONGITUDE = false;
	public static String formatGPS(String a,boolean lat){
		if(a==null)
			return "";
		
		String o = "";

		String[] p = a.split("\\:");
		boolean pos = Integer.valueOf(p[0])> 0;

		if(lat && pos) o = "N";
		else if(lat)   o = "S";
		else if(pos)   o = "E";
		else		   o = "W";

		
			
		int deg = Math.abs(Integer.valueOf(p[0]));
		int min = Integer.valueOf(p[1]);
		int sec = (int) Math.floor(Float.valueOf(p[2]));
		int dec = (int) Math.round((Float.valueOf(p[2]) - (float)sec)*1000);
		
		if(deg<100)
			o += "\u0020";
		
		o +=  String.format(Locale.getDefault(),"%02d°%02d'%02d.%03d\"", deg,min,sec,dec);
		

		return o;
	}
	public static String formatTime(Long a){

		a = (long) Math.floor( a / 1000 );

		int hours   = (int) Math.floor(a/60/60);
		a = a - hours*60*60;
		int minutes = (int) Math.floor(a/60);
		int seconds = (int) (a - minutes*60);

		String o = "";
		if(hours > 0)
			o = String.valueOf(hours) + ":";

		if(minutes>9)
			o = o + String.valueOf(minutes) + ":";
		else
			o = o + "0" + String.valueOf(minutes) + ":";

		if(seconds>9)
			o = o + String.valueOf(seconds);
		else
			o = o + "0" + String.valueOf(seconds);

		return o;
	}
	
	public static double getSpeedCal(Context ctx, int runid, long before, long length, double d, double t) {
		if(runid == 0) return 0;
		
		DbAdapter db = new DbAdapter(ctx);
		db.open();
        Cursor data = db.getLastDatapoints(runid,before,length);
        while(data.moveToNext()){
        	//if(data.getDouble(data.getColumnIndex(PC.DB_DIST_DIFF))<1)
        	//	break;
        	d = d + data.getDouble(data.getColumnIndex(PC.DB_DIST_DIFF));
			t = t + data.getDouble(data.getColumnIndex(PC.DB_TIME_DIFF));
        }
        data.close();
        db.close();

        if(t < 1000)return 0;
        else		return d/(t/1000);
	}
	public static float getSpeedCal(Context ctx, int runid) {
		if(runid == 0) return 0;
		
		float s = 0;
		
		DbAdapter db = new DbAdapter(ctx);
		db.open();
        Cursor data = db.getLastDatapoints(runid,1);
        if(data.moveToFirst())
        	s = data.getFloat(data.getColumnIndex(PC.DB_SPEED_CAL));
        
        data.close();
        db.close();
        
        return s;
	}

	public static Long getTime(Context ctx, int runid){
		if(runid == 0) return (long) 0;

		long t = 0;
		
		DbAdapter db = new DbAdapter(ctx);
		db.open();
        Cursor data = db.getLastDatapoints(runid,1);
        if(data.moveToFirst())
        	t = data.getLong(data.getColumnIndex(PC.DB_TIME_ELAP));
        
        data.close();
        db.close();
        return t;
	}
	public static double getDistance(Context ctx, int runid){
		if(runid == 0) return 0;

		double d = 0;
		
		DbAdapter db = new DbAdapter(ctx);
		db.open();
        Cursor data = db.getLastDatapoints(runid,1);
        if(data.moveToFirst()){
        	d = data.getDouble(data.getColumnIndex(PC.DB_DIST_ELAP));
        }
        data.close();
        db.close();
        return d;
		
	}
	public static double getSpeedAvg(Context ctx, int runid){
		if(runid == 0) return 0;

		double d = 0;
		double t = 0;
		
		DbAdapter db = new DbAdapter(ctx);
		db.open();
        Cursor data = db.getLastDatapoints(runid,1);
        if(data.moveToFirst()){
        	d = data.getDouble(data.getColumnIndex(PC.DB_DIST_ELAP));
        	t = data.getLong(data.getColumnIndex(PC.DB_TIME_ELAP))/1000;
        }
        data.close();
        db.close();
        if(t<1)	return 0;
        else 	return d/t;
		
	}

	public static double getAltitudeAvg(Activity ctx, int runid) {
		if(runid == 0) return 0;

		double a = 0;
		double c = 0;
		
		DbAdapter db = new DbAdapter(ctx);
		db.open();
        Cursor data = db.getRun(runid);
        while(data.moveToNext()){
        	a = a + data.getDouble(data.getColumnIndex(PC.DB_ALTITUDE));
        	c = c + 1;
        }
        data.close();
        db.close();
        if(c<1)	return 0;
        else 	return a/c;
	}

	
	public static double getSpeedSpl(Context ctx, int runid){
		if(runid == 0) return 0;
		
		double distatnce = 0;
		double splitDist = 0;
		long   splitTime = 0;
		long   curreTime = 0;
		
		DbAdapter db = new DbAdapter(ctx);
		db.open();
		
        Cursor spli	  = db.getSplits(runid);
		Cursor data   = db.getLastDatapoints(runid,1);

        if(data.moveToFirst()){ distatnce = data.getDouble(data.getColumnIndex(PC.DB_DIST_ELAP)); curreTime = data.getLong(data.getColumnIndex(PC.DB_TIME_ELAP));}
        if(spli.moveToLast()) { splitDist = spli.getDouble(spli.getColumnIndex(PC.DB_DIST_ELAP)); splitTime = spli.getLong(spli.getColumnIndex(PC.DB_TIME_ELAP));}
        
        data.close();
        spli.close();
        db.close();
        if(curreTime-splitTime<1)	return 0;
        else						return (distatnce-splitDist)/(curreTime-splitTime);
	}
	public static int getSplitNum(Context ctx, int runid){
		if(runid == 0) return 0;
		
		int s = 0;
		
		DbAdapter db = new DbAdapter(ctx);
		db.open();
        Cursor spli	  = db.getSplits(runid);

        if(spli.moveToLast()) 
        	s = spli.getInt(spli.getColumnIndex(PC.DB_SPLIT_NO));
        
        spli.close();
        db.close();
        
        return s;
	}
	public static String getText(Context ctx,String t){
		String o = t;
		if( t== null) return "";




		if( t.equals(CELLS+CELL_TYPE_TIME 					)) o = ctx.getString(R.string.time);
		if( t.equals(CELLS+CELL_TYPE_DISTANCE 				)) o = ctx.getString(R.string.distance);
		if( t.equals(CELLS+CELL_TYPE_SPEEDGPS 				)) o = ctx.getString(R.string.speedGPS);
		if( t.equals(CELLS+CELL_TYPE_SPEEDCAL 				)) o = ctx.getString(R.string.speedCal);
		if( t.equals(CELLS+CELL_TYPE_SPEEDAVG 				)) o = ctx.getString(R.string.speedAvg);
		if( t.equals(CELLS+CELL_TYPE_LOCATION 				)) o = ctx.getString(R.string.location);
		if( t.equals(CELLS+CELL_TYPE_ACCURACY 				)) o = ctx.getString(R.string.accuracy);
		if( t.equals(CELLS+CELL_TYPE_ALTITUDE 				)) o = ctx.getString(R.string.altitude);
		if( t.equals(CELLS+CELL_TYPE_MAP 	    			)) o = ctx.getString(R.string.map);
		if( t.equals(CELLS+CELL_TYPE_BLANK 					)) o = ctx.getString(R.string.blank);
		if( t.equals(CELLS+CELL_TYPE_SPLITS   		  		)) o = ctx.getString(R.string.splits);
		if( t.equals(CELLS+CELL_TYPE_SPLIT_NO   		  	)) o = ctx.getString(R.string.splitno);
		if( t.equals(CELLS+CELL_TYPE_SPLIT_SPEED 			)) o = ctx.getString(R.string.splitspeed);
		if( t.equals(CELLS+CELL_TYPE_SPLIT_DISTANCE			)) o = ctx.getString(R.string.splitdistance);
		if( t.equals(CELLS+CELL_TYPE_SPLIT_TIME 			)) o = ctx.getString(R.string.splittime);
		if( t.equals(CELLS+CELL_TYPE_SPLIT_AUTOSPLIT 		)) o = ctx.getString(R.string.splitauto);
		if( t.equals(CELLS+CELL_TYPE_SPLIT_DIST_LEFT		)) o = ctx.getString(R.string.splitdistleft);
		if( t.equals(CELLS+CELL_TYPE_SPLIT_TIME_LEFT		)) o = ctx.getString(R.string.splittimeleft);

		if( t.equals(CELLS+CELL_TYPE_GRAPH_DISTANCE			)) o = ctx.getString(R.string.graphdist);
		if( t.equals(CELLS+CELL_TYPE_GRAPH_ACCURACY			)) o = ctx.getString(R.string.graphacu);
		if( t.equals(CELLS+CELL_TYPE_GRAPH_ALTITUDE			)) o = ctx.getString(R.string.graphalt);
		if( t.equals(CELLS+CELL_TYPE_GRAPH_SPEEDGPS			)) o = ctx.getString(R.string.graphspeedgps);
		if( t.equals(CELLS+CELL_TYPE_GRAPH_SPEEDAVG			)) o = ctx.getString(R.string.graphspeedavg);
		if( t.equals(CELLS+CELL_TYPE_GRAPH_SPEEDCAL			)) o = ctx.getString(R.string.graphspeedcal);

		if( t.equals(UNITS+0 								)) o = "";
		if( t.equals(UNITS+UNITS_MIN 						)) o = ctx.getString(R.string.min);
		if( t.equals(UNITS+UNITS_MINMILE 					)) o = ctx.getString(R.string.minMile);
		if( t.equals(UNITS+UNITS_MINKILO 					)) o = ctx.getString(R.string.minKilo);
		if( t.equals(UNITS+UNITS_MPH 						)) o = ctx.getString(R.string.mph);
		if( t.equals(UNITS+UNITS_KPH 						)) o = ctx.getString(R.string.kph);
		if( t.equals(UNITS+UNITS_KNOTS 						)) o = ctx.getString(R.string.knots);
		if( t.equals(UNITS+UNITS_FPS 						)) o = ctx.getString(R.string.fps);
		if( t.equals(UNITS+UNITS_MPS						)) o = ctx.getString(R.string.mps);
		if( t.equals(UNITS+UNITS_NODURATION						)) o = ctx.getString(R.string.noduration);

		if( t.equals(UNITS+UNITS_FEET 						)) o = ctx.getString(R.string.feet);
		if( t.equals(UNITS+UNITS_YARDS 						)) o = ctx.getString(R.string.yards);
		if( t.equals(UNITS+UNITS_METERS 					)) o = ctx.getString(R.string.meters);
		if( t.equals(UNITS+UNITS_MILES 						)) o = ctx.getString(R.string.miles);
		if( t.equals(UNITS+UNITS_KILOMETERS					)) o = ctx.getString(R.string.kilometers);

		if( t.equals(MAP_MODES+MAP_MODE_AUTO				)) o = ctx.getString(R.string.mapmodeauto);
		if( t.equals(MAP_MODES+MAP_MODE_FIXED				)) o = ctx.getString(R.string.mapmodefixed);
		if( t.equals(MAP_MODES+MAP_MODE_ZOOM				)) o = ctx.getString(R.string.mapmodezoom);

		if( t.equals(MAP_TYPES+MAP_TYPE_NORMAL				)) o = ctx.getString(R.string.maptypenormal);
		if( t.equals(MAP_TYPES+MAP_TYPE_SATALITE			)) o = ctx.getString(R.string.maptypesatalite);

		if( t.equals(SREEN_ROTATIONS+ROTATE_AUTO			)) o = ctx.getString(R.string.rotateauto);
		if( t.equals(SREEN_ROTATIONS+ROTATE_PORTRAIT		)) o = ctx.getString(R.string.rotateport);
		if( t.equals(SREEN_ROTATIONS+ROTATE_LANDSCAPE		)) o = ctx.getString(R.string.rotateland);

		if( t.equals(LANDSCAPE_MODE+LANDSCAPE_ROTATE		)) o = ctx.getString(R.string.landrotate);
		if( t.equals(LANDSCAPE_MODE+LANDSCAPE_DOUBLE		)) o = ctx.getString(R.string.landdouble);
		if( t.equals(LANDSCAPE_MODE+LANDSCAPE_STRETCH		)) o = ctx.getString(R.string.landstretch);

		if( t.equals(FINISH_MODE+END_KEEP_GOING				)) o = ctx.getString(R.string.endkeepgoing);
		if( t.equals(FINISH_MODE+END_PAUSE					)) o = ctx.getString(R.string.endpause);
		if( t.equals(FINISH_MODE+END_MOVE_ON_WORKOUT		)) o = ctx.getString(R.string.endmoveonwork);
		if( t.equals(FINISH_MODE+END_MOVE_ON_COOLDOWN		)) o = ctx.getString(R.string.endmoveoncool);
		
		

		
		if( t.equals("settings_numpages"					)) o = ctx.getString(R.string.numPages);
		if( t.equals("settings_defaultpage"					)) o = ctx.getString(R.string.defaultPage);
		if( t.equals("settings_autosplits"					)) o = ctx.getString(R.string.autosplits);
		if( t.equals("settings_manualsplits"				)) o = ctx.getString(R.string.manualsplits);
		if( t.equals("settings_lockscreens"					)) o = ctx.getString(R.string.configPagesWhileRunning);
		if( t.equals("settings_lockscreens_desc"			)) o = ctx.getString(R.string.configPagesWhileRunningMore);
		if( t.equals("settings_voicealerts"					)) o = ctx.getString(R.string.voicealerts);
		if( t.equals("settings_profile"						)) o = ctx.getString(R.string.settings_profile);
		if( t.equals("settings_delete_profile"				)) o = ctx.getString(R.string.settings_delete_profile);
		if( t.equals("settings_delete_profile_more"			)) o = ctx.getString(R.string.settings_delete_profile_more);
		
		if( t.equals("settings_tts"							)) o = ctx.getString(R.string.tts);

		if( t.equals("settings_va_start"					)) o = ctx.getString(R.string.va_start);
		if( t.equals("settings_va_stop"						)) o = ctx.getString(R.string.va_stop);
		if( t.equals("settings_va_resume"					)) o = ctx.getString(R.string.va_resume);
		if( t.equals("settings_va_pause"					)) o = ctx.getString(R.string.va_pause);
		if( t.equals("settings_va_split"					)) o = ctx.getString(R.string.va_split);
		if( t.equals("settings_va_speed_l"					)) o = ctx.getString(R.string.va_speed_l);
		if( t.equals("settings_va_speed_h"					)) o = ctx.getString(R.string.va_speed_h);
		if( t.equals("settings_va_cooldown_speed_h"			)) o = ctx.getString(R.string.va_cooldown_speed_h);
		if( t.equals("settings_va_cooldown_speed_l"			)) o = ctx.getString(R.string.va_cooldown_speed_l);
		if( t.equals("settings_va_warmup_speed_h"			)) o = ctx.getString(R.string.va_warmup_speed_h);
		if( t.equals("settings_va_warmup_speed_l"			)) o = ctx.getString(R.string.va_warmup_speed_l);
		
		if( t.equals("settings_mapMode"						)) o = ctx.getString(R.string.settings_mapMode);
		if( t.equals("settings_mapType"						)) o = ctx.getString(R.string.settings_mapType);
		if( t.equals("settings_rotate"						)) o = ctx.getString(R.string.settings_rotate);
		if( t.equals("settings_landscape"					)) o = ctx.getString(R.string.settings_landscape);

		if( t.equals("settings_duration_workout"			)) o = ctx.getString(R.string.settings_duration_workout);
		
		if( t.equals("settings_duration_warmup"				)) o = ctx.getString(R.string.settings_duration_warmup);
		if( t.equals("settings_duration_warmup_include"		)) o = ctx.getString(R.string.settings_duration_warmup_include);
		if( t.equals("settings_duration_warmup_splits"		)) o = ctx.getString(R.string.settings_duration_warmup_splits);
		if( t.equals("settings_duration_warmup_include_more")) o = ctx.getString(R.string.settings_duration_warmup_include_more);

		if( t.equals("settings_duration_cooldown"			)) o = ctx.getString(R.string.settings_duration_cooldown);
		if( t.equals("settings_duration_cooldown_include"	)) o = ctx.getString(R.string.settings_duration_cooldown_include);
		if( t.equals("settings_duration_cooldown_splits"	)) o = ctx.getString(R.string.settings_duration_cooldown_splits);
		if( t.equals("settings_duration_cooldown_include_more"))o= ctx.getString(R.string.settings_duration_cooldown_include_more);

		if( t.equals("settings_warmup_end_action"))			o= ctx.getString(R.string.settings_warmup_end_action);
		if( t.equals("settings_workout_end_action"))		o= ctx.getString(R.string.settings_workout_end_action);
		if( t.equals("settings_cooldown_end_action"))		o= ctx.getString(R.string.settings_cooldown_end_action);
		
		if( t.equals("settings_warmup_end_action_more"))			o= ctx.getString(R.string.settings_warmup_end_action_more);
		if( t.equals("settings_workout_end_action_more"))		o= ctx.getString(R.string.settings_workout_end_action_more);
		if( t.equals("settings_cooldown_end_action_more"))		o= ctx.getString(R.string.settings_cooldown_end_action_more);

		if( t.equals("settings_speed_freq"))		o= ctx.getString(R.string.settings_speed_freq);
		
		
		
		
		if( t.equals("settings_plan"						)) o = ctx.getString(R.string.settings_plan);
		
		if( t.equals("confirmDelete"						)) o = ctx.getString(R.string.confirmDelete);
		if( t.equals("confirmDeleteButton"					)) o = ctx.getString(R.string.confirmDeleteButton);
		if( t.equals("confirmDeleteCancel"					)) o = ctx.getString(R.string.confirmDeleteCancel);
		
		if( t.equals("typedialog"							)) o = ctx.getString(R.string.typedialog);
		if( t.equals("unitdialog"							)) o = ctx.getString(R.string.unitdialog);

		return o;
	}
	public static void settings_text(final Context ctx,ViewGroup view, int id, String label, String description, String setting,OnClickListener l,OnClickListener more){

		View row = settings_inflate_row(ctx, view, id);

		final TextView s = (TextView) row.findViewById(R.id.value);

		if(label!=null) 		((TextView)	row.findViewById(R.id.label)).setText(label);
		if(description!=null)	((TextView)	row.findViewById(R.id.description)).setText(description);
		else 								row.findViewById(R.id.description).setVisibility(View.GONE);
		if(more==null)						row.findViewById(R.id.button).setVisibility(View.INVISIBLE);
		else								row.findViewById(R.id.button).setOnClickListener(more);

		s.setVisibility(View.VISIBLE);
		if(setting!=null) s.setText(setting);
		row.setOnClickListener(l);

	}


	public static int SETTINGS_SWITCH   = 1;
	public static int SETTINGS_CHECKBOX = 2;
	public static void settings_switch(final Context ctx,final int profile,ViewGroup view, int id, int type, String label, String description, boolean setting,final String pref,OnClickListener more){
		OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
			DbAdapter db = new DbAdapter(ctx.getApplicationContext());
			db.open();
			db.setPref(profile,pref, isChecked?1:0);
			db.close();
			
		}};
		settings_switch(ctx, view,  id, type, label,  description,  setting,  onCheckedChangeListener, more);
	}
	public static View settings_inflate_row(Context ctx, ViewGroup view, int id){
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View row = inflater.inflate(R.layout.settings_row, view,false);
		
		view.addView(row);
		row = row.findViewById(R.id.settingsRow);
		
		if(id>0)
			row.setId(id);
		return row;
	}
	public static void settings_switch(final Context ctx,ViewGroup view, int id, int type, String label, String description, boolean setting,final OnCheckedChangeListener onCheckedChangeListener,OnClickListener more){

		View row = settings_inflate_row(ctx, view, id);

		final CompoundButton s;
		if(type==SETTINGS_SWITCH) s = (CompoundButton) row.findViewById(R.id.switch1);
		else 		s = (CompoundButton) row.findViewById(R.id.checkbox1);

		if(label!=null) 		((TextView)	row.findViewById(R.id.label)).setText(label);
		if(description!=null)	((TextView)	row.findViewById(R.id.description)).setText(description);
		else 								row.findViewById(R.id.description).setVisibility(View.GONE);
		if(more==null)						row.findViewById(R.id.button).setVisibility(View.INVISIBLE);
		else								row.findViewById(R.id.button).setOnClickListener(more);
		s.setSaveEnabled(false);
		s.setSaveFromParentEnabled(false);
		s.setVisibility(View.VISIBLE);
		s.setChecked(setting);
		s.setOnCheckedChangeListener(onCheckedChangeListener);
		row.setOnClickListener(new OnClickListener(){ @Override public void onClick(View v) {
			s.setChecked(!s.isChecked());
		}});
	}

	public static void settings_select_cell(final Context ctx,final Callback callback){

		settings_select_from_list(ctx,getText(ctx,"typedialog"),string_list(ctx,PC.CELLS,cell_types),cell_types,new Callback(){
			@Override
			public boolean handleMessage(Message msg) {
				final int type = msg.arg1;

				Callback c = new Callback(){
					@Override
					public boolean handleMessage(Message msg) {
						Message message = new Message();
						message.arg1 = type;
						message.arg2 = msg.arg1;
						callback.handleMessage(message);
						return true;
					}};

					if(			type == CELL_TYPE_BLANK
							||	type == CELL_TYPE_TIME
							||	type == CELL_TYPE_LOCATION
							||	type == CELL_TYPE_MAP
							||	type == CELL_TYPE_SPLIT_TIME
							||	type == CELL_TYPE_SPLITS
							||	type == CELL_TYPE_SPLIT_AUTOSPLIT
							||  type == CELL_TYPE_SPLIT_NO){

						c.handleMessage(new Message());
					}
					else if(	type == CELL_TYPE_DISTANCE
							||	type == CELL_TYPE_GRAPH_DISTANCE
							||	type == CELL_TYPE_SPLIT_DISTANCE
							||	type == CELL_TYPE_SPLIT_DIST_LEFT ){
						settings_select_from_list(ctx,getText(ctx,"unitdialog"),string_list(ctx,PC.UNITS,units_distance_full),units_distance_full,c);
					}
					else if(	type == CELL_TYPE_ACCURACY
							||	type == CELL_TYPE_ALTITUDE
							||	type == CELL_TYPE_GRAPH_ACCURACY
							||	type == CELL_TYPE_GRAPH_ALTITUDE){
						settings_select_from_list(ctx,getText(ctx,"unitdialog"),string_list(ctx,PC.UNITS,units_distance_small),units_distance_small,c);
					}
					else if(	type == CELL_TYPE_SPEEDGPS
							||	type == CELL_TYPE_SPEEDCAL
							||	type == CELL_TYPE_SPEEDAVG
							||	type == CELL_TYPE_GRAPH_SPEEDGPS
							||	type == CELL_TYPE_GRAPH_SPEEDAVG
							||	type == CELL_TYPE_GRAPH_SPEEDCAL
							||	type == CELL_TYPE_SPLIT_SPEED) {
						settings_select_from_list(ctx,getText(ctx,"unitdialog"),string_list(ctx,PC.UNITS,units_speed),units_speed,c);
					}

					return true;
			}});
	}
	public static void settings_select_from_list(Context ctx,String title,String[] s,final int[] l, final Callback c) {
		AlertDialog.Builder builder3 = new AlertDialog.Builder(ctx);
		builder3.setTitle(title).setItems(s, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Message msg = new Message();
				msg.arg1 = l[which];
				c.handleMessage(msg);
			}});
		builder3.show();
	}
	public static String[] string_list(Context ctx,String t,int[] l){
		String[] o = new String[l.length];
		int i = 0;
		while(i<l.length){
			o[i] = getText(ctx, t+l[i]);
			i++;
		}
		return o;
	}

	public static void settings_number_picker(final Context ctx, int id, String title, int min, int max, int cur){

		Bundle b = new Bundle();
		b.putString("title",title);
		b.putInt("min", min);
		b.putInt("max", max);
		b.putInt("int1", cur);
		b.putInt("type", 1);
		b.putInt("id", id);

		SettingsPicker newFragment = SettingsPicker.newInstance(b);
		newFragment.show(((Activity) ctx).getFragmentManager(),null);
	}
	public static void settings_number_unit_picker(final Context ctx, int id, String title, int int1, int int2, int i, int[] unitsSplit){

		Bundle b = new Bundle();
		b.putString("title",title);
		b.putInt("int1", int1);
		b.putInt("int2", int2);
		b.putInt("units",i);
		b.putIntArray("unit_list", unitsSplit);
		b.putInt("type", 2);
		b.putInt("id", id);

		SettingsPicker newFragment = SettingsPicker.newInstance(b);
		newFragment.show(((Activity) ctx).getFragmentManager(),null);
	}
	public static void settings_datatype_number_unit_picker(final Context ctx, int id, String title, int j, int[] cellSpeed, int int1, int int2, int i, int[] unitsSpeed){

		Bundle b = new Bundle();
		b.putString("title",title);
		b.putInt("int1", int1);
		b.putInt("int2", int2);
		b.putInt("datatype",j);
		b.putInt("units",i);
		b.putIntArray("datatype_list", 	cellSpeed);
		b.putIntArray("unit_list", 		unitsSpeed);
		b.putInt("type", 3);
		b.putInt("id", id);

		SettingsPicker newFragment = SettingsPicker.newInstance(b);
		newFragment.show(((Activity) ctx).getFragmentManager(),null);
	}
	public static void settings_edit_text(Context ctx, int id, String title, String value){
		

		Bundle b = new Bundle();
		b.putString("title",title);
		b.putString("value", value);
		b.putInt("id", id);
		b.putInt("type", 4);
		
		SettingsPicker newFragment = SettingsPicker.newInstance(b);
		newFragment.show(((Activity) ctx).getFragmentManager(),null);
		
	}
	
	public static class Graph extends View{
		private int runid = 0;
		private int weight = 4;
		private int labelsize = 12;
		private int titlesize = 16;
		private int datatype;
		private int units;
		private double xmax = 0;
		private double ymax = 0;
		private double ymin = 0;
		private Paint p;
		private double[] xvals;
		private double[] yvals;
		private Path track;
		Rect b;
		private Path bkg;
		private Paint bp;
		private Paint title;
		
		private Context ctx;
		
		double h = 0;
		double w = 0;
		double xpad = 0;
		double ypad = 0;
		
		public void setRunId(int r){
			runid = r;
			getData();
		}
		public void setDataType(int d){
			datatype = d;
			getData();
		}
		public void setUnits(int d){
			units = d;
			getData();
		}
		private void getData(){
			if(runid == 0 || datatype == 0 || units == 0)
				return;
			
			DbAdapter db = new DbAdapter(ctx);
			db.open();
			Cursor run = db.getRun(runid);
			xvals = new double[run.getCount()];
			yvals = new double[run.getCount()];
			ymax = -100000;
			xmax = 0;
			int i = 0;
			double start = 0;
			while(run.moveToNext()){
				if(i==0) start = ((double)run.getLong(run.getColumnIndex(PC.DB_TIMESTAMP)))/1000;
				xvals[i] = ((double)run.getLong(run.getColumnIndex(PC.DB_TIMESTAMP)))/1000 - start;
				xmax = xvals[i];
				if(datatype == CELL_TYPE_DISTANCE)		yvals[i] = run.getFloat(run.getColumnIndex(PC.DB_DIST_ELAP));
				else if(datatype == CELL_TYPE_ACCURACY) yvals[i] = run.getFloat(run.getColumnIndex(PC.DB_ACCURACY));
				else if(datatype == CELL_TYPE_ALTITUDE) yvals[i] = run.getFloat(run.getColumnIndex(PC.DB_ALTITUDE));
				else if(datatype == CELL_TYPE_SPEEDGPS) yvals[i] = run.getFloat(run.getColumnIndex(PC.DB_SPEED_GPS));
				else if(datatype == CELL_TYPE_SPEEDAVG) yvals[i] = xvals[i]<1?0:run.getFloat(run.getColumnIndex(PC.DB_DIST_ELAP))/xvals[i];
				else if(datatype == CELL_TYPE_SPEEDCAL) yvals[i] = run.getFloat(run.getColumnIndex(PC.DB_SPEED_CAL));
				
				if(yvals[i]<ymin) ymin = yvals[i];
				if(yvals[i]>ymax) ymax = yvals[i];
				i++;
			}
			run.close();
			db.close();
			
			if(xmax==0 || ymax==0 || h==0 || w == 0)
				return;
			
			if(xmax<60)xmax=60;
			
			double xr = (double) ((w-2*xpad)/(xmax));
			double yr = (double) ((h-2*ypad)/(ymax-ymin));

			double x;
			double y;
			double ox = 0;
			double oy = 0;
			
			track.rewind();
			track.moveTo((float) ((xvals[0]*xr)+xpad),(float) (h - ((yvals[0]-ymin)*yr) - ypad));
			
			for( i = 0;i<yvals.length;i++){
				
				x =       ((xvals[i])*xr) + xpad;
				y = ( h - ((yvals[i]-ymin)*yr) - ypad);
				
				if(Math.abs(ox-x) < 3 || Math.abs(oy-y) < 3)
					continue;
				
				track.lineTo((float)x,(float)y);
				track.moveTo((float)x,(float)y);
				ox = x;
				oy = y;
			}
			this.invalidate();
		}
		private void initPaints(){
			
			float sp = ctx.getResources().getDisplayMetrics().scaledDensity;
			
			track = new Path();
			p = new Paint();
			p.setDither(true);
			p.setColor(ctx.getResources().getColor(R.color.tracks));
			p.setStyle(Paint.Style.FILL_AND_STROKE);
			p.setStrokeJoin(Paint.Join.ROUND);
			p.setStrokeCap(Paint.Cap.ROUND);
			p.setAntiAlias(true);
			p.setStrokeWidth(weight);
			

			bkg = new Path();
			bp = new Paint();
			bp.setDither(true);
			bp.setColor(ctx.getResources().getColor(R.color.cell_border));
			bp.setStyle(Paint.Style.FILL_AND_STROKE);
			bp.setStrokeJoin(Paint.Join.ROUND);
			bp.setStrokeCap(Paint.Cap.ROUND);
			bp.setAntiAlias(true);
			bp.setTextSize(labelsize*sp);
			bp.setStrokeWidth(1);
			

			title = new Paint();
			title.setDither(true);
			title.setColor(ctx.getResources().getColor(R.color.cell_border));
			title.setStyle(Paint.Style.FILL_AND_STROKE);
			title.setStrokeJoin(Paint.Join.ROUND);
			title.setStrokeCap(Paint.Cap.ROUND);
			title.setAntiAlias(true);
			title.setTextSize(titlesize*sp);
		}
		public Graph(Context c) {
			super(c);
			ctx = c;
			b = new Rect();
			initPaints();
		}
		public Graph(Context c,int r, int t, int u){
			super(c);
			runid = r;
			datatype = t;
			units = u;
			ctx = c;
			b = new Rect();
			initPaints();
			getData();
		}

		@Override
		protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
			super.onSizeChanged(xNew, yNew, xOld, yOld);

			w = xNew;
			h = yNew;

			xpad = (float) (xNew*0.1);
			ypad = (float) (yNew*0.1);
			getData();
		}
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
			
			int vmax = (int) Math.ceil(PC.convertUnits(ymax, units));
			int vmin = (int) Math.floor(PC.convertUnits(ymin, units));
			
			double vr = ((h-2*ypad)/(vmax-vmin));
			double v;
			int step = (int) Math.round((vmax-vmin)/10);
			if(step==0) step = 1;
			double tw;
			bkg.rewind();
			for(int i=vmin;i<vmax+step;i=i+step){
				
				bp.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), b);
				v = ( h - ((i-vmin)*vr) - ypad);

				if(v<ypad/2)
					continue;
				
				tw = (xpad/2)-(b.width()/2);
				canvas.drawText(String.valueOf(i),(float) (tw<0?0:tw),(float) (v+(b.height()/2)), bp);
				bkg.moveTo((float)(xpad<b.width()?b.width():xpad),(float) v);
				bkg.lineTo((float)(w-xpad),(float) v);
			}

			double mmax = xmax/60;
			
			String s = "";
			
			s = String.valueOf((int)Math.floor(mmax))+":00";
			bp.getTextBounds(s, 0, s.length(), b);
			
			
			float xr = (float) ((w-2*xpad)/mmax);
			step = (int) Math.round(mmax/((w-(2*ypad))/(b.width()*2)));
			
			if(step<1)
				step = 1;
			for(int i=0;i<mmax+step;i=i+step){
				
				v =       (i*xr) + xpad;
				if(v>w-xpad/2)
					continue;
				s = String.valueOf(i)+":00";
				bp.getTextBounds(s, 0, s.length(), b);
				tw = (ypad/2)-(b.height()/2);
				
				canvas.drawText(s,(float)(v-(b.width()/2)),(float)((tw<0)?h-b.height():h-tw), bp);
			}
			canvas.drawPath(bkg, bp);
			canvas.drawPath(track, p);
			
			s = PC.getText(ctx, PC.CELLS+datatype);
			bp.getTextBounds(s, 0, s.length(), b);
			
			double ty =ypad/2-b.height()/2;
			canvas.drawText(PC.getText(ctx, PC.CELLS+datatype),(float)(w/2-b.width()/2),(float)(ty<5+b.height()?b.height()+5:ty), title);
		}
		
	}
	public static class TimeView extends Chronometer{
		Paint p;
		Path track;
		int align;
		int height;
		int width;
		Typeface typeface;
		public static final int CENTER	= 0;
		public static final int TOP 	= 1;
		public static final int BOTTOM 	= 2;
		public TimeView(Context context) {
			super(context);
			
			align = 0;
			typeface = Typeface.MONOSPACE;
			track = new Path();
			p = new Paint();
			p.setDither(false);
			p.setColor(context.getResources().getColor(R.color.data_color));
			p.setTextAlign(Paint.Align.CENTER);
			p.setAntiAlias(true);
			p.setTypeface(typeface);
			p.setTextSize(50);
		}
		private void calcSize(){
			String data = (String) this.getText();
			if(data==null || (height==0 && width==0))
				return;
			data = "."+data+".";
			int h = 0;
			Rect b;
			float s = p.getTextSize();

			b = new Rect();
			p.getTextBounds(data, 0, data.length(), b);
			
			while(b.height()<height*.9 && b.width()<width*0.9){
				s = s+1;
				p.setTextSize(s);
				b = new Rect();
				p.getTextBounds(data, 0, data.length(), b);
			}
			
			while(s>10 && (b.height()>height*.9 || b.width()>width*0.9)){
				s = s-1;
				p.setTextSize(s);
				b = new Rect();
				p.getTextBounds(data, 0, data.length(), b);
			}
			
			h = height/2+b.height()/2;
			
			track.rewind();
			track.moveTo(0, 	h);
			track.lineTo(width, h);
			
			this.invalidate();
		}
		@Override
		protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
			super.onSizeChanged(xNew, yNew, xOld, yOld);

			width = xNew;
			height = yNew;
			calcSize();
			
		}
		@Override
		protected void onDraw(Canvas canvas){
			//super.onDraw(canvas);
			
			calcSize();
			
			canvas.drawTextOnPath((String) this.getText(), track, 0, 0, p);
		}
	}
	
	public static class DataView extends View{
		Paint p;
		String data;
		Path track;
		int align;
		int height;
		int width;
		Typeface typeface;
		public static final int CENTER	= 0;
		public static final int TOP 	= 1;
		public static final int BOTTOM 	= 2;
		
		public DataView(Context context) {
			super(context);
			align = 0;
			typeface = Typeface.SANS_SERIF;
			track = new Path();
			p = new Paint();
			p.setDither(false);
			p.setColor(context.getResources().getColor(R.color.data_color));
			p.setTextAlign(Paint.Align.CENTER);
			p.setAntiAlias(true);
			p.setTypeface(typeface);
			p.setTextSize(50);
		}
		public void setTypeface(Typeface t){
			typeface = t;
			p.setTypeface(typeface);
			calcSize();
		}
		public void setText(String d){
			data = d;
			calcSize();
		}
		public void setAlign(int a){
			align = a;
			calcSize();
		}
		private void calcSize(){
			if(data==null || (height==0 && width==0))
				return;
			String d = "."+data+".";
			
			int h = 0;
			Rect b;
			
			float s = p.getTextSize();

			b = new Rect();
			p.getTextBounds(d, 0, d.length(), b);
			
			while(b.height()<height*.9 && b.width()<width*0.9){
				s = s+1;
				p.setTextSize(s);
				b = new Rect();
				p.getTextBounds(d, 0, d.length(), b);
			}
			
			while(s>10 && (b.height()>height*.9 || b.width()>width*0.9)){
				s = s-1;
				p.setTextSize(s);
				b = new Rect();
				p.getTextBounds(d, 0, d.length(), b);
			}
			
			
			switch(align){
			case CENTER:
				h = height/2+b.height()/2;
				break;
			case TOP:
				h = (int) (b.height()+0.1*height);
				break;
			case BOTTOM:
				h = (int) (height*.9);
				break;
			}
			track.rewind();
			track.moveTo(0, 	h);
			track.lineTo(width, h);
			
			this.invalidate();
		}
		@Override
		protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
			super.onSizeChanged(xNew, yNew, xOld, yOld);

			width = xNew;
			height = yNew;
			calcSize();
			
		}
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);

			if(data==null)
				return;
			
			canvas.drawTextOnPath(data, track, 0, 0, p);
		}
	}
	
	
	public static class MyOverlay extends Overlay{

		private int id;
		private int splitSize 		= 14;
		private int resumeSize 		= 7;
		private int stopSize  		= 10;
		private int startSize 		= 13;
		private int trackweight 	= 5;
		Paint stopPaint;
		Paint startPaint;
		Paint splitPaint;
		Paint splitNoPaint;
		Paint trackPaint;
		Paint splitBgPaint;
		Paint splitTimePaint;
		public MyOverlay(Context ctx,int i){
			this.id = i;
			
			setupPaints(ctx);
			
		}
		private void setupPaints(Context ctx){
			stopPaint = new Paint();
			stopPaint.setDither(true);
			stopPaint.setColor(ctx.getResources().getColor(R.color.button_stop));
			stopPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			stopPaint.setAntiAlias(true);
			
			startPaint = new Paint();
			startPaint.setDither(true);
			startPaint.setColor(ctx.getResources().getColor(R.color.button_start));
			startPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			startPaint.setAntiAlias(true);
			
			splitPaint = new Paint();
			splitPaint.setDither(true);
			splitPaint.setColor(ctx.getResources().getColor(R.color.button_searching));
			splitPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			splitPaint.setAntiAlias(true);
			
			splitNoPaint = new Paint();
			splitNoPaint.setDither(false);
			splitNoPaint.setColor(ctx.getResources().getColor(R.color.splitNoText));
			splitNoPaint.setTextAlign(Paint.Align.CENTER);
			splitNoPaint.setAntiAlias(true);
			
			
			trackPaint = new Paint();
			trackPaint.setDither(true);
			trackPaint.setColor(ctx.getResources().getColor(R.color.tracks));
			trackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			trackPaint.setStrokeJoin(Paint.Join.ROUND);
			trackPaint.setStrokeCap(Paint.Cap.ROUND);
			trackPaint.setAntiAlias(true);
			trackPaint.setStrokeWidth(trackweight);

			splitBgPaint = new Paint();
			splitBgPaint.setDither(true);
			splitBgPaint.setColor(ctx.getResources().getColor(R.color.splitBg));
			splitBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			splitBgPaint.setStrokeJoin(Paint.Join.ROUND);
			splitBgPaint.setStrokeCap(Paint.Cap.BUTT);
			splitBgPaint.setStrokeWidth(splitSize*2);
			splitBgPaint.setAntiAlias(true);
			
			splitTimePaint = new Paint();
			splitTimePaint.setDither(false);
			splitTimePaint.setColor(ctx.getResources().getColor(R.color.splitTimeText));
			splitTimePaint.setTextSize(splitSize+8);
			splitTimePaint.setTextAlign(Paint.Align.CENTER);
			splitTimePaint.setAntiAlias(true);
			
			
		}

		@Override
		public void draw(Canvas canvas, MapView mapv, boolean shadow) {
			super.draw(canvas, mapv, shadow);

			if (shadow == true)
				return;
			
			
			Context ctx = mapv.getContext();
			
			Projection projection = mapv.getProjection();
			Path track = new Path();

			Point p1 = new Point();
			Point p2 = new Point();
			int x1;
			int y1;
			double lon = 0;
			double lat = 0;
			String splitTime = "";
			String splitNo = "";
			GeoPoint gp1 = null;
			GeoPoint gp2 = null;

			DbAdapter db = new DbAdapter(ctx);
			db.open();


			Cursor run = db.getRun(id);
			if(run.moveToFirst()){
				lon = run.getDouble(run.getColumnIndex(PC.DB_LONGITUDE));
				lat = run.getDouble(run.getColumnIndex(PC.DB_LATITUDE));
				gp1 = new GeoPoint((int)(lat*1000000), (int)(lon*1000000));
				projection.toPixels(gp1, p1);

				x1=p1.x;
				y1=p1.y;
				track.moveTo(x1, y1);
				
				while(run.moveToNext()){

					lon = run.getDouble(run.getColumnIndex(PC.DB_LONGITUDE));
					lat = run.getDouble(run.getColumnIndex(PC.DB_LATITUDE));
					gp2 = new GeoPoint((int)(lat*1000000), (int)(lon*1000000));
					projection.toPixels(gp2, p2);

					if(		run.getInt(run.getColumnIndex(PC.DB_MODE))==PC.SIGNAL_RESUME_WARMUP  || 
							run.getInt(run.getColumnIndex(PC.DB_MODE))==PC.SIGNAL_RESUME_WORKOUT || 
							run.getInt(run.getColumnIndex(PC.DB_MODE))==PC.SIGNAL_RESUME_COOLDOWN){

					}else if(Math.abs(x1-p2.x)+Math.abs(y1-p2.y)<15){
						continue;
					}else{
						track.lineTo(p2.x, p2.y);
					}
					canvas.drawPath(track, trackPaint);
					track.rewind();
					track.moveTo(p2.x, p2.y);
					x1=p2.x;
					y1=p2.y;
				}
			}
			run.close();
			canvas.drawPath(track, trackPaint);
			run = db.getEndpoints(id);
			canvas.drawCircle( p1.x, p1.y, startSize, startPaint);
			while(run.moveToNext()){


				lon = run.getDouble(run.getColumnIndex(PC.DB_LONGITUDE));
				lat = run.getDouble(run.getColumnIndex(PC.DB_LATITUDE));
				gp2 = new GeoPoint((int)(lat*1000000), (int)(lon*1000000));
				projection.toPixels(gp2, p2);
				if(		run.getInt(run.getColumnIndex(PC.DB_MODE))==PC.SIGNAL_RESUME_WARMUP  || 
						run.getInt(run.getColumnIndex(PC.DB_MODE))==PC.SIGNAL_RESUME_WORKOUT || 
						run.getInt(run.getColumnIndex(PC.DB_MODE))==PC.SIGNAL_RESUME_COOLDOWN)
					canvas.drawCircle( p2.x, p2.y, resumeSize, startPaint);
				else
					canvas.drawCircle( p2.x, p2.y, stopSize, stopPaint);
			}

			run.close();
			run = db.getSplits(id);
			while(run.moveToNext()){
				lon = run.getDouble(run.getColumnIndex(PC.DB_LONGITUDE));
				lat = run.getDouble(run.getColumnIndex(PC.DB_LATITUDE));
				splitTime = PC.formatTime(run.getLong(run.getColumnIndex(PC.DB_SPLIT_TIME)));
				splitNo = String.valueOf(run.getInt(run.getColumnIndex(PC.DB_SPLIT_NO)));
				gp2 = new GeoPoint((int)(lat*1000000), (int)(lon*1000000));
				projection.toPixels(gp2, p2);

				track.rewind();
				track.moveTo(p2.x, p2.y);
				track.lineTo(p2.x+2*splitSize+splitTimePaint.measureText(splitTime), p2.y);
				canvas.drawPath(track,splitBgPaint);
				canvas.drawTextOnPath(splitTime, track, splitSize/2, (splitSize/2), splitTimePaint);
				track.rewind();
				track.moveTo(p2.x-(splitSize), p2.y);
				track.lineTo(p2.x+(splitSize), p2.y);

				canvas.drawCircle( p2.x, p2.y, splitSize, splitPaint);

				splitNoPaint.setTextSize(splitSize+4-(splitNo.length()-1)*2);
				canvas.drawTextOnPath(splitNo, track, 0, (splitSize/2), splitNoPaint);
			}
			run.close();
			db.close();

		}
	}


	public static int getPrefInt(Activity ctx, int profile, String pref) {
		
        DbAdapter db = new DbAdapter(ctx.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
		int out = prefs.getInt(prefs.getColumnIndex(pref));
		prefs.close();
		db.close();
					
		return out;
	}




}