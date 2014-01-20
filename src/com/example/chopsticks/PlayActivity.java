package com.example.chopsticks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.models.WinItem;
import com.example.staticdata.StaticData;
import com.google.common.collect.Lists;

@SuppressLint("NewApi")
public class PlayActivity extends Activity {

	private float won;
	private Random random = new Random();
	private String roundBalance;
	private String roundWon;
	private String cameFrom = "";
	private int images[];
	private int count = 0;
	private int diceScore = 0;
	private int randomNum;
	private List<Integer> randomNumbersList1 = new ArrayList<Integer>();;
	private List<Integer> randomNumbersList2 = new ArrayList<Integer>();;

	private ImageButton one;
	private ImageButton two;
	private ImageButton five;
	private ImageButton ten;
	private ImageButton twenty;
	private ImageButton fifty;

	private ImageView dice1;
	private ImageView dice2;

	private TextView diceScoreDisplay = null;

	/** Sound variables */
	private SoundPool soundPool;
	private int rollingDice;
	private int cash;
	private int flip;
	private int lowPitchCheer;
	private int highPitchCheer;
	private int scoreTwo;
	private int scoreThree4;
	private int scoreFive6;
	private int score7;
	boolean loaded = false;
	private int screenWidth;

	AnimationDrawable frameAnimation;
	ImageView coin;

	private HorizontalScrollView scrollView1 = null;

