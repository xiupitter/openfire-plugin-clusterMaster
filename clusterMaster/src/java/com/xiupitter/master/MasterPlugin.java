package com.xiupitter.master;

import java.io.File;

import org.jivesoftware.openfire.cluster.ClusterManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;

import com.xiupitter.master.server.MasterServer;

public class MasterPlugin implements Plugin {
	MasterServer server = new MasterServer();
	Thread serverRunner;
	ClusterNodeListener listener;
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		// TODO Auto-generated method stub
		System.out.println("Starting MasterPlugin Plugin");
		if(ClusterManager.isClusteringEnabled()){
			System.out.println("Starting MasterServer");
			listener = new ClusterNodeListener();
			serverRunner = new Thread(server);
			serverRunner.start();
			ClusterManager.addListener(listener);
		}
	}

	@Override
	public void destroyPlugin() {
		// TODO Auto-generated method stub
		ClusterManager.removeListener(listener);
	}

}
