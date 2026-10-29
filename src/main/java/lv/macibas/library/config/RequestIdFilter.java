package lv.macibas.library.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * ⚛️ React/FE: kā Axios interceptor vai Next.js middleware — izpildās pirms KATRA pieprasījuma.
 *
 * Servlet FILTER — izpildās PIRMS Spring DispatcherServlet (pirms kontroliera).
 * Pievieno katram pieprasījumam unikālu ID -> var izsekot vienu pieprasījumu cauri visiem logiem
 * (un mikroservisos — cauri vairākiem servisiem: "correlation id").
 *
 * Pieprasījuma ceļš Spring MVC:
 *   HTTP -> Tomcat -> Filter(i) -> DispatcherServlet -> HandlerInterceptor(i) -> @Controller
 *        -> Service -> Repository -> DB
 *
 * Filter vs Interceptor:
 *  - Filter: Servlet līmenis, redz VISUS pieprasījumus (arī statiskos failus). Spring Security = filtru ķēde.
 *  - Interceptor: Spring MVC līmenis, zina, kurš kontrolieris/metode tiks izsaukts.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = request.getHeader(HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put("requestId", requestId); // MDC — logošanas konteksts konkrētajam pavedienam
        response.setHeader(HEADER, requestId);
        try {
            chain.doFilter(request, response); // nodod tālāk ķēdē
        } finally {
            MDC.remove("requestId"); // OBLIGĀTI notīrīt — pavedieni tiek atkārtoti izmantoti!
        }
    }
}
