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
 * ����ˢ�µ�listviewʵ��
 * 
 * @author �����
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

	// ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���
	private final static int RATIO = 3;
	private LayoutInflater inflater;

	// ListView��ͷ�� ������ʾˢ�µļ�ͷ��

	private LinearLayout headView;
	private TextView tipsTextView;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	
	//��ͷ��ת�Ķ���
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	
	//���ڱ�֤startY��ֵ��һ��������touch�¼���ֻ����¼һ��
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
