package com.wlwx.wlyun.gateway.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@EnableScheduling
public class SocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

/*    @Scheduled(fixedRate = 1000)
    @SendTo("/topic/dispatchstatus")
    public void dispatchstatus(SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dispatchStartTime", "2");
        params.put("dispatchStopTime", new Date());

        messagingTemplate.convertAndSend("/topic/dispatchstatus", params);
    }*/

    // @Scheduled(fixedRate = 1000)
    @SendTo("/topic/dispatchstatus")
    public Object dispatchstatus() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dispatchStartTime", "2");
        params.put("dispatchStopTime", new Date());

        messagingTemplate.convertAndSend("/topic/dispatchstatus", params);
        return "dispatchstatus";
    }

    @MessageMapping("/test2")
    public void test(String str, Principal principal){
        messagingTemplate.convertAndSendToUser(principal.getName(),"/queue/msg","haha2");
    }
}
