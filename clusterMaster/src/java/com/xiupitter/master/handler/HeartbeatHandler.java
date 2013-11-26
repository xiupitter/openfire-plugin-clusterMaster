package com.xiupitter.master.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatHandler extends IdleStateAwareChannelHandler{
    private static Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

	 int i = 0;
	 @Override
	 public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
	   throws Exception {
		  // TODO Auto-generated method stub
		 super.channelIdle(ctx, e);
		 System.out.println("connectionmanager "+e.getChannel().getRemoteAddress()+"is left cluster");
		 e.getChannel().close();
		 logger.error("no heartbeat from connection manager,close the connection.remoteAddree:"+e.getChannel().getRemoteAddress());
	 }
}