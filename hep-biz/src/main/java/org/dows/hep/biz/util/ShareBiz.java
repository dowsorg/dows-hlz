package org.dows.hep.biz.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.dows.account.util.JwtUtil;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.biz.vo.LoginContextVO;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
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

    public static String ensureCategPathSuffix(String src){
        if(ShareUtil.XObject.isEmpty(src)){
            return src;
        }
        return ShareUtil.XString.eusureEndsWith(src,"/");
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

}
