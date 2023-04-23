package org.dows.hep.rest.base.person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.person.request.PersonInstanceRequest;
import org.dows.hep.api.base.person.response.PersonInstanceResponse;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
    * 批量删除人物
    * @param
    * @return
    */
    @Operation(summary = "批量删除人物")
    @DeleteMapping("v1/basePerson/personManage/deletePersons")
    public Boolean deletePersons(@Validated String ids ) {
        return personManageBiz.deletePersons(ids);
    }

    /**
    * 查看人物基本信息
    * @param
    * @return
    */
    @Operation(summary = "查看人物基本信息")
    @GetMapping("v1/basePerson/personManage/getPerson")
    public PersonInstanceResponse getPerson(@Validated String accountId) {
        return personManageBiz.getPerson(accountId);
    }

    /**
    * 编辑人物基本信息
    * @param
    * @return
    */
    @Operation(summary = "编辑人物基本信息")
    @PutMapping("v1/basePerson/personManage/editPerson")
    public Boolean editPerson(@Validated PersonInstanceRequest personInstance ) {
        return personManageBiz.editPerson(personInstance);
    }

    /**
    * 复制人物
    * @param
    * @return
    */
    @Operation(summary = "复制人物")
    @PostMapping("v1/basePerson/personManage/copyPerson")
    public Boolean copyPerson(@RequestBody @Validated String accountId ) {
        return personManageBiz.copyPerson(accountId);
    }


}