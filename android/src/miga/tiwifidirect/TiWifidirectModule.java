package miga.tiwifidirect;

import org.appcelerator.kroll.common.Log;
import java.util.Arrays;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.KrollDict;
import java.util.HashMap;
import org.appcelerator.titanium.TiBlob;
import android.renderscript.Allocation;
import org.appcelerator.titanium.TiApplication;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.content.BroadcastReceiver;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.content.IntentFilter;
import org.appcelerator.kroll.KrollFunction;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pConfig;
import org.appcelerator.kroll.common.Log;
import java.util.ArrayList;

@Kroll.module(name="Tiwifidirect", id="miga.tiwifidirect")
public class TiWifidirectModule extends KrollModule {

	TiApplication appContext;
	Activity activity;
	Context context;
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	private ArrayList<WifiP2pDevice> peers_list;//list of peers

	public TiWifidirectModule() {
		super();
		appContext = TiApplication.getInstance();
		activity = appContext.getCurrentActivity();
		context=activity.getApplicationContext();
		peers_list = new ArrayList<WifiP2pDevice>();
	}



	@Kroll.method
	public void create(HashMap args){
		KrollDict arg = new KrollDict(args);

		mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
    	mChannel = mManager.initialize(activity, context.getMainLooper(), null);
    	mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

		mIntentFilter = new IntentFilter();
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



	}

	@Kroll.method
	public void resume() {
	    context.registerReceiver(mReceiver, mIntentFilter);
	}

	@Kroll.method
	public void pause() {
		context.unregisterReceiver(mReceiver);
	}



	@Kroll.method
	public void discover(HashMap args) {
		KrollDict arg = new KrollDict(args);
		final KrollFunction success =(KrollFunction) arg.get("success");

		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
		    @Override
		    public void onSuccess() {
				HashMap<String, KrollDict[]> event = new HashMap<String, KrollDict[]>();

				if (success!=null){
					success.call(getKrollObject(), event);
				}
		    }

		    @Override
		    public void onFailure(int reasonCode) {
		        //...
		    }
		});
	}

	@Kroll.method
	public void connect(HashMap args) {
		KrollDict arg = new KrollDict(args);
		KrollFunction success =(KrollFunction) arg.get("success");
		KrollFunction error =(KrollFunction) arg.get("error");

		if(this.peers_list.size() == 0) {
			Log.i("wifi","no devices available");
			if (error!=null){
				HashMap<String, String> event = new HashMap<String, String>();
				error.call(getKrollObject(), event);
			}
			return;
		}

		for(WifiP2pDevice device : this.peers_list) {
			WifiP2pConfig config = new WifiP2pConfig();
			config.deviceAddress = device.deviceAddress;
			final String s = config.deviceAddress;

			Log.i("wifi","Device: " + s);

			if (success!=null){
				HashMap<String,String> event = new HashMap<String, String>();
				event.put("device",s);
				success.call(getKrollObject(), event);
			}

			mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
				@Override
				public void onSuccess() {
					Log.i("wifi","connected");
				}

				@Override
				public void onFailure(int reason) {
					//TODO
				}
			});
		}

	}

	ArrayList<WifiP2pDevice> getPeersList() {
		return this.peers_list;
	}

	void setPeersList(ArrayList<WifiP2pDevice> a) {
		this.peers_list = a;
	}

}
