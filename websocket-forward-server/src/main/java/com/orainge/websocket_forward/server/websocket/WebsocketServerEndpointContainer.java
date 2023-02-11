package com.orainge.websocket_forward.server.websocket;


import com.orainge.websocket_forward.server.config.WebsocketClientConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放 Websocket 服务端对象的容器
 *
 * @author orainge
 */
@Slf4j
public class WebsocketServerEndpointContainer {
    /**
     * 所有 Websocket 连接集合对象
     */
    private static final ConcurrentHashMap<String, WebsocketServerEndpoint> ENDPOINT_CLIENT_MAP = new ConcurrentHashMap<>();

    /**
     * 添加 Websocket 服务端对象
     *
     * @param server Websocket 服务端对象
     */
    public static boolean add(WebsocketServerEndpoint server) {
        WebsocketClientConfig.Config config = server.getClientConfig();
        if (config != null) {
            String id = config.getId();
            boolean isSameIdServer = ENDPOINT_CLIENT_MAP.containsKey(id);
            if (isSameIdServer) {
                // 存在相同 ID 的客户端
                // 处理策略1: 返回 false
                return false;
                // 处理策略2：关闭原来的连接，添加当前的连接
//                try {
//                    ENDPOINT_CLIENT_MAP.get(id).getSession().close();
//                } catch (Exception e) {
//                    log.error("[Websocket 服务端] - 添加新连接时发生错误", e);
//                }
//                ENDPOINT_CLIENT_MAP.remove(id); // 删除连接
//                ENDPOINT_CLIENT_MAP.put(id, server);  // 添加新连接
//                return true;
            } else {
                // 不存在相同ID的客户端，添加
                ENDPOINT_CLIENT_MAP.put(id, server);
                return true;
            }
        }
        return false;
    }

    /**
     * 删除 Websocket 服务端对象
     *
     * @param server 要删除的 Websocket 服务端对象
     */
    public static boolean remove(WebsocketServerEndpoint server) {
        try {
            WebsocketClientConfig.Config config = server.getClientConfig();
            if (config != null) {
                // 检查要移出的 WebSocketServer 是否为已经在使用的 Server
                // 因为当相同的 ID 客户端连接时，也会发起删除请求
                // 如果只通过客户端 ID 进行判断，有可能会误删
                // 因此需要判断 sessionId 来检查是否需要删除

                String id = config.getId();
                WebsocketServerEndpoint sameIdServer = ENDPOINT_CLIENT_MAP.get(id);
                if (sameIdServer != null) {
                    if (sameIdServer.getSession().getId().equals(server.getSession().getId())) {
                        ENDPOINT_CLIENT_MAP.remove(id);
                        return true;
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * 获取 Websocket 服务端对象
     *
     * @param id Websocket 服务端 ID
     * @return Websocket 服务端对象
     */
    public static WebsocketServerEndpoint get(String id) {
        return ENDPOINT_CLIENT_MAP.get(id);
    }

    /**
     * 获取容器大小
     *
     * @return 容器大小
     */
    public static int size() {
        return ENDPOINT_CLIENT_MAP.size();
    }
}
