/*
 * Copyright (c) 2010 Sony Ericsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sonyericsson.extras.liveview.plugins.mybus;

import com.sonyericsson.extras.liveview.plugins.AbstractPluginService;
import com.sonyericsson.extras.liveview.plugins.PluginConstants;
import com.sonyericsson.extras.liveview.plugins.PluginUtils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyBusPluginService extends AbstractPluginService {
    private Intent i;
    private String titleArr[];
    private String linkArr[];
    private int arrayIndex = 0;
    private int arraySize;
    private final int TEXT_SIZE = 12;
    
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		BroadcastReceiver br = new BroadcastReceiver() {
		      @Override
		      public void onReceive(Context context, Intent intent) {
		        titleArr = intent.getExtras().getStringArray("titleArr");
		        linkArr = intent.getExtras().getStringArray("linkArr");
		        
		        arraySize = titleArr.length;
		        
		        String result = intent.getStringExtra("result");
		        int answer = intent.getIntExtra("command", 0);
		        
		        mLiveViewAdapter.clearDisplay(mPluginId);
		        switch (answer) {
		        	case 0:
			        	PluginUtils.sendTextBitmapCanvas(mLiveViewAdapter, mPluginId, titleArr[arrayIndex], 128, TEXT_SIZE, 64);
			        	break;
		        	case 1:
			        	PluginUtils.sendTextBitmapCanvas(mLiveViewAdapter, mPluginId, result, 128, 10, 5);
			        	break;
		        		
		        }
		      }
		    };
		    // create intent filter for BroadcastReceiver
		IntentFilter intFilt = new IntentFilter("com.sonyericsson.extras.liveview.plugins.mybus");
		    // register BroadcastReceiver
		registerReceiver(br, intFilt);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();	
		stopWork();
	}
	
    /**
     * Plugin is sandbox.
     */
    protected boolean isSandboxPlugin() {
        return true;
    }
	
	/**
	 * Must be implemented. Starts plugin work, if any.
	 */
	protected void startWork() {
		i = new Intent("com.udav.mybus.MyBusService");
		i.putExtra("command", 0);
    	startService(i);
	}
	
	/**
	 * Must be implemented. Stops plugin work, if any.
	 */
	protected void stopWork() {
	}
	
	/**
	 * Must be implemented.
	 * 
	 * PluginService has done connection and registering to the LiveView Service. 
	 * 
	 * If needed, do additional actions here, e.g. 
	 * starting any worker that is needed.
	 */
	protected void onServiceConnectedExtended(ComponentName className, IBinder service) {
		
	}
	
	/**
	 * Must be implemented.
	 * 
	 * PluginService has done disconnection from LiveView and service has been stopped. 
	 * 
	 * Do any additional actions here.
	 */
	protected void onServiceDisconnectedExtended(ComponentName className) {
		
	}

	/**
	 * Must be implemented.
	 * 
	 * PluginService has checked if plugin has been enabled/disabled.
	 * 
	 * The shared preferences has been changed. Take actions needed. 
	 */	
	protected void onSharedPreferenceChangedExtended(SharedPreferences prefs, String key) {
		
	}

	protected void startPlugin() {
		Log.d(PluginConstants.LOG_TAG, "startPlugin");
		startWork();
	}
			
	protected void stopPlugin() {
		Log.d(PluginConstants.LOG_TAG, "stopPlugin");
		stopWork();
	}
	
	protected void button(String buttonType, boolean doublepress, boolean longpress) {
	    Log.d(PluginConstants.LOG_TAG, "button - type " + buttonType + ", doublepress " + doublepress + ", longpress " + longpress);
		
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_UP)) {
		    if(longpress) {
		        //mLiveViewAdapter.ledControl(mPluginId, 50, 50, 50);
		    } else {
		    }
		} else if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_DOWN)) {
            if(longpress) {
                //mLiveViewAdapter.vibrateControl(mPluginId, 50, 50);
            } else {
            	
            }
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_RIGHT)) {
			if (++arrayIndex >= arraySize) 
				arrayIndex = 0; 
			mLiveViewAdapter.clearDisplay(mPluginId);
			PluginUtils.sendTextBitmapCanvas(mLiveViewAdapter, mPluginId, titleArr[arrayIndex], 128, TEXT_SIZE, 64);
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_LEFT)) {
			if (--arrayIndex < 0) 
				arrayIndex = arraySize - 1;
			mLiveViewAdapter.clearDisplay(mPluginId);
			PluginUtils.sendTextBitmapCanvas(mLiveViewAdapter, mPluginId, titleArr[arrayIndex], 128, TEXT_SIZE, 64);
		} else 
		if(buttonType.equalsIgnoreCase(PluginConstants.BUTTON_SELECT)) {
			i.putExtra("command", 1);
			i.putExtra("link", linkArr[arrayIndex]);
			startService(i);
		}
	}

	protected void displayCaps(int displayWidthPx, int displayHeigthPx) {
        Log.d(PluginConstants.LOG_TAG, "displayCaps - width " + displayWidthPx + ", height " + displayHeigthPx);
    }

	protected void onUnregistered() throws RemoteException {
		Log.d(PluginConstants.LOG_TAG, "onUnregistered");
		stopWork();
	}

	protected void openInPhone(String openInPhoneAction) {
		Log.d(PluginConstants.LOG_TAG, "openInPhone: " + openInPhoneAction);
	}
	
    protected void screenMode(int mode) {
        Log.d(PluginConstants.LOG_TAG, "screenMode: screen is now " + ((mode == 0) ? "OFF" : "ON"));
    }

    
    
}