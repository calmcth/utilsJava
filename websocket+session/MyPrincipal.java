package com.wlwx.wlyun.gateway.constant;

import java.security.Principal;

public class MyPrincipal implements Principal {

    private String sessionId;

    private String userId;

    public MyPrincipal(String sessionId,String userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

}
