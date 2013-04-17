package com.cypho.pacecoach;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import wgheaton.pacecoach.R;

public class Splits extends ListFragment {
	Activity ctx;
	private int runid;
	DbAdapter db;
	Cursor splits;
	//boolean interactive;
	boolean invert = false;

	@Override
	public void onDestroyView() {
		splits.close();
		db.close();
		super.onDestroyView();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getArguments();
		runid = b.getInt("runid");

		invert 	= b.getBoolean("invert");

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ctx = getActivity();
		db = new DbAdapter(ctx);
		db.open();
		splits = db.getSplitsExtra(runid,invert);

		//container.setFocusable(false);
		SplitsCursorAdapter adapter = new SplitsCursorAdapter( inflater.getContext(), splits);

		setListAdapter(adapter);
		ViewGroup v = (	ViewGroup) inflater.inflate(R.layout.splits, container, false);

		int px2 = ctx.getResources().getDimensionPixelSize(R.dimen.page_padding_x2);
		int px1 = ctx.getResources().getDimensionPixelSize(R.dimen.page_padding);

		((LinearLayout)v.findViewById(R.id.split_heading)).setPadding(px2, px1, px2, 0);
		((LinearLayout)v.findViewById(R.id.split_head)).setPadding(px2, 0, px2,0);
		((LinearLayout)v.findViewById(R.id.split_units)).setPadding(px2, 0, px2,0);


		((TextView)v.findViewById(R.id.splits)).setText("Split");
		((TextView)v.findViewById(R.id.total )).setText("Total");

		((TextView)v.findViewById(R.id.splitD)).setText("Distance");
		((TextView)v.findViewById(R.id.splitT)).setText("Time");
		((TextView)v.findViewById(R.id.splitS)).setText("Speed");
		((TextView)v.findViewById(R.id.splitTD)).setText("Distance");
		((TextView)v.findViewById(R.id.splitTT)).setText("Time");

		((TextView)v.findViewById(R.id.splitSu)).setText(PC.getText(ctx, PC.UNITS+PC.UNITS_MINMILE));
		((TextView)v.findViewById(R.id.splitDu)).setText(PC.getText(ctx, PC.UNITS+PC.UNITS_MILES));
		((TextView)v.findViewById(R.id.splitTDu)).setText(PC.getText(ctx, PC.UNITS+PC.UNITS_MILES));
		((TextView)v.findViewById(R.id.splitSu)).setTextSize(8);
		((TextView)v.findViewById(R.id.splitDu)).setTextSize(8);
		((TextView)v.findViewById(R.id.splitTDu)).setTextSize(8);

		return v;
	}
	public class SplitsCursorAdapter extends CursorAdapter {

		public SplitsCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}
		@Override
		public void bindView(View view, Context context, final Cursor cursor) {


			int n 		= cursor.getInt(	cursor.getColumnIndex(PC.DB_SPLIT_NO));
			double d 	= cursor.getDouble(	cursor.getColumnIndex(PC.DB_SPLIT_DIST));
			long t 		= cursor.getLong(	cursor.getColumnIndex(PC.DB_SPLIT_TIME));

			double td 	= cursor.getDouble(	cursor.getColumnIndex(PC.DB_DIST_ELAP));
			long tt		= cursor.getLong(	cursor.getColumnIndex(PC.DB_TIME_ELAP));
			int mode	= cursor.getInt(	cursor.getColumnIndex(PC.DB_MODE));

			if(mode==PC.SIGNAL_SPLIT)
				((TextView)view.findViewById(R.id.splitNo)).setText(String.valueOf(n));
			else
				((TextView)view.findViewById(R.id.splitNo)).setText("");

			((TextView)view.findViewById(R.id.splitD)).setText(PC.formatUnits(d, PC.UNITS_MILES));
			((TextView)view.findViewById(R.id.splitT)).setText(PC.formatTime(t));
			((TextView)view.findViewById(R.id.splitS)).setText(PC.formatUnits(d/(double)t*1000,PC.UNITS_MINMILE));
			((TextView)view.findViewById(R.id.splitTD)).setText(PC.formatUnits(td, PC.UNITS_MILES));
			((TextView)view.findViewById(R.id.splitTT)).setText(PC.formatTime(tt));

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.splits_row, parent, false);
			bindView(v, context, cursor);
			return v;
		}
	}



}
