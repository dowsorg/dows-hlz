//package org.dows.hep.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.TimeZone;
//
///**
// * @author RunSix
// */
//@Configuration
//public class JacksonConfig {
//
//    /**
//     * DateTime格式化字符串
//     */
//    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
//
//    /**
//     * Date格式化字符串
//     */
//    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
//
//    /**
//     * Time格式化字符串
//     */
//    private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
//
//    /**
//     * Date格式化字符串
//     */
//    private static final String DATE_FORMAT = "yyyy-MM-dd";
//    /**
//     * DateTime格式化字符串
//     */
//    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
//
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return jacksonObjectMapperBuilder -> {
//            // 配置 Jackson 序列化 LocalDate、LocalDateTime 时使用的格式
//            jacksonObjectMapperBuilder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
//            jacksonObjectMapperBuilder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//            // 配置 Jackson 反序列化 LocalDate、LocalDateTime 时使用的格式
//            jacksonObjectMapperBuilder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//            jacksonObjectMapperBuilder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
//        };
//    }
//
////    /**
////     * 统一处理locadate
////     * @return
////     */
////    @Bean
////    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
////        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
////        ObjectMapper objectMapper = new ObjectMapper();
////        // 指定时区
////        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
////        // 日期类型字符串处理
////        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATETIME_PATTERN));
////
////        // Java8日期日期处理
////        JavaTimeModule javaTimeModule = new JavaTimeModule();
////        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)));
////        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
////        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));
////        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)));
////        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
////        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));
////        objectMapper.registerModule(javaTimeModule);
////
////        converter.setObjectMapper(objectMapper);
////        return converter;
////    }
//}
