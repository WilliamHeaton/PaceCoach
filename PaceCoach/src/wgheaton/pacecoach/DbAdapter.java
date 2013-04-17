package wgheaton.pacecoach;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {

	private Context context;
	private SQLiteDatabase db;
	private DbRunHelper dbHelper;

	public DbAdapter(Context context) {
		this.context = context;
	}

	public DbAdapter open() throws SQLException {
		dbHelper = new DbRunHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}


	public Cursor getCell(int profile, int page,int cellid){
		return db.rawQuery("SELECT * FROM "+PC.DB_CELL+" WHERE "+PC.DB_PAGE+" = "+page+" AND "+PC.DB_CELL+" = "+cellid+" AND "+PC.DB_PREFS+" = "+profile, null);
	}
	public void updateCell(int profile, int page, int cellid, int datatype, int u) {
		if(datatype == PC.CELL_TYPE_MAP)
			db.delete(PC.DB_CELL, PC.DB_DATATYPE+" = ? AND "+PC.DB_PREFS+" = ? ",new String[] {String.valueOf(PC.CELL_TYPE_MAP),String.valueOf(profile)});
		
		db.delete(PC.DB_CELL, PC.DB_PAGE+" = ? and "+PC.DB_CELL+" = ? AND "+PC.DB_PREFS+" = ? ",new String[] {String.valueOf(page),String.valueOf(cellid),String.valueOf(profile)});
		
		ContentValues cv = new ContentValues();
		cv.put(PC.DB_PAGE, page);
		cv.put(PC.DB_CELL, cellid);
		cv.put(PC.DB_DATATYPE, datatype);
		cv.put(PC.DB_UNITS, u);
		cv.put(PC.DB_PREFS, profile);
		db.insert(PC.DB_CELL, null, cv);
	}
	public Cursor getCells(int profile,int page){
		return db.rawQuery("SELECT * FROM "+PC.DB_CELL+" WHERE "+PC.DB_PAGE+" = "+page+" AND "+PC.DB_PREFS+" = "+profile+" ORDER BY "+PC.DB_CELL, null);
	}
	public int getPageTemplate(int profile,int page){
		int template;
		
		Cursor c = db.rawQuery("SELECT "+PC.DB_TEMPLATE+" FROM "+PC.DB_PAGE+" WHERE "+PC.DB_PAGE+" = "+page+" AND "+PC.DB_PREFS+" = "+profile, null);
		if(c.moveToFirst())		template = c.getInt(0); 
		else					template = 1;

		c.close();
		return template;
	}
	public Cursor getRuns(int profile){
		return db.rawQuery("SELECT * FROM "+PC.DB_RUNSUMMARY+" WHERE "+PC.DB_PREFS+"="+profile+" ORDER BY "+PC.DB_TIMESTAMP+" DESC", null);
	}
	public long logRun(int runid, int profile, long timestamp, long elapsedtime, double distance) {

		ContentValues insertValues = new ContentValues();
		insertValues.put(PC.DB_RUNID, 		runid);
		insertValues.put(PC.DB_TIMESTAMP, 	timestamp);
		insertValues.put(PC.DB_TIME_ELAP,	elapsedtime );
		insertValues.put(PC.DB_DIST_ELAP,	distance );
		insertValues.put(PC.DB_PREFS,		profile );
		
		return db.insert(PC.DB_RUNSUMMARY, null, insertValues);
	}
	public Cursor getPages(int profile){
		return db.rawQuery("SELECT * FROM "+PC.DB_PAGE+" WHERE "+PC.DB_PREFS+"="+profile+" ORDER BY "+PC.DB_PAGE, null);
	}
	public void updateNumPages(int profile, int v) {
		ContentValues cv;
		int id;
		
		db.delete(PC.DB_PAGE, PC.DB_PAGE+" > ? AND "+PC.DB_PREFS+" = ?", new String[] {String.valueOf(v),String.valueOf(profile)});

		Cursor maxID = db.rawQuery("SELECT MAX("+PC.DB_PAGE+") FROM "+PC.DB_PAGE + " WHERE "+PC.DB_PREFS+" = "+profile, null);
		if(maxID.moveToFirst())	id = maxID.getInt(0) + 1; 
		else					id = 1;

		maxID.close();
		
		while(id <= v){
			cv = new ContentValues();
			cv.put(PC.DB_PAGE, id);
			cv.put(PC.DB_TEMPLATE,1);
			cv.put(PC.DB_PREFS,profile);
			db.insert(PC.DB_PAGE, null, cv);
			id++;
		}

		db.delete(PC.DB_CELL, PC.DB_PAGE+" > ? AND "+PC.DB_PREFS+" = ?",new String[] {String.valueOf(v),String.valueOf(profile)});
	}

	public void setTemplate(int profile,int page,int template){

		db.delete(PC.DB_PAGE, PC.DB_PAGE+" = ? AND "+PC.DB_PREFS+" = ?", new String[] {String.valueOf(page),String.valueOf(profile)});
		ContentValues cv = new ContentValues();
		cv.put(PC.DB_PAGE, page);
		cv.put(PC.DB_TEMPLATE,template);
		cv.put(PC.DB_PREFS,profile);
		db.insert(PC.DB_PAGE, null, cv);
	}

	public void deleteRun(int r) {
		db.delete(PC.DB_RUNSUMMARY, PC.DB_RUNID+" = ?", new String[] {String.valueOf(r)});
		db.delete(PC.DB_RUNDATA,   PC.DB_RUNID+" NOT IN (SELECT "+PC.DB_RUNID+" FROM "+PC.DB_RUNSUMMARY+")",null);
		db.delete(PC.DB_SPLITS,  PC.DB_RUNDATA+" NOT IN (SELECT "+PC.DB_ROWID+" FROM "+PC.DB_RUNDATA+")",   null);
	}
	public int getNewRunID(){
		int runid;
		
		Cursor maxID = db.rawQuery("SELECT MAX("+PC.DB_RUNID+") FROM "+PC.DB_RUNDATA, null);
		if(maxID.moveToFirst())	runid = maxID.getInt(0) + 1; 
		else					runid = 1;

		maxID.close();
		return runid;
	}
	public Cursor getLastDatapoints(int id, int lim){
		return db.rawQuery("SELECT * FROM "+PC.DB_RUNDATA+" WHERE "+PC.DB_RUNID+" = "+id+" and "+PC.DB_MODE+" != "+PC.SIGNAL_SPLIT+" AND "+PC.DB_MODE+" != "+PC.SPLIT_EXTRA+" ORDER BY "+PC.DB_TIMESTAMP+" DESC LIMIT "+lim, null);
	}
	public Cursor getLastDatapoints(int id, long before, long length){
		return db.rawQuery("SELECT * FROM "+PC.DB_RUNDATA+
				" WHERE "+PC.DB_RUNID+" = "+id+
				" and "+PC.DB_MODE+" != "+PC.SIGNAL_SPLIT+
				" AND "+PC.DB_MODE+" != "+PC.SPLIT_EXTRA+
				" AND "+PC.DB_TIMESTAMP+" <= "+before+
				" AND "+PC.DB_TIMESTAMP+" >= "+(before-length)+
				" ORDER BY "+PC.DB_TIMESTAMP+" DESC", null);
	}
	public Cursor getRun(int id){
		return db.rawQuery("SELECT * FROM "+PC.DB_RUNDATA+" WHERE "+PC.DB_RUNID+" = "+id+" and "+PC.DB_MODE+" != "+PC.SIGNAL_SPLIT+" AND "+PC.DB_MODE+" != "+PC.SPLIT_EXTRA+" ORDER BY "+PC.DB_TIMESTAMP+" ASC", null);
	}

	public Cursor getEndpoints(int id) {
		return db.rawQuery(
				"SELECT * FROM "+PC.DB_RUNDATA+
				" WHERE "+PC.DB_RUNID+" = "+id+
				" AND ( "+ PC.DB_MODE+" = " + PC.SIGNAL_PAUSE_WARMUP + 
				" OR "	 + PC.DB_MODE+" = " + PC.SIGNAL_PAUSE_WORKOUT +
				" OR "	 + PC.DB_MODE+" = " + PC.SIGNAL_PAUSE_COOLDOWN +
				" OR "	 + PC.DB_MODE+" = " + PC.SIGNAL_RESUME_WARMUP +
				" OR "	 + PC.DB_MODE+" = " + PC.SIGNAL_RESUME_WORKOUT +
				" OR "	 + PC.DB_MODE+" = " + PC.SIGNAL_RESUME_COOLDOWN +
				" OR "	 + PC.DB_MODE+" = " + PC.SIGNAL_BEGIN_WARMUP +
				" OR "	 + PC.DB_MODE+" = " + PC.SIGNAL_BEGIN_WORKOUT +
				" OR "	 + PC.DB_MODE+" = " + PC.SIGNAL_BEGIN_COOLDOWN +
				") ORDER BY "+PC.DB_TIMESTAMP+" ASC", null);
	}
	public Cursor getMinMaxLatLon(int id) {
		return db.rawQuery("SELECT MIN("+PC.DB_LATITUDE+") minlat,MAX("+PC.DB_LATITUDE+") maxlat, MIN("+PC.DB_LONGITUDE+") minlon,MAX("+PC.DB_LONGITUDE+") maxlon FROM "+PC.DB_RUNDATA+" WHERE "+PC.DB_RUNID+" = "+id, null);
	}

	public Cursor getSplits(int id) {
		return db.rawQuery("SELECT "+PC.DB_SPLITS+".*,"+PC.DB_RUNDATA+".* FROM "+PC.DB_SPLITS+" LEFT JOIN "+PC.DB_RUNDATA+" ON "+PC.DB_SPLITS+"."+PC.DB_RUNDATA+" = "+PC.DB_RUNDATA+"."+PC.DB_ROWID+" WHERE "+PC.DB_RUNDATA+"."+PC.DB_RUNID+" = "+id+" AND "+PC.DB_RUNDATA+"."+PC.DB_MODE+"="+PC.SIGNAL_SPLIT+" ORDER BY "+PC.DB_SPLIT_NO+" ASC", null);
	}
	public Cursor getSplitsExtra(int id, boolean invert) {
		return db.rawQuery("SELECT "+PC.DB_SPLITS+".*,"+PC.DB_RUNDATA+".* FROM "+PC.DB_SPLITS+" LEFT JOIN "+PC.DB_RUNDATA+" ON "+PC.DB_SPLITS+"."+PC.DB_RUNDATA+" = "+PC.DB_RUNDATA+"."+PC.DB_ROWID+" WHERE "+PC.DB_RUNDATA+"."+PC.DB_RUNID+" = "+id+" ORDER BY "+PC.DB_SPLIT_NO+" "+(invert?"DESC":"ASC"), null);
	}

	public Cursor getProfile(int profile) {
		return db.rawQuery("SELECT "+PC.DB_ROWID+", "+ PC.DB_PROFILE_LABEL+" FROM "+PC.DB_PREFS+" WHERE "+PC.DB_ROWID+" = " + profile,null);
	}
	public Cursor getPrefs(int profile) {
		return db.rawQuery("SELECT * FROM "+PC.DB_PREFS+" WHERE "+PC.DB_ROWID+" = " + profile,null);
	}
	
	public Cursor getProfiles() {
		return db.rawQuery("SELECT "+PC.DB_ROWID+", "+ PC.DB_PROFILE_LABEL+" FROM "+PC.DB_PREFS+" ORDER BY "+PC.DB_ROWID+" ASC",null);
	}
	public long addSplit(int splitNo, int type, long id, long splitTime, double distanceSinceSplit) {
		
		ContentValues insertValues = new ContentValues();
		insertValues.put(PC.DB_SPLIT_NO,	splitNo );
		insertValues.put(PC.DB_RUNDATA,		id );
		insertValues.put(PC.DB_SPLIT_TIME,	splitTime );
		insertValues.put(PC.DB_SPLIT_DIST,	distanceSinceSplit );
		insertValues.put(PC.DB_SPLIT_TYPE,	type );
		
		return db.insert(PC.DB_SPLITS, null, insertValues);
	}

	
	public long addPoint(int    runid,
						 long 	timestamp,
						 int	mode,
						 double longitude,
						 double latitude,
						 double  accuracy, 
						 double altitude,
						 double  speed,
						 double  bearing,
						 long   thetime,
						 long   elapsedtime,
						 long   difftime,
						 double  distance2,
						 double  d,
						 double 	speedcal,
						 long 	timesincesplit,
						 double	distanceSinceSplit2 ) {
		
		ContentValues insertValues = new ContentValues();
		insertValues.put(PC.DB_RUNID, 			runid);
		insertValues.put(PC.DB_MODE, 			mode);
		insertValues.put(PC.DB_TIMESTAMP, 		timestamp);
		insertValues.put(PC.DB_LONGITUDE, 		longitude);
		insertValues.put(PC.DB_LATITUDE, 		latitude);
		insertValues.put(PC.DB_ACCURACY,		accuracy );
		insertValues.put(PC.DB_ALTITUDE,		altitude );
		insertValues.put(PC.DB_SPEED_GPS,		speed );
		insertValues.put(PC.DB_BEARING,			bearing );
		insertValues.put(PC.DB_THETIME,			thetime );
		insertValues.put(PC.DB_TIME_ELAP,		elapsedtime );
		insertValues.put(PC.DB_DIST_ELAP,		distance2 );
		insertValues.put(PC.DB_TIME_DIFF,		difftime );
		insertValues.put(PC.DB_DIST_DIFF,		d );
		insertValues.put(PC.DB_SPEED_CAL,		speedcal );
		insertValues.put(PC.DB_DIST_SINCESPLIT,	distanceSinceSplit2 );
		insertValues.put(PC.DB_TIME_SINCESPLIT,	timesincesplit );
		
		return db.insert(PC.DB_RUNDATA, null, insertValues);
		 
	}
	public void updateLastPoint( long previd,
								 int    runid,
								 long	timestamp,
								 double longitude,
								 double latitude,
								 double  accuracy, 
								 double altitude,
								 double  speed,
								 double  bearing,
								 long   thetime,
								 long   elapsedtime,
								 long   difftime,
								 double  distance,
								 double  diffdist,
								 double  speedcal,
								 long 	timesincesplit,
								 double	distanceSinceSplit) {


		ContentValues updateValues = new ContentValues();
		updateValues.put(PC.DB_RUNID, 		runid);
		updateValues.put(PC.DB_TIMESTAMP, 	timestamp);
		updateValues.put(PC.DB_LONGITUDE, 	longitude);
		updateValues.put(PC.DB_LATITUDE, 	latitude);
		updateValues.put(PC.DB_ACCURACY,	accuracy );
		updateValues.put(PC.DB_ALTITUDE,	altitude );
		updateValues.put(PC.DB_SPEED_GPS,		speed );
		updateValues.put(PC.DB_BEARING,			bearing );
		updateValues.put(PC.DB_THETIME,			thetime );
		updateValues.put(PC.DB_TIME_ELAP,		elapsedtime );
		updateValues.put(PC.DB_DIST_ELAP,		distance );
		updateValues.put(PC.DB_TIME_DIFF,		difftime );
		updateValues.put(PC.DB_DIST_DIFF,		diffdist );
		updateValues.put(PC.DB_SPEED_CAL,		speedcal );
		updateValues.put(PC.DB_DIST_SINCESPLIT,	distanceSinceSplit );
		updateValues.put(PC.DB_TIME_SINCESPLIT,	timesincesplit );

	}

	public void setPref(int profile, String pref, int v) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(PC.DB_ROWID, 		profile);
		updateValues.put(pref, v);
		db.update(PC.DB_PREFS, updateValues,PC.DB_ROWID+" = ?", new String[]{String.valueOf(profile)});
	}
	public void setPref(int profile, String pref, double v) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(PC.DB_ROWID, 		profile);
		updateValues.put(pref, v);
		db.update(PC.DB_PREFS, updateValues,PC.DB_ROWID+" = ?", new String[]{String.valueOf(profile)});
	}
	public void setPref(int profile, String pref, String v) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(PC.DB_ROWID, 		profile);
		updateValues.put(pref, v);
		db.update(PC.DB_PREFS, updateValues,PC.DB_ROWID+" = ?", new String[]{String.valueOf(profile)});
	}

	public int addProfile(String n) {
		return addProfile(db,n);
	}
	public int addProfile(SQLiteDatabase database,String n) {
		ContentValues cv = new ContentValues();
		cv.put(PC.DB_PROFILE_LABEL, 		n);
		return (int) database.insert(PC.DB_PREFS, null, cv);

	}
	public void deleteProfile(int p) {
		
		db.delete(PC.DB_PREFS, PC.DB_ROWID+" = ?", new String[]{String.valueOf(p)});
		
		db.delete(PC.DB_CELL,   	PC.DB_PREFS  +" NOT IN (SELECT "+PC.DB_ROWID+" FROM "+PC.DB_PREFS+")",null);
		db.delete(PC.DB_PAGE,   	PC.DB_PREFS  +" NOT IN (SELECT "+PC.DB_ROWID+" FROM "+PC.DB_PREFS+")",null);
		db.delete(PC.DB_RUNSUMMARY, PC.DB_PREFS  +" NOT IN (SELECT "+PC.DB_ROWID+" FROM "+PC.DB_PREFS+")",null);
		db.delete(PC.DB_RUNDATA,	PC.DB_RUNID  +" NOT IN (SELECT "+PC.DB_RUNID+" FROM "+PC.DB_RUNSUMMARY+")",null);
		db.delete(PC.DB_SPLITS, 	PC.DB_RUNDATA+" NOT IN (SELECT "+PC.DB_ROWID+" FROM "+PC.DB_RUNDATA+")",   null);
	}
	public class DbRunHelper  extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = PC.DB_RUNDATA;

		private static final int DATABASE_VERSION = 4;

		public DbRunHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// Method is called during creation of the database
		@Override
		public void onCreate(SQLiteDatabase database) {

			database.execSQL(DB_PREFS_CREATE);
			database.execSQL(DB_CELLS_CREATE);
			database.execSQL(DB_PAGES_CREATE);
			database.execSQL(DB_RUNSUMMARY_CREATE);
			database.execSQL(DB_RUNDATA_CREATE);
			database.execSQL(DB_SPLITS_CREATE);
			
			addProfile(database,"Running");
			addProfile(database,"Walking");
			addProfile(database,"Bycicling");
		}

		// Method is called during an upgrade of the database,
		// e.g. if you increase the database version
		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion,
				int newVersion) {

			database.execSQL("DROP TABLE IF EXISTS "+PC.DB_CELL);
			database.execSQL("DROP TABLE IF EXISTS "+PC.DB_PAGE);
			database.execSQL("DROP TABLE IF EXISTS "+PC.DB_RUNSUMMARY);
			database.execSQL("DROP TABLE IF EXISTS "+PC.DB_RUNDATA);
			database.execSQL("DROP TABLE IF EXISTS "+PC.DB_SPLITS);
			database.execSQL("DROP TABLE IF EXISTS "+PC.DB_PREFS);
			onCreate(database);

		//	database.execSQL(DB_PREFS_CREATE);
		//	database.execSQL("INSERT INTO "+PC.DB_PREFS+"("+PC.DB_PROFILE_LABEL+") VALUES('Running')");
		//	database.execSQL("INSERT INTO "+PC.DB_PREFS+"("+PC.DB_PROFILE_LABEL+") VALUES('Walking')");
		//	database.execSQL("INSERT INTO "+PC.DB_PREFS+"("+PC.DB_PROFILE_LABEL+") VALUES('Bycicling')");
			
		}
		
		private static final String DB_CELLS_CREATE = 
				"create table "+PC.DB_CELL+" (" +
				
					PC.DB_ROWID				+ " integer primary key autoincrement, " +
					PC.DB_PREFS				+ " integer default 1, " +
					PC.DB_PAGE				+ " integer null, " +
					PC.DB_CELL				+ " integer null, " +
					PC.DB_DATATYPE			+ " integer null, " +
					PC.DB_UNITS				+ " integer null " +
				");";
		private static final String DB_PAGES_CREATE = 
				"create table "+PC.DB_PAGE+" (" +
					PC.DB_ROWID				+ " integer primary key autoincrement, " +
					PC.DB_PREFS				+ " integer default 1, " +
					PC.DB_PAGE				+ " integer null, " +
					PC.DB_TEMPLATE			+ " integer null " +
				");";
		private static final String DB_RUNSUMMARY_CREATE = 
				"create table "+PC.DB_RUNSUMMARY+" (" +
					
					PC.DB_ROWID				+ " integer primary key autoincrement, " +
					PC.DB_PREFS				+ " integer default 1, " +
					PC.DB_RUNID				+ " integer null, " +
					PC.DB_TIMESTAMP			+ " integer null, " +
					PC.DB_TIME_ELAP			+ "	integer null, " +
					PC.DB_DIST_ELAP			+ "	real    null " +
					
				");";
		private static final String DB_RUNDATA_CREATE = 
				"create table "+PC.DB_RUNDATA+" (" +
				
					PC.DB_ROWID				+ " integer primary key autoincrement, " +
					PC.DB_RUNID				+ " integer null, " +
					PC.DB_TIMESTAMP			+ " integer null, " +
					PC.DB_MODE				+ " integer null, " + 
					
					PC.DB_LONGITUDE			+ " real    null, " +
					PC.DB_LATITUDE			+ " real    null, " +
					PC.DB_ACCURACY			+ " real    null, " +
					PC.DB_ALTITUDE			+ " real    null, " +
					PC.DB_SPEED_GPS			+ " real    null, " +
					PC.DB_SPEED_CAL			+ " real    null, " +
					PC.DB_BEARING			+ " real    null, " +
					PC.DB_THETIME			+ " integer null, " +
					PC.DB_TIME_ELAP			+ "	integer null, " +
					PC.DB_TIME_DIFF			+ "	integer null, " +
					PC.DB_TIME_SINCESPLIT	+ " integer null, " +
					PC.DB_DIST_ELAP			+ "	real    null, " +
					PC.DB_DIST_DIFF 		+ " real    null, " +
					PC.DB_DIST_SINCESPLIT	+ " real    null " +
					
				");";
		

		private static final String DB_SPLITS_CREATE = 
				"create table "+PC.DB_SPLITS+" (" +

					PC.DB_ROWID				+ " integer primary key autoincrement, " +
					PC.DB_RUNDATA			+ " integer null, " + 
					PC.DB_SPLIT_TYPE		+ " integer null, " + 
					PC.DB_SPLIT_NO			+ " integer null, " +
					PC.DB_SPLIT_TIME		+ " integer null, " + 
					PC.DB_SPLIT_DIST		+ " integer null " +  

				");";
		private static final String DB_PREFS_CREATE = 
				"create table "+PC.DB_PREFS+" (" +
				
					PC.DB_ROWID									+ " integer primary key autoincrement, " +
					
					PC.DB_PROFILE_LABEL							+ " string null," +

					PC.PREF_MAP_MODE							+ " integer default "  +  PC.DEFAULT_PREF_MAP_MODE					+ "," +
					PC.PREF_MAP_TYPE							+ " integer default "  +  PC.DEFAULT_PREF_MAP_TYPE					+ "," +
					PC.PREF_ROTATE								+ " integer default "  +  PC.DEFAULT_PREF_ROTATE					+ "," +
					PC.PREF_PORTRAIT							+ " integer default "  +  PC.DEFAULT_PREF_PORTRAIT					+ "," +
					
					PC.PREF_SPEAK								+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK?1:0)				+ "," +
					PC.PREF_SPEAK_START							+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_START?1:0)			+ "," +
					PC.PREF_SPEAK_STOP							+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_STOP?1:0)			+ "," +
					PC.PREF_SPEAK_RESUME						+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_RESUME?1:0)		+ "," +
					PC.PREF_SPEAK_PAUSE							+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_PAUSE?1:0)			+ "," +
					PC.PREF_SPEAK_SPLITS						+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_SPLITS?1:0)		+ "," +
					
					PC.PREF_SPEAK_SPEED_FREQ					+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_SPEED_FREQ)		+ "," +
					PC.PREF_SPEAK_SPEED_L						+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_SPEED_L?1:0)		+ "," +
					PC.PREF_SPEAK_SPEED_H						+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_SPEED_H?1:0)		+ "," +
					PC.PREF_SPEAK_SPEED_L_DATA					+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_L_DATA		+ "," +
					PC.PREF_SPEAK_SPEED_H_DATA					+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_H_DATA		+ "," +
					PC.PREF_SPEAK_SPEED_L_INT1					+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_L_INT1		+ "," +
					PC.PREF_SPEAK_SPEED_L_INT2					+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_L_INT2		+ "," +
					PC.PREF_SPEAK_SPEED_H_INT1					+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_H_INT1		+ "," +
					PC.PREF_SPEAK_SPEED_H_INT2					+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_H_INT2		+ "," +
					PC.PREF_SPEAK_SPEED_L_UNIT					+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_L_UNIT		+ "," +
					PC.PREF_SPEAK_SPEED_H_UNIT					+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_H_UNIT		+ "," +
					PC.PREF_SPEAK_SPEED_L_FORM					+ " string default \""	+  PC.DEFAULT_PREF_SPEAK_SPEED_L_FORM		+ "\"," +
					PC.PREF_SPEAK_SPEED_H_FORM					+ " string default \""	+  PC.DEFAULT_PREF_SPEAK_SPEED_H_FORM		+ "\"," +
					PC.PREF_SPEAK_SPEED_L_VALU					+ " real   default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_L_VALU		+ "," +
					PC.PREF_SPEAK_SPEED_H_VALU					+ " real   default "	+  PC.DEFAULT_PREF_SPEAK_SPEED_H_VALU		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_L				+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L?1:0)		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_H				+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H?1:0)		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_L_DATA			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_DATA		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_H_DATA			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_DATA		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_L_INT1			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_INT1		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_L_INT2			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_INT2		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_H_INT1			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_INT1		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_H_INT2			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_INT2		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_L_UNIT			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_UNIT		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_H_UNIT			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_UNIT		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_L_FORM			+ " string default \""	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_FORM		+ "\"," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_H_FORM			+ " string default \""	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_FORM		+ "\"," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_L_VALU			+ " real   default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_L_VALU		+ "," +
					PC.PREF_SPEAK_COOLDOWN_SPEED_H_VALU			+ " real   default "	+  PC.DEFAULT_PREF_SPEAK_COOLDOWN_SPEED_H_VALU		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_L				+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_L?1:0)		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_H				+ " integer default " 	+ (PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_H?1:0)		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_L_DATA			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_DATA		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_H_DATA			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_DATA		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_L_INT1			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_INT1		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_L_INT2			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_INT2		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_H_INT1			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_INT1		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_H_INT2			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_INT2		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_L_UNIT			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_UNIT		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_H_UNIT			+ " integer default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_UNIT		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_L_FORM			+ " string default \""	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_FORM		+ "\"," +
					PC.PREF_SPEAK_WARMUP_SPEED_H_FORM			+ " string default \""	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_FORM		+ "\"," +
					PC.PREF_SPEAK_WARMUP_SPEED_L_VALU			+ " real   default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_L_VALU		+ "," +
					PC.PREF_SPEAK_WARMUP_SPEED_H_VALU			+ " real   default "	+  PC.DEFAULT_PREF_SPEAK_WARMUP_SPEED_H_VALU		+ "," +
					
					PC.PREF_WARMUP								+ " integer default " 	+ (PC.DEFAULT_PREF_WARMUP?1:0)				+ "," +
					PC.PREF_WARMUP_INT1							+ " integer default "	+  PC.DEFAULT_PREF_WARMUP_INT1				+ "," +
					PC.PREF_WARMUP_INT2							+ " integer default "	+  PC.DEFAULT_PREF_WARMUP_INT2				+ "," +
					PC.PREF_WARMUP_UNIT							+ " integer default "	+  PC.DEFAULT_PREF_WARMUP_UNIT				+ "," +
					PC.PREF_WARMUP_FORM							+ " string default \""	+  PC.DEFAULT_PREF_WARMUP_FORM				+ "\"," +
					PC.PREF_WARMUP_VALU							+ " real   default "	+  PC.DEFAULT_PREF_WARMUP_VALU				+ "," +
					PC.PREF_WARMUP_INCL							+ " integer default " 	+ (PC.DEFAULT_PREF_WARMUP_INCL?1:0)			+ "," +
					PC.PREF_WARMUP_SPLITS						+ " integer default " 	+ (PC.DEFAULT_PREF_WARMUP_SPLITS?1:0)		+ "," +
					PC.PREF_WARMUP_END_ACTION					+ " integer default " 	+  PC.DEFAULT_PREF_WARMUP_END_ACTION		+ "," +
					PC.PREF_WORKOUT_INT1						+ " integer default "	+  PC.DEFAULT_PREF_WORKOUT_INT1				+ "," +
					PC.PREF_WORKOUT_INT2						+ " integer default "	+  PC.DEFAULT_PREF_WORKOUT_INT2				+ "," +
					PC.PREF_WORKOUT_UNIT						+ " integer default "	+  PC.DEFAULT_PREF_WORKOUT_UNIT				+ "," +
					PC.PREF_WORKOUT_FORM						+ " string default \""	+  PC.DEFAULT_PREF_WORKOUT_FORM				+ "\"," +
					PC.PREF_WORKOUT_VALU						+ " real   default "	+  PC.DEFAULT_PREF_WORKOUT_VALU				+ "," +
					PC.PREF_WORKOUT_END_ACTION					+ " integer default " 	+  PC.DEFAULT_PREF_WORKOUT_END_ACTION		+ "," +
					PC.PREF_COOLDOWN							+ " integer default " 	+ (PC.DEFAULT_PREF_COOLDOWN?1:0)			+ "," +
					PC.PREF_COOLDOWN_INT1						+ " integer default "	+  PC.DEFAULT_PREF_COOLDOWN_INT1			+ "," +
					PC.PREF_COOLDOWN_INT2						+ " integer default "	+  PC.DEFAULT_PREF_COOLDOWN_INT2			+ "," +
					PC.PREF_COOLDOWN_UNIT						+ " integer default "	+  PC.DEFAULT_PREF_COOLDOWN_UNIT			+ "," +
					PC.PREF_COOLDOWN_FORM						+ " string default \""	+  PC.DEFAULT_PREF_COOLDOWN_FORM			+ "\"," +
					PC.PREF_COOLDOWN_VALU						+ " real   default "	+  PC.DEFAULT_PREF_COOLDOWN_VALU			+ "," +
					PC.PREF_COOLDOWN_INCL						+ " integer default " 	+ (PC.DEFAULT_PREF_COOLDOWN_INCL?1:0)		+ "," +
					PC.PREF_COOLDOWN_SPLITS						+ " integer default " 	+ (PC.DEFAULT_PREF_COOLDOWN_SPLITS?1:0)		+ "," +
					PC.PREF_COOLDOWN_END_ACTION					+ " integer default " 	+  PC.DEFAULT_PREF_COOLDOWN_END_ACTION		+ "," +
					
					
					PC.PREF_MANUAL_SPLITS						+ " integer default " 	+ (PC.DEFAULT_PREF_MANUAL_SPLITS?1:0)		+ "," +
					PC.PREF_AUTO_SPLIT							+ " integer default " 	+ (PC.DEFAULT_PREF_AUTO_SPLIT?1:0)			+ "," +
					PC.PREF_AUTO_SPLIT_DISTANCE_VALU			+ " integer default "	+  PC.DEFAULT_PREF_AUTO_SPLIT_DISTANCE_VALU	+ "," +
					PC.PREF_AUTO_SPLIT_DISTANCE_INT1			+ " integer default "	+  PC.DEFAULT_PREF_AUTO_SPLIT_DISTANCE_INT1	+ "," +
					PC.PREF_AUTO_SPLIT_DISTANCE_INT2			+ " integer default "	+  PC.DEFAULT_PREF_AUTO_SPLIT_DISTANCE_INT2	+ "," +
					PC.PREF_AUTO_SPLIT_DISTANCE_UNIT			+ " integer default "	+  PC.DEFAULT_PREF_AUTO_SPLIT_DISTANCE_UNIT	+ "," +
					PC.PREF_AUTO_SPLIT_DISTANCE_FORM			+ " string default \""	+  PC.DEFAULT_PREF_AUTO_SPLIT_DISTANCE_FORM	+ "\"," +

					PC.PREF_PAGE_NUMBER							+ " integer default "	+  PC.DEFAULT_PREF_PAGE_NUMBER				+ "," +
					PC.PREF_PAGE_DEFAULT						+ " integer default "	+  PC.DEFAULT_PREF_PAGE_DEFAULT				+ "," +
					PC.PREF_PAGE_LOCK							+ " integer default " 	+ (PC.DEFAULT_PREF_PAGE_LOCK?1:0)			+ "," +
					PC.PREF_SPEEDCAL_TIME						+ " integer default "	+  PC.DEFAULT_PREF_SPEEDCAL_TIME +
				
				");";
	}




}
