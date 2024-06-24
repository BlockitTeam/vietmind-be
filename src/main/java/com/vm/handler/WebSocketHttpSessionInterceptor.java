package com.vm.handler;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class WebSocketHttpSessionInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest servletRequest = servletRequestAttributes.getRequest();
            if (servletRequest != null) {
                HttpSession session = servletRequest.getSession(false);
                if (session != null) {
                    // Xử lí logic khi có session
                    // Ví dụ:
                    String username = (String) session.getAttribute("username");
                    attributes.put("username", username); // Lưu thông tin vào attributes để sử dụng trong WebSocketHandler
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Thực hiện các thao tác dọn dẹp nếu cần thiết sau khi kết nối WebSocket được thiết lập
    }
}
