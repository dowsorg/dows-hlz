package org.dows.hep.biz;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 实验暂停拦截器
 */
@Slf4j
public class ExperimentPausedInterceptor implements HandlerInterceptor {

    private ExperimentTimerBiz experimentTimerBiz;

    public ExperimentPausedInterceptor(ExperimentTimerBiz experimentTimerBiz) {
        this.experimentTimerBiz = experimentTimerBiz;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 如果实验是暂停,并且是学生端所有的请求接口，v1/user打头的url都是学生端
         */
        if (experimentTimerBiz.getExperimentState() && request.getRequestURI().contains("v1/user")) {
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
