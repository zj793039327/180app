package com.example.neo.app.act1;

import com.example.neo.app.R;
import com.example.neo.app.act1.My2048View.GameChangeListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 2048 ”Œœ∑act
 * 
 * @author zj
 * @since 2014-8-7 1.0
 */
public class Act2048 extends Activity {
	private TextView scoreText;
	private TextView maxScoreText;
	private My2048View my2048View;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act1_2048);
		scoreText = (TextView) findViewById(R.id.score);
		maxScoreText = (TextView) findViewById(R.id.maxScore);
		my2048View = (My2048View) findViewById(R.id.my2048View);
		my2048View.setOnGameChangeListener(new GameChangeListener() {

			@Override
			public void onChangedScore(int score) {
				scoreText.setText(score + "");
			}

			@Override
			public void onChangedGameOver(int score, int maxScore) {
				scoreText.setText(score + "");
				maxScoreText.setText(maxScore + "");
			}
		});
	}
}
