package org.dows.hep.biz;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/11/1 13:02
 */
@Aspect
@Component
@Slf4j
public class ControllerAspect {

    @Autowired
    protected HttpServletRequest httpServletRequest;

    @Autowired
    protected HttpServletResponse httpServletResponse;

    private final static int SLOWApiThresholdMs=1000;

    private final static Set<String> TRACEUrl=Set.of("/baseIndicator/indicatorJudgeRiskFactor/createOrUpdateRs",
            "baseIndicator/indicatorJudgeHealthGuidance/createOrUpdateRs",
            "baseIndicator/indicatorJudgeHealthProblem/createOrUpdateRs"
            );

    @Around("execution(public * org.dows.hep.rest..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        long start=System.currentTimeMillis();
        Object rst=pjp.proceed();
        long cost=System.currentTimeMillis()-start;
        String url=httpServletRequest.getServletPath();
        if(cost<SLOWApiThresholdMs&&TRACEUrl.stream().noneMatch(url::contains)){
            return rst;
        }
        StringBuilder sb=new StringBuilder("APITrace--slow--");
        sb.append(" url:").append(url);
        sb.append(" cost:").append(cost);
        Object[] args = pjp.getArgs();
        if(ShareUtil.XObject.isEmpty(args)){
            return rst;
        }
        sb.append(" req:");
        for (Object arg : args) {
            if(arg instanceof HttpSession
            ||arg instanceof HttpServletRequest
            ||arg instanceof HttpServletResponse
            ||arg instanceof MultipartFile){
                continue;
            }
            if(arg instanceof String
                    ||arg instanceof Number
                    ||arg instanceof Boolean){
                sb.append(arg).append(",");
                continue;
            }
            sb.append(JacksonUtil.toJsonSilence(arg, false))
                    .append(",");

        }
        sb.append("\n");
        log.info(sb.toString());
        sb.setLength(0);
        return rst;
    }
}
