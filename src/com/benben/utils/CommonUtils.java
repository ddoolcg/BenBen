package com.benben.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.alibaba.fastjson.util.IOUtils;

/**
 *@projectName: BB 
 *@fileName: CommonUtils.java
 *@author:Pengkun 
 *@createDate:2014-8-17 ����6:01:50
 *@Description:
 */
public class CommonUtils {
	public static final String ROOT_DIR = "bb";
	private static final String TAG = "CommonUtils";
	/** �ж�SD���Ƿ���� */
	public static boolean isSDCardAvailable() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return true;
		} else {
			return false;
		}
	}
	/** ��ȡӦ��Ŀ¼����SD������ʱ����ȡSD���ϵ�Ŀ¼����SD��������ʱ����ȡӦ�õ�cacheĿ¼ */
	public static String getDir(String name) {
		StringBuilder sb = new StringBuilder();
		if (isSDCardAvailable()) {
			sb.append(getExternalStoragePath());
		} else {
			sb.append(getCachePath());
		}
		sb.append(name);
		sb.append(File.separator);
		String path = sb.toString();
		if (createDirs(path)) {
			return path;
		} else {
			return null;
		}
	}

	/** ��ȡSD�µ�Ӧ��Ŀ¼ */
	public static String getExternalStoragePath() {
		StringBuilder sb = new StringBuilder();
		sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
		sb.append(File.separator);
		sb.append(ROOT_DIR);
		sb.append(File.separator);
		return sb.toString();
	}

	/** ��ȡӦ�õ�cacheĿ¼ */
	public static String getCachePath() {
		File f = UIUtils.getContext().getCacheDir();
		if (null == f) {
			return null;
		} else {
			return f.getAbsolutePath() + "/";
		}
	}

	/** �����ļ��� */
	public static boolean createDirs(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists() || !file.isDirectory()) {
			return file.mkdirs();
		}
		return true;
	}

	/** �����ļ�������ѡ���Ƿ�ɾ��Դ�ļ� */
	public static boolean copyFile(String srcPath, String destPath, boolean deleteSrc) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		return copyFile(srcFile, destFile, deleteSrc);
	}

	/** �����ļ�������ѡ���Ƿ�ɾ��Դ�ļ� */
	public static boolean copyFile(File srcFile, File destFile, boolean deleteSrc) {
		if (!srcFile.exists() || !srcFile.isFile()) {
			return false;
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = in.read(buffer)) > 0) {
				out.write(buffer, 0, i);
				out.flush();
			}
			if (deleteSrc) {
				srcFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtils.close(out);
			IOUtils.close(in);
		}
		return true;
	}

	/** �ж��ļ��Ƿ��д */
	public static boolean isWriteable(String path) {
		try {
			if (TextUtils.isEmpty(path)) {
				return false;
			}
			File f = new File(path);
			return f.exists() && f.canWrite();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/** �޸��ļ���Ȩ��,����"777"�� */
	public static void chmod(String path, String mode) {
		try {
			String command = "chmod " + mode + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������д���ļ�
	 * @param is       ������
	 * @param path     �ļ�·��
	 * @param recreate ����ļ����ڣ��Ƿ���Ҫɾ���ؽ�
	 * @return �Ƿ�д��ɹ�
	 */
	public static boolean writeFile(InputStream is, String path, boolean recreate) {
		boolean res = false;
		File f = new File(path);
		FileOutputStream fos = null;
		try {
			if (recreate && f.exists()) {
				f.delete();
			}
			if (!f.exists() && null != is) {
				File parentFile = new File(f.getParent());
				parentFile.mkdirs();
				int count = -1;
				byte[] buffer = new byte[1024];
				fos = new FileOutputStream(f);
				while ((count = is.read(buffer)) != -1) {
					fos.write(buffer, 0, count);
				}
				res = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(fos);
			IOUtils.close(is);
		}
		return res;
	}

	/**
	 * ���ַ�������д���ļ�
	 * @param content ��Ҫд����ַ���
	 * @param path    �ļ�·������
	 * @param append  �Ƿ�����ӵ�ģʽд��
	 * @return �Ƿ�д��ɹ�
	 */
	public static boolean writeFile(byte[] content, String path, boolean append) {
		boolean res = false;
		File f = new File(path);
		RandomAccessFile raf = null;
		try {
			if (f.exists()) {
				if (!append) {
					f.delete();
					f.createNewFile();
				}
			} else {
				f.createNewFile();
			}
			if (f.canWrite()) {
				raf = new RandomAccessFile(f, "rw");
				raf.seek(raf.length());
				raf.write(content);
				res = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(raf);
		}
		return res;
	}
	/** ���� */
	public static boolean copy(String src, String des, boolean delete) {
		File file = new File(src);
		if (!file.exists()) {
			return false;
		}
		File desFile = new File(des);
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = new FileOutputStream(desFile);
			byte[] buffer = new byte[1024];
			int count = -1;
			while ((count = in.read(buffer)) != -1) {
				out.write(buffer, 0, count);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtils.close(in);
			IOUtils.close(out);
		}
		if (delete) {
			file.delete();
		}
		return true;
	}
	/** ��ȡandroidϵͳ�汾�� */
	public static String getOSVersion() {
		String release = android.os.Build.VERSION.RELEASE; // androidϵͳ�汾��
		release = "android" + release;
		return release;
	}

	/** ���androidϵͳsdk�汾�� */
	public static String getOSVersionSDK() {
		return android.os.Build.VERSION.SDK;
	}

	/** ���androidϵͳsdk�汾�� */
	public static int getOSVersionSDKINT() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/** ��ȡ�ֻ��ͺ� */
	public static String getDeviceModel() {
		return android.os.Build.MODEL;
	}

	/** ��ȡ�豸��IMEI */
	public static String getIMEI() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return null;
		}
		String imei = null;
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imei;
	}

	/** ����ֻ��Ƿ��Ѳ���SIM�� */
	public static boolean isCheckSimCardAvailable() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return false;
		}
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
	}

	/** sim���Ƿ�ɶ� */
	public static boolean isCanUseSim() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return false;
		}
		try {
			TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/** ȡ�õ�ǰsim�ֻ�����imsi */
	public static String getIMSI() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return null;
		}
		String imsi = null;
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = tm.getSubscriberId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imsi;
	}

	/** ���ر����ֻ����룬������벻һ���ܻ�ȡ�� */
	public static String getNativePhoneNumber() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return null;
		}
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;
	}

	/** �����ֻ����������� */
	public static String getProvidersName() {
		String ProvidersName = null;
		// ����Ψһ���û�ID;�������ſ��ı�������
		String IMSI = getIMSI();
		// IMSI��ǰ��3λ460�ǹ��ң������ź���2λ00 02���й��ƶ���01���й���ͨ��03���й����š�
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "�й��ƶ�";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "�й���ͨ";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "�й�����";
		} else {
			ProvidersName = "����������:" + IMSI;
		}
		return ProvidersName;
	}

	/** ��ȡ��ǰ�豸��SN */
	public static String getSimSN() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return null;
		}
		String simSN = null;
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			simSN = tm.getSimSerialNumber();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return simSN;
	}

	/** ��ȡ��ǰ�豸��MAC��ַ */
	public static String getMacAddress() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return null;
		}
		String mac = null;
		try {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wm.getConnectionInfo();
			mac = info.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mac;
	}

	/** ����豸ip��ַ */
	public static String getLocalAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** ��ȡ��Ļ�ķֱ��� */
	@SuppressWarnings("deprecation")
	public static int[] getResolution() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return null;
		}
		WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int[] res = new int[2];
		res[0] = windowMgr.getDefaultDisplay().getWidth();
		res[1] = windowMgr.getDefaultDisplay().getHeight();
		return res;
	}

	/** ����豸�ĺ���dpi */
	public static float getWidthDpi() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return 0;
		}
		DisplayMetrics dm = null;
		try {
			if (context != null) {
				dm = new DisplayMetrics();
				dm = context.getApplicationContext().getResources().getDisplayMetrics();
			}

			return dm.densityDpi;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/** ����豸������dpi */
	public static float getHeightDpi() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return 0;
		}
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		return dm.ydpi;
	}

	/** ��ȡ�豸��Ϣ */
	public static String[] getDivceInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = {"", ""};
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cpuInfo;
	}

	/** �ж��ֻ�CPU�Ƿ�֧��NEONָ� */
	public static boolean isNEON() {
		boolean isNEON = false;
		String cupinfo = getCPUInfos();
		if (cupinfo != null) {
			cupinfo = cupinfo.toLowerCase();
			isNEON = cupinfo != null && cupinfo.contains("neon");
		}
		return isNEON;
	}

	/** ��ȡCPU��Ϣ�ļ�����ȡCPU��Ϣ */
	@SuppressWarnings("resource")
	private static String getCPUInfos() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		StringBuilder resusl = new StringBuilder();
		String resualStr = null;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			while ((str2 = localBufferedReader.readLine()) != null) {
				resusl.append(str2);
				// String cup = str2;
			}
			if (resusl != null) {
				resualStr = resusl.toString();
				return resualStr;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resualStr;
	}

	/** ��ȡ��ǰ�豸cpu���ͺ� */
	public static int getCPUModel() {
		return matchABI(getSystemProperty("ro.product.cpu.abi")) | matchABI(getSystemProperty("ro.product.cpu.abi2"));
	}

	/** ƥ�䵱ǰ�豸��cpu�ͺ� */
	private static int matchABI(String abiString) {
		if (TextUtils.isEmpty(abiString)) {
			return 0;
		}
		if ("armeabi".equals(abiString)) {
			return 1;
		} else if ("armeabi-v7a".equals(abiString)) {
			return 2;
		} else if ("x86".equals(abiString)) {
			return 4;
		} else if ("mips".equals(abiString)) {
			return 8;
		}
		return 0;
	}

	/** ��ȡCPU������ */
	public static int getCpuCount() {
		return Runtime.getRuntime().availableProcessors();
	}

	/** ��ȡRom�汾 */
	public static String getRomversion() {
		String rom = "";
		try {
			String modversion = getSystemProperty("ro.modversion");
			String displayId = getSystemProperty("ro.build.display.id");
			if (modversion != null && !modversion.equals("")) {
				rom = modversion;
			}
			if (displayId != null && !displayId.equals("")) {
				rom = displayId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rom;
	}

	/** ��ȡϵͳ���ò��� */
	public static String getSystemProperty(String key) {
		String pValue = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method m = c.getMethod("get", String.class);
			pValue = m.invoke(null, key).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pValue;
	}

	/** ��ȡϵͳ�е�Library�� */
	public static List<String> getSystemLibs() {
		Context context = UIUtils.getContext();
		if (null == context) {
			return null;
		}
		PackageManager pm = context.getPackageManager();
		String[] libNames = pm.getSystemSharedLibraryNames();
		List<String> listLibNames = Arrays.asList(libNames);
		LogUtil.e(TAG,"SystemLibs: " + listLibNames);
		return listLibNames;
	}

	/** ��ȡ�ֻ��ⲿ���ÿռ��С����λΪbyte */
	@SuppressWarnings("deprecation")
	public static long getExternalTotalSpace() {
		long totalSpace = -1L;
		if (CommonUtils.isSDCardAvailable()) {
			try {
				String path = Environment.getExternalStorageDirectory().getPath();// ��ȡ�ⲿ�洢Ŀ¼�� SDCard
				StatFs stat = new StatFs(path);
				long blockSize = stat.getBlockSize();
				long totalBlocks = stat.getBlockCount();
				totalSpace = totalBlocks * blockSize;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return totalSpace;
	}

	/** ��ȡ�ⲿ�洢���ÿռ䣬��λΪbyte */
	@SuppressWarnings("deprecation")
	public static long getExternalSpace() {
		long availableSpace = -1L;
		if (CommonUtils.isSDCardAvailable()) {
			try {
				String path = Environment.getExternalStorageDirectory().getPath();
				StatFs stat = new StatFs(path);
				availableSpace = stat.getAvailableBlocks() * (long) stat.getBlockSize();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return availableSpace;
	}

	/** ��ȡ�ֻ��ڲ��ռ��С����λΪbyte */
	@SuppressWarnings("deprecation")
	public static long getTotalInternalSpace() {
		long totalSpace = -1L;
		try {
			String path = Environment.getDataDirectory().getPath();
			StatFs stat = new StatFs(path);
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();// ��ȡ��������õ��ļ�ϵͳ��
			totalSpace = totalBlocks * blockSize;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalSpace;
	}

	/** ��ȡ�ֻ��ڲ����ÿռ��С����λΪbyte */
	@SuppressWarnings("deprecation")
	public static long getAvailableInternalMemorySize() {
		long availableSpace = -1l;
		try {
			String path = Environment.getDataDirectory().getPath();// ��ȡ Android ����Ŀ¼
			StatFs stat = new StatFs(path);// һ��ģ��linux��df�����һ����,���SD�����ֻ��ڴ��ʹ�����
			long blockSize = stat.getBlockSize();// ���� Int ����С�����ֽ�Ϊ��λ��һ���ļ�ϵͳ
			long availableBlocks = stat.getAvailableBlocks();// ���� Int ����ȡ��ǰ���õĴ洢�ռ�
			availableSpace = availableBlocks * blockSize;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return availableSpace;
	}

	/** ��ȡ����Ӧ���������ڴ棬��λΪbyte */
	public static long getOneAppMaxMemory() {
		Context context = UIUtils.getContext();
		if (context == null) {
			return -1;
		}
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return activityManager.getMemoryClass() * 1024 * 1024;
	}

	/** ��ȡָ����Ӧ��ռ�õ��ڴ棬��λΪbyte */
	public static long getUsedMemory() {
		return getUsedMemory(null);
	}

	/** ��ȡָ������Ӧ��ռ�õ��ڴ棬��λΪbyte */
	public static long getUsedMemory(String packageName) {
		Context context = UIUtils.getContext();
		if (context == null) {
			return -1;
		}
		if (TextUtils.isEmpty(packageName)) {
			packageName = context.getPackageName();
		}
		long size = 0;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runapps = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo runapp : runapps) { // ���������еĳ���
			if (packageName.equals(runapp.processName)) {// �õ������������������һ����ǰ���������Щ����Ľ�����������Ӧһ������
				// ����ָ��PID������ڴ���Ϣ�����Դ��ݶ��PID�����ص�Ҳ�������͵���Ϣ
				Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runapp.pid});
				// �õ��ڴ���Ϣ����ʹ�õ��ڴ棬��λ��K
				size = processMemoryInfo[0].getTotalPrivateDirty() * 1024;
			}
		}
		return size;
	}

	/** ��ȡ�ֻ�ʣ���ڴ棬��λΪbyte */
	public static long getAvailableMemory() {
		Context context = UIUtils.getContext();
		if (context == null) {
			return -1;
		}
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);
		return info.availMem;
	}

//	/** ��ȡ�ֻ����ڴ棬��λΪbyte */
//	public static long getTotalMemory() {
//		long size = 0;
//		String path = "/proc/meminfo";// ϵͳ�ڴ���Ϣ�ļ�
//		try {
//			String totalMemory = FileUtils.readProperties(path, "MemTotal", null);// �������Ǵ���λkb�ģ����ҵ�λǰ�пո�����ȥ�������λ
//			if (!TextUtils.isEmpty(totalMemory) && totalMemory.length() > 3) {
//				size = Long.valueOf(totalMemory.substring(0, totalMemory.length() - 3)) * 1024;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return size;
//	}

	/** �ֻ����ڴ����з�ֵ����λΪbyte */
	public static long getThresholdMemory() {
		Context context = UIUtils.getContext();
		if (context == null) {
			return -1;
		}
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);
		return info.threshold;
	}

	/** �ֻ��Ƿ��ڵ��ڴ����� */
	public static boolean isLowMemory() {
		Context context = UIUtils.getContext();
		if (context == null) {
			return false;
		}
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);
		return info.lowMemory;
	}

}
