package com.xiupitter.master;

import org.jboss.netty.channel.Channel;
import org.jivesoftware.openfire.cluster.ClusterEventListener;
import org.jivesoftware.openfire.cluster.ClusterNodeInfo;
import org.jivesoftware.openfire.cluster.NodeID;
import org.jivesoftware.util.cache.CacheFactory;

import com.xiupitter.master.handler.CMEventHandler;

public class ClusterNodeListener implements ClusterEventListener {

	public ClusterNodeListener() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void joinedCluster() {
		// TODO Auto-generated method stub

	}

	@Override
	public void joinedCluster(byte[] nodeID) {
		// TODO Auto-generated method stub
		ClusterNodeInfo info = CacheFactory.getClusterNodeInfo(nodeID);
		String node = ClusterEventEnum.Add.toString()+":"+info.getHostName()+":"+"5262";
		CMEventHandler.nodes.put(info.getNodeID(), info.getHostName());
		for(Channel c:CMEventHandler.channels){
			c.write(node);
		}
	}

	@Override
	public void leftCluster() {
		// TODO Auto-generated method stub

	}

	@Override
	public void leftCluster(byte[] nodeID) {
		// TODO Auto-generated method stub
		String hostname =CMEventHandler.nodes.get(NodeID.getInstance(nodeID));
		String node = ClusterEventEnum.Remove.toString()+":"+hostname+":"+"5262";
		CMEventHandler.nodes.remove(NodeID.getInstance(nodeID));
		for(Channel c:CMEventHandler.channels){
			c.write(node);
		}
	}

	@Override
	public void markedAsSeniorClusterMember() {
		// TODO Auto-generated method stub

	}

}
