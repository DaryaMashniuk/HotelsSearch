package by.mashnyuk.hotels.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hotel info API",
                version = "1.0",
                description = "API for managing hotels and their properties"
        ),
        servers = {
                @Server(
                        url="http://localhost:8092",
                        description = "Development Server"
                )
        }
)
public class SwaggerConfig {
}
