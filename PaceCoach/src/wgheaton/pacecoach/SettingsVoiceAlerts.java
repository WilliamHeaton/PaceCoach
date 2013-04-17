package wgheaton.pacecoach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import wgheaton.pacecoach.R;

public class SettingsVoiceAlerts extends Activity implements SettingsPicker.OnSettingsPickedListener{
	Context ctx;
	int profile;
	@Override
	public void onBackPressed(){
		Intent i = new Intent(ctx, Settings.class);
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
        
        ViewGroup g = (ViewGroup)findViewById(R.id.settings_container);


		DbAdapter db = new DbAdapter(this.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
        
        // TTS Settings 
        //TODO Figure out how to link to TTS Settings
        PC.settings_text(ctx, g,R.id.defaultpage,
        		PC.getText(ctx, "settings_tts"),
        		null,
        		null,
        		null,
            	null);

        

     		
        // Warmup Low Speed 
        if(prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP))==1)
        PC.settings_switch(ctx,profile,g,R.id.lowwarmspeed,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_warmup_speed_l"),
        	prefs.getString(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L_FORM)), 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L))==1,
        	PC.PREF_SPEAK_WARMUP_SPEED_L,
        	new OnClickListener(){ @Override public void onClick(View v) {

        		DbAdapter db = new DbAdapter(ctx.getApplicationContext());
        		db.open();
        		Cursor prefs = db.getPrefs(profile);
        		prefs.moveToFirst();
                
        		PC.settings_datatype_number_unit_picker(ctx,5,
        				PC.getText(ctx, "settings_va_warmup_speed_h"),
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L_DATA)),
        				PC.cell_speed,
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L_INT1)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L_INT2)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_L_UNIT)),
        				PC.units_speed);
        		prefs.close();
        		db.close();
			}});

        // Warmup High Speed 
        if(prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP))==1)
        PC.settings_switch(ctx,profile,g,R.id.highwarmspeed,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_warmup_speed_h"),
        	prefs.getString(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H_FORM)), 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H))==1,
        	PC.PREF_SPEAK_WARMUP_SPEED_H,
        	new OnClickListener(){ @Override public void onClick(View v) {

        		DbAdapter db = new DbAdapter(ctx.getApplicationContext());
        		db.open();
        		Cursor prefs = db.getPrefs(profile);
        		prefs.moveToFirst();

        		PC.settings_datatype_number_unit_picker(ctx,6,
        				PC.getText(ctx, "settings_va_warmup_speed_h"),
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H_DATA)),
        				PC.cell_speed,
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H_INT1)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H_INT2)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_WARMUP_SPEED_H_UNIT)),
        				PC.units_speed);
        		prefs.close();
        		db.close();
			}});
        
        
        // Low Speed 
        PC.settings_switch(ctx,profile,g,R.id.lowspeed,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_speed_l"),
        	prefs.getString(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L_FORM)), 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L))==1,
        	PC.PREF_SPEAK_SPEED_L,
        	new OnClickListener(){ @Override public void onClick(View v) {

        		DbAdapter db = new DbAdapter(ctx.getApplicationContext());
        		db.open();
        		Cursor prefs = db.getPrefs(profile);
        		prefs.moveToFirst();
                
        		PC.settings_datatype_number_unit_picker(ctx,1,
        				PC.getText(ctx, "settings_va_speed_h"),
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L_DATA)),
        				PC.cell_speed,
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L_INT1)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L_INT2)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_L_UNIT)),
        				PC.units_speed);
        		prefs.close();
        		db.close();
			}});

        // High Speed 
        PC.settings_switch(ctx,profile,g,R.id.highspeed,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_speed_h"),
        	prefs.getString(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H_FORM)), 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H))==1,
        	PC.PREF_SPEAK_SPEED_H,
        	new OnClickListener(){ @Override public void onClick(View v) {

        		DbAdapter db = new DbAdapter(ctx.getApplicationContext());
        		db.open();
        		Cursor prefs = db.getPrefs(profile);
        		prefs.moveToFirst();

        		PC.settings_datatype_number_unit_picker(ctx,2,
        				PC.getText(ctx, "settings_va_speed_h"),
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H_DATA)),
        				PC.cell_speed,
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H_INT1)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H_INT2)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_H_UNIT)),
        				PC.units_speed);
        		prefs.close();
        		db.close();
			}});

        // Cooldown Low Speed 
        if(prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN))==1)
        PC.settings_switch(ctx,profile,g,R.id.lowcoolspeed,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_cooldown_speed_l"),
        	prefs.getString(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L_FORM)), 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L))==1,
        	PC.PREF_SPEAK_COOLDOWN_SPEED_L,
        	new OnClickListener(){ @Override public void onClick(View v) {

        		DbAdapter db = new DbAdapter(ctx.getApplicationContext());
        		db.open();
        		Cursor prefs = db.getPrefs(profile);
        		prefs.moveToFirst();
                
        		PC.settings_datatype_number_unit_picker(ctx,3,
        				PC.getText(ctx, "settings_va_cooldown_speed_h"),
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L_DATA)),
        				PC.cell_speed,
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L_INT1)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L_INT2)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_L_UNIT)),
        				PC.units_speed);
        		prefs.close();
        		db.close();
			}});

        // Cooldown High Speed 
        if(prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN))==1)
        PC.settings_switch(ctx,profile,g,R.id.highcoolspeed,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_cooldown_speed_h"),
        	prefs.getString(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H_FORM)), 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H))==1,
        	PC.PREF_SPEAK_COOLDOWN_SPEED_H,
        	new OnClickListener(){ @Override public void onClick(View v) {

        		DbAdapter db = new DbAdapter(ctx.getApplicationContext());
        		db.open();
        		Cursor prefs = db.getPrefs(profile);
        		prefs.moveToFirst();

        		PC.settings_datatype_number_unit_picker(ctx,4,
        				PC.getText(ctx, "settings_va_cooldown_speed_h"),
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H_DATA)),
        				PC.cell_speed,
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H_INT1)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H_INT2)), 
        				prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_COOLDOWN_SPEED_H_UNIT)),
        				PC.units_speed);
        		prefs.close();
        		db.close();
			}});

    	// Frequency of speed alerts 
 		PC.settings_text(ctx, g,R.id.speedfreq,
 				PC.getText(ctx, "settings_speed_freq"),
 				
 				PC.partsFormat(ctx, 0, prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_FREQ)), PC.UNITS_MIN), 
 				null,
 				new OnClickListener(){ @Override public void onClick(View v) {
 					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
 					db.open();
 					Cursor prefs = db.getPrefs(profile);
 					prefs.moveToFirst();
 					
 					PC.settings_number_picker(ctx,7,
 							PC.getText(ctx, "settings_speed_freq"),
 							1,
 							60,
 							prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPEED_FREQ)));
 					prefs.close();
 					db.close();
 				}},
 				null);
 		
        // Split 
        PC.settings_switch(ctx,profile,g,0,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_split"),
        	null, 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_SPLITS))==1,
        	PC.PREF_SPEAK_SPLITS,
        	null);

        // Start 
        PC.settings_switch(ctx,profile,g,0,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_start"),
        	null, 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_START))==1,
        	PC.PREF_SPEAK_START,
        	null);

        // Pause 
        PC.settings_switch(ctx,profile,g,0,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_pause"),
        	null, 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_PAUSE))==1,
        	PC.PREF_SPEAK_PAUSE,
        	null);
        
        // Resume 
        PC.settings_switch(ctx,profile,g,0,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_resume"),
        	null, 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_RESUME))==1,
        	PC.PREF_SPEAK_RESUME,
        	null);

        // Stop 
        PC.settings_switch(ctx,profile,g,0,PC.SETTINGS_SWITCH,
        	PC.getText(ctx, "settings_va_stop"),
        	null, 
        	prefs.getInt(prefs.getColumnIndex(PC.PREF_SPEAK_STOP))==1,
        	PC.PREF_SPEAK_STOP,
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
		


		switch(id){
			case 1:
				db.setPref(profile,PC.PREF_SPEAK_SPEED_L_INT1, msg.arg1);
				db.setPref(profile,PC.PREF_SPEAK_SPEED_L_INT2, msg.arg2);
				db.setPref(profile,PC.PREF_SPEAK_SPEED_L_DATA, ((int[])msg.obj)[0]);
				db.setPref(profile,PC.PREF_SPEAK_SPEED_L_UNIT, ((int[])msg.obj)[1]);
				db.setPref(profile,PC.PREF_SPEAK_SPEED_L_VALU, PC.partsVal(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]));
				
				formatted = "During Workout, Alert when " 
							+ PC.getText(ctx,PC.CELLS+((int[])msg.obj)[0]) 
							+ " < " 
							+ PC.partsFormat(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]);
				
				db.setPref(profile,PC.PREF_SPEAK_SPEED_L_FORM,formatted);
				((TextView) findViewById(R.id.lowspeed).findViewById(R.id.description)).setText(formatted);
				
				break;
			case 2:
				db.setPref(profile,PC.PREF_SPEAK_SPEED_H_INT1, msg.arg1);
				db.setPref(profile,PC.PREF_SPEAK_SPEED_H_INT2, msg.arg2);
				db.setPref(profile,PC.PREF_SPEAK_SPEED_H_DATA, ((int[])msg.obj)[0]);
				db.setPref(profile,PC.PREF_SPEAK_SPEED_H_UNIT, ((int[])msg.obj)[1]);
				db.setPref(profile,PC.PREF_SPEAK_SPEED_H_VALU, (float) PC.partsVal(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]));
				formatted =  "During Workout, Alert when " 
							+ PC.getText(ctx,PC.CELLS+((int[])msg.obj)[0]) 
							+ " > " 
							+ PC.partsFormat(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]);

				db.setPref(profile,PC.PREF_SPEAK_SPEED_H_FORM,formatted);
				((TextView) findViewById(R.id.highspeed).findViewById(R.id.description)).setText(formatted);
				break;
		

			case 3:
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_L_INT1, msg.arg1);
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_L_INT2, msg.arg2);
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_L_DATA, ((int[])msg.obj)[0]);
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_L_UNIT, ((int[])msg.obj)[1]);
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_L_VALU, PC.partsVal(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]));
				
				formatted = "During Cooldown, Alert when " 
							+ PC.getText(ctx,PC.CELLS+((int[])msg.obj)[0]) 
							+ " < " 
							+ PC.partsFormat(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]);
				
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_L_FORM,formatted);
				((TextView) findViewById(R.id.lowcoolspeed).findViewById(R.id.description)).setText(formatted);
				
				break;
			case 4:
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_H_INT1, msg.arg1);
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_H_INT2, msg.arg2);
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_H_DATA, ((int[])msg.obj)[0]);
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_H_UNIT, ((int[])msg.obj)[1]);
				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_H_VALU, (float) PC.partsVal(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]));
				formatted =  "During Cooldown, Alert when " 
							+ PC.getText(ctx,PC.CELLS+((int[])msg.obj)[0]) 
							+ " > " 
							+ PC.partsFormat(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]);

				db.setPref(profile,PC.PREF_SPEAK_COOLDOWN_SPEED_H_FORM,formatted);
				((TextView) findViewById(R.id.highcoolspeed).findViewById(R.id.description)).setText(formatted);
				break;
			case 5:
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_L_INT1, msg.arg1);
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_L_INT2, msg.arg2);
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_L_DATA, ((int[])msg.obj)[0]);
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_L_UNIT, ((int[])msg.obj)[1]);
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_L_VALU, PC.partsVal(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]));
				
				formatted = "During Warmup, Alert when " 
							+ PC.getText(ctx,PC.CELLS+((int[])msg.obj)[0]) 
							+ " < " 
							+ PC.partsFormat(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]);
				
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_L_FORM,formatted);
				((TextView) findViewById(R.id.lowwarmspeed).findViewById(R.id.description)).setText(formatted);
				
				break;
			case 6:
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_H_INT1, msg.arg1);
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_H_INT2, msg.arg2);
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_H_DATA, ((int[])msg.obj)[0]);
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_H_UNIT, ((int[])msg.obj)[1]);
				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_H_VALU, PC.partsVal(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]));
				formatted =  "During Warmup, Alert when " 
							+ PC.getText(ctx,PC.CELLS+((int[])msg.obj)[0]) 
							+ " > " 
							+ PC.partsFormat(ctx, msg.arg1, msg.arg2, ((int[])msg.obj)[1]);

				db.setPref(profile,PC.PREF_SPEAK_WARMUP_SPEED_H_FORM,formatted);
				((TextView) findViewById(R.id.highwarmspeed).findViewById(R.id.description)).setText(formatted);
				break;
				

			case 7:
				db.setPref(profile,PC.PREF_SPEAK_SPEED_FREQ, msg.arg1);
				
				((TextView) findViewById(R.id.speedfreq).findViewById(R.id.description)).setText(PC.partsFormat(ctx, 0, msg.arg1, PC.UNITS_MIN));
				break;
		}
		db.close();
		prefs.close();
	}
}
