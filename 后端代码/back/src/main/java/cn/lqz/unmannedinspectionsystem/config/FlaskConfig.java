
package cn.lqz.unmannedinspectionsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@Data
public class FlaskConfig {
    @Value("${service.flask.base-url}")
    private String flaskBaseUrl;
    @Value("${service.flask2.base-url}")
    private String flask2BaseUrl;
} 