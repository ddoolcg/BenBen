package com.benben.ui.activity;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.benben.R;
import com.benben.app.MyApp;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 *@projectName: BB 
 *@fileName: BaseActivity.java
 *@author:Pengkun 
 *@createDate:2014-8-17 ����4:21:06
 *@Description:������ͨactivity�ĸ���
 */
public abstract class BaseActivity extends Activity implements OnClickListener{
	protected   String TAG = BaseActivity.this.getClass().getSimpleName();
	
	public ImageLoader mLoader;
	public DisplayImageOptions options;
	private TextView tvBack,tvTitle,tvRight;
	private Button btnRight;
	/** ��¼����ǰ̨��Activity */
	private static BaseActivity mForegroundActivity = null;
	/** ��¼���л��Activity */
	private static final List<BaseActivity> mActivities = new LinkedList<BaseActivity>();
	
	
	
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if(viewId==R.id.tvBack){
			finish();
		}else{
			click(v.getId());
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		MyApp.getInstance().addInstance(this);
		initViews();
		tvBack = (TextView) findViewById(R.id.tvBack);
		tvTitle= (TextView) findViewById(R.id.tvTitle);
		tvRight= (TextView) findViewById(R.id.tvRight);
		btnRight = (Button) findViewById(R.id.btnRight);
		tvBack.setOnClickListener(this);
		tvRight.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		setData();
		setListener();
	}
	
	/***
	 * ���ñ������Ҳఴť
	 * @param resId btnRight
	 */
	public void setBtnRight(int resId){
		btnRight.setVisibility(View.VISIBLE);
		if(resId!=-1)
			btnRight.setBackgroundResource(resId);
	}
	/***
	 * �������Ƿ���ʾ����
	 * @param show
	 */
	public void showBack(boolean show){
		if(!show)
			tvBack.setVisibility(View.GONE);
	}
	/***
	 * ���ñ������Ҳ����ְ�ť������߼�
	 * @param str
	 */
	public void setTextRight(String str){
		tvRight.setVisibility(View.VISIBLE);
		tvRight.setText(str);
	}
	/***
	 * ���ñ������Ҳ����ְ�ť������߼�
	 * @param textId
	 */
	public void setTextRight(int textId){
		tvRight.setVisibility(View.VISIBLE);
		tvRight.setText(textId);
	}
	/***
	 * ���ñ���
	 * @param title
	 */
	public void setTitle(String title){
		tvTitle.setText(title);
	}
	/***
	 * ���ñ���
	 * @param textId
	 */
	public void setTitle(int textId){
		tvTitle.setText(textId);
	}
	/***
	 * ��ʼ�����ֺͿؼ�
	 */
	public abstract void initViews();
	/***
	 * �����������
	 */
	public abstract void setData();
	/***
	 * ���ø��ֵ��������...����
	 */
	public abstract void setListener();
	/***
	 * �������¼�
	 * @param viewId
	 */
	public abstract void click(int viewId);
	/***
	 * ����Extra��תactivity
	 * @param clazz
	 * @param needFinish �Ƿ�finish��ǰactivity
	 */
	public void jump(Class<?> clazz,boolean needFinish){
		Intent intent = new Intent(this,clazz);
		startActivity(intent);
		if(needFinish){
			finish();
		}
	}
	/***
	 * ��Extra��תactivity
	 * @param clazz
	 * @param needFinish �Ƿ�finish��ǰactivity
	 */
	public void jump(Intent intent,boolean needFinish){
		startActivity(intent);
		if(needFinish){
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			finish();
		}
		return true;
	}
	/***
	 * ��ͬ��activity�������Ҫ��ʼ����ͬ��Ĭ��ͼƬ����ʼ��ImgeLoader��Ĭ����ʾͼƬ
	 * @param resId
	 */
	public  void initOptions(int resId){
		options = new DisplayImageOptions.Builder() 
		.showStubImage(resId) 
		.showImageForEmptyUri(resId) 
		.showImageOnFail(resId)
		.cacheInMemory() 
		.cacheOnDisc() 
		.build();
		mLoader =  ImageLoader.getInstance();
	}
	/** �Ƿ���������Activity */
	public static boolean hasActivity() {
		return mActivities.size() > 0;
	}

	/** ��ȡ��ǰ����ǰ̨��activity */
	public static BaseActivity getForegroundActivity() {
		return mForegroundActivity;
	}

	/** ��ȡ��ǰ����ջ����activity���������Ƿ���ǰ̨ */
	public static BaseActivity getCurrentActivity() {
		List<BaseActivity> copy;
		synchronized (mActivities) {
			copy = new ArrayList<BaseActivity>(mActivities);
		}
		if (copy.size() > 0) {
			return copy.get(copy.size() - 1);
		}
		return null;
	}
	
//	public void loginSucceed(User teacher){
//		DataWrapper.getInstance().setTeahcer(teacher);
//		DataWrapper.getInstance().setLoginSucceed(true);
//		PrefUtils.putString("userName", teacher.getPhone());
//		PrefUtils.putString("password", teacher.getPassword());
//		PrefUtils.putString(Constants.PREF_KEY_TEACHER,teacher.getTeacherInfo());
//	}
//	
//	public void logOut(Class targetClazz,boolean clearPwd,boolean showDialog,boolean needFinish){
//		DataWrapper.getInstance().setTeahcer(null);
//		DataWrapper.getInstance().setLoginSucceed(false);
//		PrefUtils.remove("password");
//		PrefUtils.remove(Constants.PREF_KEY_TEACHER); 
//		showToast( "�����µ�¼");
//		Intent intent = new Intent(this,targetClazz);
//		intent.putExtra("clearPwd", clearPwd);
//		intent.putExtra("showDialog", showDialog);
//		jump(intent, needFinish);
//	}
	/***
	 * ֻ��һ��ȷ�ϰ�ť����ʾdialog
	 * @param title ��ʾ����
	 * @param msg ��ʾ����
	 * @param btnText ��ť��ʾ������
	 * @param needFinish �ر�dialog��ʱ���Ƿ�رյ�ǰactivity
	 */
	public void showConfirmDialog(String title,String msg,String btnText,final boolean needFinish){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setMessage(msg).setPositiveButton(btnText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(needFinish){
					BaseActivity.this.finish();
				}
			}
		}).show();
	}
	/***
	 * ��������ť����ʾdialog
	 * @param title ��ʾ����
	 * @param msg ��ʾ����
	 * @param btnText postiveButton����ʾ������
	 * @param listener postiveButton�������
	 */
	public void showChooseDialog(String title,String msg,String btnText,DialogInterface.OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setMessage(msg).setPositiveButton(btnText, listener)
		.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
	
}
