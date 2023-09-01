package org.dows.hep.biz;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.hep.api.annotation.Resubmit;
import org.dows.hep.api.status.ResubmitCode;
import org.dows.hep.biz.cache.LocalCache;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交
 */
@RequiredArgsConstructor
@Slf4j
public class ExperimentResubmitInterceptor implements HandlerInterceptor {

    private final LocalCache localCache;
    private static final Set<String> URLIncludes=new HashSet<>();

    static {
        URLIncludes.add("/userExperiment/");


    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = Optional.ofNullable( request.getHeader("token")).orElse("");
        String appId =  Optional.ofNullable(request.getHeader("appId")).orElse("");
        String experimentId = Optional.ofNullable(request.getHeader("experimentId")).orElse("");
        String clientIP = Optional.ofNullable(getRemoteIP(request)).orElse("");

        //String key = token + clientIP + request.getMethod() + request.getRequestURI();
        int sampleLen=10;
        String key=String.format("%s-%s-%s-%s-%s-%s",
                token.hashCode(),
                token.length(),
                token.length()>sampleLen? token.substring(token.length()-sampleLen, token.length()):token,
                clientIP, request.getMethod(),request.getRequestURI())
        //log.info("拦截UIR:{}", key);
        ;
        String val = localCache.get(key);
        if (val != null) {
            // todo 先简单实现，后学还要根据提交参数判断
            Response response1 = Response.fail(ResubmitCode.RESUBMIT);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            PrintWriter out = response.getWriter();
            out.println(JSONUtil.toJsonStr(response1));
            out.flush();
            out.close();
            return false;
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            Resubmit methodAnnotation = AnnotationUtils.findAnnotation(method.getMethod(), Resubmit.class);
            Resubmit repeatSubmitByCls = AnnotationUtils.findAnnotation(method.getMethod().getDeclaringClass(), Resubmit.class);

            long dueSec=-1;
            if(null!=methodAnnotation){
                if(methodAnnotation.value()){
                    return true;
                }
                dueSec= methodAnnotation.duration();
            }
            if(null!=repeatSubmitByCls){
                if(repeatSubmitByCls.value()){
                    return true;
                }
                if(dueSec<=0){
                    dueSec=repeatSubmitByCls.duration();
                }
            }
            if(dueSec<=0){
                if(!request.getMethod().toLowerCase().equals("post")){
                    return true;
                }
                dueSec=2;//默认2秒内禁止重复提交
            }
            localCache.set(key, "", dueSec * 1000, TimeUnit.MILLISECONDS);

/*
            // 没有限制重复提交，直接跳过
            if (Objects.isNull(methodAnnotation) && Objects.isNull(repeatSubmitByCls)) {
                return true;
            }
            Object controller = method.getBean();
            boolean value = methodAnnotation.value();
            if (value) {
                return true;
            } else {
                localCache.set(key, "", methodAnnotation.duration() * 1000, TimeUnit.MILLISECONDS);
            }
*/


        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    protected String getRemoteIP(HttpServletRequest request) {
        String ip = null;
        // X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        // 还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        //如果获取到的是127.0.0.1或0:0:0:0:0:0:0:1，就获取本地ip
        try {
            ip = getHostIP();
        } catch (SocketException e) {
            log.error("获取本机IP异常！", e);
        }

        return ip;
    }

    private String getHostIP() throws SocketException {
        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();

        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();

            // 去除回环接口，子接口，未运行接口
            if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                continue;
            }
            if (!netInterface.getDisplayName().contains("Intel") && !netInterface.getDisplayName().contains("Realtek")) {
                continue;
            }

            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    return ip.getHostAddress();
                }
            }
        }
        return null;
    }

}
