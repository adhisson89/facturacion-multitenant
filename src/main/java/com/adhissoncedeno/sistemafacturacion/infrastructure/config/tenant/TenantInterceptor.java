package com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant;

import com.adhissoncedeno.sistemafacturacion.domain.model.Tenant;
import com.adhissoncedeno.sistemafacturacion.domain.port.out.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantRepository tenantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantSchema = request.getHeader("X-Tenant-Schema");
        log.info("Request for URI: {} with tenant schema: {}", request.getRequestURI(), tenantSchema);

        if (tenantSchema != null && !tenantSchema.isEmpty()) {
            Optional<Tenant> tenant = tenantRepository.findBySchema(tenantSchema);
            if (tenant.isPresent() && tenant.get().active()) {
                TenantContextHolder.setTenantId(tenantSchema);
                log.info("Tenant context set to: {}", tenantSchema);
                return true;
            } else {
                log.warn("Invalid tenant schema requested: {}", tenantSchema);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }
        }

        if (request.getRequestURI().startsWith("/api/tenants")) {
            log.info("Accessing tenant API without tenant context");
            return true;
        }

        log.warn("Missing tenant header for URI: {}", request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex) {
        TenantContextHolder.clear();
    }
}