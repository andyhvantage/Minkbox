package com.minkbox.utils;

import java.io.File;

import android.util.Log;


public class LogUtil {
	
	/**
	 * Writing logs inside file.
	 */
	private static File logFile, appLogFileDir;

	/**
	 * Log error
	 * 
	 * @param tag
	 *            - The tag to use for this logging event
	 * 
	 * @param msg
	 *            - The message to print out for this logging event
	 */
	public static void e(String tag, String msg) {
		if (AppConstants.IS_DEV_BUILD && msg != null) {
			Log.e(tag, msg);
			insertLog(tag, msg);
		}

	}

	public static void d(String tag, String msg, Throwable tr) {
		if (AppConstants.IS_DEV_BUILD && msg != null) {
			Log.d(tag, msg, tr);
			insertLog(tag, msg);
		}

	}

	public static void e(String tag, String msg, Throwable tr) {
		if (AppConstants.IS_DEV_BUILD && msg != null) {
			Log.e(tag, msg, tr);
			insertLog(tag, msg);
		}

	}

	public static void i(String tag, String msg, Throwable tr) {
		if (AppConstants.IS_DEV_BUILD && msg != null) {
			Log.i(tag, msg, tr);
			insertLog(tag, msg);
		}

	}

	/**
	 * Log AppConstants.IS_DEVELOPER_BUILD
	 * 
	 * @param tag
	 *            - The tag to use for this logging event
	 * 
	 * @param msg
	 *            - The message to print out for this logging event
	 */
	public static void d(String tag, String msg) {
		if (AppConstants.IS_DEV_BUILD && msg != null) {
			Log.d(tag, msg);
			insertLog(tag, msg);
		}
	}

	/**
	 * Log verbose
	 * 
	 * @param tag
	 *            - The tag to use for this logging event
	 * 
	 * @param msg
	 *            - The message to print out for this logging event
	 */
	public static void v(String tag, String msg) {
		if (AppConstants.IS_DEV_BUILD && msg != null) {
			Log.v(tag, msg);
			insertLog(tag, msg);
		}
	}

	/**
	 * Log info
	 * 
	 * @param tag
	 *            - The tag to use for this logging event
	 * 
	 * @param msg
	 *            - The message to print out for this logging event
	 */
	public static void i(String tag, String msg) {
		if (AppConstants.IS_DEV_BUILD && msg != null) {
			Log.i(tag, msg);
			insertLog(tag, msg);
		}
	}

	/**
	 * Print the stack trace
	 * 
	 * @param e
	 *            - The exception to use for this stack trace
	 * 
	 */
	public static void pst(Throwable e) {
		if (AppConstants.IS_DEV_BUILD) {
			e.printStackTrace();
			insertLog("Exception", e.getMessage());
		}
	}

	
	public static void insertLog(String key, String value) {
	
	}

	public static void writeLogs(String text) {

	
	}

	private static void createLogFile() {
		if (appLogFileDir == null) {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED))
				appLogFileDir = new File(
						android.os.Environment.getExternalStorageDirectory(),
						"");

			if (appLogFileDir != null && !appLogFileDir.exists())
				appLogFileDir.mkdirs();
			logFile = new File(appLogFileDir, "log.txt");
		}

	}
}
