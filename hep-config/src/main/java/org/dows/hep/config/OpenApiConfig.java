package org.dows.hep.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc 配置类
 */
@Configuration
public class OpenApiConfig {
    /**
     * SpringDoc 标题、描述、版本等信息配置
     *
     * @return openApi 配置信息
     */
    @Bean
    public OpenAPI springDocOpenAPI() {
        return new OpenAPI().info(new Info()
                        .title("HEP")
                        .description("HEP 接口文档说明")
                        .version("1.0.0-SNAPSHOT")
                        .license(new License().name("dows").url("")))
                .externalDocs(new ExternalDocumentation()
                        .description("sss")
                        .url("http://localhost:9001"));
        // 配置Authorizations
//                .components(new Components().addSecuritySchemes("bearer-key",
//                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")));
    }


    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .packagesToScan("org.dows.hep.rest.base")
                .group("hep-admin")
                //.pathsToMatch("/v1/schema/**")
                .build();
    }


    @Bean
    public GroupedOpenApi tenantApi() {
        return GroupedOpenApi.builder()
                .packagesToScan("org.dows.hep.rest.tenant")
                .group("hep-tenant")
                //.pathsToMatch("/v1/schema/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .packagesToScan("org.dows.hep.rest.user")
                .group("hep-user")
                //.pathsToMatch("/v1/schema/**")
                .build();
    }
}
