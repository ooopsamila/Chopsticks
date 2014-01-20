package com.example.chopsticks;

import com.example.models.WinItem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class WiningDetailsActivity extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.winingdetails);
		
		Intent i = getIntent();
		WinItem item = (WinItem)i.getSerializableExtra("winItem");
		
		TextView diceScore = (TextView) findViewById(R.id.txtViewDiceScore);
		TextView won = (TextView) findViewById(R.id.txtViewWon);
		TextView winingPercentage = (TextView) findViewById(R.id.txtViewWonPercentage);
		TextView bonusPersentage = (TextView) findViewById(R.id.txtViewBonusPercentage);
		
		diceScore.setText(item.dice_score.toString());		
		won.setText(item.winning);
		winingPercentage.setText(item.winning_percentage.toString()+"%");
		bonusPersentage.setText(item.bonus_percentage.toString()+"%");
		
		
	}
}
