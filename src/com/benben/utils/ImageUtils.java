package com.benben.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

/**
 *@projectName: BB 
 *@fileName: ImageUtils.java
 *@author:Pengkun 
 *@createDate:2014-8-17 ����6:09:27
 *@Description:
 */
public class ImageUtils {
	/**
	 * ����һ��ͼƬ
	 * @param contentColor �ڲ������ɫ
	 * @param strokeColor  �����ɫ
	 * @param radius       Բ��
	 */
	public static GradientDrawable createDrawable(int contentColor, int strokeColor, int radius) {
		GradientDrawable drawable = new GradientDrawable(); // ����Shape
		drawable.setGradientType(GradientDrawable.RECTANGLE); // ���þ���
		drawable.setColor(contentColor);// �����������ɫ
		drawable.setStroke(1, strokeColor); // �������,��ߺ��Ľ�����ΪԲ�ǣ�������ֺ�ɫ��Ӱ������������ǿ��Ի����ģ���Ҫ�Ѹ�View����setScrollCache(false)
		drawable.setCornerRadius(radius); // �����ĽǶ�ΪԲ��
		return drawable;
	}

	/**
	 * ����һ��ͼƬѡ����
	 * @param normalState  ��ͨ״̬��ͼƬ
	 * @param pressedState ��ѹ״̬��ͼƬ
	 */
	public static StateListDrawable createSelector(Drawable normalState, Drawable pressedState) {
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressedState);
		bg.addState(new int[]{android.R.attr.state_enabled}, normalState);
		bg.addState(new int[]{}, normalState);
		return bg;
	}

	/** ��ȡͼƬ�Ĵ�С */
	@SuppressLint("NewApi")
	public static int getDrawableSize(Drawable drawable) {
		if (drawable == null) {
			return 0;
		}
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		} else {
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
	}

}
