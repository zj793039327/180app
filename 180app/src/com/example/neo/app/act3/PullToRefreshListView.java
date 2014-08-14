package com.example.neo.app.act3;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
	private boolean isRecorded;
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
	
	public PullToRefreshListView(Context context, AttributeSet attrs)   
    {  
        super(context, attrs);  
        init(context);  
    }  

	private void init(Context context) {

		setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
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

	private void measureView(LinearLayout headView2) {
		// TODO Auto-generated method stub
		
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
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(ev);
	}
	
	public void setonRefreshListener(OnRefreshListener refreshListener)  
    {  
        this.refreshListener = refreshListener;  
        isRefreshable = true;  
    }  
	
	public interface OnRefreshListener   
    {  
        public void onRefresh();  
    }

	public void onRefreshComplete() {
		// TODO Auto-generated method stub
		
	}  
}
