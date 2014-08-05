package com.example.neo.app;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * My2048View
 * 
 * @author zj
 * @since 2014-7-25 1.0
 */
public class My2048View extends View {

	private enum State {
		FAILL, // 失败
		ANIMATION, // 合并动画
		RUNING // 运行
	}

	private enum Directory {
		LEFT, RIGHT, BOTTOM, TOP, NONE
	}

	private static final int TOTAL_ROW = 4;// 行
	private static final int TOTAL_COL = 4;
	private static final int SPACE = 15;
	private static final int ANGLE_SPEED = 45;

	private int mViewWidth;
	private int mViewHeight;
	private float cellSpace;

	private Paint paint;
	private Paint textPaint;
	private RectF rectf;
	private Random random;
	private int touchSlop;
	private Directory currentDirectory = Directory.NONE;// 当前方向
	private Directory oldDirectory;
	private boolean isLocked;

	private int count = 0;
	private int score = 0;
	private boolean isMoved = false;
	private int angler = 0;

	private SharedPreferences sharedPreference;
	private GameChangeListener gameChangeListener;

	private State currentState = State.RUNING;
	private BitmapDrawable bitmapDrawable;

	private int[] colors = { Color.rgb(204, 192, 178), // 1
			Color.rgb(253, 235, 213), // 2
			Color.rgb(252, 224, 174), // 4
			Color.rgb(255, 95, 95), // 8
			Color.rgb(255, 68, 68), // 16
			Color.rgb(248, 58, 58), // 32
			Color.rgb(240, 49, 49), // 64
			Color.rgb(233, 39, 39), // 128
			Color.rgb(226, 29, 29), // 256
			Color.rgb(219, 19, 19), // 562
			Color.rgb(211, 10, 10), // 1024
			Color.rgb(204, 0, 0) // 2048
	};

	private int[][] datas = new int[TOTAL_ROW][TOTAL_COL];
	private int[][] animationData = new int[TOTAL_ROW][TOTAL_COL];
	private RefreshHandler refreshHandler = new RefreshHandler();

	public interface GameChangeListener {
		public void onChangedGameOver(int score, int maxScore);

		public void onChangeScore(int score);
	}

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			My2048View.this.update();
			My2048View.this.invalidate();
		}

		private void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	}

	public My2048View(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		textPaint = new Paint();

		rectf = new RectF();
		random = new Random();
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		sharedPreference = context.getSharedPreferences("my2048",
				context.MODE_PRIVATE);
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		count = 0;
		score = 0;
		isMoved = false;
		for (int i = 0; i < TOTAL_ROW; i++) {
			for (int j = 0; j < TOTAL_COL; j++) {
				datas[i][j] = 0;
			}
		}
		randomOneOrTwo();
	}

	private void clearAnimationData() {
		for (int i = 0; i < TOTAL_ROW; i++) {
			for (int j = 0; j < TOTAL_COL; j++) {
				animationData[i][j] = 0;
			}
		}
	}

	public void setOnGameChangeListener(GameChangeListener gameChangeListener) {
		this.gameChangeListener = gameChangeListener;
		gameChangeListener.onChangedGameOver(score,
				sharedPreference.getInt("maxScore", 0));
		gameChangeListener.onChangeScore(score);
	}

	/**
	 * 随机的产生1或者2
	 */
	private void randomOneOrTwo() {
		if (count >= TOTAL_COL * TOTAL_ROW) {
			int maxScore = sharedPreference.getInt("maxScore", 0);
			if (score > maxScore) {
				Editor edit = sharedPreference.edit();
				edit.putInt("maxScore", score);
				edit.commit();
			}
			gameChangeListener.onChangedGameOver(score, maxScore);
			currentState = State.FAILL;
			return;
		}

		int row = random.nextInt(TOTAL_ROW);
		int col = random.nextInt(TOTAL_COL);

		// 判断所在位置是否已经存在数据
		if (datas[row][col] != 0) {
			randomOneOrTwo();
		} else {
			datas[row][col] = random.nextInt(2) + 1;
			count++;
		}
	}

	private void update() {
		if (currentState == State.ANIMATION) {
			angler = angler + ANGLE_SPEED;
			if (angler > 180) {
				angler = 0;
				currentState = State.RUNING;
				clearAnimationData();
			} else {
				refreshHandler.sleep(100);
			}

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

	}

	private float pointX;
	private float pointY;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		String showNum;

		if (currentState == State.RUNING || currentState == State.ANIMATION) {
			for (int i = 0; i < TOTAL_ROW; i++) {
				for (int j = 0; j < TOTAL_COL; j++) {
					pointX = SPACE * (j + 1) + j + cellSpace;
					pointY = SPACE * (i + 1) + i * cellSpace;
					rectf.set(pointX, pointY, cellSpace, pointY + cellSpace);
					paint.setColor(colors[datas[i][j]]);
					// 绘制背景
					if (currentState == State.ANIMATION && datas[i][j] != 0
							&& animationData[i][j] != 0) {
						canvas.save();
						canvas.rotate(angler, pointX + cellSpace / 2, pointY
								+ cellSpace / 2);
						canvas.drawRect(rectf, paint);

					} else {
						canvas.drawRect(rectf, paint);
					}

					if (datas[i][j] != 0) {
						if (datas[i][j] == 1 || datas[i][j] == 2) {
							textPaint.setColor(Color.rgb(0, 0, 0));
						} else {
							textPaint.setColor(Color.rgb(255, 255, 255));
						}
						showNum = (int) Math.pow(2, datas[i][j]) + "";
						canvas.drawText(
								showNum,
								pointX
										+ (cellSpace - textPaint
												.measureText(showNum)) / 2,
								pointY
										+ (cellSpace - textPaint.measureText(
												showNum, 0, 1)) / 2, textPaint);

					}
				}
			}
		}
		if (currentState == State.FAILL) {
			rectf.set(0, mViewHeight - cellSpace, mViewHeight, mViewHeight);
			paint.setColor(colors[5]);
			canvas.drawRect(rectf, paint);
			textPaint.setColor(Color.rgb(255, 255, 255));
			canvas.drawText("游戏结束", 0, 0, paint);
			canvas.drawText("重新开始", 100, 0, paint);

		}
	}
}
