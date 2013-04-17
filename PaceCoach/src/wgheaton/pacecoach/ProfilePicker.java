package wgheaton.pacecoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class ProfilePicker extends DialogFragment {
	OnProfilePickedListener callback;
	int selected;
	DbAdapter db;
    Cursor profiles;
    
    public interface OnProfilePickedListener {
        public void onProfilePicked(boolean remember,int selected);
    }
    public static ProfilePicker newInstance(Bundle b) {
    	ProfilePicker frag = new ProfilePicker();
        frag.setArguments(b);
        return frag;
    }
    @Override
	public void onDetach() {
		profiles.close();
		db.close();
		super.onDetach();
	}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	callback = (OnProfilePickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProfilePickedListener");
        }
    }

	@Override
	public void onCancel(DialogInterface dialog) {
		callback.onProfilePicked(false,selected);
		super.onCancel(dialog);
	}

    @Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("profile",selected);
		super.onSaveInstanceState(outState);
	}
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

    	Bundle b = getArguments();
    	
		if(savedInstanceState != null)
			b.putAll(savedInstanceState);
		
    	selected = b.getInt("profile");
    	Builder d = new AlertDialog.Builder(getActivity());
    	
		db = new DbAdapter(getActivity().getApplicationContext());
        db.open();
        profiles = db.getProfiles();
        int i=0;
    	while(profiles.moveToNext() && selected != profiles.getInt(profiles.getColumnIndex(PC.DB_ROWID))) i++;
        
    	d.setTitle("Select Profile");
    	
        d.setSingleChoiceItems(profiles, i, PC.DB_PROFILE_LABEL,  new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int whichButton) {
        	profiles.moveToPosition(whichButton);
        	selected = profiles.getInt(profiles.getColumnIndex(PC.DB_ROWID));
		}});
		d.setPositiveButton("Remember Selection", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int whichButton) {
			callback.onProfilePicked(true,selected);
		}});
		d.setNeutralButton("New Profile", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int whichButton) {

			DbAdapter db;
			db = new DbAdapter(getActivity().getApplicationContext());
	        db.open();
	        int p = db.addProfile("New Profile");
	        db.close();
	        
			Intent i = new Intent(getActivity(), Settings.class);
			i.putExtra(PC.PREF_PROFILE, p);
			startActivity(i);
			getActivity().finish();
	        
		}});
		d.setNegativeButton("Always Ask", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int whichButton) {
			callback.onProfilePicked(false,selected);
		}});
        return d.create();
    }
	
}
