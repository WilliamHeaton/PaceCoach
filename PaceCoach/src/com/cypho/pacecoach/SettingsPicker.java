package com.cypho.pacecoach;

import java.util.Locale;
import wgheaton.pacecoach.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class SettingsPicker extends DialogFragment {
	View v;
	int[] unit_list;
	int[] datatype_list;

    OnSettingsPickedListener callback;

    // Container Activity must implement this interface
    public interface OnSettingsPickedListener {
        public void onSettingsPicked(int id,Message msg);
    }
    public static SettingsPicker newInstance(Bundle b) {
    	
    	SettingsPicker frag = new SettingsPicker();
        frag.setArguments(b);
        return frag;
    }

    @Override
	public void onSaveInstanceState(Bundle outState) {

		outState.putInt("int1", 						((NumberPicker) v.findViewById(R.id.numberPicker1	)).getValue());
		outState.putInt("int2", 						((NumberPicker) v.findViewById(R.id.numberPicker2	)).getValue());
		outState.putInt("units", 		unit_list[		((NumberPicker) v.findViewById(R.id.unitPicker1 	)).getValue()]);
		outState.putInt("datatype", 	datatype_list[	((NumberPicker) v.findViewById(R.id.datatypePicker1	)).getValue()]);
		
		super.onSaveInstanceState(outState);
	}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	callback = (OnSettingsPickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSettingsPickedListener");
        }
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	Builder d = null;
    	
    	Bundle b = getArguments();
    	
		if(savedInstanceState != null)
			b.putAll(savedInstanceState);
		
		
		unit_list = new int[2];
		datatype_list = new int[2];
		switch (b.getInt("type")){
		//  Pick Integer without Units
			case 1:
				d = settings_select_number(	b.getString("title"),
											b.getInt("min"),
											b.getInt("max"),
											b.getInt("int1"));
				break;
		//  Pick Number & units
			case 2:
				unit_list = b.getIntArray("unit_list");
				datatype_list = new int[2];
				d = settings_select_number_units(b.getString("title"),
												 b.getInt("int1"),
												 b.getInt("int2"),
												 b.getInt("units"));
				break;
		// Pick Datatype, Number & Units
			case 3:
				unit_list 		= b.getIntArray("unit_list");
				datatype_list 	= b.getIntArray("datatype_list");
				d = settings_select_datatype_number_units(	b.getString("title"),
															b.getInt("datatype"),
															b.getInt("int1"),
															b.getInt("int2"),
															b.getInt("units"));
				break;
		// Enter Text
			case 4:
				
				d = settings_enter_text(b.getString("title"),
										b.getString("value"));
		}
		
        d.setNegativeButton(R.string.numberPickerCancel, new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int whichButton) { dialog.cancel(); }});
        return d.create();
    }

	private Builder settings_enter_text(String title, String value) {
		Builder d = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

    	v = inflater.inflate(R.layout.picker, null);

    	((TextView) v.findViewById(R.id.point)).setVisibility(View.GONE);
    	((NumberPicker) v.findViewById(R.id.numberPicker1)).setVisibility(View.GONE);
    	((NumberPicker) v.findViewById(R.id.numberPicker2)).setVisibility(View.GONE);
    	((NumberPicker) v.findViewById(R.id.unitPicker1)).setVisibility(View.GONE);
    	((NumberPicker) v.findViewById(R.id.datatypePicker1)).setVisibility(View.GONE);
    	((EditText)     v.findViewById(R.id.editText1)).setText(value);
    	
		d.setTitle(title);
		d.setView(v);

		d.setPositiveButton(R.string.numberPickerSave,new DialogInterface.OnClickListener() {  public void onClick(DialogInterface dialog, int whichButton) {
		  	Message msg = new Message();
		  	msg.obj =  (String) ((EditText)v.findViewById(R.id.editText1)).getText().toString();
		  	callback.onSettingsPicked(getArguments().getInt("id"),msg);
        }});
		
		return d;
	}

	private Builder settings_select_number(String title, int min, int max, int cur){
    	Builder d = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

    	v = inflater.inflate(R.layout.picker, null);

    	((TextView) v.findViewById(R.id.point)).setVisibility(View.GONE);
    	((NumberPicker) v.findViewById(R.id.numberPicker2)).setVisibility(View.GONE);
    	((NumberPicker) v.findViewById(R.id.unitPicker1)).setVisibility(View.GONE);
    	((NumberPicker) v.findViewById(R.id.datatypePicker1)).setVisibility(View.GONE);
    	((EditText)     v.findViewById(R.id.editText1)).setVisibility(View.GONE);
    	setDontSave();
    	
    	final NumberPicker p = (NumberPicker) v.findViewById(R.id.numberPicker1);
		p.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		p.setMinValue(min);
		p.setMaxValue(max);
		p.setValue(cur);
		d.setTitle(title);
		d.setView(v);
		d.setPositiveButton(R.string.numberPickerSave,new DialogInterface.OnClickListener() {  public void onClick(DialogInterface dialog, int whichButton) {
		  	Message msg = new Message();
		  	msg.arg1 =  (int) p.getValue();
		  	callback.onSettingsPicked(getArguments().getInt("id"),msg);
        }});
		return d;
	}
    private Builder settings_select_datatype_number_units(String title, int datatype, int int1, int int2, int units) {
    	Builder d = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    	v = inflater.inflate(R.layout.picker, null);

    	
    	updateMinMax(v,units);
    	((NumberPicker) v.findViewById(R.id.numberPicker1  )).setValue(int1);
		((NumberPicker) v.findViewById(R.id.numberPicker2  )).setValue(int2);
		((NumberPicker) v.findViewById(R.id.numberPicker1  )).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		((NumberPicker) v.findViewById(R.id.numberPicker2  )).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		((NumberPicker) v.findViewById(R.id.unitPicker1    )).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		((NumberPicker) v.findViewById(R.id.datatypePicker1)).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    	((EditText)     v.findViewById(R.id.editText1)).setVisibility(View.GONE);
    	setDontSave();
    	
		NumberPicker dt = (NumberPicker) v.findViewById(R.id.datatypePicker1);
		dt.setDisplayedValues(flushOut(PC.CELLS,datatype_list));
		dt.setMaxValue(datatype_list.length-1);
		int i = 0;
		for(int un:datatype_list){
			if(un == datatype) dt.setValue(i);
			i++;
		}
		
		NumberPicker u = (NumberPicker) v.findViewById(R.id.unitPicker1);
		u.setDisplayedValues(flushOut(PC.UNITS,unit_list));
		u.setMaxValue(unit_list.length-1);
		i = 0;
		for(int un:unit_list){
			if(un == units) u.setValue(i);
			i++;
		}
		u.setOnValueChangedListener(new OnValueChangeListener(){ @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				updateMinMax(v,unit_list[newVal]);
			}});
		d.setView(v);
		d.setTitle(title);
		d.setPositiveButton(R.string.numberPickerSave,new DialogInterface.OnClickListener() {  public void onClick(DialogInterface dialog, int whichButton) {
		  	Message msg = new Message();
		  	msg.arg1 =  (int) ((NumberPicker) v.findViewById(R.id.numberPicker1)).getValue();
		  	msg.arg2 =  (int) ((NumberPicker) v.findViewById(R.id.numberPicker2)).getValue();
		  	msg.obj = 	new int[] { datatype_list[((NumberPicker) v.findViewById(R.id.datatypePicker1	)).getValue()],
		  								unit_list[((NumberPicker) v.findViewById(R.id.unitPicker1 	)).getValue()]};
		  	
		  	callback.onSettingsPicked(getArguments().getInt("id"),msg);
        }});
		return d;
	}
    private Builder settings_select_number_units(String title, int int1, int int2, int j) {
    	Builder d = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    	v = inflater.inflate(R.layout.picker, null);

    	
    	updateMinMax(v,j);
    	((NumberPicker) v.findViewById(R.id.numberPicker1)).setValue(int1);
		((NumberPicker) v.findViewById(R.id.numberPicker2)).setValue(int2);
		((NumberPicker) v.findViewById(R.id.numberPicker1)).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		((NumberPicker) v.findViewById(R.id.numberPicker2)).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		((NumberPicker) v.findViewById(R.id.unitPicker1  )).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		((NumberPicker) v.findViewById(R.id.datatypePicker1)).setVisibility(View.GONE);
    	((EditText)     v.findViewById(R.id.editText1)).setVisibility(View.GONE);
    	setDontSave();
    	
    	
		NumberPicker u = (NumberPicker) v.findViewById(R.id.unitPicker1);
		u.setDisplayedValues(flushOut(PC.UNITS,unit_list));
		u.setMaxValue(unit_list.length-1);
		int i = 0;
		for(int un:unit_list){
			if(un == j) u.setValue(i);
			i++;
		}
		u.setOnValueChangedListener(new OnValueChangeListener(){ @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				updateMinMax(v,unit_list[newVal]);
			}});
		d.setView(v);
		d.setTitle(title);
		d.setPositiveButton(R.string.numberPickerSave,new DialogInterface.OnClickListener() {  public void onClick(DialogInterface dialog, int whichButton) {
		  	Message msg = new Message();
		  	msg.arg1 =  (int) ((NumberPicker) v.findViewById(R.id.numberPicker1)).getValue();
		  	msg.arg2 =  (int) ((NumberPicker) v.findViewById(R.id.numberPicker2)).getValue();
		  	msg.obj = 	(int) unit_list[((NumberPicker) v.findViewById(R.id.unitPicker1 )).getValue()];
		  	
		  	callback.onSettingsPicked(getArguments().getInt("id"),msg);
        }});
		return d;
	}
    private void updateMinMax(View v, int units){
    	NumberPicker p1 = (NumberPicker) v.findViewById(R.id.numberPicker1);
    	int min = min1(units);
    	int max = max1(units);
    	
    	int val = p1.getValue();
    	if(val>max) p1.setValue(max);
    	if(val<min) p1.setValue(min);
    	
    	p1.setDisplayedValues(null);
    	p1.setMinValue(min);
		p1.setMaxValue(max);
		p1.setDisplayedValues(vals1(units));
		
		p1.setVisibility(visINT1(units));
		LayoutParams lp = (LayoutParams) p1.getLayoutParams();
		lp.weight = wei1(units);
		p1.setLayoutParams(lp);

		TextView t1 = ((TextView) v.findViewById(R.id.point));
		t1.setText(point(units));
		t1.setVisibility(visINT2(units));
    	NumberPicker p2 = (NumberPicker) v.findViewById(R.id.numberPicker2);
    	
    	val = p2.getValue();
    	min = min2(units);
    	max = max2(units);
    	
    	if(val>max) p1.setValue(max);
    	if(val<min) p1.setValue(min);
    	
    	p2.setVisibility(visINT2(units));
    	p2.setMinValue(min);
		p2.setMaxValue(max);
		
		p2.setFormatter(form1(units));
    }
	private String[] flushOut(String t,int[] l){
		String[] out = new String[l.length];
		int i = 0;
		for(int s:l){
			out[i] = PC.getText(getActivity(),t + s);	
			i++;
		}
		return out;
	}

	private static String point(int unit) {
    	if(unit == PC.UNITS_MINMILE)	return ":";
    	if(unit == PC.UNITS_MINKILO)	return ":";
    	if(unit == PC.UNITS_MIN)		return ":";
    	return ".";
	}

	private static int visINT1(int unit) {
    	
    	if(unit == PC.UNITS_NODURATION)		return View.GONE;
		return View.VISIBLE;
	}
	private static int visINT2(int unit) {

    	if(unit == PC.UNITS_NODURATION)		return View.GONE;
    	if(unit == PC.UNITS_FEET)		return View.GONE;
    	if(unit == PC.UNITS_METERS)		return View.GONE;
    	if(unit == PC.UNITS_YARDS)		return View.GONE;
		return View.VISIBLE;
	}
	private static int wei1(int unit){

    	if(unit == PC.UNITS_FEET)		return 4;
    	if(unit == PC.UNITS_METERS)	return 4;
    	if(unit == PC.UNITS_YARDS)		return 4;
		return 2;
	}

	private static int max1(int unit){
    	if(unit == PC.UNITS_FEET)		return 39;
    	if(unit == PC.UNITS_METERS)		return 39;
    	if(unit == PC.UNITS_YARDS)		return 39;
    	
    	if(unit == PC.UNITS_MILES)		return 99;
    	if(unit == PC.UNITS_KILOMETERS) return 99;
    	
    	if(unit == PC.UNITS_KNOTS)		return 99;
    	if(unit == PC.UNITS_MPH)		return 99;
    	if(unit == PC.UNITS_KPH)		return 99;
    	if(unit == PC.UNITS_FPS)		return 99;
    	if(unit == PC.UNITS_MPS)		return 99;

    	if(unit == PC.UNITS_MINMILE)	return 59;
    	if(unit == PC.UNITS_MINKILO)	return 59;
    	if(unit == PC.UNITS_MIN)		return 59;
    	return 9;
    }
	private static int min1(int units){
    	return 0;
    }
	private static String[] vals1(int unit){

    	if(unit == PC.UNITS_FEET)		return multiples(50,2000,50);
    	if(unit == PC.UNITS_METERS)		return multiples(50,2000,50);
    	if(unit == PC.UNITS_YARDS)		return multiples(50,2000,50);
		return multiples(0,100,1);
	}

	private static String[] multiples(int min, int max, int increment){
		int length = (max-min)/increment +1;
		int v = min;
		int i = 0;
		String[] out = new String[length];
		while(v<=max){
			out[i] = String.valueOf(v);
			i++;
			v = v+increment;
		}
		return out;
	}
	private static int max2(int unit){
    	if(unit == PC.UNITS_MINMILE)	return 59;
    	if(unit == PC.UNITS_MINKILO)	return 59;
    	if(unit == PC.UNITS_MIN)		return 59;
    	return 9;
    }
	private static int min2(int unit){
    	return 0;
    }
	private static Formatter form1(int units) {
    	if(units == PC.UNITS_MIN || units == PC.UNITS_MINMILE || units == PC.UNITS_MINKILO)
    		return new Formatter(){ @Override public String format(int value) { return String.format(Locale.getDefault(),"%02d" , value); }};
    	
    	return new Formatter(){ @Override public String format(int value) { return String.valueOf(value); }};
	}
	private void setDontSave(){

    	((NumberPicker) v.findViewById(R.id.numberPicker1)).setSaveFromParentEnabled(false);
    	((NumberPicker) v.findViewById(R.id.numberPicker1)).setSaveEnabled(false);
    	((NumberPicker) v.findViewById(R.id.numberPicker2)).setSaveFromParentEnabled(false);
    	((NumberPicker) v.findViewById(R.id.numberPicker2)).setSaveEnabled(false);
		((NumberPicker) v.findViewById(R.id.unitPicker1  )).setSaveFromParentEnabled(false);
		((NumberPicker) v.findViewById(R.id.unitPicker1  )).setSaveEnabled(false);
		((NumberPicker) v.findViewById(R.id.datatypePicker1)).setSaveFromParentEnabled(false);
		((NumberPicker) v.findViewById(R.id.datatypePicker1)).setSaveEnabled(false);
    	((EditText)     v.findViewById(R.id.editText1)).setSaveFromParentEnabled(false);
    	((EditText)     v.findViewById(R.id.editText1)).setSaveEnabled(false);
	}
}