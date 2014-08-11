package com.example.neo.app.act3;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * PullToRefreshListView
 * 
 * 下拉刷新的listview实现
 * 
 * @author 任玉刚
 * @since 2014-8-11 1.0
 *        http://blog.csdn.net/singwhatiwanna/article/details/9223363
 */
public class PullToRefreshListView extends ListView {

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

	
	//箭头旋转的动画
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	
	//用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecorded;
	private int headContentHeight;
	private int headContentWidth;
	private int startY;
	private int firstItemIndex;
	private int state;
	private boolean isBack;
	
	
	private OnRefreshListener refreshListener;
	
	
	
	
	public PullToRefreshListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

}
