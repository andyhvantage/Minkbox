/**
 * Interface to handle the callback for the task controller
 */
package com.minkbox.network;


public interface TaskCompleteListener {

	public void taskCompleted(byte taskType, int taskID, Object data);
}
