package wgheaton.pacecoach;

import java.util.ArrayList;
import wgheaton.pacecoach.R;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Page {
	public int template;
	public int id;
	public View view;
	public int cellCount;
	private Activity ctx;
	private boolean configure_mode;
	ArrayList<Cell> cellList;
	int mode;
	int runid;
	int profile;
	
	public Page(Activity c, int pr, int t, int i, View v, boolean b,int m, int r) {
		profile = pr;
		template = t;
		id = i;
		view = v;
		ctx = c;
		configure_mode = b;
		mode = m;
		runid = r;
		cellCount = PC.cell_count(template);
		populateCells();
	}
	private void populateCells(){
		
		DbAdapter cellsDB = new DbAdapter(ctx);
        cellsDB.open();
        Cursor cells = cellsDB.getCells(profile,id);
        int cellid = 0;
        int datatype;
        int units;
        ViewGroup cell;

        cellList = new ArrayList<Cell>();
        while (cells.moveToNext()) {
        	while(cells.getInt(cells.getColumnIndex(PC.DB_CELL))>cellid+1){
            	cellid++;
            	if(cellid > cellCount)
            		continue;
        		cell = getCell(PC.cell_link(cellid));
        		
            	cellList.add((Cell) new Cell(ctx,profile,id,template,cellid,PC.CELL_TYPE_BLANK,0,cell,configure_mode,mode,runid));
        	}
        	cellid 		= cells.getInt(cells.getColumnIndex(PC.DB_CELL));
        	if(cellid > cellCount)
        		continue;
        	datatype 	= cells.getInt(cells.getColumnIndex(PC.DB_DATATYPE));
        	units 		= cells.getInt(cells.getColumnIndex(PC.DB_UNITS));
        	cell 		= getCell(PC.cell_link(cellid));
        	cellList.add((Cell) new Cell(ctx,profile,id,template,cellid,datatype,units,cell,configure_mode,mode,runid));
        }
        cellsDB.close();
        while (cellid < cellCount){
        	cellid++;
        	cell = getCell(PC.cell_link(cellid));
        	cellList.add((Cell) new Cell(ctx,profile,id,template,cellid,PC.CELL_TYPE_BLANK,0,cell,configure_mode,mode,runid));
        }
	}
	public ViewGroup getCell(int cellid){
		return (ViewGroup) view.findViewById(cellid);
	}

	public void changePage(int p) {
		id = p;
		populateCells();
	}
	public void signalCell(int cellid, int signal) {
		switch(signal){
			case PC.SIGNAL_WARMUP:
			case PC.SIGNAL_WORKOUT:
			case PC.SIGNAL_COOLDOWN:
			case PC.SIGNAL_STOP:
			case PC.SIGNAL_RESUME_WARMUP:
			case PC.SIGNAL_RESUME_WORKOUT:
			case PC.SIGNAL_RESUME_COOLDOWN:
			case PC.SIGNAL_PAUSE_WARMUP:
			case PC.SIGNAL_PAUSE_WORKOUT:
			case PC.SIGNAL_PAUSE_COOLDOWN:
				mode = signal;
				break;
		}
		
		int i = 0;
		for ( Cell cell : cellList){
			i++;
			if(i==cellid)
				cell.recieveSignal(signal);
        }
	}
	public void signalCells(int signal){
		switch(signal){
			case PC.SIGNAL_WARMUP:
			case PC.SIGNAL_WORKOUT:
			case PC.SIGNAL_COOLDOWN:
			case PC.SIGNAL_STOP:
			case PC.SIGNAL_RESUME_WARMUP:
			case PC.SIGNAL_RESUME_WORKOUT:
			case PC.SIGNAL_RESUME_COOLDOWN:
			case PC.SIGNAL_PAUSE_WARMUP:
			case PC.SIGNAL_PAUSE_WORKOUT:
			case PC.SIGNAL_PAUSE_COOLDOWN:
				mode = signal;
				break;
		}
		for ( Cell cell : cellList){  
			cell.recieveSignal(signal);
        }
	}
	public void newLocation(Location argLocation){
		for ( Cell cell : cellList){  
			cell.newLocation(argLocation);
        }
	}
	public void setRunid(int r) {
		runid = r;
		for ( Cell cell : cellList){  
			cell.setRunid(runid);
        }
	}
	public void rotate() {

		LinearLayout row;
		FrameLayout c;
		LayoutParams p;
		int p1;
		int p2;
		for ( Cell cell : cellList){  
			c = (FrameLayout)cell.view;
			p = (LayoutParams) c.getLayoutParams();
			p1 = p.width;
			p2 = p.height;
			p.width = p2;
			p.height = p1;
			c.setLayoutParams(p);
        }
		
		((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);
		row = (LinearLayout)view.findViewById(R.id.row1);
		if(row!=null){
			p = (LayoutParams) row.getLayoutParams();
			p1 = p.width;
			p2 = p.height;
			p.width = p2;
			p.height = p1;
			row.setLayoutParams(p);
			row.setOrientation(LinearLayout.VERTICAL);
		}
		row = (LinearLayout)view.findViewById(R.id.row2);
		if(row!=null){
			p = (LayoutParams) row.getLayoutParams();
			p1 = p.width;
			p2 = p.height;
			p.width = p2;
			p.height = p1;
			row.setLayoutParams(p);
			row.setOrientation(LinearLayout.VERTICAL);
		}
		row = (LinearLayout)view.findViewById(R.id.row3);
		if(row!=null){
			p = (LayoutParams) row.getLayoutParams();
			p1 = p.width;
			p2 = p.height;
			p.width = p2;
			p.height = p1;
			row.setLayoutParams(p);
			row.setOrientation(LinearLayout.VERTICAL);
		}
		row = (LinearLayout)view.findViewById(R.id.row4);
		if(row!=null){
			p = (LayoutParams) row.getLayoutParams();
			p1 = p.width;
			p2 = p.height;
			p.width = p2;
			p.height = p1;
			row.setLayoutParams(p);
			row.setOrientation(LinearLayout.VERTICAL);
		}
		
	}
}
