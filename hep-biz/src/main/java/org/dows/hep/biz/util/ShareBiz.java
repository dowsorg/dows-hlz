package org.dows.hep.biz.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.dows.account.util.JwtUtil;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.base.indicator.response.CaseIndicatorExpressionResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.biz.base.indicator.CaseIndicatorExpressionBiz;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.vo.LoginContextVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * 业务规则公共类
 *
 * @author : wuzl
 * @date : 2023/5/4 15:37
 */
public class ShareBiz {

    private static final int NUMBERScale2=2;
    public static BigDecimal fixDecimalWithScale(String src){
        return BigDecimalUtil.roundDecimal(BigDecimalUtil.tryParseDecimalElseZero(src),NUMBERScale2);
    }


    /**
     * 分页输出转换
     * @param page
     * @param func
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T,R> Page<R> buildPage(IPage<T> page, Function<? super T, ? extends R> func) {
        return Page.<R>of(page.getCurrent(), page.getSize(), page.getTotal(), page.searchCount())
                .setRecords(ShareUtil.XCollection.map(page.getRecords(),  func));
    }

    /**
     * 获取登录用户
     * @param request
     * @return
     */
    public static LoginContextVO getLoginUser(HttpServletRequest request){
        LoginContextVO rst=new LoginContextVO();
        String token = request.getHeader("token");
        if(ShareUtil.XObject.isEmpty(token)){
            return rst;
        }
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        return rst.setAccountId(Optional.ofNullable(map.get("accountId")).map(Object::toString).orElse(""))
                .setAccountName(Optional.ofNullable(map.get("accountName")).map(Object::toString).orElse(""));

    }


    /**
     * 计算当前游戏内天数
     * @param appId
     * @param experimentInstanceId
     * @param dt
     * @return
     */
    public static Integer calcGameDay(String appId, String experimentInstanceId, Date dt){
        final LocalDateTime ldt=ShareUtil.XDate.localDT4Date(dt);
        final ExperimentCacheKey key=new ExperimentCacheKey().setAppId(appId).setExperimentInstanceId(experimentInstanceId);
        ExperimentTimePoint timePoint= ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(key,ldt,true);
        return Optional.ofNullable(timePoint).map(ExperimentTimePoint::getGameDay).orElse(null);
    }

    //region 获取公式
    public static List<CaseIndicatorExpressionResponseRs> getCaseExpressionsByReasonId(CaseIndicatorExpressionBiz indicatorExpressionBiz, String appId, String reasonId) {
        if (ShareUtil.XObject.isEmpty(reasonId)) {
            return Collections.emptyList();
        }
        indicatorExpressionBiz = Optional.ofNullable(indicatorExpressionBiz).orElseGet(() -> CrudContextHolder.getBean(CaseIndicatorExpressionBiz.class));
        Map<String, List<CaseIndicatorExpressionResponseRs>> map = new HashMap<>(1);
        Set<String> reasonIds = new HashSet<>();
        reasonIds.add(reasonId);
        indicatorExpressionBiz.populateKCaseReasonIdVCaseIndicatorExpressionResponseRsListMap(appId, reasonIds, map);
        List<CaseIndicatorExpressionResponseRs> rst = map.getOrDefault(reasonId, Collections.emptyList());
        rst.sort(Comparator.comparingLong(CaseIndicatorExpressionResponseRs::getId));
        reasonIds.clear();
        map.clear();
        return rst;
    }
    public static Map<String, List<CaseIndicatorExpressionResponseRs>> getCaseExpressionsByReasonIds(CaseIndicatorExpressionBiz indicatorExpressionBiz, String appId, Set<String> reasonIds){
        if(ShareUtil.XObject.isEmpty(reasonIds)){
            return Collections.emptyMap();
        }
        indicatorExpressionBiz=Optional.ofNullable(indicatorExpressionBiz).orElseGet(()-> CrudContextHolder.getBean(CaseIndicatorExpressionBiz.class));
        Map<String, List<CaseIndicatorExpressionResponseRs>> rst=new HashMap<>(reasonIds.size());
        indicatorExpressionBiz.populateKCaseReasonIdVCaseIndicatorExpressionResponseRsListMap(appId,reasonIds,rst);
        rst.values().forEach(i->i.sort(Comparator.comparingLong(CaseIndicatorExpressionResponseRs::getId)));
        return rst;
    }
    public static List<IndicatorExpressionResponseRs> getExpressionsByReasonId(IndicatorExpressionBiz indicatorExpressionBiz, String appId,String reasonId) {
        if (ShareUtil.XObject.isEmpty(reasonId)) {
            return Collections.emptyList();
        }
        indicatorExpressionBiz = Optional.ofNullable(indicatorExpressionBiz).orElseGet(() -> CrudContextHolder.getBean(IndicatorExpressionBiz.class));
        Map<String, List<IndicatorExpressionResponseRs>> map = new HashMap<>(1);
        Set<String> reasonIds = new HashSet<>();
        reasonIds.add(reasonId);
        indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, reasonIds, map);
        List<IndicatorExpressionResponseRs> rst = map.getOrDefault(reasonId, Collections.emptyList());
        rst.sort(Comparator.comparingLong(IndicatorExpressionResponseRs::getId));
        reasonIds.clear();
        map.clear();
        return rst;
    }
    public static Map<String, List<IndicatorExpressionResponseRs>> getExpressionsByReasonIds(IndicatorExpressionBiz indicatorExpressionBiz, String appId, Set<String> reasonIds){
        if(ShareUtil.XObject.isEmpty(reasonIds)){
            return Collections.emptyMap();
        }
        indicatorExpressionBiz=Optional.ofNullable(indicatorExpressionBiz).orElseGet(()-> CrudContextHolder.getBean(IndicatorExpressionBiz.class));
        Map<String, List<IndicatorExpressionResponseRs>> rst=new HashMap<>(reasonIds.size());
        indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId,reasonIds,rst);
        rst.values().forEach(i->i.sort(Comparator.comparingLong(IndicatorExpressionResponseRs::getId)));
        return rst;
    }
    //endreigon

}
