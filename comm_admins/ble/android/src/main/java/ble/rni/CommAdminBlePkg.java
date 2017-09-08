package ble.rni;

import android.app.Activity;
import android.content.Context;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommAdminBlePkg implements ReactPackage {

	private Activity mActivity;
	private Context mBaseContext;

	public CommAdminBlePkg(Context baseContext) {
		mBaseContext = baseContext;
	}




	@Override
	public List<NativeModule> createNativeModules( ReactApplicationContext reactContext) {
		List<NativeModule> modules = new ArrayList<>();


		//modules.add(new BleAdmin(reactContext, mBaseContext));
		modules.add(new CommAdminBleRNI(reactContext, mBaseContext));
		return modules;
	}



	@Override
	public List<Class<? extends JavaScriptModule>> createJSModules() {
		return Collections.emptyList();
	}

	@Override
	public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
		return Collections.emptyList();
	}

}
