package com.xiupitter.master.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.util.HashedWheelTimer;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiupitter.master.handler.CMEventHandler;
import com.xiupitter.master.handler.HeartbeatHandler;

public class MasterServer implements Runnable{
	private static final Logger log =  LoggerFactory.getLogger(MasterServer.class);

	public void startServer(){
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("child.tcpNoDelay", true);
        config.put("child.keepAlive", true);
        ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool());
        ServerBootstrap serverBootstrap = new ServerBootstrap(factory);
        serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new StringEncoder(CharsetUtil.UTF_8),new StringDecoder(CharsetUtil.UTF_8), new IdleStateHandler(new HashedWheelTimer(),60,0,0), new HeartbeatHandler(),new CMEventHandler());
            }
        });
        String interfaceName = JiveGlobals.getXMLProperty("network.interface");
        InetAddress bindInterface = null;
        if (interfaceName != null) {
            if (interfaceName.trim().length() > 0) {
                try {
					bindInterface = InetAddress.getByName(interfaceName);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        serverBootstrap.bind(new InetSocketAddress(bindInterface, 6666));
        log.info("cluster master startup");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		startServer();
	}
	
}
