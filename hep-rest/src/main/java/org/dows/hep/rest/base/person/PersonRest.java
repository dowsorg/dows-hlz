package org.dows.hep.rest.base.person;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.hep.biz.base.person.PersonBiz;
import org.springframework.web.bind.annotation.*;
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
    public Boolean resetPwd(@RequestBody AccountInstanceRequest request) {
        return personBiz.resetPwd(request);
    }

    /**
     * 查看 个人中心-我的资料
     * @param
     * @return
     */
    @Operation(summary =  "查看个人资料")
    @GetMapping("v1/basePerson/person/getPersonalInformation/{accountId}/{appId}")
    public AccountInstanceResponse getPersonalInformation(@PathVariable("accountId") String accountId, @PathVariable("appId") String appId) {
        return personBiz.getPersonalInformation(accountId,appId);
    }

    /**
     * 修改 个人中心-我的资料
     * @param
     * @return
     */
    @Operation(summary =  "修改个人资料")
    @PutMapping("v1/basePerson/person/updatePersonalInformation")
    public String updatePersonalInformation(@RequestBody AccountInstanceRequest request) {
        return personBiz.updatePersonalInformation(request);
    }

    /**
     * 创建教师/学生
     * @param
     * @return
     */
    @Operation(summary =  "创建教师/学生")
    @PostMapping("v1/basePerson/person/createTeacherOrStudent")
    public AccountInstanceResponse createTeacherOrStudent(@RequestBody AccountInstanceRequest request){
        return personBiz.createTeacherOrStudent(request);
    }

    /**
     * 获取教师/学生列表
     *
     * @param
     * @return
     */
    @Operation(summary =  "获取教师/学生列表")
    @PostMapping("v1/basePerson/person/listTeacherOrStudent")
    public IPage<AccountInstanceResponse> listTeacherOrStudent(@RequestBody AccountInstanceRequest request){
          return personBiz.listTeacherOrStudent(request);
    }

    /**
     * 编辑教师/学生
     *
     * @param
     * @return
     */
    @Operation(summary =  "编辑教师/学生")
    @PostMapping("v1/basePerson/person/editTeacherOrStudent")
    public String editTeacherOrStudent(@RequestBody AccountInstanceRequest request){
        return personBiz.editTeacherOrStudent(request);
    }

    /**
     * 教师 判断是否有班级
     */
    @Operation(summary =  "教师 判断是否有班级")
    @PostMapping("v1/basePerson/person/checkOwnClass")
    public Boolean checkOwnClass(@RequestBody AccountInstanceRequest request){
        return personBiz.checkOwnClass(request);
    }

}
