package org.dows.hep.rest.base.person;

import com.baomidou.dynamic.datasource.annotation.DS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.hep.biz.base.person.PersonBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * @author jx
 * @date 2023/4/20 13:15
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "人物", description = "人物")
public class PersonRest {
    private final PersonBiz personBiz;

    /**
     * 登录
     * @param
     * @return
     */
    @Operation(summary = "登录")
    @PostMapping("v1/basePerson/person/login")
    @DS("uim")
    public Map<String, Object> login(@RequestBody AccountInstanceRequest request) {
        return personBiz.login(request);
    }

    /**
     * 重置密码
     * @param
     * @return
     */
    @Operation(summary = "重置密码")
    @PutMapping("v1/basePerson/person/resetPwd")
    @DS("uim")
    public Boolean resetPwd(@RequestBody AccountInstanceRequest request) {
        return personBiz.resetPwd(request);
    }

}
