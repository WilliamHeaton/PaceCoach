package com.cypho.pacecoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import wgheaton.pacecoach.R;

public class Settings extends Activity implements SettingsPicker.OnSettingsPickedListener{
	Activity ctx;
	int profile;
	
	@Override
	public void onBackPressed(){
		Intent i = new Intent(ctx, History.class);
		i.putExtra(PC.PREF_PROFILE, profile);
		startActivity(i);
		finish();
	}

    @Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(PC.PREF_PROFILE,profile);
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings);

		ctx = this;
		
       if(savedInstanceState!=null && savedInstanceState.getInt(PC.PREF_PROFILE)!=0){
        	profile = savedInstanceState.getInt(PC.PREF_PROFILE);
        }else if(getIntent().getExtras()!= null && getIntent().getExtras().getInt(PC.PREF_PROFILE)!=0)
			profile =  getIntent().getExtras().getInt(PC.PREF_PROFILE);
	        
		
		
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
		
		
		ViewGroup g = (ViewGroup)findViewById(R.id.settings_container);

		// Profile Name 
		PC.settings_text(ctx, g,R.id.profile,
				PC.getText(ctx, "settings_profile"),
				null,
				prefs.getString(prefs.getColumnIndex(PC.DB_PROFILE_LABEL)),
				new OnClickListener(){ @Override public void onClick(View v) {
					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					Cursor prefs = db.getPrefs(profile);
					prefs.moveToFirst();
					PC.settings_edit_text(ctx, 4, PC.getText(ctx, "settings_profile"), prefs.getString(prefs.getColumnIndex(PC.DB_PROFILE_LABEL)));
					prefs.close();
					db.close();
				}},
				null);

		// Plan 
		PC.settings_text(ctx,g,0,
				PC.getText(ctx, "settings_plan"),
				null,
				null,
				new OnClickListener(){ @Override public void onClick(View v) {

					Intent i = new Intent(ctx, SettingsPlan.class);
					i.putExtra(PC.PREF_PROFILE, profile);
					startActivity(i);
					finish();
				}},
				new OnClickListener(){ @Override public void onClick(View v) {

					Intent i = new Intent(ctx, SettingsPlan.class);
					i.putExtra(PC.PREF_PROFILE, profile);
					startActivity(i);
					finish();
				}});
		// Voice Alerts 
		PC.settings_switch(ctx,profile,g,0,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_voicealerts"),
				null, 
				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK))==1,
				PC.PREF_SPEAK,
				new OnClickListener(){ @Override public void onClick(View v) {
					Intent i = new Intent(ctx, SettingsVoiceAlerts.class);
					i.putExtra(PC.PREF_PROFILE, profile);
					startActivity(i);
					finish();
				}});

		// Number of Pages 
		PC.settings_text(ctx, g,R.id.numberofpages,
				PC.getText(ctx, "settings_numpages"),
				null,
				String.valueOf(prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_NUMBER))),
				new OnClickListener(){ @Override public void onClick(View v) {
					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					Cursor prefs = db.getPrefs(profile);
					prefs.moveToFirst();
					
					PC.settings_number_picker(ctx,1,
							PC.getText(ctx, "settings_numpages"),
							PC.DEFAULT_PREF_MIN_PAGES,
							PC.DEFAULT_PREF_MAX_PAGES,
							prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_NUMBER)));
					prefs.close();
					db.close();
				}},
				new OnClickListener(){ @Override public void onClick(View v) {
					Intent i = new Intent(ctx, SettingsConfigPages.class);
					i.putExtra(PC.PREF_PROFILE, profile);
					startActivity(i);
					finish();
				}});
		// Default Page
		PC.settings_text(ctx, g,R.id.defaultpage,
				PC.getText(ctx, "settings_defaultpage"),
				null,
				String.valueOf(prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_DEFAULT))),
				new OnClickListener(){ @Override public void onClick(View v) {
					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					Cursor prefs = db.getPrefs(profile);
					prefs.moveToFirst();
					
					PC.settings_number_picker(ctx,2,
							PC.getText(ctx, "settings_defaultpage"),
							1,
							prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_NUMBER)),
							prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_DEFAULT)));
					prefs.close();
					db.close();
				}},
				null);

		// Lock Screens 
		PC.settings_switch(ctx,profile,g,0,PC.SETTINGS_CHECKBOX,
				PC.getText(ctx, "settings_lockscreens"),
				PC.getText(ctx, "settings_lockscreens_desc"),
				prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_LOCK))==1,
				PC.PREF_PAGE_LOCK,
				null);

		// Rotation Mode
		PC.settings_text(ctx, g,0,
				PC.getText(ctx, "settings_rotate"),
				null,
				PC.getText(ctx,PC.SREEN_ROTATIONS+prefs.getInt(prefs.getColumnIndex(PC.PREF_ROTATE))),
				new OnClickListener(){ @Override public void onClick(final View v) {

					PC.settings_select_from_list(ctx,PC.getText(ctx, "settings_rotate"),PC.string_list(ctx,PC.SREEN_ROTATIONS,PC.rotation_list),PC.rotation_list,new Callback(){ @Override public boolean handleMessage(Message msg) {
						

						DbAdapter db = new DbAdapter(ctx.getApplicationContext());
						db.open();
						db.setPref(profile,PC.PREF_ROTATE, msg.arg1);
						db.close();
						
						((TextView)v.findViewById(R.id.value)).setText(PC.getText(ctx,PC.SREEN_ROTATIONS+msg.arg1));
						return false;
					}});
				}},
				null);

		// Portrait Mode
		PC.settings_text(ctx, g,0,
				PC.getText(ctx, "settings_landscape"),
				null,
				PC.getText(ctx,PC.LANDSCAPE_MODE+prefs.getInt(prefs.getColumnIndex(PC.PREF_PORTRAIT))),
				new OnClickListener(){ @Override public void onClick(final View v) {

					PC.settings_select_from_list(ctx,PC.getText(ctx, "settings_landscape"),PC.string_list(ctx,PC.LANDSCAPE_MODE,PC.portrait_list),PC.portrait_list,new Callback(){ @Override public boolean handleMessage(Message msg) {
						

						DbAdapter db = new DbAdapter(ctx.getApplicationContext());
						db.open();
						db.setPref(profile,PC.PREF_PORTRAIT, msg.arg1);
						db.close();
						
						((TextView)v.findViewById(R.id.value)).setText(PC.getText(ctx,PC.LANDSCAPE_MODE+msg.arg1));
						return false;
					}});
				}},
				null);
		// Map Mode
		PC.settings_text(ctx, g,0,
				PC.getText(ctx, "settings_mapMode"),
				null,
				PC.getText(ctx,PC.MAP_MODES+prefs.getInt(prefs.getColumnIndex(PC.PREF_MAP_MODE))),
				new OnClickListener(){ @Override public void onClick(final View v) {

					PC.settings_select_from_list(ctx,PC.getText(ctx, "settings_mapMode"),PC.string_list(ctx,PC.MAP_MODES,PC.map_mode_list),PC.map_mode_list,new Callback(){ @Override public boolean handleMessage(Message msg) {
						

						DbAdapter db = new DbAdapter(ctx.getApplicationContext());
						db.open();
						db.setPref(profile,PC.PREF_MAP_MODE, msg.arg1);
						db.close();
						
						((TextView)v.findViewById(R.id.value)).setText(PC.getText(ctx,PC.MAP_MODES+msg.arg1));
						return false;
					}});
				}},
				null);

		// Map Type
		PC.settings_text(ctx, g,0,
				PC.getText(ctx, "settings_mapType"),
				null,
				PC.getText(ctx,PC.MAP_TYPES+prefs.getInt(prefs.getColumnIndex(PC.PREF_MAP_TYPE))),
				new OnClickListener(){ @Override public void onClick(final View v) {

					PC.settings_select_from_list(ctx,PC.getText(ctx, "settings_mapType"),PC.string_list(ctx,PC.MAP_TYPES,PC.map_type_list),PC.map_type_list,new Callback(){ @Override public boolean handleMessage(Message msg) {
						

						DbAdapter db = new DbAdapter(ctx.getApplicationContext());
						db.open();
						db.setPref(profile,PC.PREF_MAP_TYPE, msg.arg1);
						db.close();
						
						((TextView)v.findViewById(R.id.value)).setText(PC.getText(ctx,PC.MAP_TYPES+msg.arg1));
						return false;
					}});
				}},
				null);
		

		// DELETE PROFILE 
		PC.settings_text(ctx, g,R.id.profile,
				PC.getText(ctx, "settings_delete_profile"),
				null,
				null,
				new OnClickListener(){ @Override public void onClick(View v) {
					
	    			AlertDialog.Builder builder3 = new AlertDialog.Builder(ctx);
	    			builder3.setTitle(  PC.getText(ctx, "settings_delete_profile"));
	    			builder3.setMessage(PC.getText(ctx, "settings_delete_profile_more"));
	    			builder3.setPositiveButton(PC.getText(ctx, "confirmDeleteButton"), new DialogInterface.OnClickListener(){ @Override public void onClick(DialogInterface dialog, int which) {
	    					
	    					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
	    					db.open();
	    					db.deleteProfile(profile);
	    					db.close();
	    					
	    					Intent i = new Intent(ctx, History.class);
	    					startActivity(i);
	    					finish();
	    					
	    				}});
	    			builder3.setNegativeButton(PC.getText(ctx, "confirmDeleteCancel"), null);
	    			builder3.show();
					
				}},
				null);
		
		prefs.close();
		db.close();
	}


	@Override
	public void onSettingsPicked(int id, Message msg) {
		

		DbAdapter db = new DbAdapter(ctx.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();

		String formatted;
		float val;
		
		switch(id){
		case 1:
			
			db.setPref(profile,PC.PREF_PAGE_NUMBER, msg.arg1);
			((TextView) findViewById(R.id.numberofpages).findViewById(R.id.value)).setText(String.valueOf(msg.arg1));

			if(msg.arg1<prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_DEFAULT))){
				
				db.setPref(profile,PC.PREF_PAGE_DEFAULT, msg.arg1);
				((TextView) findViewById(R.id.defaultpage).findViewById(R.id.value)).setText(String.valueOf(msg.arg1));
			}
			
			db.updateNumPages(profile,msg.arg1);

			break;
		case 2:
			((TextView) findViewById(R.id.defaultpage).findViewById(R.id.value)).setText(String.valueOf(msg.arg1));

			db.setPref(profile,PC.PREF_PAGE_DEFAULT, msg.arg1);

			break;
		case 3:
			formatted = PC.partsFormat(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);
			val = PC.partsVal(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);
			
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_INT1, msg.arg1);
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_INT2, msg.arg2);
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_UNIT, (Integer)msg.obj);
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_VALU, val);
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_FORM, formatted);
			((TextView) findViewById(R.id.autosplit).findViewById(R.id.description)).setText(formatted);
			break;
		case 4:
			db.setPref(profile,PC.DB_PROFILE_LABEL, (String)msg.obj);
			((TextView) findViewById(R.id.profile).findViewById(R.id.value)).setText((String)msg.obj);
			break;
		}
		db.close();
		prefs.close();
	}
}
