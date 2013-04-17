package wgheaton.pacecoach;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import wgheaton.pacecoach.R;

public class SettingsPlan extends Activity implements SettingsPicker.OnSettingsPickedListener{
	Activity ctx;
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



		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();


		ViewGroup g = (ViewGroup)findViewById(R.id.settings_container);

		// Manual Splits 
		PC.settings_switch(ctx,g,R.id.manualsplit,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_manualsplits"),
				null, 
				prefs.getInt(prefs.getColumnIndex(PC.PREF_MANUAL_SPLITS))==1,
				new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					db.setPref(profile,PC.PREF_MANUAL_SPLITS, isChecked?1:0);
					db.close();

					if(isChecked)
						((CompoundButton) ctx.findViewById(R.id.autosplit).findViewById(R.id.switch1)).setChecked(false);
				}},
				null);

		// Auto Splits 
		PC.settings_switch(ctx,g,R.id.autosplit,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_autosplits"),
				prefs.getString(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT_DISTANCE_FORM)), 
				prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT))==1,
				new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					db.setPref(profile,PC.PREF_AUTO_SPLIT, isChecked?1:0);
					db.close();

					if(isChecked)
						((CompoundButton) ctx.findViewById(R.id.manualsplit).findViewById(R.id.switch1)).setChecked(false);
				}},
				new OnClickListener(){ @Override public void onClick(View v) {
					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					Cursor prefs = db.getPrefs(profile);
					prefs.moveToFirst();

					PC.settings_number_unit_picker(ctx,1,
							PC.getText(ctx, "settings_autosplits"),
							prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT_DISTANCE_INT1)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT_DISTANCE_INT2)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_AUTO_SPLIT_DISTANCE_UNIT)),
							PC.units_split);
					prefs.close();
					db.close();
				}});

		// Warmup Duration 
		PC.settings_switch(ctx,g,R.id.warmup,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_duration_warmup"),
				prefs.getString(prefs.getColumnIndex(PC.PREF_WARMUP_FORM)), 
				prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP))==1,

				new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					db.setPref(profile,PC.PREF_WARMUP, isChecked?1:0);
					db.close();

					if(!isChecked){
						((CompoundButton)ctx.findViewById(R.id.warmupinclude).findViewById(R.id.switch1)).setChecked(false);
						((CompoundButton)ctx.findViewById(R.id.warmupsplits).findViewById(R.id.switch1)).setChecked(false);

						((View)ctx.findViewById(R.id.warmupinclude).getParent()).setVisibility(View.GONE);
						((View)ctx.findViewById(R.id.warmupsplits).getParent()).setVisibility(View.GONE);
						((View)ctx.findViewById(R.id.warmupend).getParent()).setVisibility(View.GONE);
					}else{
						((View)ctx.findViewById(R.id.warmupinclude).getParent()).setVisibility(View.VISIBLE);
						((View)ctx.findViewById(R.id.warmupsplits).getParent()).setVisibility(View.VISIBLE);
						((View)ctx.findViewById(R.id.warmupend).getParent()).setVisibility(View.VISIBLE);
					}
				}},
				new OnClickListener(){ @Override public void onClick(View v) {
					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					Cursor prefs = db.getPrefs(profile);
					prefs.moveToFirst();

					PC.settings_number_unit_picker(ctx,2,
							PC.getText(ctx, "settings_duration_warmup"),
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP_INT1)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP_INT2)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP_UNIT)),
							PC.units_duration);
					prefs.close();
					db.close();
				}});

		// Warmup Over Action
		PC.settings_text(ctx, g,R.id.warmupend,
				PC.getText(ctx, "settings_warmup_end_action"),
				PC.getText(ctx, "settings_warmup_end_action_more"),
				PC.getText(ctx,PC.FINISH_MODE+prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP_END_ACTION))),
				new OnClickListener(){ @Override public void onClick(final View v) {

					PC.settings_select_from_list(ctx,PC.getText(ctx, "settings_warmup_end_action"),PC.string_list(ctx,PC.FINISH_MODE,PC.end_warmup_list),PC.end_warmup_list,new Callback(){ @Override public boolean handleMessage(Message msg) {


						DbAdapter db = new DbAdapter(ctx.getApplicationContext());
						db.open();
						db.setPref(profile,PC.PREF_WARMUP_END_ACTION, msg.arg1);
						db.close();

						((TextView)v.findViewById(R.id.value)).setText(PC.getText(ctx,PC.FINISH_MODE+msg.arg1));
						return false;
					}});
				}},
				null);


		// Warmup Include 
		PC.settings_switch(ctx,g,R.id.warmupinclude,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_duration_warmup_include"),
				PC.getText(ctx, "settings_duration_warmup_include_more"),
				prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP_INCL))==1,

				new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					db.setPref(profile,PC.PREF_WARMUP_INCL, isChecked?1:0);
					db.close();

					if(isChecked){
						((CompoundButton)ctx.findViewById(R.id.warmup).findViewById(R.id.switch1)).setChecked(true);
						((CompoundButton)ctx.findViewById(R.id.warmupsplits).findViewById(R.id.switch1)).setChecked(true);
					}
				}},
				null);

		// Warmup Splits 
		PC.settings_switch(ctx,g,R.id.warmupsplits,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_duration_warmup_splits"),
				null,
				prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP_SPLITS))==1,

				new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					db.setPref(profile,PC.PREF_WARMUP_SPLITS, isChecked?1:0);
					db.close();


					if(isChecked){
						((CompoundButton)ctx.findViewById(R.id.warmup).findViewById(R.id.switch1)).setChecked(true);
					}else{
						((CompoundButton)ctx.findViewById(R.id.warmupinclude).findViewById(R.id.switch1)).setChecked(false);
					}
				}},
				null);




		if(prefs.getInt(prefs.getColumnIndex(PC.PREF_WARMUP))!=1){
			((View)ctx.findViewById(R.id.warmupinclude).getParent()).setVisibility(View.GONE);
			((View)ctx.findViewById(R.id.warmupsplits).getParent()).setVisibility(View.GONE);
			((View)ctx.findViewById(R.id.warmupend).getParent()).setVisibility(View.GONE);
		}

		// Workout Duration 
		PC.settings_text(ctx,g,R.id.workout,
				PC.getText(ctx, "settings_duration_workout"),
				prefs.getString(prefs.getColumnIndex(PC.PREF_WORKOUT_FORM)), 
				null,
				new OnClickListener(){ @Override public void onClick(View v) {
					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					Cursor prefs = db.getPrefs(profile);
					prefs.moveToFirst();

					PC.settings_number_unit_picker(ctx,3,
							PC.getText(ctx, "settings_duration_workout"),
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WORKOUT_INT1)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WORKOUT_INT2)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WORKOUT_UNIT)),
							PC.units_duration);
					prefs.close();
					db.close();
				}},
				new OnClickListener(){ @Override public void onClick(View v) {
					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					Cursor prefs = db.getPrefs(profile);
					prefs.moveToFirst();

					PC.settings_number_unit_picker(ctx,3,
							PC.getText(ctx, "settings_duration_workout"),
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WORKOUT_INT1)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WORKOUT_INT2)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_WORKOUT_UNIT)),
							PC.units_duration);
					prefs.close();
					db.close();
				}});

		
		// Workout Over Action
		PC.settings_text(ctx, g,R.id.workoutend1,
				PC.getText(ctx, "settings_workout_end_action"),
				PC.getText(ctx, "settings_workout_end_action_more"),
				PC.getText(ctx,PC.FINISH_MODE+prefs.getInt(prefs.getColumnIndex(PC.PREF_WORKOUT_END_ACTION))),
				new OnClickListener(){ @Override public void onClick(final View v) {

					PC.settings_select_from_list(ctx,PC.getText(ctx, "settings_workout_end_action"),PC.string_list(ctx,PC.FINISH_MODE,PC.end_workout_list1),PC.end_workout_list1,new Callback(){ @Override public boolean handleMessage(Message msg) {


						DbAdapter db = new DbAdapter(ctx.getApplicationContext());
						db.open();
						db.setPref(profile,PC.PREF_WORKOUT_END_ACTION, msg.arg1);
						db.close();


						((TextView)(ctx.findViewById(R.id.workoutend1)).findViewById(R.id.value)).setText(PC.getText(ctx,PC.FINISH_MODE+msg.arg1));
						((TextView)(ctx.findViewById(R.id.workoutend2)).findViewById(R.id.value)).setText(PC.getText(ctx,PC.FINISH_MODE+msg.arg1));
						return false;
					}});
				}},
				null);
		// Workout Over Action
		PC.settings_text(ctx, g,R.id.workoutend2,
				PC.getText(ctx, "settings_workout_end_action"),
				PC.getText(ctx, "settings_workout_end_action_more"),
				PC.getText(ctx,PC.FINISH_MODE+prefs.getInt(prefs.getColumnIndex(PC.PREF_WORKOUT_END_ACTION))),
				new OnClickListener(){ @Override public void onClick(final View v) {

					PC.settings_select_from_list(ctx,PC.getText(ctx, "settings_workout_end_action"),PC.string_list(ctx,PC.FINISH_MODE,PC.end_workout_list2),PC.end_workout_list2,new Callback(){ @Override public boolean handleMessage(Message msg) {


						DbAdapter db = new DbAdapter(ctx.getApplicationContext());
						db.open();
						db.setPref(profile,PC.PREF_WORKOUT_END_ACTION, msg.arg1);
						db.close();

						((TextView)(ctx.findViewById(R.id.workoutend1)).findViewById(R.id.value)).setText(PC.getText(ctx,PC.FINISH_MODE+msg.arg1));
						((TextView)(ctx.findViewById(R.id.workoutend2)).findViewById(R.id.value)).setText(PC.getText(ctx,PC.FINISH_MODE+msg.arg1));
						return false;
					}});
				}},
				null);
		// Cooldown Duration 
		PC.settings_switch(ctx,g,R.id.cooldown,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_duration_cooldown"),
				prefs.getString(prefs.getColumnIndex(PC.PREF_COOLDOWN_FORM)), 
				prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN))==1,

				new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					db.setPref(profile,PC.PREF_COOLDOWN, isChecked?1:0);
					Cursor p = db.getPrefs(profile);
					p.moveToFirst();
					int wea = p.getInt(p.getColumnIndex(PC.PREF_WORKOUT_END_ACTION));
					p.close();
					db.close();

					if(!isChecked){
						((CompoundButton)ctx.findViewById(R.id.cooldowninclude).findViewById(R.id.switch1)).setChecked(false);
						((CompoundButton)ctx.findViewById(R.id.cooldownsplits).findViewById(R.id.switch1)).setChecked(false);

						((View)ctx.findViewById(R.id.cooldowninclude).getParent()).setVisibility(View.GONE);
						((View)ctx.findViewById(R.id.cooldownsplits).getParent()).setVisibility(View.GONE);
						((View)ctx.findViewById(R.id.cooldownend).getParent()).setVisibility(View.GONE);
						((View)ctx.findViewById(R.id.workoutend2).getParent()).setVisibility(View.GONE);
						((View)ctx.findViewById(R.id.workoutend1).getParent()).setVisibility(View.VISIBLE);
						
						if(wea==PC.END_MOVE_ON_COOLDOWN){
							db = new DbAdapter(ctx.getApplicationContext());
							db.open();
							db.setPref(profile,PC.PREF_WORKOUT_END_ACTION, PC.END_KEEP_GOING);
							db.close();
							((TextView)(ctx.findViewById(R.id.workoutend1)).findViewById(R.id.value)).setText(PC.getText(ctx,PC.FINISH_MODE+PC.END_KEEP_GOING));
							((TextView)(ctx.findViewById(R.id.workoutend2)).findViewById(R.id.value)).setText(PC.getText(ctx,PC.FINISH_MODE+PC.END_KEEP_GOING));
						}
						
					}else{

						((View)ctx.findViewById(R.id.cooldowninclude).getParent()).setVisibility(View.VISIBLE);
						((View)ctx.findViewById(R.id.cooldownsplits).getParent()).setVisibility(View.VISIBLE);
						((View)ctx.findViewById(R.id.cooldownend).getParent()).setVisibility(View.VISIBLE);
						((View)ctx.findViewById(R.id.workoutend1).getParent()).setVisibility(View.GONE);
						((View)ctx.findViewById(R.id.workoutend2).getParent()).setVisibility(View.VISIBLE);
					}
				}},
				new OnClickListener(){ @Override public void onClick(View v) {
					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					Cursor prefs = db.getPrefs(profile);
					prefs.moveToFirst();

					PC.settings_number_unit_picker(ctx,4,
							PC.getText(ctx, "settings_duration_cooldown"),
							prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN_INT1)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN_INT2)), 
							prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN_UNIT)),
							PC.units_duration);
					prefs.close();
					db.close();
				}});


		// Cooldown Over Action
		PC.settings_text(ctx, g,R.id.cooldownend,
				PC.getText(ctx, "settings_cooldown_end_action"),
				PC.getText(ctx, "settings_cooldown_end_action_more"),
				PC.getText(ctx,PC.FINISH_MODE+prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN_END_ACTION))),
				new OnClickListener(){ @Override public void onClick(final View v) {

					PC.settings_select_from_list(ctx,PC.getText(ctx, "settings_cooldown_end_action"),PC.string_list(ctx,PC.FINISH_MODE,PC.end_cooldown_list),PC.end_cooldown_list,new Callback(){ @Override public boolean handleMessage(Message msg) {


						DbAdapter db = new DbAdapter(ctx.getApplicationContext());
						db.open();
						db.setPref(profile,PC.PREF_COOLDOWN_END_ACTION, msg.arg1);
						db.close();

						((TextView)v.findViewById(R.id.value)).setText(PC.getText(ctx,PC.FINISH_MODE+msg.arg1));
						return false;
					}});
				}},
				null);

		// Cooldown Include 
		PC.settings_switch(ctx,g,R.id.cooldowninclude,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_duration_cooldown_include"),
				PC.getText(ctx, "settings_duration_cooldown_include_more"),
				prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN_INCL))==1,

				new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					db.setPref(profile,PC.PREF_COOLDOWN_INCL, isChecked?1:0);
					db.close();

					if(isChecked){
						((CompoundButton)ctx.findViewById(R.id.cooldown).findViewById(R.id.switch1)).setChecked(true);
					}
				}},
				null);

		// Cooldown Splits 
		PC.settings_switch(ctx,g,R.id.cooldownsplits,PC.SETTINGS_SWITCH,
				PC.getText(ctx, "settings_duration_cooldown_splits"),
				null,
				prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN_SPLITS))==1,

				new OnCheckedChangeListener(){ @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					DbAdapter db = new DbAdapter(ctx.getApplicationContext());
					db.open();
					db.setPref(profile,PC.PREF_COOLDOWN_SPLITS, isChecked?1:0);
					db.close();

					if(isChecked){
						((CompoundButton)ctx.findViewById(R.id.cooldown).findViewById(R.id.switch1)).setChecked(true);
					}
				}},
				null);		




		if(prefs.getInt(prefs.getColumnIndex(PC.PREF_COOLDOWN))!=1){
			((View)ctx.findViewById(R.id.cooldowninclude).getParent()).setVisibility(View.GONE);
			((View)ctx.findViewById(R.id.cooldownsplits).getParent()).setVisibility(View.GONE);
			((View)ctx.findViewById(R.id.cooldownend).getParent()).setVisibility(View.GONE);
			((View)ctx.findViewById(R.id.workoutend2).getParent()).setVisibility(View.GONE);
		}else{
			((View)ctx.findViewById(R.id.workoutend1).getParent()).setVisibility(View.GONE);
		}


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
			formatted = PC.partsFormat(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);
			val = PC.partsVal(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);

			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_INT1, msg.arg1);
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_INT2, msg.arg2);
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_UNIT, (Integer)msg.obj);
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_VALU, val);
			db.setPref(profile,PC.PREF_AUTO_SPLIT_DISTANCE_FORM, formatted);
			((TextView) findViewById(R.id.autosplit).findViewById(R.id.description)).setText(formatted);
			break;
		case 2:
			formatted = PC.partsFormat(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);
			val = PC.partsVal(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);

			db.setPref(profile,PC.PREF_WARMUP_INT1, msg.arg1);
			db.setPref(profile,PC.PREF_WARMUP_INT2, msg.arg2);
			db.setPref(profile,PC.PREF_WARMUP_UNIT, (Integer)msg.obj);
			db.setPref(profile,PC.PREF_WARMUP_VALU, val);
			db.setPref(profile,PC.PREF_WARMUP_FORM, formatted);
			((TextView) findViewById(R.id.warmup).findViewById(R.id.description)).setText(formatted);
			break;
		case 3:
			formatted = PC.partsFormat(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);
			val = PC.partsVal(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);

			db.setPref(profile,PC.PREF_WORKOUT_INT1, msg.arg1);
			db.setPref(profile,PC.PREF_WORKOUT_INT2, msg.arg2);
			db.setPref(profile,PC.PREF_WORKOUT_UNIT, (Integer)msg.obj);
			db.setPref(profile,PC.PREF_WORKOUT_VALU, val);
			db.setPref(profile,PC.PREF_WORKOUT_FORM, formatted);
			((TextView) findViewById(R.id.workout).findViewById(R.id.description)).setText(formatted);
			break;
		case 4:
			formatted = PC.partsFormat(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);
			val = PC.partsVal(ctx, msg.arg1, msg.arg2, (Integer)msg.obj);

			db.setPref(profile,PC.PREF_COOLDOWN_INT1, msg.arg1);
			db.setPref(profile,PC.PREF_COOLDOWN_INT2, msg.arg2);
			db.setPref(profile,PC.PREF_COOLDOWN_UNIT, (Integer)msg.obj);
			db.setPref(profile,PC.PREF_COOLDOWN_VALU, val);
			db.setPref(profile,PC.PREF_COOLDOWN_FORM, formatted);
			((TextView) findViewById(R.id.cooldown).findViewById(R.id.description)).setText(formatted);
			break;

		}
		db.close();
		prefs.close();
	}
}
