package cc.bitky.clustermanage.tcp.server.netty.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.bitky.clustermanage.server.bean.ServerTcpMessageHandler;
import cc.bitky.clustermanage.server.message.IMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Service
@ChannelHandler.Sharable
public class ParsedMessageInBoundHandler extends SimpleChannelInboundHandler<IMessage> {
    private final ServerTcpMessageHandler serverTcpMessageHandler;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    public ParsedMessageInBoundHandler(ServerTcpMessageHandler serverTcpMessageHandler) {
        super();
        this.serverTcpMessageHandler = serverTcpMessageHandler;
    }

    ServerTcpMessageHandler getServerTcpMessageHandler() {
        return serverTcpMessageHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) {
        //将常规回复帧信息传入「常规回复信息处理方法」
        if (msg.getMsgId() > 0x40 && msg.getMsgId() <= 0x4f) {
            serverTcpMessageHandler.handleTcpResponseMsg(msg);
            return;
        }
        //将初始化帧信息传入「初始化信息处理方法」
        if (msg.getMsgId() >= 0xA0 && msg.getMsgId() <= 0xAf) {
            serverTcpMessageHandler.handleTcpInitMsg(msg);
            return;
        }
        //将其余功能帧信息传入「功能信息处理方法」
        serverTcpMessageHandler.handleTcpMsg(msg);

    }
}

