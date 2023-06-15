package org.dows.hep.biz;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 实验暂停拦截器
 */
@Slf4j
public class ExperimentPausedInterceptor implements HandlerInterceptor {

    private ExperimentManageBiz experimentManageBiz;

    public ExperimentPausedInterceptor(ExperimentManageBiz experimentManageBiz) {
        this.experimentManageBiz = experimentManageBiz;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 如果实验是暂停,并且是学生端所有的请求接口，v1/user打头的url都是学生端
         */
        ExperimentContext experimentContext = ExperimentContext.getExperimentContext(request.getHeader("ExperimentId"));
        if (experimentContext == null) {
            return false;
        }
        if (experimentContext.getState() == ExperimentStateEnum.SUSPEND && request.getRequestURI().contains("v1/user")) {
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
