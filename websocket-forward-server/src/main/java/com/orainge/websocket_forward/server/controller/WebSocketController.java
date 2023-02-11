package com.orainge.websocket_forward.server.controller;

import com.orainge.websocket_forward.server.service.WebSocketService;
import com.orainge.websocket_forward.vo.Result;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
public class WebSocketController {
    @Resource
    private WebSocketService websocketService;

    @RequestMapping("/send/{clientId}")
    public Result send(@PathVariable String clientId,
                       @RequestParam(name = "text") String text,
                       @RequestParam(name = "requireReply", required = false) String requireReply,
                       HttpServletResponse response) {
        try {
            return websocketService.send(clientId, text, Boolean.parseBoolean(requireReply));
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setStatus(HttpStatus.SC_BAD_GATEWAY);
        return null;
    }
}
