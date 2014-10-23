package com.yusun.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yusun.music.application.MyApplication;
import com.yusun.music.bean.Album;
import com.yusun.music.bean.Mp3;
import com.yusun.music.service.MusicPlayService;
import com.yusun.music.util.MusicUtils;

public class allsettings extends Activity{

	private ListView listView;
	private TextView tv_newPlaylist;
	private SimpleAdapter adapter;
	boolean isReturePlaylist;
	private int type = -1;
	private List<Mp3> songs;// 歌曲集合
	private List<String> singers;// 歌手集合
	private List<String> al_playlist;// 播放列表集合
	private List<Album> albums;// 专辑集合
	private MusicPlayService mService;
	public static final int PLAYLIST = 1;//适配器加载的数据是歌曲列表
	public static final int SONGS_LIST = 2;//适配器加载的数据是歌曲列表
	public static final int ALL_SINGERS_LIST = 3;//适配器加载的数据是歌手列表
	public static final int ALL_ALBUMS_LIST = 4;//适配器加载的数据是专辑列表
	private MyApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		application = (MyApplication) getApplication();
		initView();
		initListener();
	}
	public void initView() {
		listView = (ListView) this.findViewById(R.id.listview);
		allsongs();
	}
	
	public void initListener() {

		listView.setOnItemClickListener(new OnItemClickListener() {
			private List<Map<String, Object>> listItems;
			Intent it = new Intent();
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			
					if(null == mService){
						mService = application.getmService();
					}
					mService.setCurrentListItme(position);
					mService.setSongs(songs);
					mService.playMusic(songs.get(position).getUrl());
					it.setClass(allsettings.this, MusicPlayActivity.class);
					startActivity(it);
				
				
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		//这里要重新刷新列表，因为跳到列表歌曲界面时可能把这个列表删了，
		//所有再跳回来当然要刷新，另外新建列表再回来肯定要刷新的
		if (isReturePlaylist) {
			al_playlist = MusicUtils.PlaylistList(allsettings.this);
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < al_playlist.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", al_playlist.get(i));
				map.put("songName", "");
				map.put("singerName", "");
				listItems.add(map);
			}
			adapter = new SimpleAdapter(allsettings.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
					R.id.tv_songName, R.id.tv_singerName });
			listView.setAdapter(adapter);
			isReturePlaylist = false;
		}
	}
public void allsongs(){
		songs = MusicUtils.getAllSongs(allsettings.this);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < songs.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", songs.get(i).getSqlId());
			map.put("songName", songs.get(i).getName());
			if (songs.get(i).getSingerName().equals("<unknown>")) {
				map.put("singerName", "----");
			} else {
				map.put("singerName", songs.get(i).getSingerName());
			}
			listItems.add(map);
		}
		adapter = new SimpleAdapter(allsettings.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
				R.id.tv_songName, R.id.tv_singerName });
		type = SONGS_LIST;
		listView.setAdapter(adapter);
	}
}
