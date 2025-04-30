package com.adhissoncedeno.sistemafacturacion.infrastructure.web.filter;

import com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant.TenantContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String tenantId = req.getHeader("X-TenantID");

        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContextHolder.setTenantId(tenantId);
        } else {
            TenantContextHolder.setTenantId("public");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
}