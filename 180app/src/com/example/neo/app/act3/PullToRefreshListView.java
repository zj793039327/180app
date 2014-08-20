package com.example.neo.app.act3;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neo.app.R;

/**
 * PullToRefreshListView
 * 
 * 下拉刷新的listview实现
 * 
 * @author 任玉刚
 * @since 2014-8-11 1.0
 *        http://blog.csdn.net/singwhatiwanna/article/details/9223363
 */
public class PullToRefreshListView extends ListView implements OnScrollListener {

	private static final String TAG = "PullToRefreshListView";

	private final static int RELEASE_TO_REFRESH = 0;
	private final static int PULL_TO_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// 实际的padding的局里与界面上偏移距离的比例
	private final static int RATIO = 3;
	private LayoutInflater inflater;

	// ListView的头部 用于显示刷新的箭头等

	private LinearLayout headView;
	private TextView tipsTextView;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	// 箭头旋转的动画
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;
	private int headContentHeight;
	private int headContentWidth;
	private int startY;
	private int firstItemIndex;
	private int state;
	private boolean isBack;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	public PullToRefreshListView(Context context) {
		super(context);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {

		setCacheColorHint(context.getResources().getColor(
				android.R.color.transparent));
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.header, null);

		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);

		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);

		tipsTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		Log.v("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);

		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
	}

	private void measureView(LinearLayout child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		firstItemIndex = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "在down的时候记录位置");
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// do nothing
					}
					if (state == PULL_TO_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
						Log.v(TAG, "由下拉刷新状态, 到done状态");
					}
					if (state == RELEASE_TO_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();

						Log.v(TAG, "由松开刷新状态, 到 done状态");
					}
				}
				isRecored = false;
				isBack = false;
				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move的时候记录位置");
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					// 保证在设置padding的过程中, 当前we位置一直在head, 否则当列表超出屏幕时候, 再向上推,
					// 列表回同时进行滚动
					// 可以松手去刷新了
					if (state == RELEASE_TO_REFRESH) {
						setSelection(0);
						// 往上推, 推到了屏幕足够掩盖head的程度, 但是还没有全部掩盖
						if ((tempY - startY) / RATIO < headContentHeight
								&& (tempY - startY) > 0) {
							state = PULL_TO_REFRESH;
							changeHeaderViewByState();
							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						} else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.v(TAG, "由松开刷新状态转变到done状态");

						}
						// 往下拉了, 或者还没有上推到屏幕顶部遮盖head的地步
						else {
							// 不用进行特别的操作, 更新paddingTop的值就可以了
						}
					}
					// 还没有到达显示松开刷新的时候, DONE或者是PULL_TO_REFRESH的状态
					if (state == PULL_TO_REFRESH) {
						setSelection(0);
						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_TO_REFRESH;
							isBack = true;
							changeHeaderViewByState();
							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}
					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_TO_REFRESH;
							changeHeaderViewByState();
						}
					}
					// 更新headView的size
					if (state == PULL_TO_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
					}
					// 更新headView的paddingTop
					if (state == RELEASE_TO_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
					break;
				}
			}
		}
		return super.onTouchEvent(event);
	}

	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_TO_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextView.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);
			tipsTextView.setText("放开以刷新");
			Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_TO_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextView.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				tipsTextView.setText("下拉刷新");
			} else {
				tipsTextView.setText("下拉刷新");
			}
			Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextView.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.arrow);
			tipsTextView.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.v(TAG, "当前状态，done");
			isRefreshable = true;
			break;
		}

	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		 state = DONE;  
	        lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());  
	        changeHeaderViewByState();  

	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}
}
