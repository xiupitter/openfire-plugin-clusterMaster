package com.xiupitter.master.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.util.CharsetUtil;
import org.jivesoftware.openfire.cluster.ClusterNodeInfo;
import org.jivesoftware.openfire.cluster.NodeID;
import org.jivesoftware.util.cache.CacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiupitter.master.ClusterEventEnum;

public class CMEventHandler extends SimpleChannelHandler {
	public static List<Channel> channels = new CopyOnWriteArrayList<Channel>();
	public static Map<NodeID,String> nodes = new ConcurrentHashMap<NodeID,String>();

	private static final Logger log =  LoggerFactory.getLogger(CMEventHandler.class);
	
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
    	System.out.println("heartbeat did");
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel channel = e.getChannel();
        channels.add(channel);
    	StringBuilder builder = new StringBuilder();
        for(ClusterNodeInfo info :CacheFactory.getClusterNodesInfo()){
        	nodes.put(info.getNodeID(), info.getHostName());
        	if(!info.getNodeID().equals(CacheFactory.getClusterMemberID())){
	    		builder.append(ClusterEventEnum.Add.toString()+":"+info.getHostName()+":"+"5262") ;
	    		builder.append(",");
        	}
        }
        if(builder.length()>0){
	        String nodes = builder.substring(0, builder.length()-1);
	        if(!nodes.trim().isEmpty()){
	    		channel.write(nodes);
	        }
        }
        log.info("CLIENT - CONNECTED: " + channel.getRemoteAddress());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel channel = e.getChannel();
        channels.remove(channel);
        log.info("CLIENT - DISCONNECTED: " + channel.getRemoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Channel channel = e.getChannel();
        channel.close();
    	log.warn("CLIENT - EXCEPTION: " + e.getCause().getMessage());
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) {
    	String message = (String) e.getMessage();
        ChannelBuffer buffer = ChannelBuffers.directBuffer(message.getBytes(CharsetUtil.UTF_8).length);
        if (!message.isEmpty()) {
            buffer.writeBytes(message.getBytes());
            Channels.write(ctx, e.getFuture(), buffer);
        }
    }

}
