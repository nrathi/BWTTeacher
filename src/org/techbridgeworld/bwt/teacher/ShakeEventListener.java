/**
 * Note: This code was taken from http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it. 
 */

package org.techbridgeworld.bwt.teacher;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeEventListener implements SensorEventListener {

	private static final int MIN_FORCE = 10;
	private static final int MIN_DIRECTION_CHANGE = 3;
	private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;
	private static final int MAX_TOTAL_DURATION_OF_SHAKE = 400;

	private long mFirstDirectionChangeTime = 0;
	private long mLastDirectionChangeTime;
	private int mDirectionChangeCount = 0;

	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;

	private OnShakeListener mShakeListener;

	public interface OnShakeListener {

		/**
		 * Called when the shake gesture is detected
		 */
		void onShake();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		mShakeListener = listener;
	}

	@Override
	public void onSensorChanged(SensorEvent se) {
		float x = se.values[0];
		float y = se.values[1];
		float z = se.values[2];
		
		float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

		if (totalMovement > MIN_FORCE) {
			long now = System.currentTimeMillis();
			
			if (mFirstDirectionChangeTime == 0) {
				mFirstDirectionChangeTime = now;
				mLastDirectionChangeTime = now;
			}

			long lastChangeWasAgo = now - mLastDirectionChangeTime;
			
			if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {
				mLastDirectionChangeTime = now;
				mDirectionChangeCount++;
				
				lastX = x;
				lastY = y;
				lastZ = z;

				if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {
					long totalDuration = now - mFirstDirectionChangeTime;
					if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
						mShakeListener.onShake();
						resetShakeParameters();
					}
				}
			} 
			else
				resetShakeParameters();
		}
	}

	/**
	 * Reset the shake parameters to their default values
	 */
	private void resetShakeParameters() {
		mFirstDirectionChangeTime = 0;
		mDirectionChangeCount = 0;
		mLastDirectionChangeTime = 0;
		lastX = 0;
		lastY = 0;
		lastZ = 0;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}