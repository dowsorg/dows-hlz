package org.dows.hep.config;

import org.dows.framework.api.web.ResponseWrapperHandler;
import org.dows.hep.biz.ExperimentPausedInterceptor;
import org.dows.hep.biz.ExperimentResubmitInterceptor;
import org.dows.hep.biz.cache.LocalCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LocalCache localCache;
    @Bean
    public ResponseWrapperHandler responseWrapperHandler() {
        return new ResponseWrapperHandler();
    }


//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        // 用于get 全局格式化日期转换
//        registry.addFormatterForFieldType(LocalDate.class, new LocalDateFormatter());
//        registry.addFormatterForFieldType(LocalDateTime.class, new LocalDateTimeFormatter());
//    }

/*    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 代替框架默认的JackSon配置 用于post 全局格式化日期转换，long转字符串
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jackson2HttpMessageConverter.setObjectMapper(ObjectMapper());
        // 基于顺序，先执行自定义的
        converters.add(0, jackson2HttpMessageConverter);
    }

    private ObjectMapper ObjectMapper() {
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String dateFormat = "yyyy-MM-dd";
        String timeFormat = "HH:mm:ss";
        ObjectMapper objectMapper = new ObjectMapper();
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //忽略 在json字符串中存在，但是在对象中不存在对应属性的情况，防止错误。
        // 例如json数据中多出字段，而对象中没有此字段。如果设置true，抛出异常，因为字段不对应；false则忽略多出的字段，默认值为null，将其他字段反序列化成功
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 序列化
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
        javaTimeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(timeFormat)));
        javaTimeModule.addSerializer(Date.class,
                new DateSerializer(false, new SimpleDateFormat(dateTimeFormat)));

        // 反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        javaTimeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)));
        javaTimeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(timeFormat)));
        javaTimeModule.addDeserializer(Date.class, new DateDeserializers.DateDeserializer() {
            @SneakyThrows
            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext dc) {
                String text = jsonParser.getText().trim();
                SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
                return sdf.parse(text);
            }
        });
        javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance);
        javaTimeModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }*/

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(0, new MappingJackson2HttpMessageConverter());
//    }
//
//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        FastJsonHttpMessageConverter fjc = new FastJsonHttpMessageConverter();
//        FastJsonConfig fj = new FastJsonConfig();
//        fj.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
//        fjc.setFastJsonConfig(fj);
//        converters.add(fjc);
//    }


//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/swagger-ui/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
//                .resourceChain(false);
//
//        registry.addResourceHandler("/favicon.ico")//favicon.ico
//                .addResourceLocations("classpath:/static/");
//
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//
//        registry.addResourceHandler("doc.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/doc.html#/**")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        // 配置访问前缀
//        registry.addResourceHandler("/templates/**")
//                //配置文件真实路径
//                .addResourceLocations("classpath:/templates/");
//
//    }
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/swagger-ui/")
//                .setViewName("forward:/swagger-ui/index.html");
//    }

    //    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(new HeaderArgumentResolver());
//    }
//
//
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor();
//        registry.addInterceptor(new JwtAuthenticationFilter());
        registry.addInterceptor(new ExperimentPausedInterceptor());
        registry.addInterceptor(new ExperimentResubmitInterceptor(localCache));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源访问路径和存放路径配置(本地磁盘图片映射)
        registry.addResourceHandler("/hepapi/**")
                .addResourceLocations("file:/data/resources/images/hep/");
    }


    /**
     * 跨域处理
     *
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //1,允许任何来源
        corsConfiguration.addAllowedOriginPattern(CorsConfiguration.ALL);
        //2,允许任何请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        //3,允许任何方法
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        //4,允许凭证
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }


}

