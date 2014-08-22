package com.benben.utils;

import android.text.TextUtils;
import android.util.Log;
/**
 *@projectName: BB 
 *@fileName: LogUtil.java
 *@author:Pengkun 
 *@createDate:2014-8-17 ����4:21:06
 *@Description:��־��ӡ������
 */
public class LogUtil {
	public static boolean isDebug = true;

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.v(tag, TextUtils.isEmpty(msg)?"��־Ϊ��":TextUtils.isEmpty(msg)?"��־Ϊ��":msg);
	}
	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, TextUtils.isEmpty(msg)?"��־Ϊ��":msg);
	}

	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, TextUtils.isEmpty(msg)?"��־Ϊ��":msg);
	}

	public static void w(String tag, String msg) {
		if (isDebug)
			Log.w(tag, TextUtils.isEmpty(msg)?"��־Ϊ��":msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.e(tag, TextUtils.isEmpty(msg)?"��־Ϊ��":msg);
	}
	public static void println(String tag,String msg){
		if (isDebug)
			System.out.println(tag+"____"+msg);
	}
}
