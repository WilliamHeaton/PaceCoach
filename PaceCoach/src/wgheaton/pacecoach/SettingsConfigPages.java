package wgheaton.pacecoach;

import java.util.ArrayList;
import wgheaton.pacecoach.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsConfigPages extends Activity {
	
	Activity ctx;
	int width;
	int numpages;
	int onpage;
    int ontab;
	ArrayList<Page> pageList;
    ArrayList<TextView> tabList;
    private int[] pages = PC.page_list;
	GestureDetector mGestureDetector;
	int profile;
	
	public void onBackPressed(){
		Intent i = new Intent(ctx, Settings.class);
		i.putExtra(PC.PREF_PROFILE, profile);
		startActivity(i);
		finish();
	}

    @Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(PC.PREF_PROFILE,profile);
		outState.putInt("onpage",onpage);
		outState.putInt("ontab",ontab);
		super.onSaveInstanceState(outState);
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        
        ctx = this;
        width = 300;
        numpages = 0;
        pageList = new ArrayList<Page>();
        tabList = new ArrayList<TextView>();
        View v;
        TextView f;
        LayoutParams p = new LayoutParams(-1, -1);


		MyCustomView mcv = (MyCustomView) new MyCustomView(ctx);
		mcv.setLayoutParams(p);
		mcv.addView(getLayoutInflater().inflate(R.layout.settings_config_pages, mcv,false));
		setContentView(mcv);
        


        if(savedInstanceState!=null && savedInstanceState.getInt(PC.PREF_PROFILE)!=0){
        	profile = savedInstanceState.getInt(PC.PREF_PROFILE);
        	onpage = savedInstanceState.getInt("onpage");
        	ontab = savedInstanceState.getInt("ontab");
        }else if(getIntent().getExtras()!= null && getIntent().getExtras().getInt(PC.PREF_PROFILE)!=0){
			profile = getIntent().getExtras().getInt(PC.PREF_PROFILE);
			ontab = PC.getPrefInt(ctx,profile,PC.PREF_PAGE_DEFAULT);
			onpage = ontab-1;
        }else{
        	throw new ClassCastException(this.toString() + " can not determine profile");
        }
		
    	
        if(savedInstanceState!=null && savedInstanceState.getInt(PC.PREF_PROFILE)!=0){
        	profile = savedInstanceState.getInt(PC.PREF_PROFILE);
        	onpage = savedInstanceState.getInt("onpage");
        	ontab = savedInstanceState.getInt("ontab");
        }else if(getIntent().getExtras()!= null && getIntent().getExtras().getInt(PC.PREF_PROFILE)!=0)
			profile =  getIntent().getExtras().getInt(PC.PREF_PROFILE);

        DbAdapter db = new DbAdapter(this.getApplicationContext());
		db.open();
		Cursor prefs = db.getPrefs(profile);
		prefs.moveToFirst();
		
        

        int t = db.getPageTemplate(profile,ontab);
        LinearLayout pageholder = (LinearLayout) findViewById(R.id.pages);
        for(int page: pages){
        	
        	if(page==t)
        		onpage = numpages;
        	
        	numpages++;
        	
        	v = getLayoutInflater().inflate(PC.page_link(page), pageholder,false);
        	p = v.getLayoutParams();
        	p.width = width;
        	v.setLayoutParams(p);
        	pageholder.addView(v);
        	pageList.add((Page) new Page(ctx,profile,page,ontab,v,true,PC.SIGNAL_SEARCHING,0));
        }
        

        LinearLayout tabs  = (LinearLayout) findViewById(R.id.tabs);
        int np = prefs.getInt(prefs.getColumnIndex(PC.PREF_PAGE_NUMBER));
        for(int i = 1; i<np+1;i++){
        	f = (TextView) getLayoutInflater().inflate(R.layout.tab, tabs,false);
        	f.setText(String.valueOf(i));
        	f.setOnClickListener(clicktab);
        	if(i==ontab)
        		f.setBackgroundResource(R.drawable.tab_on);
        	tabList.add(f);
        	tabs.addView(f);
        }
        

		mGestureDetector = new GestureDetector(this,new MyGestureDetector());
        HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);
        scroller.setOnTouchListener(touchListener);
        
        prefs.close();
        db.close();
    }
    
	class MyCustomView extends LinearLayout{
        public MyCustomView(Context context) {
                super(context);
        }
        @Override
        protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
                super.onSizeChanged(xNew, yNew, xOld, yOld);
                
		        if(xNew>yNew)
		        	xNew = xNew/2;
		        width = xNew;
		        
		        
		        LayoutParams p;
		        for ( Page page : pageList){  
		            p = (LayoutParams) page.view.getLayoutParams();
		        	p.width = xNew;
		        	page.view.setLayoutParams(p);
		        }

		        HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);
		    	
		    	scroller.postDelayed(new Runnable() {
		    	    @Override
		    	    public void run() {
		    	    	HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);
		    	    	scroller.scrollTo(onpage*width, 0);
		    	    } 
		    	}, 100);
		    	
        }
    }

    OnClickListener clicktab = new OnClickListener(){
		@Override
		public void onClick(View v) {
			TextView view = (TextView) v;
			int ot = Integer.parseInt(view.getText().toString());
			if(ot==ontab)
				return;
			ontab = ot;
			int i = 0;
			for ( TextView tab : tabList){  
	            i++;
	            if(i==ontab)
	        		tab.setBackgroundResource(R.drawable.tab_on);
	            else
	        		tab.setBackgroundResource(R.drawable.tab_off);
	        }
			DbAdapter pagesDB = new DbAdapter(ctx);
	        pagesDB.open();
	        
	        int t = pagesDB.getPageTemplate(profile,ontab);
	        pagesDB.close();
	        i=0;
	        for(int page: pages){
	            if(page==t)
	        		onpage = i;
	        	i++;
	        }
	        HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);
	        scroller.smoothScrollTo(onpage*width,0);
	        
	        for ( Page page : pageList){  
	            page.changePage(ontab);
	        }
		}
    };
    OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mGestureDetector.onTouchEvent(event)) {
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL ){
				HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);
            	int op = (int) Math.round((float)scroller.getScrollX()/width);
                scroller.smoothScrollTo(op*width,0);
                
                if(op != onpage){
                	onpage = op;
                	DbAdapter pagesDB = new DbAdapter(ctx);
                    pagesDB.open();
                    pagesDB.setTemplate(profile,ontab,pages[onpage]);
                    pagesDB.close();
                }
                
				return true;
            }else{
            	return false;
            }
		}
    };

	class MyGestureDetector extends SimpleOnGestureListener {


		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scroller);

			if(e1 == null || e2==null )
				return false;

			//right to left
			if( e1.getRawX() > e2.getRawX() ){
				onpage = (int) Math.ceil((float)scroller.getScrollX()/width);
				scroller.smoothScrollTo(onpage*width, 0);
				
            	DbAdapter pagesDB = new DbAdapter(ctx);
                pagesDB.open();
                pagesDB.setTemplate(profile,ontab,pages[onpage]);
                pagesDB.close();
                
				return true;
			}
			//left to right
			else if ( e1.getRawX() < e2.getRawX() ){

				onpage = (int) Math.floor((float)scroller.getScrollX()/width);
				scroller.smoothScrollTo(onpage*width, 0);

            	DbAdapter pagesDB = new DbAdapter(ctx);
                pagesDB.open();
                pagesDB.setTemplate(profile,ontab,pages[onpage]);
                pagesDB.close();
				return true;
			}
			return false;
		}
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
	    if (mGestureDetector != null) {
			super.dispatchTouchEvent(ev);
	    	return mGestureDetector.onTouchEvent(ev);
	    }
	    return super.dispatchTouchEvent(ev);
	}


	public void bubbleUp(int cellid) {
		
		for ( Page page : pageList){  
            page.signalCell(cellid,PC.SIGNAL_REFRESH);
        }
	}
}
