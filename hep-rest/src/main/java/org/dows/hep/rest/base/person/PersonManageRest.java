package org.dows.hep.rest.base.person;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.hep.api.annotation.Resubmit;
import org.dows.hep.api.base.person.request.PersonInstanceRequest;
import org.dows.hep.api.base.person.response.PersonInstanceResponse;
import org.dows.hep.api.tenant.casus.request.CasePersonIndicatorFuncRequest;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.dows.hep.biz.base.person.PersonManageExtBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
* @description project descr:人物:人物管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "人物管理", description = "人物管理")
public class PersonManageRest {
    private final PersonManageBiz personManageBiz;
    private final PersonManageExtBiz personManageExtBiz;
    /**
    * 批量删除人物
    * @param
    * @return
    */
    @Operation(summary = "批量删除人物")
    @DeleteMapping("v1/basePerson/personManage/deletePersons")
    public Integer deletePersons(@RequestBody @Validated Set<String> accountIds) {
        return personManageBiz.deletePersons(accountIds);
    }

    /**
    * 查看人物基本信息
    * @param
    * @return
    */
    @Operation(summary = "查看人物基本信息")
    @GetMapping("v1/basePerson/personManage/getPerson/{accountId}")
    public PersonInstanceResponse getPerson(@PathVariable @Validated String accountId) {
        return personManageBiz.getPerson(accountId);
    }

    /**
    * 编辑人物基本信息
    * @param
    * @return
    */
    @Operation(summary = "编辑人物基本信息")
    @PutMapping("v1/basePerson/personManage/editPerson")
    public Boolean editPerson(@RequestBody @Validated PersonInstanceRequest request) {
        return personManageBiz.editPerson(request);
    }

    /**
     * 编辑人物状态
     * @param
     * @return
     */
    @Operation(summary = "编辑人物状态")
    @PutMapping("v1/basePerson/personManage/editPersonStatus")
    public String editPersonStatus(@RequestBody @Validated PersonInstanceRequest request) {
        return personManageBiz.editPersonStatus(request);
    }

    /**
    * 复制人物
    * @param
    * @return
    */
    @Resubmit(duration = 3)
    @Operation(summary = "复制人物")
    @PostMapping("v1/basePerson/personManage/copyPerson")
    public PersonInstanceResponse copyPerson(@RequestParam @Validated String accountId,
                                             @RequestParam @Validated String source) throws ExecutionException, InterruptedException {
//        return personManageBiz.copyPerson(accountId,source);
        return personManageExtBiz.duplicatePerson(accountId,source,true);
    }

    /**
     * 新增人物
     * @param
     * @return
     */
    @Operation(summary = "新增人物")
    @PostMapping("v1/basePerson/personManage/addPerson")
    public PersonInstanceResponse addPerson(@RequestBody @Validated AccountInstanceRequest request) throws ExecutionException, InterruptedException {
        return personManageBiz.addPerson(request);
    }

    /**
     *
     * 新增其他图示管理
     * @param
     * @return
     */
    @Operation(summary = "新增其他图示管理")
    @PostMapping("v1/basePerson/personManage/addOtherBackground")
    public boolean addOtherBackground(@RequestBody @Validated List<CasePersonIndicatorFuncRequest> list) {
        return personManageBiz.addOtherBackground(list);
    }

    /**
     * 人物列表
     * @param
     * @return
     */
    @Operation(summary = "人物列表")
    @PostMapping("v1/basePerson/personManage/listPerson")
    public IPage<PersonInstanceResponse> listPerson(@RequestBody @Validated AccountInstanceRequest request) {
        return personManageBiz.listPerson(request);
    }

    /**
     * 登录
     * @param
     * @return
     */
    @Operation(summary = "登录")
    @PostMapping("v1/basePerson/person/login")
    public Map<String, Object> login(@RequestBody AccountInstanceRequest request, HttpServletRequest request1) {
        return personManageBiz.login(request,request1);
    }

    /**
     * 登出
     * @param
     * @return
     */
    @Operation(summary = "登出")
    @PostMapping("v1/basePerson/person/loginOut")
    public void loginOut(@RequestParam String accountId) {
        personManageBiz.loginOut(accountId);
    }

    /**
     * 重置密码
     * @param
     * @return
     */
    @Operation(summary = "重置密码")
    @PutMapping("v1/basePerson/person/resetPwd")
    public Boolean resetPwd(@Nullable @RequestParam String oldPassword,@RequestBody AccountInstanceRequest request) {
        return personManageBiz.resetPwd(oldPassword,request);
    }

    /**
     * 查看 个人中心-我的资料
     * @param
     * @return
     */
    @Operation(summary =  "查看个人资料")
    @GetMapping("v1/basePerson/person/getPersonalInformation/{accountId}/{appId}")
    public AccountInstanceResponse getPersonalInformation(@PathVariable("accountId") String accountId,
                                                          @PathVariable("appId") String appId) {
        return personManageBiz.getPersonalInformation(accountId,appId);
    }

    /**
     * 修改 个人中心-我的资料
     * @param
     * @return
     */
    @Operation(summary =  "修改个人资料")
    @PutMapping("v1/basePerson/person/updatePersonalInformation")
    public String updatePersonalInformation(@RequestBody AccountInstanceRequest request) {
        return personManageBiz.updatePersonalInformation(request);
    }

    /**
     * 创建教师/学生
     * @param
     * @return
     */
    @Resubmit(duration = 1)
    @Operation(summary =  "创建教师/学生")
    @PostMapping("v1/basePerson/person/createTeacherOrStudent")
    public AccountInstanceResponse createTeacherOrStudent(@RequestBody AccountInstanceRequest request){
        return personManageBiz.createTeacherOrStudent(request);
    }

    /**
     * 获取教师/学生列表
     *
     * @param
     * @return
     */
    @Operation(summary =  "获取教师/学生列表")
    @PostMapping("v1/basePerson/person/listTeacherOrStudent")
    public IPage<AccountInstanceResponse> listTeacherOrStudent(@RequestBody AccountInstanceRequest request,
                                                               @Nullable @RequestParam String accountId){
        return personManageBiz.listTeacherOrStudent(request,accountId);
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
        return personManageBiz.editTeacherOrStudent(request);
    }

    /**
     * 教师 获取负责有班级列表
     *
     * @param
     * @return
     */
    @Operation(summary =  "教师 获取负责班级列表")
    @PostMapping("v1/basePerson/person/listOwnClass")
    public Set<String> listOwnClass(@RequestBody AccountInstanceRequest request){
        return personManageBiz.listOwnClass(request);
    }

    /**
     * 教师 班级转移
     *
     * @param
     * @return
     */
    @Operation(summary =  "教师 班级转移")
    @PostMapping("v1/basePerson/person/transferClass")
    public Boolean transferClass(@RequestBody AccountInstanceRequest request){
        return personManageBiz.transferClass(request);
    }

    /**
     * 删除 教师/学生
     * 删除教师时需要将班级连带删除，同时删除学生
     *
     * @param
     * @return
     */
    @Operation(summary =  "删除 教师/学生")
    @DeleteMapping("v1/basePerson/person/deleteTeacherOrStudents")
    public Boolean deleteTeacherOrStudents(@RequestBody Set<String> accountIds,
                                           @RequestParam String roleName,
                                           @RequestParam String appId
                                           ){
        return personManageBiz.deleteTeacherOrStudents(accountIds,roleName,appId);
    }
}