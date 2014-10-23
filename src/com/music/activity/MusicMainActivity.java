package com.music.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.music.R;
import com.music.application.MyApplication;
import com.music.bean.Album;
import com.music.bean.Mp3;
import com.music.service.MusicPlayService;
import com.music.util.MusicUtils;

public class MusicMainActivity extends Activity {

	private ListView listView;
	private TextView btn_playlist, btn_allSongs, btn_singers, btn_albums;
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
		setContentView(R.layout.music_main_activity);
		application = (MyApplication) getApplication();
		initView();
		initListener();
	}

	public void initView() {
		listView = (ListView) this.findViewById(R.id.listview);
		btn_playlist = (TextView) this.findViewById(R.id.btn_playlist);
		btn_allSongs = (TextView) this.findViewById(R.id.btn_allSongs);
		btn_singers = (TextView) this.findViewById(R.id.btn_singers);
		btn_albums = (TextView) this.findViewById(R.id.btn_albums);
		tv_newPlaylist = (TextView) this.findViewById(R.id.tv_newPlaylist);
		allsongs();
	}

	public void initListener() {
		//新增列表
		tv_newPlaylist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addplaylist();
			}
		});
		//列出所有列表
		btn_playlist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_newPlaylist.setVisibility(View.VISIBLE);
				al_playlist = MusicUtils.PlaylistList(MusicMainActivity.this);
				List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < al_playlist.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", "");
					map.put("songName", al_playlist.get(i));
					map.put("singerName", "");
					listItems.add(map);
				}
				adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
						R.id.tv_songName, R.id.tv_singerName });
				type = PLAYLIST;
				listView.setAdapter(adapter);
			}
		});
		//列出所有歌曲
		btn_allSongs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allsongs();
				
			}
		});
		//列出所有歌手
		btn_singers.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_newPlaylist.setVisibility(View.GONE);
				List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
				singers = MusicUtils.MusicSingerList(MusicMainActivity.this);
				for (int i = 0; i < singers.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", "");
					map.put("songName", singers.get(i));
					map.put("singerName", "");
					listItems.add(map);
				}
				adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
						R.id.tv_songName, R.id.tv_singerName });
				type = ALL_SINGERS_LIST;
				listView.setAdapter(adapter);
			}
		});
		//列出所有专辑
		btn_albums.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_newPlaylist.setVisibility(View.GONE);
				albums = MusicUtils.MusicAlbumList(MusicMainActivity.this);
				List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < albums.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", "");
					map.put("songName", albums.get(i).getName());
					map.put("singerName", "");
					listItems.add(map);
				}
				adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
						R.id.tv_songName, R.id.tv_singerName });
				type = ALL_ALBUMS_LIST;
				listView.setAdapter(adapter);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			private List<Map<String, Object>> listItems;
			Intent it = new Intent();
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				switch (type) {
				case PLAYLIST:
					isReturePlaylist = true;
					it.putExtra("position", position);
					it.setClass(MusicMainActivity.this, PlaylistSongActivity.class);
					startActivity(it);
					break;
				case SONGS_LIST:
					if(null == mService){
						mService = application.getmService();
					}
					mService.setCurrentListItme(position);
					mService.setSongs(songs);
					mService.playMusic(songs.get(position).getUrl());
					it.setClass(MusicMainActivity.this, MusicPlayActivity.class);
					startActivity(it);
					break;
				case ALL_SINGERS_LIST:
					songs = MusicUtils.MusicMp3ListbySinger(MusicMainActivity.this, singers.get(position));
					listItems = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < songs.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", "");
						map.put("songName", songs.get(i).getName());
						map.put("singerName", "");
						listItems.add(map);
					}
					adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
							R.id.tv_songName, R.id.tv_singerName });
					type = SONGS_LIST;
					listView.setAdapter(adapter);
					break;
				case ALL_ALBUMS_LIST:
					songs = MusicUtils.MusicMp3ListbyAlbum(MusicMainActivity.this, albums.get(position).getName());
					listItems = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < songs.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", "");
						map.put("songName", songs.get(i).getName());
						map.put("singerName", "");
						listItems.add(map);
					}
					adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
							R.id.tv_songName, R.id.tv_singerName });
					type = SONGS_LIST;
					listView.setAdapter(adapter);
					break;
				}
			}
		});
	}

	/**
	 * 新建列表
	 */
	public void addplaylist() {
		final EditText inputServer = new EditText(MusicMainActivity.this);
		AlertDialog.Builder builder = new AlertDialog.Builder(MusicMainActivity.this);

		builder.setTitle("Input Name").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String name = inputServer.getText().toString().trim();
				if (!TextUtils.isEmpty(name)) {
					ContentResolver resolver = getContentResolver();
					int id = idForplaylist(name);
					Uri uri;
					if (id >= 0) {
						uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
						MusicUtils.clearPlaylist(MusicMainActivity.this, id);
					} else {
						ContentValues values = new ContentValues(1);
						values.put(MediaStore.Audio.Playlists.NAME, name);
						uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
					}
					setResult(RESULT_OK, (new Intent()).setData(uri));

					isReturePlaylist = true;
					Intent it = new Intent();
					it.putExtra("playListName", name);
					it.putExtra("autoAddSong", true);
					it.setClass(MusicMainActivity.this, PlaylistSongActivity.class);
					startActivity(it);
				} else {
					Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		builder.create().show();
	}

	/**
	 * 通过列表名得到列表id
	 */
	private int idForplaylist(String name) {
		Cursor c = MusicUtils.query(this, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.Playlists._ID }, MediaStore.Audio.Playlists.NAME + "=?",
				new String[] { name }, MediaStore.Audio.Playlists.NAME);
		int id = -1;
		if (c != null) {
			c.moveToFirst();
			if (!c.isAfterLast()) {
				id = c.getInt(0);
			}
			c.close();
		}
		return id;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//这里要重新刷新列表，因为跳到列表歌曲界面时可能把这个列表删了，
		//所有再跳回来当然要刷新，另外新建列表再回来肯定要刷新的
		if (isReturePlaylist) {
			al_playlist = MusicUtils.PlaylistList(MusicMainActivity.this);
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < al_playlist.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", al_playlist.get(i));
				map.put("songName", "");
				map.put("singerName", "");
				listItems.add(map);
			}
			adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
					R.id.tv_songName, R.id.tv_singerName });
			listView.setAdapter(adapter);
			isReturePlaylist = false;
		}
	}
	public void allsongs(){
		tv_newPlaylist.setVisibility(View.GONE);
		songs = MusicUtils.getAllSongs(MusicMainActivity.this);
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
		adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
				R.id.tv_songName, R.id.tv_singerName });
		type = SONGS_LIST;
		listView.setAdapter(adapter);
	}
}