	private ViewFlipper vf;
	private float lastX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);

		// Get mobile device 'width' to adjust bet buttons.(Because only three
		// buttons will be appear at a time)
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;

		// Loading dice images to images array
		images = new int[6];
		images[0] = R.drawable.one;
		images[1] = R.drawable.two;
		images[2] = R.drawable.three;
		images[3] = R.drawable.four;
		images[4] = R.drawable.five;
		images[5] = R.drawable.six;

		one = (ImageButton) findViewById(R.id.one);
		two = (ImageButton) findViewById(R.id.two);
		five = (ImageButton) findViewById(R.id.five);
		ten = (ImageButton) findViewById(R.id.ten);
		twenty = (ImageButton) findViewById(R.id.twenty);
		fifty = (ImageButton) findViewById(R.id.fifty);

		dice1 = (ImageView) findViewById(R.id.imageViewrollingdiceOne);
		dice2 = (ImageView) findViewById(R.id.imageViewrollingdiceTwo);

		vf = (ViewFlipper) findViewById(R.id.view_flipper);

		scrollView1 = (HorizontalScrollView) findViewById(R.id.scrollview1);
		// Hide scroll bar of the scrollView
		scrollView1.setHorizontalScrollBarEnabled(false);

		final ListView listView = (ListView) findViewById(R.id.list);
		diceScoreDisplay = (TextView) findViewById(R.id.textViewDiceScoreDisplay);

		LinearLayout layout1 = (LinearLayout) findViewById(R.id.LinearLayoutbetOne);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.LinearLayoutbetTwo);
		LinearLayout layout5 = (LinearLayout) findViewById(R.id.LinearLayoutbetFive);
		LinearLayout layout10 = (LinearLayout) findViewById(R.id.LinearLayoutbetTen);
		LinearLayout layout20 = (LinearLayout) findViewById(R.id.LinearLayoutbetTwenty);
		LinearLayout layout50 = (LinearLayout) findViewById(R.id.LinearLayoutbetFifty);

		LayoutParams params1 = layout1.getLayoutParams();
		LayoutParams params2 = layout2.getLayoutParams();
		LayoutParams params5 = layout5.getLayoutParams();
		LayoutParams params10 = layout10.getLayoutParams();
		LayoutParams params20 = layout20.getLayoutParams();
		LayoutParams params50 = layout50.getLayoutParams();

		// Changes the width of the bet button container layout width, We do
		// this because we need to show only 3 buttons at a time
		params1.width = screenWidth / 3;
		params2.width = screenWidth / 3;
		params5.width = screenWidth / 3;
		params10.width = screenWidth / 3;
		params20.width = screenWidth / 3;
		params50.width = screenWidth / 3;

		coin = (ImageView) findViewById(R.id.coin);
		coin.setBackgroundResource(R.drawable.heads);

		// Set the balance
		TextView balance = (TextView) findViewById(R.id.textView_play_balance);
		roundBalance = String.format("%.2f", StaticData.balance);
		balance.setText(roundBalance);

		// Set the hardware buttons to control the music
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		// Loading sound clips
		rollingDice = soundPool.load(this, R.raw.open, 1);
		cash = soundPool.load(this, R.raw.cash, 1);
		flip = soundPool.load(this, R.raw.coinflip, 1);
		lowPitchCheer = soundPool.load(this, R.raw.lowpitchcheer, 1);
		highPitchCheer = soundPool.load(this, R.raw.highpitchcheer, 1);
		scoreTwo = soundPool.load(this, R.raw.two, 1);
		scoreThree4 = soundPool.load(this, R.raw.three4, 1);
		scoreFive6 = soundPool.load(this, R.raw.five6, 1);
		score7 = soundPool.load(this, R.raw.seven, 1);

		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});

		// Set the adapter to the ListView
		listView.setAdapter(new ArraysItemAdapter(getApplicationContext(),
				R.layout.rowlayout, Lists.reverse(StaticData.winList)));

		/**
		 * OnClickListner for Winning List Item
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Object o = listView.getItemAtPosition(position);
				WinItem wi = (WinItem) o;// As you are using Default String
											// Adapter
				Intent i = new Intent(PlayActivity.this,
						WiningDetailsActivity.class);
				i.putExtra("winItem", wi);
				startActivity(i);
				finish();
			}
		});

		/**
		 * OnClickListner for dice image1 click
		 */
		dice1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (StaticData.balance >= 1) {
					// Do this to prevent clicking two image Views at the same
					// time
					ViewGroup group = (ViewGroup) findViewById(R.id.container);
					for (View touchable : group.getTouchables()) {
						if (touchable == view && touchable.isPressed()) {
							dice2.setEnabled(false);
							dice1.setEnabled(false);
							doButton1Action();
						}
					}
				}

			}
		});

		/**
		 * OnClickListner for dice image2 click
		 */
		dice2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (StaticData.balance >= 1) {
					// Do this to prevent clicking two image Views at the same
					// time
					ViewGroup group = (ViewGroup) findViewById(R.id.container);
					for (View touchable : group.getTouchables()) {
						if (touchable == view && touchable.isPressed()) {
							dice2.setEnabled(false);
							dice1.setEnabled(false);
							doButton1Action();
						}
					}
				}
			}
		});

		coin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				float volume;
				AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
				float actualVolume = (float) audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				float maxVolume = (float) audioManager
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				volume = actualVolume / maxVolume;

				if (loaded) {
					// then play cash register sound.
					soundPool.play(flip, volume, volume, 1, 0, 1f);

				}

				v.setBackgroundResource(R.drawable.coin_animation);

				// Get the background, which has been compiled to an
				// AnimationDrawable object.
				frameAnimation = (AnimationDrawable) v.getBackground();

				// Start the animation (looped playback by default).
				frameAnimation.start();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Random generator = new Random();
								int random = generator
										.nextInt(Integer.MAX_VALUE) + 1;
								System.out.println(random);
								if (random % 2 == 0) {
									frameAnimation.stop();
									coin.setBackgroundResource(R.drawable.heads);

								} else {
									frameAnimation.stop();
									coin.setBackgroundResource(R.drawable.tails);

								}

							}
						});
					}
				}, 5 * 1000);

			}
		});

	}

	@Override
	public void onDestroy() // main thread stopped
	{
		super.onDestroy();
		System.runFinalizersOnExit(true); // wait for threads to exit before
											// clearing app
		android.os.Process.killProcess(android.os.Process.myPid()); // remove
																	// app from
																	// memory
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * onBackPressed. If back button pressed, it should ask
	 * "Are you sure you want to quit?"
	 */
	@Override
	public void onBackPressed() {

		AlertDialog.Builder alert = new AlertDialog.Builder(PlayActivity.this);

		alert.setTitle("Are you sure you want to quit?");
		// if user want to quit, do this.
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		// if NO ,do nothing
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});

		alert.show();
	}

	/**
	 * Handling viewFlipper animations -
	 */
	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float currentX = touchevent.getX();
			if (lastX < currentX) {
				if (vf.getDisplayedChild() == 0)
					break;
				vf.setInAnimation(this, R.anim.in_from_left);
				vf.setOutAnimation(this, R.anim.out_to_right);
				vf.showNext();
			}
			if (lastX > currentX) {
				if (vf.getDisplayedChild() == 1)
					break;
				vf.setInAnimation(this, R.anim.in_from_right);
				vf.setOutAnimation(this, R.anim.out_to_left);
				vf.showPrevious();
			}
			break;
		}
		}
		return false;
	}

	// If balance textClicked, go to the chopstick page
	public void onBalanceTextClick(View arg0) {
		Intent i = new Intent(PlayActivity.this, MainActivity.class);
		startActivity(i);

	}

	/**
	 * bet1onClickListner
	 * 
	 * @param arg0
	 * @throws InterruptedException
	 */
	public void bet1onClickListner(View arg0) {
		if (StaticData.balance >= 1) {
			doButton1Action();
		}

	}

	/**
	 * Do this if bet 1 button Clicked
	 */
	public void doButton1Action() {

		// disable buttons from being clicked after a button pressed
		one.setEnabled(false);
		two.setEnabled(false);
		five.setEnabled(false);
		ten.setEnabled(false);
		twenty.setEnabled(false);
		fifty.setEnabled(false);
		dice1.setEnabled(false);
		dice2.setEnabled(false);

		cameFrom = "bet1";
		randomNumbersList1.clear();// clear previous random num list1
		randomNumbersList2.clear();// clear previous random num list2

		// Generate 20 random numbers and add them to randomNumbersList1
		for (int x = 0; x < 20; x++) {
			randomNum = random.nextInt(6);
			randomNumbersList1.add(randomNum);
		}
		// Generate 20 random numbers and add them to randomNumbersList2
		for (int y = 0; y < 20; y++) {
			randomNum = random.nextInt(6);
			randomNumbersList2.add(randomNum);
		}

		// This is used to change dice images rapidly. dice image will change 10
		// times per second.
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				if (count < randomNumbersList1.size()) {

					dice1.setImageResource(images[randomNumbersList1.get(count)]);
					dice2.setImageResource(images[randomNumbersList2.get(count)]);
					count++;

					handler.postDelayed(this, 100);
				}

			}
		}, 100);
		count = 0;

		// Call async Task class
		SoundDelayedPlay s = new SoundDelayedPlay();
		s.execute();
	}

	/**
	 * bet2onClickListner
	 * 
	 * @param arg0
	 */
	public void bet2onClickListner(View arg0) {

		// scrollView1.smoothScrollTo(0, 0);
		if (StaticData.balance >= 2) {// bet is accepted only if balance is
										// greater than the bet

			cameFrom = "bet2";
			randomNumbersList1.clear();// clear previous random num list1
			randomNumbersList2.clear();// clear previous random num list1

			// disable buttons from being clicked after a button pressed
			one.setEnabled(false);
			two.setEnabled(false);
			five.setEnabled(false);
			ten.setEnabled(false);
			twenty.setEnabled(false);
			fifty.setEnabled(false);
			dice1.setEnabled(false);
			dice2.setEnabled(false);

			// Generate 20 random numbers and add them to randomNumbersList1
			for (int x = 0; x < 20; x++) {
				randomNum = random.nextInt(6);
				randomNumbersList1.add(randomNum);
			}
			// Generate 20 random numbers and add them to randomNumbersList2
			for (int y = 0; y < 20; y++) {
				randomNum = random.nextInt(6);
				randomNumbersList2.add(randomNum);
			}
			// This is used to change dice images rapidly. dice image will
			// change 10 times per second.
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if (count < randomNumbersList1.size()) {

						dice1.setImageResource(images[randomNumbersList1
								.get(count)]);
						dice2.setImageResource(images[randomNumbersList2
								.get(count)]);

						count++;

						handler.postDelayed(this, 100);
					}

				}
			}, 100);
			count = 0;

			SoundDelayedPlay s = new SoundDelayedPlay();
			s.execute();
		}

	}

	/**
	 * bet5onClickListner
	 * 
	 * @param arg0
	 * @throws InterruptedException
	 */
	public void bet5onClickListner(View arg0) throws InterruptedException {
		// bet is accepted only if balance is greater than the bet
		if (StaticData.balance >= 5) {

			cameFrom = "bet5";
			randomNumbersList1.clear();// clear previous random num list1
			randomNumbersList2.clear();// clear previous random num list2

			// disable buttons from being clicked after a button pressed
			one.setEnabled(false);
			two.setEnabled(false);
			five.setEnabled(false);
			ten.setEnabled(false);
			twenty.setEnabled(false);
			fifty.setEnabled(false);
			dice1.setEnabled(false);
			dice2.setEnabled(false);

			// Generate 20 random numbers and add them to randomNumbersList1
			for (int x = 0; x < 20; x++) {
				randomNum = random.nextInt(6);
				randomNumbersList1.add(randomNum);
			}
			// //Generate 20 random numbers and add them to randomNumbersList2
			for (int y = 0; y < 20; y++) {
				randomNum = random.nextInt(6);
				randomNumbersList2.add(randomNum);
			}

			// This is used to change dice images rapidly. dice image will
			// change 10 times per second.
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if (count < randomNumbersList1.size()) {

						dice1.setImageResource(images[randomNumbersList1
								.get(count)]);
						dice2.setImageResource(images[randomNumbersList2
								.get(count)]);

						count++;

						handler.postDelayed(this, 100);
					}

				}
			}, 100);
			count = 0;

			SoundDelayedPlay s = new SoundDelayedPlay();
			s.execute();
		}

	}

	public void bet10onClickListner(View arg0) throws InterruptedException {

		// bet is accepted only if balance is greater than the bet
		if (StaticData.balance >= 10) {

			cameFrom = "bet10";
			randomNumbersList1.clear();
			randomNumbersList2.clear();

			// disable buttons from being clicked after a button pressed
			one.setEnabled(false);
			two.setEnabled(false);
			five.setEnabled(false);
			ten.setEnabled(false);
			twenty.setEnabled(false);
			fifty.setEnabled(false);

			dice1.setEnabled(false);
			dice2.setEnabled(false);

			// Generate 20 random numbers and add them to randomNumbersList1
			for (int x = 0; x < 20; x++) {
				randomNum = random.nextInt(6);
				randomNumbersList1.add(randomNum);
			}
			// Generate 20 random numbers and add them to randomNumbersList2
			for (int y = 0; y < 20; y++) {
				randomNum = random.nextInt(6);
				randomNumbersList2.add(randomNum);
			}

			// This is used to change dice images rapidly. dice image will
			// change 10 times per second.
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if (count < randomNumbersList1.size()) {

						dice1.setImageResource(images[randomNumbersList1
								.get(count)]);
						dice2.setImageResource(images[randomNumbersList2
								.get(count)]);

						count++;

						handler.postDelayed(this, 100);
					}

				}
			}, 100);
			count = 0;

			SoundDelayedPlay s = new SoundDelayedPlay();
			s.execute();
		}
	}

	public void bet20onClickListner(View arg0) throws InterruptedException {

		// bet is accepted only if balance is greater than the bet
		if (StaticData.balance >= 20) {

			cameFrom = "bet20";
			randomNumbersList1.clear();
			randomNumbersList2.clear();

			// disable buttons from being clicked after a button pressed
			one.setEnabled(false);
			two.setEnabled(false);
			five.setEnabled(false);
			ten.setEnabled(false);
			twenty.setEnabled(false);
			fifty.setEnabled(false);

			dice1.setEnabled(false);
			dice2.setEnabled(false);

			// Generate 20 random numbers and add them to randomNumbersList1
			for (int x = 0; x < 20; x++) {
				randomNum = random.nextInt(6);
				randomNumbersList1.add(randomNum);
			}
			// Generate 20 random numbers and add them to randomNumbersList2
			for (int y = 0; y < 20; y++) {
				randomNum = random.nextInt(6);
				randomNumbersList2.add(randomNum);
			}

			// This is used to change dice images rapidly. dice image will
			// change 10 times per second.
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if (count < randomNumbersList1.size()) {

						dice1.setImageResource(images[randomNumbersList1
								.get(count)]);
						dice2.setImageResource(images[randomNumbersList2
								.get(count)]);

						count++;

						handler.postDelayed(this, 100);
					}

				}
			}, 100);
			count = 0;

			SoundDelayedPlay s = new SoundDelayedPlay();
			s.execute();
		}

	}

	public void bet50onClickListner(View arg0) throws InterruptedException {
		// bet is accepted only if balance is greater than the bet
		if (StaticData.balance >= 50) {
			cameFrom = "bet50";
			randomNumbersList1.clear();
			randomNumbersList2.clear();

			// disable buttons from being clicked after a button pressed
			one.setEnabled(false);
			two.setEnabled(false);
			five.setEnabled(false);
			ten.setEnabled(false);
			twenty.setEnabled(false);
			fifty.setEnabled(false);
			dice1.setEnabled(false);
			dice2.setEnabled(false);

			// Generate 20 random numbers and add them to randomNumbersList1
			for (int x = 0; x < 20; x++) {
				randomNum = random.nextInt(6);
				randomNumbersList1.add(randomNum);
			}
			// Generate 20 random numbers and add them to randomNumbersList2
			for (int y = 0; y < 20; y++) {
				randomNum = random.nextInt(6);
				randomNumbersList2.add(randomNum);
			}

			// This is used to change dice images rapidly. dice image will
			// change 10 times per second.
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if (count < randomNumbersList1.size()) {

						dice1.setImageResource(images[randomNumbersList1
								.get(count)]);
						dice2.setImageResource(images[randomNumbersList2
								.get(count)]);

						count++;

						handler.postDelayed(this, 100);
					}

				}
			}, 100);
			count = 0;

			SoundDelayedPlay s = new SoundDelayedPlay();
			s.execute();
		}

	}

	/**
	 * List item Array adapter This is used to create wining list view
	 * 
	 * @author Basna PC
	 * 
	 */
	public class ArraysItemAdapter extends ArrayAdapter<WinItem> {

		private List<WinItem> wins;

		public ArraysItemAdapter(Context context, int textViewResourceId,
				List<WinItem> winList) {
			super(context, textViewResourceId, winList);
			this.wins = winList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = vi.inflate(R.layout.rowlayout, null);
			}

			WinItem item = wins.get(position);
			if (item != null) {
				if (position == 0) {
					LinearLayout lLayout = (LinearLayout) v
							.findViewById(R.id.listItemBackground);
					lLayout.setBackgroundColor(Color.parseColor("#EAEAEA"));
				}
				TextView diceScore = (TextView) v
						.findViewById(R.id.textViewDiceTotalListView);
				TextView betAmount = (TextView) v
						.findViewById(R.id.textViewBetAmountListView);
				TextView wintext = (TextView) v.findViewById(R.id.textViewWin);

				diceScore.setText(item.dice_score.toString());
				betAmount.setText(", bet " + item.bet.toString());
				wintext.setText(", Won: " + item.winning);

			}
			return v;
		}

	}

	/**
	 * Balance should not be updated until dice rolling finishes So, AsyncTask
	 * class implemented for waiting until dice rolling finish
	 * 
	 * @author Basna PC
	 * 
	 */
	public class SoundDelayedPlay extends AsyncTask<Void, Void, Void> {
		float volume;

		@Override
		protected Void doInBackground(Void... arg0) {

			// play sound clip
			AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			float actualVolume = (float) audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxVolume = (float) audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			volume = actualVolume / maxVolume;
			// Is the sound loaded already?
			if (loaded) {
				for (int x = 0; x < 20; x++) {
					soundPool.play(rollingDice, volume, volume, 1, 0, 1f);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			return null;

		}

		// This method is called after the dice rolling finished.
		@Override
		protected void onPostExecute(Void result) {

			/**
			 * If bet 1 Clicked
			 */
			if (cameFrom.equalsIgnoreCase("bet1")) {

				// reduce betting amount from the balance.
				TextView balance = (TextView) findViewById(R.id.textView_play_balance);
				StaticData.balance = StaticData.balance - 1;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				// Calculate the dice score
				diceScore = randomNumbersList1.get(19)
						+ randomNumbersList2.get(19);
				WinItem i = new WinItem();
				i.bet = 1;

				// dice score=0 means dice score =2
				if (diceScore == 0) {
					// calculate wining amount
					won = 1 * ((float) 10 / 100);
					// round it to 2 decimal points
					roundWon = String.format("%.2f", won);
					// create a winItem Object

					i.winning = roundWon;
					i.dice_score = 2;
					i.winning_percentage = 10;
					// add win item object to win list
					StaticData.winList.add(i);
				}
				// dice score=1 means dice score =3
				else if (diceScore == 1) {
					won = 1 * ((float) 20 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 3;
					i.winning_percentage = 20;
					StaticData.winList.add(i);
				}
				// dice score=2 means dice score =4 ...
				else if (diceScore == 2) {
					won = 1 * ((float) 40 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 4;
					i.winning_percentage = 40;
					StaticData.winList.add(i);
				} else if (diceScore == 3) {
					won = 1 * ((float) 60 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 5;
					i.winning_percentage = 60;
					StaticData.winList.add(i);
				} else if (diceScore == 4) {
					won = 1 * ((float) 80 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 6;
					i.winning_percentage = 80;
					StaticData.winList.add(i);
				} else if (diceScore == 5) {
					won = 1 * ((float) 101 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 7;
					i.winning_percentage = 101;
					StaticData.winList.add(i);
				} else if (diceScore == 6) {
					won = 1 * ((float) 120 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 8;
					i.winning_percentage = 120;
					StaticData.winList.add(i);
				} else if (diceScore == 7) {
					won = 1 * ((float) 140 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 9;
					i.winning_percentage = 140;
					StaticData.winList.add(i);
				} else if (diceScore == 8) {
					won = 1 * ((float) 160 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 10;
					i.winning_percentage = 160;
					StaticData.winList.add(i);
				} else if (diceScore == 9) {
					won = 1 * ((float) 180 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 11;
					i.winning_percentage = 180;
					StaticData.winList.add(i);
				} else if (diceScore == 10) {
					int randomNum = random.nextInt(300) + 1;
					float bonus = 1 * ((float) randomNum / 100);
					won = 1 * ((float) 200 / 100) + bonus;
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 12;
					i.winning_percentage = 200;
					i.bonus_percentage = randomNum;
					StaticData.winList.add(i);
				}

				// Update new Balance
				StaticData.balance = StaticData.balance + won;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				// re enable buttons
				one.setEnabled(true);
				two.setEnabled(true);
				five.setEnabled(true);
				ten.setEnabled(true);
				twenty.setEnabled(true);
				fifty.setEnabled(true);

				dice1.setEnabled(true);
				dice2.setEnabled(true);

			}
			/**
			 * If bet 2 Clicked
			 */
			if (cameFrom.equalsIgnoreCase("bet2")) {

				// reduce betting amount from the balance
				TextView balance = (TextView) findViewById(R.id.textView_play_balance);
				StaticData.balance = StaticData.balance - 2;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				// Calculate the dice score
				diceScore = randomNumbersList1.get(19)
						+ randomNumbersList2.get(19);

				WinItem i = new WinItem();
				i.bet = 2;
				if (diceScore == 0) {
					won = 2 * ((float) 10 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 2;
					i.winning_percentage = 10;
					StaticData.winList.add(i);
				} else if (diceScore == 1) {
					won = 2 * ((float) 20 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 3;
					i.winning_percentage = 20;
					StaticData.winList.add(i);
				} else if (diceScore == 2) {
					won = 2 * ((float) 40 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 4;
					i.winning_percentage = 40;
					StaticData.winList.add(i);
				} else if (diceScore == 3) {
					won = 2 * ((float) 60 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 5;
					i.winning_percentage = 60;
					StaticData.winList.add(i);
				} else if (diceScore == 4) {
					won = 2 * ((float) 80 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 6;
					i.winning_percentage = 80;
					StaticData.winList.add(i);
				} else if (diceScore == 5) {
					won = 2 * ((float) 101 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 7;
					i.winning_percentage = 101;
					StaticData.winList.add(i);
				} else if (diceScore == 6) {
					won = 2 * ((float) 120 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 8;
					i.winning_percentage = 120;
					StaticData.winList.add(i);
				} else if (diceScore == 7) {
					won = 2 * ((float) 140 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 9;
					i.winning_percentage = 140;
					StaticData.winList.add(i);
				} else if (diceScore == 8) {
					won = 2 * ((float) 160 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 10;
					i.winning_percentage = 160;
					StaticData.winList.add(i);
				} else if (diceScore == 9) {
					won = 2 * ((float) 180 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 11;
					i.winning_percentage = 180;
					StaticData.winList.add(i);
				} else if (diceScore == 10) {
					int randomNum = random.nextInt(300) + 1;
					float bonus = 2 * ((float) randomNum / 100);
					won = 2 * ((float) 200 / 100) + bonus;
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 12;
					i.winning_percentage = 200;
					i.bonus_percentage = randomNum;
					StaticData.winList.add(i);
				}

				// Update new Balance
				StaticData.balance = StaticData.balance + won;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				// re enable buttons
				one.setEnabled(true);
				two.setEnabled(true);
				five.setEnabled(true);
				one.setEnabled(true);
				two.setEnabled(true);
				five.setEnabled(true);
				ten.setEnabled(true);
				twenty.setEnabled(true);
				fifty.setEnabled(true);

				dice1.setEnabled(true);
				dice2.setEnabled(true);
			}
			/**
			 * If bet 5 Clicked
			 */
			if (cameFrom.equalsIgnoreCase("bet5")) {
				// reduce betting amount from the balance
				TextView balance = (TextView) findViewById(R.id.textView_play_balance);
				StaticData.balance = StaticData.balance - 5;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				// Calculate the dice score
				diceScore = randomNumbersList1.get(19)
						+ randomNumbersList2.get(19);

				WinItem i = new WinItem();
				i.bet = 5;
				if (diceScore == 0) {
					won = 5 * ((float) 10 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 2;
					i.winning_percentage = 10;
					StaticData.winList.add(i);
				} else if (diceScore == 1) {
					won = 5 * ((float) 20 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 3;
					i.winning_percentage = 20;
					StaticData.winList.add(i);
				} else if (diceScore == 2) {
					won = 5 * ((float) 40 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 4;
					i.winning_percentage = 40;
					StaticData.winList.add(i);
				} else if (diceScore == 3) {
					won = 5 * ((float) 60 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 5;
					i.winning_percentage = 60;
					StaticData.winList.add(i);
				} else if (diceScore == 4) {
					won = 5 * ((float) 80 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 6;
					i.winning_percentage = 80;
					StaticData.winList.add(i);
				} else if (diceScore == 5) {
					won = 5 * ((float) 101 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 7;
					i.winning_percentage = 101;
					StaticData.winList.add(i);
				} else if (diceScore == 6) {
					won = 5 * ((float) 120 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 8;
					i.winning_percentage = 120;
					StaticData.winList.add(i);
				} else if (diceScore == 7) {
					won = 5 * ((float) 140 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 9;
					i.winning_percentage = 140;
					StaticData.winList.add(i);
				} else if (diceScore == 8) {
					won = 5 * ((float) 160 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 10;
					i.winning_percentage = 160;
					StaticData.winList.add(i);
				} else if (diceScore == 9) {
					won = 5 * ((float) 180 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 11;
					i.winning_percentage = 180;
					StaticData.winList.add(i);
				} else if (diceScore == 10) {
					int randomNum = random.nextInt(300) + 1;
					float bonus = 5 * ((float) randomNum / 100);
					won = 5 * ((float) 200 / 100) + bonus;
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 12;
					i.winning_percentage = 200;
					i.bonus_percentage = randomNum;
					StaticData.winList.add(i);
				}

				// Update new Balance
				StaticData.balance = StaticData.balance + won;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				if (StaticData.balance >= 10) {
					// automatically scroll button 5 to middle of the screen
					scrollView1.smoothScrollTo((int) (screenWidth / 3), 0);
				}

				// re enable buttons
				one.setEnabled(true);
				two.setEnabled(true);
				five.setEnabled(true);
				one.setEnabled(true);
				two.setEnabled(true);
				five.setEnabled(true);
				ten.setEnabled(true);
				twenty.setEnabled(true);
				fifty.setEnabled(true);

				dice1.setEnabled(true);
				dice2.setEnabled(true);
			}

			/**
			 * If bet 10 Clicked
			 */
			if (cameFrom.equalsIgnoreCase("bet10")) {

				// reduce betting amount from the balance.
				TextView balance = (TextView) findViewById(R.id.textView_play_balance);
				StaticData.balance = StaticData.balance - 10;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				// Calculate the dice Score
				diceScore = randomNumbersList1.get(19)
						+ randomNumbersList2.get(19);

				WinItem i = new WinItem();
				i.bet = 10;
				if (diceScore == 0) {
					won = 10 * ((float) 10 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 2;
					i.winning_percentage = 10;
					StaticData.winList.add(i);
				} else if (diceScore == 1) {
					won = 10 * ((float) 20 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 3;
					i.winning_percentage = 20;
					StaticData.winList.add(i);
				} else if (diceScore == 2) {
					won = 10 * ((float) 40 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 4;
					i.winning_percentage = 40;
					StaticData.winList.add(i);
				} else if (diceScore == 3) {
					won = 10 * ((float) 60 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 5;
					i.winning_percentage = 60;
					StaticData.winList.add(i);
				} else if (diceScore == 4) {
					won = 10 * ((float) 80 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 6;
					i.winning_percentage = 80;
					StaticData.winList.add(i);
				} else if (diceScore == 5) {
					won = 10 * ((float) 101 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 7;
					i.winning_percentage = 101;
					StaticData.winList.add(i);
				} else if (diceScore == 6) {
					won = 10 * ((float) 120 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 8;
					i.winning_percentage = 120;
					StaticData.winList.add(i);
				} else if (diceScore == 7) {
					won = 10 * ((float) 140 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 9;
					i.winning_percentage = 140;
					StaticData.winList.add(i);
				} else if (diceScore == 8) {
					won = 10 * ((float) 160 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 10;
					i.winning_percentage = 160;
					StaticData.winList.add(i);
				} else if (diceScore == 9) {
					won = 10 * ((float) 180 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 11;
					i.winning_percentage = 180;
					StaticData.winList.add(i);
				} else if (diceScore == 10) {
					int randomNum = random.nextInt(300) + 1;
					float bonus = 10 * ((float) randomNum / 100);
					won = 10 * ((float) 200 / 100) + bonus;
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 12;
					i.winning_percentage = 200;
					i.bonus_percentage = randomNum;
					StaticData.winList.add(i);
				}

				// Update new Balance
				StaticData.balance = StaticData.balance + won;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				if (StaticData.balance >= 20) {
					// automatically scroll button 5 to middle of the screen
					scrollView1
							.smoothScrollTo((int) ((screenWidth / 3) * 2), 0);
				} else if (StaticData.balance >= 10) {
					scrollView1.smoothScrollTo((int) (screenWidth / 3), 0);
				} else if (StaticData.balance < 10) {
					scrollView1.smoothScrollTo(0, 0);
				}

				// re enable buttons
				one.setEnabled(true);
				two.setEnabled(true);
				five.setEnabled(true);
				ten.setEnabled(true);
				twenty.setEnabled(true);
				fifty.setEnabled(true);

				dice1.setEnabled(true);
				dice2.setEnabled(true);

			}

			/**
			 * If bet 20 Clicked
			 */
			if (cameFrom.equalsIgnoreCase("bet20")) {

				// reduce betting amount from the balance.
				TextView balance = (TextView) findViewById(R.id.textView_play_balance);
				StaticData.balance = StaticData.balance - 20;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				// Calculate the dice Score
				diceScore = randomNumbersList1.get(19)
						+ randomNumbersList2.get(19);

				WinItem i = new WinItem();
				i.bet = 20;
				if (diceScore == 0) {
					won = 20 * ((float) 10 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 2;
					i.winning_percentage = 10;
					StaticData.winList.add(i);
				} else if (diceScore == 1) {
					won = 20 * ((float) 20 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 3;
					i.winning_percentage = 20;
					StaticData.winList.add(i);
				} else if (diceScore == 2) {
					won = 20 * ((float) 40 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 4;
					i.winning_percentage = 40;
					StaticData.winList.add(i);
				} else if (diceScore == 3) {
					won = 20 * ((float) 60 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 5;
					i.winning_percentage = 60;
					StaticData.winList.add(i);
				} else if (diceScore == 4) {
					won = 20 * ((float) 80 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 6;
					i.winning_percentage = 80;
					StaticData.winList.add(i);
				} else if (diceScore == 5) {
					won = 20 * ((float) 101 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 7;
					i.winning_percentage = 101;
					StaticData.winList.add(i);
				} else if (diceScore == 6) {
					won = 20 * ((float) 120 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 8;
					i.winning_percentage = 120;
					StaticData.winList.add(i);
				} else if (diceScore == 7) {
					won = 20 * ((float) 140 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 9;
					i.winning_percentage = 140;
					StaticData.winList.add(i);
				} else if (diceScore == 8) {
					won = 20 * ((float) 160 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 10;
					i.winning_percentage = 160;
					StaticData.winList.add(i);
				} else if (diceScore == 9) {
					won = 20 * ((float) 180 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 11;
					i.winning_percentage = 180;
					StaticData.winList.add(i);
				} else if (diceScore == 10) {
					int randomNum = random.nextInt(300) + 1;
					float bonus = 20 * ((float) randomNum / 100);
					won = 20 * ((float) 200 / 100) + bonus;
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 12;
					i.winning_percentage = 200;
					i.bonus_percentage = randomNum;
					StaticData.winList.add(i);
				}

				// Update new Balance
				StaticData.balance = StaticData.balance + won;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				if (StaticData.balance >= 50) {
					// automatically scroll button 5 to middle of the screen
					scrollView1.smoothScrollTo(screenWidth, 0);
				} else if (StaticData.balance >= 20) {
					scrollView1
							.smoothScrollTo((int) ((screenWidth / 3) * 2), 0);
				} else if (StaticData.balance < 20 && StaticData.balance >= 10) {
					scrollView1.smoothScrollTo((int) (screenWidth / 3), 0);
				} else if (StaticData.balance < 10) {
					scrollView1.smoothScrollTo(0, 0);
				}

				// re enable buttons
				one.setEnabled(true);
				two.setEnabled(true);
				five.setEnabled(true);
				ten.setEnabled(true);
				twenty.setEnabled(true);
				fifty.setEnabled(true);

				dice1.setEnabled(true);
				dice2.setEnabled(true);

			}

			/**
			 * If bet 50 Clicked
			 */
			if (cameFrom.equalsIgnoreCase("bet50")) {

				// reduce betting amount from the balance.
				TextView balance = (TextView) findViewById(R.id.textView_play_balance);
				StaticData.balance = StaticData.balance - 50;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				// Calculate the dice Score
				diceScore = randomNumbersList1.get(19)
						+ randomNumbersList2.get(19);

				WinItem i = new WinItem();
				i.bet = 50;
				if (diceScore == 0) {
					won = 50 * ((float) 10 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 2;
					i.winning_percentage = 10;
					StaticData.winList.add(i);
				} else if (diceScore == 1) {
					won = 50 * ((float) 20 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 3;
					i.winning_percentage = 20;
					StaticData.winList.add(i);
				} else if (diceScore == 2) {
					won = 50 * ((float) 40 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 4;
					i.winning_percentage = 40;
					StaticData.winList.add(i);
				} else if (diceScore == 3) {
					won = 50 * ((float) 60 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 5;
					i.winning_percentage = 60;
					StaticData.winList.add(i);
				} else if (diceScore == 4) {
					won = 50 * ((float) 80 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 6;
					i.winning_percentage = 80;
					StaticData.winList.add(i);
				} else if (diceScore == 5) {
					won = 50 * ((float) 101 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 7;
					i.winning_percentage = 101;
					StaticData.winList.add(i);
				} else if (diceScore == 6) {
					won = 50 * ((float) 120 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 8;
					i.winning_percentage = 120;
					StaticData.winList.add(i);
				} else if (diceScore == 7) {
					won = 50 * ((float) 140 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 9;
					i.winning_percentage = 140;
					StaticData.winList.add(i);
				} else if (diceScore == 8) {
					won = 50 * ((float) 160 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 10;
					i.winning_percentage = 160;
					StaticData.winList.add(i);
				} else if (diceScore == 9) {
					won = 50 * ((float) 180 / 100);
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 11;
					i.winning_percentage = 180;
					StaticData.winList.add(i);
				} else if (diceScore == 10) {
					int randomNum = random.nextInt(300) + 1;
					float bonus = 50 * ((float) randomNum / 100);
					won = 50 * ((float) 200 / 100) + bonus;
					roundWon = String.format("%.2f", won);
					i.winning = roundWon;
					i.dice_score = 12;
					i.winning_percentage = 200;
					i.bonus_percentage = randomNum;
					StaticData.winList.add(i);
				}

				// Update new Balance
				StaticData.balance = StaticData.balance + won;
				roundBalance = String.format("%.2f", StaticData.balance);
				balance.setText(roundBalance);

				if (StaticData.balance >= 50) {
					// do nothing
				} else if (StaticData.balance < 50 && StaticData.balance >= 20) {
					scrollView1
							.smoothScrollTo((int) ((screenWidth / 3) * 2), 0);
				} else if (StaticData.balance < 20 && StaticData.balance >= 10) {
					scrollView1.smoothScrollTo((int) (screenWidth / 3), 0);
				} else if (StaticData.balance < 10) {
					scrollView1.smoothScrollTo(0, 0);
				}

				// re enable buttons
				one.setEnabled(true);
				two.setEnabled(true);
				five.setEnabled(true);
				ten.setEnabled(true);
				twenty.setEnabled(true);
				fifty.setEnabled(true);

				dice1.setEnabled(true);
				dice2.setEnabled(true);

			}

			Integer value = diceScore + 2;
			diceScoreDisplay.setText(value.toString());
			diceScoreDisplay.setVisibility(View.VISIBLE);

			// Is the sound loaded already?
			if (loaded) {
				// then play cash register sound.
				if (diceScore == 0) {
					soundPool.play(scoreTwo, volume, volume, 1, 0, 1f);
				} else if (diceScore == 1 || diceScore == 2 || diceScore == 3
						|| diceScore == 4 || diceScore == 5) {
					soundPool.play(scoreThree4, volume, volume, 1, 0, 1f);
				} else if (diceScore == 6 || diceScore == 7 || diceScore == 8
						|| diceScore == 9) {
					soundPool.play(cash, volume, volume, 1, 0, 1f);
				} else {
					soundPool.play(highPitchCheer, volume, volume, 1, 0, 1f);
				}

			}

			// Load/update wining list again
			final ListView listView = (ListView) findViewById(R.id.list);

			listView.setAdapter(new ArraysItemAdapter(getApplicationContext(),
					R.layout.rowlayout, Lists.reverse(StaticData.winList)));

			// on Click listener for wining lsit item
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					Object o = listView.getItemAtPosition(position);
					WinItem wi = (WinItem) o;

					Intent i = new Intent(PlayActivity.this,
							WiningDetailsActivity.class);
					i.putExtra("winItem", wi);
					startActivity(i);
				}
			});

		}

	}

}
