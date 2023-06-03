package org.dows.hep.biz.user.experiment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.dows.account.util.JwtUtil;
import org.dows.hep.api.enums.EnumToken;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author fhb
 * @description
 * @date 2023/5/30 15:50
 */
@AllArgsConstructor
@Service
public class ExperimentBaseBiz {
//    private final ExperimentInstanceService experimentInstanceService;
//
//    public String getCaseInstanceId(String experimentInstanceId) {
//        return Optional.ofNullable(experimentInstanceService.lambdaQuery()
//                        .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
//                        .one())
//                .map(ExperimentInstanceEntity::getCaseInstanceId)
//                .orElse("");
//    }

    public String getAccountId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        return map.get("accountId").toString();
    }

    public String getAccountName(HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        return map.get("accountName").toString();
    }
}
