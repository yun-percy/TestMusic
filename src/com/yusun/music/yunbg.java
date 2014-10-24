

package com.yusun.music;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

public class yunbg extends RelativeLayout {
    public yunbg(final Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
        Bitmap bm =blur(context);
        this.setBackground(new BitmapDrawable(context.getResources(),bm));
        final Handler handler2 =new Handler();
		final Runnable runnable=new Runnable() {
			@Override
			public void run() {
				update(context);
				handler2.postDelayed(this, 10000);

			}
			
	};
		handler2.postDelayed(runnable, 2000);
    }
    public void update(Context context){
    	Bitmap bm =blur(context);
        this.setBackground(new BitmapDrawable(context.getResources(),bm));
    }
    private Bitmap blur(Context context){
		
    	WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
    	Drawable wallpaperDrawable =wallpaperManager.getDrawable();
    	Bitmap bm=((BitmapDrawable) wallpaperDrawable).getBitmap();
    	float scaleFactor = 18;
    	float radius =4;
    	 DisplayMetrics dm =context.getApplicationContext().getResources().getDisplayMetrics();

    	 int width = dm.widthPixels;//宽度
	 	  int height = dm.heightPixels ;//高度
        Bitmap overlay = Bitmap.createBitmap((int) (width/scaleFactor), 
        (int) (height/scaleFactor), Bitmap.Config.ARGB_8888); 
        Canvas canvas = new Canvas(overlay); 
        canvas.scale(1 / scaleFactor, 1 / scaleFactor); 
        Paint paint = new Paint();
                                         paint.setFlags(Paint.FILTER_BITMAP_FLAG); 
   canvas.drawBitmap( bm,0, 0, paint); 
   overlay =FastBlur.doBlur(overlay, (int)radius, true);
   return overlay;
    }
    
}
