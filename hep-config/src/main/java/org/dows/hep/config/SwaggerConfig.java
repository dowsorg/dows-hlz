package org.dows.hep.config;//package org.dows.aicode.config;
//
//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.ReflectionUtils;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
//import springfox.documentation.builders.*;
//import springfox.documentation.oas.annotations.EnableOpenApi;
//import springfox.documentation.schema.ScalarType;
//import springfox.documentation.service.*;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
//import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@EnableKnife4j
//@EnableOpenApi
//@Configuration
//public class SwaggerConfig {
//
//    /**
//     * 短信管理端
//     *
//     * @return
//     */
//    @Bean
//    public Docket payAdminApi() {
//        return new Docket(DocumentationType.OAS_30)
//                .apiInfo(apiInfo())
//                .enable(true)
//                .select()
//                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .apis(RequestHandlerSelectors.basePackage("org.dows.aicodea.rest"))
//                .paths(PathSelectors.any())
//                .build()
//                .groupName("支付管理端");
//        //.pathMapping("/sms");
//    }
//
//
//    private ApiInfo apiInfo() {
//        Contact contact = new Contact("lait", "", "lait.zhang@gmail.com");
//        return new ApiInfoBuilder()
//                .title("dows saas pay")
//                .contact(contact)
//                .version("3.0")
//                .description("支付服务")
//                .version("1.0.0")
//                .build();
//    }
//
//
//    //生成全局通用参数
//    private List<RequestParameter> getGlobalRequestParameters() {
//        List<RequestParameter> parameters = new ArrayList<>();
//        parameters.add(new RequestParameterBuilder()
//                .name("appid")
//                .description("应用id")
//                .required(true)
//                .in(ParameterType.QUERY)
//                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
//                .required(false)
//                .build());
//        parameters.add(new RequestParameterBuilder()
//                .name("token")
//                .description("token")
//                .required(true)
//                .in(ParameterType.QUERY)
//                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
//                .required(false)
//                .build());
//        return parameters;
//    }
//
//    //生成通用响应信息
//    private List<Response> getGlobalResonseMessage() {
//        List<Response> responseList = new ArrayList<>();
//        responseList.add(new ResponseBuilder().code("404").description("找不到资源").build());
//        return responseList;
//    }
//
//    /**
//     * 增加如下配置可解决Spring Boot 6.x 与Swagger 3.0.0 不兼容问题
//     **/
//    @Bean
//    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
//        return new BeanPostProcessor() {
//
//            @Override
//            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
//                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
//                }
//                return bean;
//            }
//
//            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
//                List<T> copy = mappings.stream()
//                        .filter(mapping -> mapping.getPatternParser() == null)
//                        .collect(Collectors.toList());
//                mappings.clear();
//                mappings.addAll(copy);
//            }
//
//            @SuppressWarnings("unchecked")
//            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
//                try {
//                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
//                    field.setAccessible(true);
//                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
//                } catch (IllegalArgumentException | IllegalAccessException e) {
//                    throw new IllegalStateException(e);
//                }
//            }
//        };
//    }
//
//
///*    @Bean
//    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
//        return new BeanPostProcessor() {
//            @Override
//            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
//                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
//                }
//                return bean;
//            }
//
//            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
//                List<T> copy = mappings.stream()
//                        .filter(mapping -> mapping.getPatternParser() == null)
//                        .collect(Collectors.toList());
//                mappings.clear();
//                mappings.addAll(copy);
//            }
//
//            @SuppressWarnings("unchecked")
//            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
//                try {
//                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
//                    field.setAccessible(true);
//                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
//                } catch (IllegalArgumentException | IllegalAccessException e) {
//                    throw new IllegalStateException(e);
//                }
//            }
//        };
//    }*/
//
///*
//    @Bean
//    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
//                                                                         ServletEndpointsSupplier servletEndpointsSupplier,
//                                                                         ControllerEndpointsSupplier controllerEndpointsSupplier,
//                                                                         EndpointMediaTypes endpointMediaTypes,
//                                                                         CorsEndpointProperties corsProperties,
//                                                                         WebEndpointProperties webEndpointProperties,
//                                                                         Environment environment) {
//        List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
//        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
//        allEndpoints.addAll(webEndpoints);
//        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
//        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
//        String basePath = webEndpointProperties.getBasePath();
//        EndpointMapping endpointMapping = new EndpointMapping(basePath);
//        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
//        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
//                corsProperties.toCorsConfiguration(),
//                new EndpointLinksResolver(allEndpoints, basePath),
//                shouldRegisterLinksMapping);
//    }
//
//    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
//        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
//    }*/
//
//}
