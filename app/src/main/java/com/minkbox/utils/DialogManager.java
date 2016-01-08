package com.minkbox.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public class DialogManager {
	
	  ProgressHUD mProgressHUD;
	
	@SuppressWarnings("deprecation")
	public void showAlertDialog(final Context context, String title, String message,
			 Boolean status) {
		final Boolean localStatus = status;
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
//		if(status != null)
//			alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(localStatus)
				{
				}
			}
		});
		try
		{
			alertDialog.show();
		}catch(Exception e)
		{
			
		}
	}
	
	  public void showProcessDialog(Context context, String title) {
		    try {
		      /*
		       * pDialog = new ProgressDialog(context); pDialog.setMessage(title);
		       * pDialog.setIndeterminate(false); pDialog.setCancelable(false); pDialog.show();
		       */

		      mProgressHUD = ProgressHUD.show(context, title, true, true, new OnCancelListener() {

		        @Override
		        public void onCancel(DialogInterface dialog) {
		          mProgressHUD.dismiss();

		        }
		      });
		    } catch (Exception e) {
		    }

		  }

		  public void stopProcessDialog() {
		    /*
		     * if (pDialog != null && pDialog.isShowing()) pDialog.dismiss();
		     */

		    if (mProgressHUD != null && mProgressHUD.isShowing())mProgressHUD.dismiss();

		  }
}
