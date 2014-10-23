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

public class yun2settings extends Activity{
	private ListView listView;
	private TextView tv_newPlaylist;
	private SimpleAdapter adapter;
	boolean isReturePlaylist;
	private int type = -1;
	private List<Mp3> songs;// 歌曲集合
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
		listView = (ListView) this.findViewById(R.id.listview);
		tv_newPlaylist = (TextView) this.findViewById(R.id.tv_newPlaylist);
		tv_newPlaylist.setVisibility(View.GONE);
		album_list();
	}
	public void album_list() {
		albums = MusicUtils.MusicAlbumList(yun2settings.this);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < albums.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", "");
			map.put("songName", albums.get(i).getName());
			map.put("singerName", "");
			listItems.add(map);
		}
		adapter = new SimpleAdapter(yun2settings.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
				R.id.tv_songName, R.id.tv_singerName });
		type = ALL_ALBUMS_LIST;
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
					it.setClass(yun2settings.this, MusicPlayActivity.class);
					startActivity(it);
					break;
				
				case ALL_ALBUMS_LIST:
					songs = MusicUtils.MusicMp3ListbyAlbum(yun2settings.this, albums.get(position).getName());
					listItems = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < songs.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", "");
						map.put("songName", songs.get(i).getName());
						map.put("singerName", "");
						listItems.add(map);
					}
					adapter = new SimpleAdapter(yun2settings.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
							R.id.tv_songName, R.id.tv_singerName });
					type = SONGS_LIST;
					listView.setAdapter(adapter);
					break;
				}
			}
			});
	}
}
