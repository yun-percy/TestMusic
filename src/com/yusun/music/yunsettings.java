package com.yusun.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import com.yusun.music.application.MyApplication;
import com.yusun.music.bean.Mp3;
import com.yusun.music.service.MusicPlayService;
import com.yusun.music.util.MusicUtils;

@SuppressWarnings("deprecation")
public class yunsettings extends Activity{
	private TextView tv_newPlaylist;
	private SimpleAdapter adapter;
	private int type = -1;
	public static final int PLAYLIST = 1;//适配器加载的数据是歌曲列表
	private ListView listView;
	boolean isReturePlaylist;
	public static final int SONGS_LIST = 2;//适配器加载的数据是歌曲列表
	public static final int ALL_SINGERS_LIST = 3;//适配器加载的数据是歌手列表
	public static final int ALL_ALBUMS_LIST = 4;//适配器加载的数据是专辑列表
	Context context = null;
	LocalActivityManager manager = null;
	ViewPager pager = null;
	TabHost tabHost = null;
	private List<String> singers;// 歌手集合
	private List<Mp3> songs;// 歌曲集合
	private MusicPlayService mService;
	private MyApplication application;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		application = (MyApplication) getApplication();
		listView = (ListView) this.findViewById(R.id.listview);
		tv_newPlaylist = (TextView) this.findViewById(R.id.tv_newPlaylist);
		tv_newPlaylist.setVisibility(View.GONE);
		singer_list();
	}
		public void singer_list(){
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		singers = MusicUtils.MusicSingerList(yunsettings.this);
		for (int i = 0; i < singers.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", "");
			map.put("songName", singers.get(i));
			map.put("singerName", "");
			listItems.add(map);
		}
		adapter = new SimpleAdapter(yunsettings.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
				R.id.tv_songName, R.id.tv_singerName });
		type = ALL_SINGERS_LIST;
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			private List<Map<String, Object>> listItems;
			Intent it = new Intent();
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				switch (type) {
				
				case SONGS_LIST:
					if(null == mService){
						mService = application.getmService();
					}
					mService.setCurrentListItme(position);
					mService.setSongs(songs);
					mService.playMusic(songs.get(position).getUrl());
					it.setClass(yunsettings.this, MusicPlayActivity.class);
					startActivity(it);
					break;
				case ALL_SINGERS_LIST:
					songs = MusicUtils.MusicMp3ListbySinger(yunsettings.this, singers.get(position));
					listItems = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < songs.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", "");
						map.put("songName", songs.get(i).getName());
						map.put("singerName", "");
						listItems.add(map);
					}
					adapter = new SimpleAdapter(yunsettings.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
							R.id.tv_songName, R.id.tv_singerName });
					type = SONGS_LIST;
					listView.setAdapter(adapter);
					break;
				}
			}
			});
	}
}
