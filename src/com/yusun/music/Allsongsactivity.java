package com.yusun.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.yusun.music.application.MyApplication;
import com.yusun.music.bean.Mp3;
import com.yusun.music.service.MusicPlayService;
import com.yusun.music.util.MusicUtils;

public class Allsongsactivity extends Activity{

	private ListView listView;
	private SimpleAdapter adapter;
	boolean isReturePlaylist;
	private int type = -1;
	private List<Mp3> songs;// 歌曲集合
	private List<String> al_playlist;// 播放列表集合
	private MusicPlayService mService;
	public static final int PLAYLIST = 1;//适配器加载的数据是歌曲列表
	public static final int SONGS_LIST = 2;//适配器加载的数据是歌曲列表
	public static final int ALL_SINGERS_LIST = 3;//适配器加载的数据是歌手列表
	public static final int ALL_ALBUMS_LIST = 4;//适配器加载的数据是专辑列表
	private MyApplication application;
//	private TextView list_song_name;
	public static final String SONG_NAME = "wangjiaping",SINGER_NAME="xiaolajiao";  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		application = (MyApplication) getApplication();
		listView = (ListView) this.findViewById(R.id.listview);
		
		allsongs();
		initListener();
	}
	public void initListener() {

		listView.setOnItemClickListener(new OnItemClickListener() {
			private List<Map<String, Object>> listItems;
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			
					if(null == mService){
						mService = application.getmService();
					}
					mService.setCurrentListItme(position);
					mService.setSongs(songs);
					mService.playMusic(songs.get(position).getUrl());
					String text = String.format(getResources().getString(R.string.songname), "hehe");
//					list_song_name.setText(songs.get(position).getName());
					
//				发送歌曲名广播
					Intent song=new Intent();
					song.setAction(SONG_NAME);  
			        //标记作用的，广播接收器通过匹配"android.intent.action.MY_BROADCAST"接收发送的消息，在AndroidMainfest.xml中进行过滤匹配
			        song.putExtra("name",songs.get(position).getName());//发送的消息
			        sendBroadcast(song);
//			     发送艺术家广播
			        Intent singer=new Intent();
			        singer.setAction(SINGER_NAME);
			        singer.putExtra("singer",songs.get(position).getSingerName());//发送的消息
			        sendBroadcast(singer);
//			        点击专辑触发播放界面
			        
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		//这里要重新刷新列表，因为跳到列表歌曲界面时可能把这个列表删了，
		//所有再跳回来当然要刷新，另外新建列表再回来肯定要刷新的
		if (isReturePlaylist) {
			al_playlist = MusicUtils.PlaylistList(Allsongsactivity.this);
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < al_playlist.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", al_playlist.get(i));
				map.put("songName", "");
				map.put("singerName", "");
				listItems.add(map);
			}
			adapter = new SimpleAdapter(Allsongsactivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
					R.id.tv_songName, R.id.tv_singerName });
			listView.setAdapter(adapter);
			isReturePlaylist = false;
		}
	}
public void allsongs(){
		songs = MusicUtils.getAllSongs(Allsongsactivity.this);
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
		adapter = new SimpleAdapter(Allsongsactivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
				R.id.tv_songName, R.id.tv_singerName });
		type = SONGS_LIST;
		
		listView.setAdapter(adapter);
	}
}
