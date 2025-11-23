package demos.springdata.fitmanage.client;

import demos.springdata.fitmanage.service.impl.StripeServiceImpl;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.web.exchanges.Include;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

import static org.springframework.boot.actuate.web.exchanges.Include.AUTHORIZATION_HEADER;


@Component
public class FeignClientInterceptor implements RequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientInterceptor.class);
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();

            // 1. ИЗПОЛЗВАЙ СТАНДАРТНА КОНСТАНТА ИЛИ "Authorization"
            // HttpHeaders.AUTHORIZATION е равно на стринга "Authorization"
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            // ДЕБЪГ ЛОГ: Виж какво реално има в заявката
            if (authorizationHeader == null) {
                LOGGER.warn("Authorization header is NULL in Interceptor!");

                // Опционално: Принтирай всички хедъри, за да видиш какво идва
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String key = headerNames.nextElement();
                    LOGGER.info("Header found: {} = {}", key, request.getHeader(key));
                }
            } else {
                LOGGER.info("✅ Forwarding Authorization token: {}", authorizationHeader.substring(0, 15) + "...");

                // 2. ЗАКАЧИ ГО КЪМ FEIGN ЗАЯВКАТА
                template.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
            }
        }
    }
}
