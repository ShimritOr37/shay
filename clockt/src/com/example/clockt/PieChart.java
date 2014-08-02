package com.example.clockt;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
public	class PieChart extends View {
	
	private final Handler timeHandler = new Handler();
	private Time mCalendar;
	boolean flip=false;
	
	int radius,screen,minw,w,minh,h,centerx,centery;
	
	static Paint mPiePaint=new Paint();
	Paint mTextPaint=new Paint();
	
	float chour,cminute,csec=1;
	Drawable min,hour,sec,clock;
	
	private final BroadcastReceiver  tickReceiver=new BroadcastReceiver(){
		Integer array[]={0xff0000ff,0xffff0000,0xffff00ff,0xffffff00};
		int i=0;
        @Override
        public void onReceive(Context context, Intent intent) {
        	
	        if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0)
	        {
	        	
	        	clock.setColorFilter(array[i], Mode.MULTIPLY);
	        	i++;
	        	i=i%4;
	        	
	        	setTime();
	        	invalidate();//call onDraw again and sets up the new value
	        
	        }
        }
    };
	

	    public PieChart(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        TypedArray a = context.getTheme().obtainStyledAttributes(
	                attrs,
	                R.styleable.PieChart,
	                0, 0);

	             min=a.getDrawable(R.styleable.PieChart_handMin);
	             sec=a.getDrawable(R.styleable.PieChart_handSec);
	             hour=a.getDrawable(R.styleable.PieChart_handHour);
	             clock=a.getDrawable(R.styleable.PieChart_clock);
	             
	          
	             mCalendar = new Time();
	             init();//not used here but can be modified
	             
	        }
	    
	
	    
	    private void init() {//mTextPaint and mPiePaint are optional future drawing not used in this app.
	        
	     
	    	Log.d("bugbug", "init");
	    	Paint mTextPaint;
	    	int mTextColor = 0;
	    	   mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    	   mTextPaint.setColor(mTextColor);
	    	   mTextPaint.setStrokeWidth(400);
	    	   int mTextHeight = 220;
			if (mTextHeight == 0) {
	    	       mTextHeight = (int) mTextPaint.getTextSize();
	    	   } else {
	    	       mTextPaint.setTextSize(mTextHeight);
	    	   }

	    	   Paint mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    	   mPiePaint.setStyle(Paint.Style.FILL);
	    	
	    	   mPiePaint.setTextSize(mTextHeight);
	    	   mPiePaint.setStrokeWidth(400);
	    	   Paint mShadowPaint = new Paint(0);
	    	   mShadowPaint.setColor(0xff101010);
	    	   mShadowPaint.setMaskFilter(new BlurMaskFilter(800, BlurMaskFilter.Blur.NORMAL));
	    }
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	       // Try for a width based on our minimum
	        minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
	        w = resolveSizeAndState(minw, widthMeasureSpec, 1);

	       int mTextWidth = 0;
		// Whatever the width ends up being, ask for a height that would let the pie
	       // get as big as it can
	        minh = MeasureSpec.getSize(w) - (int)mTextWidth + getPaddingBottom() + getPaddingTop();
	        h = resolveSizeAndState(MeasureSpec.getSize(w) - (int)mTextWidth, heightMeasureSpec, 0);
	       Log.d("bugbug", "h"+h+" minh"+minh+" w"+w+" minw"+minw);
	       setMeasuredDimension(w, h);
	       radius = Math.min(w, h)/2;
	       screen=Math.max(w, h);//for the screen big size
	    }
	    
	    @SuppressLint("ResourceAsColor")
		@SuppressWarnings("deprecation")
		protected void onDraw(final Canvas canvas) {
	    	   super.onDraw(canvas);
	    	  
	    	mTextPaint.setStyle(Paint.Style.STROKE); 
			mTextPaint.setStrokeWidth(10);
		
			
			//min//GRE
			//sec//TURKIZ
			//hour//BLUE
			
			
			if ((h>w)){//Vertical hold
				clock.setBounds(0,(h-radius*2)/2,2*radius,2*radius+(h-radius*2)/2);
				min.setBounds(w/2,0,w,h);
				sec.setBounds(w/2,0,w,h);
				hour.setBounds(w/2,0,w,h);
		
				
			}
			if (h<w){//Horizontal hold
				clock.setBounds((w-radius*2)/2,0,radius*2+(w-radius*2)/2,radius*2);
				min.setBounds(w/2,0,radius+w/2,h);
				sec.setBounds(w/2,0,radius+w/2,h);
				hour.setBounds(w/2,0,radius+w/2,h);
			
			}
			
			clock.draw(canvas);
			
			canvas.save();
			
			Log.d("bugbug","value min"+cminute);
			Log.d("bugbug","div"+chour/12+"mod"+chour%12);
			
			canvas.rotate((cminute*6)-90, w/2, h/2);
			min.draw(canvas);
			canvas.restore();
			
			canvas.save();
			
			canvas.rotate(((chour%12)*30+(cminute/60)*30)-90, w/2, h/2);
			hour.draw(canvas);
			canvas.restore();
			
			canvas.save();
			
			canvas.rotate(csec*6-90, w/2, h/2);
			sec.draw(canvas);
			canvas.restore();
	       
	        
			canvas.save();
		       
	
			
			Log.d("bugbug","onDraw");
			 
	    }
	    
	    
	
	    protected void onAttachedToWindow() {//called before onDraw, updating time with receiver
	        super.onAttachedToWindow();
	        	       
	        
	        new LongOperation().execute();
	        
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(Intent.ACTION_TIME_TICK);
	        
	        
	        getContext().registerReceiver(tickReceiver, filter,null,timeHandler);

	       
	       Log.d("bugbug","name:"+this.toString());
	    }
	    

	    private class LongOperation extends AsyncTask<Void, Void, String> {
	    	
	    	Time mCalendar= new Time();
	    
              
	        @Override
	        protected String doInBackground(Void... params) {
	        	
	        	
	        	 
	  
	          for (int i=0;i==i;i++){
	        
	        	
	                try {
		                 mCalendar.setToNow();
		   	        	 chour = mCalendar.hour;
		   	             cminute = mCalendar.minute;
		   	             csec = mCalendar.second;
	                	 Thread.sleep(1000);
	                	 publishProgress();
	              
	                } catch (InterruptedException e) {
	                    Thread.interrupted();
	                }
	          
	           
	        }
	          return String.valueOf(csec);
	        }
	          

	        @Override
	        protected void onPostExecute(String result) {
	        	 Log.d("bugbug","sec is"+result);
	        	
	           
	        }
	        @Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {
	        	
		           setTime();
		           invalidate();
		           
		          
	        }
	    }
	    
	    public void setTime(){
	    	  mCalendar.setToNow();

              chour = mCalendar.hour;
              cminute = mCalendar.minute;
              csec = mCalendar.second;
          

          Log.d("bugbug","H"+chour+" M"+cminute+" S"+csec);
	    }
	    
            //Register the broadcast receiver to receive TIME_TICK
           
	    @Override
	    protected void onDetachedFromWindow() {
	        super.onDetachedFromWindow();
	      
	            getContext().unregisterReceiver(tickReceiver);
	          
	    }
	        
	    

         
        
	    
}
	 
