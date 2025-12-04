package demos.springdata.fitmanage.client;

import demos.springdata.fitmanage.exception.DamilSoftException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class FeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {

            if (response.status() == 400) {
                return new DamilSoftException("Stripe Connect Account is not configured.", HttpStatus.BAD_REQUEST);
            }

            return new Exception("Generic error");
        }
    }
}
