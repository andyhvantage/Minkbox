package com.minkbox.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.minkbox.R;
import com.minkbox.network.TaskCompleteListener;


/**
 * Abstract class for handling the common component within our application i.e
 * all screens and activity. This would ensure session management and control
 * our Activity Life Cycle Flow as per application requirements.
 * 
 * @author arpit.garg
 */
public abstract class BaseActivity extends Activity implements
		TaskCompleteListener {
	/**
	 * method to define the listeners to of activity view in this method.
	 * 
	 * @return void
	 */
	protected abstract void addListeners();

	/**
	 * method to initialize the view of activity in this method.
	 * 
	 * @return void
	 */
	protected abstract void bindComponents();

	/**
	 * ProgressDialog for showing on activity.
	 */
	protected static ProgressDialog mProgressDialog;

	/**
	 * Abstract method for activity for assigning the activity for initialize
	 * the DialogBox ,ProgressDialog,Toast etc.
	 * 
	 * @return Activity
	 */
	protected abstract Activity getActivity();

	/**
	 * show the Toast for short duration with the specified message
	 * 
	 * @param - message for the toast
	 * @return - void
	 */
	public void showToast(final String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * show native dialog box with title and message.
	 * 
	 * @param Context
	 *            - provide activity context for showing the dialog
	 * @return void
	 */
	public static void showProgressDialog(final Context context) {
		showProgressDialog(context, context.getString(R.string.loading),
				context.getString(R.string.please_wait));
	}

	/**
	 * show progress dialog with the specified title , message.
	 * 
	 * @param Context
	 *            - ActivityContext for showing the dialog
	 * @return void
	 */
	public static void showProgressDialog(final Context context,
			final String title, final String message) {
		if (mProgressDialog == null
				|| (mProgressDialog != null && (!mProgressDialog.isShowing()))) {

			try {
				mProgressDialog = ProgressDialog.show(context, title, message,
						true, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtil.e(AppConstants.TAG,
						"showProgressDialog " + e.getMessage());
			}
		}
	}

	/**
	 * show progress dialog with the specified title , message.
	 * 
	 * @param Context
	 *            - ActivityContext for showing the dialog
	 * @param title
	 *            - Title text for the dialog
	 * @param message
	 *            - message text for the dialog
	 * @param isCancelable
	 *            - true - if it can be canceled by user interrupt else false
	 * @return void
	 */
	public static void showProgressDialog(final Context context,
			final String title, final String message, final boolean isCancelable) {
		if (mProgressDialog == null
				|| (mProgressDialog != null && (!mProgressDialog.isShowing()))) {
			try {
				mProgressDialog = ProgressDialog.show(context, title, message,
						true, isCancelable);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtil.e(AppConstants.TAG,
						"showProgressDialog " + e.getMessage());
			}
		}
	}

	/**
	 * dismiss the progress dialog if showing
	 * 
	 * @return void
	 */
	public static void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtil.e(AppConstants.TAG,
						"dismissProgressDialog " + e.getMessage());
			}

		}
	}



	/**
	 * show Toast for network timeout with the specified message
	 * 
	 */
	public void showTimeOutToast() {
		showToast(getResources().getString(R.string.error_timeout_message));
	}

	/**
	 * show Toast for network unavailable with the specified message
	 */
	public void showNetworkUnavailableToast(String msg) {
		showToast(/*getResources().getString(R.string.error_unable_to_connect)*/msg);
	}

}
