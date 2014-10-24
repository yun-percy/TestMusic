package com.yusun.music;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity {

	Context context = null;
	LocalActivityManager manager = null;
	ViewPager pager = null;
	TabHost tabHost = null;
	TextView t1,t2,t3,t4;
	
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private ImageView cursor;
	private TextView list_song_name,list_singnername;
	SongReceiver songReceiver;
	SingerReceiver singerReceiver;
	public static final String SONG_NAME = "wangjiaping",SINGER_NAME="xiaolajiao";  

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = MainActivity.this;
		manager = new LocalActivityManager(this , true);
		manager.dispatchCreate(savedInstanceState);
		yun_boardcast();
		InitImageView();
		initTextView();
		initPagerViewer();
		
		ImageView list_show_album;
		list_show_album=(ImageView)findViewById(R.id.list_show_album);
		list_show_album.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent();
				it.setClass(MainActivity.this, MusicPlayActivity.class);
				startActivity(it);
				
			}
		});

	}
	private void yun_boardcast() {
		songReceiver =new SongReceiver();
		singerReceiver =new SingerReceiver();
		IntentFilter songfilter = new IntentFilter();
	       songfilter.addAction(SONG_NAME);
		registerReceiver(songReceiver,songfilter);
		IntentFilter singerfilter = new IntentFilter();
	       singerfilter.addAction(SINGER_NAME);
		registerReceiver(singerReceiver,singerfilter);
	}
	private void initTextView() {
		t1 = (TextView) findViewById(R.id.btn_playlist);
		t2 = (TextView) findViewById(R.id.btn_allSongs);
		t3 = (TextView) findViewById(R.id.btn_singers);
		t4 = (TextView) findViewById(R.id.btn_albums);
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
		
	}

	private void initPagerViewer() {
		pager = (ViewPager) findViewById(R.id.viewpage);
		final ArrayList<View> list = new ArrayList<View>();
		Intent intent = new Intent(context, normalsettings.class);
		list.add(getView("A", intent));
		Intent intent2 = new Intent(context, Allsongsactivity.class);
		list.add(getView("B", intent2));
		Intent intent3 = new Intent(context, yunsettings.class);
		list.add(getView("C", intent3));
		Intent intent4 = new Intent(context, yun2settings.class);
		list.add(getView("C", intent4));

		pager.setAdapter(new MyPagerAdapter(list));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.roller)
		.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 4 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}


	public class MyPagerAdapter extends PagerAdapter{
		List<View> list =  new ArrayList<View>();
		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position,
				Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;
		int two = one * 2;
		int three = one *3;
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
					animation = new TranslateAnimation(one, 0, 0, 0);
					t1.setTextColor(0xffffffff);
					t2.setTextColor(0xff808080);
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
					t2.setTextColor(0xffffffff);
					t1.setTextColor(0xff808080);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);	
					t2.setTextColor(0xffffffff);
					t3.setTextColor(0xff808080);
				}
				break;
			case 2:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					t3.setTextColor(0xffffffff);
					t2.setTextColor(0xff808080);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);	
					t3.setTextColor(0xffffffff);
					t4.setTextColor(0xff808080);
				}
				break;
			case 3:
				animation = new TranslateAnimation(two, three, 0, 0);
				t4.setTextColor(0xffffffff);
				t3.setTextColor(0xff808080);
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			pager.setCurrentItem(index);
		}
	};
//	广播接受
	public class SongReceiver extends BroadcastReceiver{
	    public void onReceive(Context context, Intent intent) {
	        String msg=intent.getStringExtra("name");//接收信息
			list_song_name=(TextView)findViewById(R.id.list_song_name);
			list_song_name.setText(msg);
	    }
	}
	public class SingerReceiver extends BroadcastReceiver{
	    public void onReceive(Context context, Intent intent) {
	        String msg=intent.getStringExtra("singer");//接收信息
			list_singnername=(TextView)findViewById(R.id.list_singnername);
			list_singnername.setText(msg);
	    }
	}
}
