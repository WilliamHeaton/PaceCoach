package com.cypho.pacecoach;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import wgheaton.pacecoach.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class History extends ListActivity implements ProfilePicker.OnProfilePickedListener{
	Activity ctx;
	int profile;

    @Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(PC.PREF_PROFILE,profile);
		super.onSaveInstanceState(outState);
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
		ctx = this;
		setContentView(R.layout.history);
		

        if(savedInstanceState!=null && savedInstanceState.getInt(PC.PREF_PROFILE)!=0){
        	profile = savedInstanceState.getInt(PC.PREF_PROFILE);
        }
        if(getIntent().getExtras()!=null && getIntent().getExtras().getInt(PC.PREF_PROFILE) != 0){
    		profile =  getIntent().getExtras().getInt(PC.PREF_PROFILE);
    		setupPage();
        	return;
        }
        
		SharedPreferences prefs = ctx.getSharedPreferences(PC.PREFS,Context.MODE_MULTI_PROCESS);
		profile = prefs.getInt(PC.PREF_PROFILE, PC.DEFAULT_PREF_PROFILE);
		
		setupPage();
		
		if(!prefs.getBoolean(PC.PREF_REMEMBER, PC.DEFAULT_PREF_REMEMBER)){
			pickActivity();
		}
    }

    private void pickActivity(){
    	Bundle b = new Bundle();
    	b.putInt(PC.PREF_PROFILE, profile);
    	ProfilePicker newFragment = ProfilePicker.newInstance(b);
    	newFragment.show(((Activity) ctx).getFragmentManager(),null);
    }
	@Override
	public void onProfilePicked(boolean remember, int selected) {
		SharedPreferences prefs = ctx.getSharedPreferences(PC.PREFS,Context.MODE_MULTI_PROCESS);
		Editor mEditor = prefs.edit();
		mEditor.putBoolean(PC.PREF_REMEMBER, remember);
		mEditor.putInt(PC.PREF_PROFILE, selected);
		mEditor.commit();
		profile = selected;
		setupPage();
	}
    private void setupPage(){

		DbAdapter db = new DbAdapter(this.getApplicationContext());
        db.open();
        
		Cursor prefs = db.getPrefs(profile);
		if( !prefs.moveToFirst() ){
			profile = 0;
			pickActivity();
			return;
		}
        
		Cursor runs = db.getRuns(profile);
		HistoryCursorAdapter adapter = new HistoryCursorAdapter( ctx, runs);
		setListAdapter(adapter);
		
		db.updateNumPages(profile,prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_NUMBER)));
        
        Button runbutton = (Button) findViewById(R.id.runbutton);
        runbutton.setText("Begin " + prefs.getString(prefs.getColumnIndex(PC.DB_PROFILE_LABEL)));
        prefs.close();
        db.close();
        runbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(ctx, Running.class);
				i.putExtra(PC.PREF_PROFILE, profile);
				startActivity(i);
				finish();
			}
        });
        ImageButton settingsbutton = (ImageButton) findViewById(R.id.settingsbutton);
        settingsbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx, Settings.class);
				i.putExtra(PC.PREF_PROFILE, profile);
				startActivity(i);
				finish();
			}
        });
        ImageButton logbutton = (ImageButton) findViewById(R.id.logbutton);
        logbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pickActivity();
			}
        });
        
		
        
    }
    public class HistoryCursorAdapter extends CursorAdapter {

    	public HistoryCursorAdapter(Context context, Cursor c) {
    		super(context, c);
    	}

    	@Override
    	public void bindView(View view, Context context, final Cursor cursor) {
    		
    		long timestamp = cursor.getLong(cursor.getColumnIndex(PC.DB_TIMESTAMP));
    		
    		SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd, yyy  -  h:m a",Locale.getDefault());
    		
    		String s = PC.formatUnits(cursor.getDouble(cursor.getColumnIndex(PC.DB_DIST_ELAP))/cursor.getDouble(cursor.getColumnIndex(PC.DB_TIME_ELAP))*1000,PC.UNITS_MINMILE);;
    		String d = PC.formatUnits(cursor.getDouble(cursor.getColumnIndex(PC.DB_DIST_ELAP)),PC.UNITS_MILES);
    		String t = PC.formatTime(cursor.getLong(cursor.getColumnIndex(PC.DB_TIME_ELAP)));
    		
    		TextView date = (TextView) view.findViewById(R.id.date);
    		date.setText(format.format(new Date(timestamp)));

    		TextView time = (TextView) view.findViewById(R.id.time);
    		time.setText(t);
    		
    		TextView distance = (TextView) view.findViewById(R.id.distance);
    		distance.setText(d);
    		
    		TextView speed = (TextView) view.findViewById(R.id.speed);
    		speed.setText(s);
    		view.setTag((int) cursor.getInt(cursor.getColumnIndex(PC.DB_RUNID)));
    		view.setOnClickListener(new OnClickListener(){
    			@Override
    			public void onClick(View v) {
    				int r = (Integer) v.getTag();
    				Intent i = new Intent(ctx, ReviewRun.class);
    				i.putExtra("runid", r);
    				i.putExtra(PC.PREF_PROFILE, profile);
    				ctx.startActivity(i);
    				ctx.finish();

    			}});
    		view.setOnLongClickListener(new OnLongClickListener(){@Override public boolean onLongClick(final View v) {
    			
    			AlertDialog.Builder builder3 = new AlertDialog.Builder(ctx);
    			builder3.setMessage(PC.getText(ctx, "confirmDelete"));
    			builder3.setPositiveButton(PC.getText(ctx, "confirmDeleteButton"), new DialogInterface.OnClickListener(){ @Override public void onClick(DialogInterface dialog, int which) {
    					int r = (Integer) v.getTag();
    					
    					DbAdapter db = new DbAdapter(v.getContext().getApplicationContext());
    					db.open();
    					db.deleteRun(r);
    					db.close();
    					
    					ctx.recreate();
    					
    				}});
    			builder3.setNegativeButton(PC.getText(ctx, "confirmDeleteCancel"), null);
    			builder3.show();
    			
    			
    			return false;
    		}});
    	}

    	@Override
    	public View newView(Context context, Cursor cursor, ViewGroup parent) {
    		LayoutInflater inflater = LayoutInflater.from(context);
    		View v = inflater.inflate(R.layout.history_row, parent, false);
    		bindView(v, context, cursor);
    		return v;
    	}
    }
}
