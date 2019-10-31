/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package cordova.plugins.screenorientation;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

public class CDVOrientation extends CordovaPlugin {
    
    private static final String TAG = "YoikScreenOrientation";
    private CallbackContext context;
    /**
     * Screen Orientation Constants
     */
    
    private static final String ANY = "any";
    private static final String PORTRAIT_PRIMARY = "portrait-primary";
    private static final String PORTRAIT_SECONDARY = "portrait-secondary";
    private static final String LANDSCAPE_PRIMARY = "landscape-primary";
    private static final String LANDSCAPE_SECONDARY = "landscape-secondary";
    private static final String PORTRAIT = "portrait";
    private static final String LANDSCAPE = "landscape";
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        
        Log.d(TAG, "execute action: " + action);
        this.context = callbackContext;
        // Route the Action
        if (action.equals("screenOrientation")) {
            return routeScreenOrientation(args, callbackContext);
        }
        
        // Action not found
        callbackContext.error("action not recognised");
        return false;
    }

    private boolean routeScreenOrientation(JSONArray args, CallbackContext callbackContext) {

        
        String action = args.optString(0);
        
        String orientation = args.optString(1);
        
        Log.d(TAG, "Requested ScreenOrientation: " + orientation);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.webView.getView().getDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Activity activity = cordova.getActivity();

        
        if (orientation.equals(ANY)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else if (orientation.equals(LANDSCAPE_PRIMARY)) {
            // activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
            this.webView.getView().setRotation(0f);
            this.resizeView(width, height);
        } else if (orientation.equals(PORTRAIT_PRIMARY)) {
            this.webView.getView().setRotation(90f);
            this.resizeView(height, width);
        } else if (orientation.equals(LANDSCAPE)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else if (orientation.equals(PORTRAIT)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (orientation.equals(LANDSCAPE_SECONDARY)) {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            this.webView.getView().setRotation(180f);
            this.resizeView(width, height);
        } else if (orientation.equals(PORTRAIT_SECONDARY)) {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            this.webView.getView().setRotation(270f);
            this.resizeView(height, width);
        }
        
        callbackContext.success();
        return true;
    }

    private void resizeView(int width, int height){
        class ViewResizeTask implements Runnable {
            CordovaWebView webView;

            ViewResizeTask(CordovaWebView paramView, int width, int height) {
                webView = paramView;
            }

            public void run() {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height, Gravity.CENTER);
                this.webView.getView().setLayoutParams(params);
            }
        }

        Activity activity = cordova.getActivity();
        activity.runOnUiThread(new ViewResizeTask(this.webView, width, height));
    }
}
