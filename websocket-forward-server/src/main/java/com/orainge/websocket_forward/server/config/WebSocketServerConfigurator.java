package com.orainge.websocket_forward.server.config;

import com.orainge.websocket_forward.ApplicationContextUtils;
import com.orainge.websocket_forward.server.util.WebsocketClientUtil;
import com.orainge.websocket_forward.consts.AuthHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

/**
 * 服务端 Websocket 配置
 *
 * @author orainge
 */
@Slf4j
public class WebSocketServerConfigurator extends ServerEndpointConfig.Configurator {
    private WebsocketClientUtil websocketServerUtil;

    /**
     * 鉴权认证
     */
    @Override
    public boolean checkOrigin(String originHeaderValue) {
        if (websocketServerUtil == null) {
            this.websocketServerUtil = ApplicationContextUtils.getBeanByClass(WebsocketClientUtil.class);
        }

        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();

            String clientId = request.getHeader(AuthHeader.ID_HEADER_NAME);
            String clientCode = request.getHeader(AuthHeader.KEY_HEADER_NAME);

            // 返回检查结果
            return websocketServerUtil.verifyClient(clientId, clientCode);
        } else {
            return false;
        }
    }

    /**
     * 由于websocket的协议与Http协议是不同的<br>
     * 所以造成了无法直接拿到session<br>
     * 重写 modifyHandshake 可以获取 httpSession<br>
     * 也可以在这里获取请求ID
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//        /*如果没有监听器,那么这里获取到的HttpSession是null*/
//        StandardSessionFacade ssf = (StandardSessionFacade) request.getHttpSession();
//        if (ssf != null) {
//            sec.getUserProperties().put("HttpSession", request.getHttpSession());
//        }

        // 获取请求的客户端 ID
        Map<String, List<String>> httpHeaders = request.getHeaders();
        String idHeaderName = AuthHeader.ID_HEADER_NAME;
        List<String> httpHeader = httpHeaders.get(idHeaderName);
        if (httpHeader != null && !httpHeader.isEmpty()) {
            sec.getUserProperties().put(idHeaderName, httpHeader.get(0));
        }

        super.modifyHandshake(sec, request, response);
    }
}