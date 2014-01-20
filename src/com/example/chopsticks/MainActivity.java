package com.example.chopsticks;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.staticdata.StaticData;

public class MainActivity extends Activity {

	String roundBalance;
	/** Sound variables */
	private SoundPool soundPool;
	private int applause;
	boolean loaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set the balance to the 'Balance' text view.
		TextView balance = (TextView) findViewById(R.id.textView_main_balance);
		roundBalance = String.format("%.2f", StaticData.balance);
		balance.setText(roundBalance);

		// Set the hardware buttons to control the music
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		// Load the sound
		applause = soundPool.load(this, R.raw.applause1, 1);
		// Did sound load successfully?
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * OnClickListner for PlayButton
	 * 
	 * @param arg0
	 */
	public void onPlayButtonClick(View arg0) {

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		// Is the sound loaded already?
		if (loaded) {
			soundPool.play(applause, volume, volume, 1, 0, 1f);
		}

		Intent i = new Intent(MainActivity.this, PlayActivity.class);
		startActivity(i);
	}

	/**
	 * OnClickListner for Send Button
	 * 
	 * @param arg0
	 */
	public void onSendButtonClick(View arg0) {
		Intent i = new Intent(MainActivity.this, SendActivity.class);
		startActivity(i);
	}

	/**
	 * OnClickListner for Receive Button
	 * 
	 * @param arg0
	 */
	public void onReceiveButtonClick(View arg0) {
		Intent i = new Intent(MainActivity.this, ReceiveActivity.class);
		startActivity(i);
	}

}
