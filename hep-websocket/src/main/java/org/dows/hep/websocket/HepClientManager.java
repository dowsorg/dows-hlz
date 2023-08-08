package org.dows.hep.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.framework.websocket.util.NettyUtil;
import org.dows.hep.websocket.config.WsProperties;
import org.dows.hep.websocket.proto.MessageProto;
import org.dows.hep.websocket.schedule.MsgScheduler;
import org.dows.sequence.api.IdGenerator;
import org.dows.sequence.snowflake.SnowflakeIdGenerator;
import org.dows.sequence.snowflake.config.SnowFlakeConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class HepClientManager {
    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    // 在线用户总数
    private static AtomicInteger accountCount = new AtomicInteger(0);

    private static ConcurrentMap<String, ConcurrentMap<Channel, AccountInfo>> ONLINE_ACCOUNT = new ConcurrentHashMap<>();

    // 设备集合  key 房间号 value：设备集合
    private static ConcurrentMap<String, List<String>> equipments = new ConcurrentHashMap<>();

    private static IdGenerator idGenerator = new SnowflakeIdGenerator(new SnowFlakeConfiguration());
    private static ConcurrentMap<String, String> MSGIDS = new ConcurrentHashMap<>();

    public static final AttributeKey<String> EXPERIMENT_IN_SESSION_ATTRIBUTE = AttributeKey.newInstance("experimentId");

    /**
     * 保存用户身份信息
     *
     * @param channel
     * @param onlineAccount
     * @return
     */
    public static AccountInfo saveUser(Channel channel, OnlineAccount onlineAccount) {
        String addr = NettyUtil.parseChannelRemoteAddr(channel);
        // 判断通道状态是否正常
        if (!channel.isActive()) {
            log.error("channel is not active, accountInfo:{}", addr, JSONUtil.toJsonStr(onlineAccount));
            return null;
        }
        // 添加当前用户身份信息到通道数据
        channel.attr(EXPERIMENT_IN_SESSION_ATTRIBUTE).set(onlineAccount.getExperimentId());
        //channel.attr(USER_NAME_IN_SESSION_ATTRIBUTE_ATTR).set(nick);
        // 增加一个用户数
        accountCount.incrementAndGet();
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountName(onlineAccount.getAccountId());
        // 保存用户到指定房间数据
        if (ONLINE_ACCOUNT.get(onlineAccount.getExperimentId()) == null) {
            ConcurrentMap<Channel, AccountInfo> userInfoConcurrentMap = new ConcurrentHashMap<>();
            ONLINE_ACCOUNT.put(onlineAccount.getExperimentId(), userInfoConcurrentMap);
        }
        ONLINE_ACCOUNT.get(onlineAccount.getExperimentId()).put(channel, accountInfo);
        // 返回数据
        return accountInfo;
    }

    /**
     * 通过用户身份获取指纹
     *
     * @param session
     * @return
     */
    public static String getRoomIdFromSession(Channel session) {
        Object attr = null;
        if (session != null) {
            attr = session.attr(EXPERIMENT_IN_SESSION_ATTRIBUTE).get();
            if (attr != null) {
                return (String) attr;
            }
        }
        return null;
    }

    /**
     * 通过用户身份获取指纹
     *
     * @param session
     * @return
     */
    public static String getUserNameFromSession(Channel session) {
        Object attr = null;
        if (session != null) {
            // 重通道（Channel）中获取自定义的属性USER_ID_IN_SESSION_ATTRIBUTE_ATTR的属性值
            //attr = session.attr(USER_NAME_IN_SESSION_ATTRIBUTE_ATTR).get();
            if (attr != null) {
                return (String) attr;
            }
        }
        return null;
    }

    /**
     * 从缓存中移除Channel，并且关闭Channel
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        try {
            log.warn("channel will be remove, address is :{}", NettyUtil.parseChannelRemoteAddr(channel));
            rwLock.writeLock().lock();
            channel.close();
            // 获取通道房间数据
            String room = getRoomIdFromSession(channel);
            AccountInfo userInfo = ONLINE_ACCOUNT.get(room).get(channel);
            if (userInfo != null) {
                AccountInfo tmp = ONLINE_ACCOUNT.get(room).remove(channel);
                if (tmp != null && tmp.getAuth()) {
                    // 减去一个认证用户
                    accountCount.decrementAndGet();
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    /**
     * 广播普通消息
     *
     * @param message
     */
    public static void broadcastMsg(int uid, String nick, String room, String message) {
        if (!StrUtil.isBlank(message)) {
            try {
                rwLock.readLock().lock();
                Set<Channel> keySet = ONLINE_ACCOUNT.get(room).keySet();
                for (Channel ch : keySet) {
                    AccountInfo accountInfo = ONLINE_ACCOUNT.get(room).get(ch);
                    if (accountInfo == null || !accountInfo.getAuth()) {
                        continue;
                    }
                    ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildMessProto(uid, nick, message)));
                }
            } finally {
                rwLock.readLock().unlock();
            }
        }
    }

    /**
     * 广播事件消息
     *
     * @param uid
     * @param nick
     * @param room
     * @param message
     */
    public static void broadcastEventMsg(int uid, String nick, String room, String message) {
        if (!StrUtil.isBlank(message)) {
            try {
                rwLock.readLock().lock();
                Set<Channel> keySet = ONLINE_ACCOUNT.get(room).keySet();
                for (Channel ch : keySet) {
                    AccountInfo accountInfo = ONLINE_ACCOUNT.get(room).get(ch);
                    if (accountInfo == null || !accountInfo.getAuth()) {
                        continue;
                    }
                    ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildMessProto(uid, nick, message)));
                }
            } finally {
                rwLock.readLock().unlock();
            }
        }
    }

    /**
     * 广播系统消息
     */
    public static void broadcastSysMsg(int code, Object mess) {
        try {
            rwLock.readLock().lock();
            // 获取所有的通道发送数据
            Collection<ConcurrentMap<Channel, AccountInfo>> collection = ONLINE_ACCOUNT.values();
            for (ConcurrentMap<Channel, AccountInfo> userInfos : collection) {
                Set<Channel> keySet = userInfos.keySet();
                for (Channel ch : keySet) {
                    AccountInfo accountInfo = userInfos.get(ch);
                    if (accountInfo == null || !accountInfo.getAuth()) {
                        continue;
                    }

                    ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildSystProto(idGenerator.nextIdStr(), code, mess)));
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }


    /**
     * 广播系统消息
     */
    public static void broadcastSysMsg(int code, String room, Object mess) {
        try {
            rwLock.readLock().lock();
            // 获取所有的通道发送数据
            Set<Channel> keySet = ONLINE_ACCOUNT.get(room).keySet();
            for (Channel ch : keySet) {
                AccountInfo accountInfo = ONLINE_ACCOUNT.get(room).get(ch);
                if (accountInfo == null || !accountInfo.getAuth()) {
                    continue;
                }
                ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildSystProto(idGenerator.nextIdStr(), code, mess)));
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * 广播PING消息查询失效通道
     */
    public static void broadCastPing() {
        try {
            rwLock.readLock().lock();
            log.info("broadCastPing userCount: {}", accountCount.intValue());
            // 获取所有的通道发送数据
            Collection<ConcurrentMap<Channel, AccountInfo>> collection = ONLINE_ACCOUNT.values();
            for (ConcurrentMap<Channel, AccountInfo> accountInfos : collection) {
                Set<Channel> keySet = accountInfos.keySet();
                for (Channel ch : keySet) {
                    AccountInfo accountInfo = accountInfos.get(ch);
                    if (accountInfo == null || !accountInfo.getAuth()) {
                        continue;
                    }
                    ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildPingProto()));
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }


    /**
     * 发送系统消息
     *
     * @param code
     * @param mess
     */
    public static String sendInfo(Channel channel, int code, Object mess) {
        String msgid = idGenerator.nextIdStr();
        String sc = MessageProto.buildSystProto(msgid, code, mess);
        channel.writeAndFlush(new TextWebSocketFrame(sc));
        return msgid;
    }

    /**
     * 发送系统消息
     *
     * @param code
     * @param mess
     */
    public static String sendInfoRetry(Channel channel, int code, Object mess, String cron) {
        if (StrUtil.isBlank(cron)) {
            WsProperties bean = MsgScheduler.getApplicationContext().getBean(WsProperties.class);
            if (null != bean) {
                cron = bean.getProducer().getRetry().getCron();
            } else {
                cron = "0/3 * * * * ?";
            }
        }
        String msgid = idGenerator.nextIdStr();
        MsgScheduler.schedule(() -> {
            if(Thread.currentThread().isInterrupted()) {
                return;
            }

            String sc = MSGIDS.get(msgid);
            if (null == sc) {
                sc = MessageProto.buildSystProto(msgid, code, mess);
                MSGIDS.put(msgid, sc);
            }
            channel.writeAndFlush(new TextWebSocketFrame(sc));
        }, cron, msgid,0L);
        return msgid;
    }

    public static void removeMsgById(String msgId) {
        MSGIDS.remove(msgId);
    }

    /**
     * 发送心跳数据
     *
     * @param channel 需要发送的通道
     */
    public static void sendPong(Channel channel) {
        channel.writeAndFlush(new TextWebSocketFrame(MessageProto.buildPongProto()));
    }

    /**
     * 扫描并关闭失效的Channel
     */
    public static void scanNotActiveChannel() {
        // 所有通道
        Collection<ConcurrentMap<Channel, AccountInfo>> collection = ONLINE_ACCOUNT.values();
        for (ConcurrentMap<Channel, AccountInfo> accountInfos : collection) {
            Set<Channel> keySet = accountInfos.keySet();
            for (Channel ch : keySet) {
                // 创建者
                AccountInfo accountInfo = accountInfos.get(ch);
                if (accountInfo == null) {
                    continue;
                }
                // 判断通道状态
                if (!ch.isOpen() || !ch.isActive() || (!accountInfo.getAuth() &&
                        // 过期时间（10秒）
                        (System.currentTimeMillis() - accountInfo.getTime()) > 10000)) {
                    // 移除通道
                    removeChannel(ch);
                }
            }
        }
    }

    /**
     * 通过通道获取创建者信息
     *
     * @param channel 通道
     * @return
     */
    public static AccountInfo getAccountInfo(Channel channel) {
        String room = getRoomIdFromSession(channel);
        return ONLINE_ACCOUNT.get(room).get(channel);
    }

    /**
     * 获取存储的所有用户及通讯数据
     *
     * @return
     */
    public static ConcurrentMap<Channel, AccountInfo> getUserInfos() {
        ConcurrentMap<Channel, AccountInfo> userInfos = new ConcurrentHashMap<>();
        Collection<ConcurrentMap<Channel, AccountInfo>> collection = ONLINE_ACCOUNT.values();
        for (ConcurrentMap<Channel, AccountInfo> userInfoConcurrentMap : collection) {
            userInfos.putAll(userInfoConcurrentMap);
        }
        return userInfos;
    }

    /**
     * 按实验id获取实验下的在线用户
     *
     * @param experimentId
     * @return
     */
    public static ConcurrentMap<Channel, AccountInfo> getUserInfosByExperimentId(String experimentId) {
        return ONLINE_ACCOUNT.getOrDefault(experimentId, new ConcurrentHashMap<>());
    }

    /**
     * 获取在线人数信息
     *
     * @return
     */
    public static int getAuthUserCount() {
        return accountCount.get();
    }

    /**
     * 获取指定房间在线人数信息
     *
     * @return
     */
    public static int getAuthUserCount(String room) {
        return ONLINE_ACCOUNT.get(room).size();
    }

    /**
     * 更新用户过期信息
     *
     * @param channel 通道
     */
    public static void updateUserTime(Channel channel) {
        AccountInfo accountInfo = getAccountInfo(channel);
        if (accountInfo != null) {
            //accountInfo.setTime(System.currentTimeMillis());
        }
    }
}
