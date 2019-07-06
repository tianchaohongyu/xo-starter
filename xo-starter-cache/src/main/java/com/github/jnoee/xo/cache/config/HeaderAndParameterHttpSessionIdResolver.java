package com.github.jnoee.xo.cache.config;

import com.github.jnoee.xo.utils.StringUtils;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * 协议头以及参数 session id 分析器
 */
public class HeaderAndParameterHttpSessionIdResolver extends HeaderHttpSessionIdResolver {

    private String paramName;

    public HeaderAndParameterHttpSessionIdResolver(String paramName) {
        super(paramName);
        this.paramName = paramName;
    }

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        String sessionId = request.getParameter(paramName);
        if (!StringUtils.isBlank(sessionId)) {
            return Collections.singletonList(sessionId);
        } else {
            return super.resolveSessionIds(request);
        }
    }
}
