package com.yusun.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.yusun.music.util.MusicUtils;

public class normalsettings extends Activity{
	private TextView tv_newPlaylist;
	private List<String> al_playlist;// 播放列表集合
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		listView = (ListView) this.findViewById(R.id.listview);
		tv_newPlaylist = (TextView) this.findViewById(R.id.tv_newPlaylist);
		tv_newPlaylist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addplaylist();
			}
		});
		playlist();
	}
	public void playlist(){
		tv_newPlaylist.setVisibility(View.VISIBLE);
		al_playlist = MusicUtils.PlaylistList(normalsettings.this);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < al_playlist.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", "");
			map.put("songName", al_playlist.get(i));
			map.put("singerName", "");
			listItems.add(map);
		}
		adapter = new SimpleAdapter(normalsettings.this, listItems, R.layout.item4music_main_activity, new String[] { "id", "songName", "singerName" }, new int[] { R.id.tv_id,
				R.id.tv_songName, R.id.tv_singerName });
		type = PLAYLIST;
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			private List<Map<String, Object>> listItems;
			Intent it = new Intent();
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					isReturePlaylist = true;
					it.putExtra("position", position);
					it.setClass(normalsettings.this, PlaylistSongActivity.class);
					startActivity(it);
					
				};
		});
		
	}
	public void addplaylist() {
	final EditText inputServer = new EditText(normalsettings.this);
	AlertDialog.Builder builder = new AlertDialog.Builder(normalsettings.this);

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
					MusicUtils.clearPlaylist(normalsettings.this, id);
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
				it.setClass(normalsettings.this, PlaylistSongActivity.class);
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
	
	
}

