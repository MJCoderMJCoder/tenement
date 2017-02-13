package com.lzf.tenement.util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class RotateMatrix {

	/**
	 * ��ȡͼƬ����ת�ĽǶ�
	 *
	 * @param path
	 *            ͼƬ����·��
	 * @return ͼƬ����ת�Ƕ�
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// ��ָ��·���¶�ȡͼƬ������ȡ��EXIF��Ϣ
			ExifInterface exifInterface = new ExifInterface(path);
			// ��ȡͼƬ����ת��Ϣ
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * ��ͼƬ����ĳ���ǶȽ�����ת
	 *
	 * @param bm
	 *            ��Ҫ��ת��ͼƬ
	 * @param degree
	 *            ��ת�Ƕ�
	 * @return ��ת���ͼƬ
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// ������ת�Ƕȣ�������ת����
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// ��ԭʼͼƬ������ת���������ת�����õ��µ�ͼƬ
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

}
