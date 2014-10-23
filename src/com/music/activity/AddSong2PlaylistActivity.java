package com.music.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.music.R;
import com.music.adapter.ListViewAdapter;
import com.music.bean.Mp3;
import com.music.util.MusicUtils;

@SuppressLint("NewApi")
public class AddSong2PlaylistActivity extends Activity {

	private TextView tv_finish, tv_back;
	private ListView listView;
	public Button  btn_back;
	private long playlistId;// 当前播放列表id
	private List<Mp3> songs;// 得到全部歌曲
	private ArrayList<String> addSongIds = new ArrayList<String>();// 将要添加的歌曲的id集合
	private long[] addSongs;// 将要添加的歌曲的id集合
	private ListViewAdapter listViewAdapter;
	private List<Map<String, Object>> listItems;// 传进适配器的数据
	private ArrayList<String> songIds;// 全部歌曲的id
	private final int SETADAPTER = 111;
	private Timer timer;
	private TimerTask myTimerTask;
	Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case SETADAPTER:
				setAdapter();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addsong2playlist);
		initView();
		initListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 以下是定时器0.1秒后再跳到handler加载适配器
		timer = new Timer();
		myTimerTask = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = SETADAPTER;
				handler.sendMessage(message);
			}
		};
		timer.schedule(myTimerTask, 100);
	}

	public void initView() {
		listView = (ListView) findViewById(R.id.listView);
		tv_finish = (TextView) findViewById(R.id.tv_finish);
		tv_back = (TextView) findViewById(R.id.tv_back);
		btn_back = (Button) findViewById(R.id.back_btn);
	}

	private void initListener() {
		// 完成添加
		tv_finish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (addSongIds.size() > 0) {
					addSongs = new long[addSongIds.size()];
					for (int i = 0; i < addSongIds.size(); i++) {
						addSongs[i] = Integer.parseInt(addSongIds.get(i));
					}
					Intent it = getIntent();
					playlistId = it.getLongExtra("playlistId", -1);

					ArrayList<Mp3> songListForPlaylist = MusicUtils.getSongListForPlaylist(getApplicationContext(), playlistId);
					ArrayList<Long> tempSong = new ArrayList<Long>();

					long[] addSongTemp = null;
					if (songListForPlaylist.isEmpty()) {
						addSongTemp = addSongs;
					} else {
						for (int i = 0; i < addSongs.length; i++) {
							boolean playListContain = false;

							for (Mp3 tempMp3 : songListForPlaylist) {
								long sqlId = tempMp3.getAllSongIndex();
								Log.i("PLAYLIST", "CHECK: mp3: " + sqlId + " ADDID: " + addSongs[i]);
								if (sqlId == addSongs[i]) {
									playListContain = true;
									break;
								}
							}

							if (!playListContain) {
								tempSong.add(new Long(addSongs[i]));
							}
						}

						addSongTemp = new long[tempSong.size()];
						for (int i = 0; i < tempSong.size(); i++) {
							addSongTemp[i] = tempSong.get(i);
						}
					}

					MusicUtils.addToPlaylist(AddSong2PlaylistActivity.this, addSongTemp, playlistId);
				}
				finish();
			}
		});
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void setAdapter() {

		listItems = getListItems();
		if (null != listViewAdapter) {
			listViewAdapter.setListItems(listItems);
		} else {
			listViewAdapter = new ListViewAdapter(this, listItems, R.layout.pl_songs_add); // 创建适配器
		}
		listViewAdapter.setSongIds(songIds);
		listView.setAdapter(listViewAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				if (isSelected(songs.get(position).getSqlId())) {
					addSongIds.remove(String.valueOf(songs.get(position).getSqlId()));
				} else {
					addSongIds.add(songs.get(position).getSqlId() + "");
				}

				listViewAdapter.setAddSongIds(addSongIds);

				listViewAdapter.notifyDataSetChanged();
			}
		});

	}

	public boolean isSelected(int songID) {
		return addSongIds.contains(String.valueOf(songID));
	}

	/**
	 * 返回适配器所要加载的数据集合
	 */
	private List<Map<String, Object>> getListItems() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		songs = MusicUtils.getAllSongs(AddSong2PlaylistActivity.this);
		songIds = new ArrayList<String>();
		for (int i = 0; i < songs.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			songIds.add(songs.get(i).getSqlId() + "");
			map.put("deleteIcon", R.drawable.music_add_disable);
			map.put("songName", songs.get(i).getName()); // 歌曲名
			if (songs.get(i).getSingerName().equals("<unknown>")) {
				map.put("singerName", "----");
			} else {
				map.put("singerName", songs.get(i).getSingerName()); // 歌手名
			}
			listItems.add(map);
		}

		return listItems;
	}

}
