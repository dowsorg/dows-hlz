package org.dows.hep.rest.person;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.person.response.PersonInstanceResponse;
import org.dows.hep.api.person.request.PersonInstanceRequest;
import org.dows.hep.biz.person.PersonManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:人物:人物管理
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "人物管理")
public class PersonManageRest {
    private final PersonManageBiz personManageBiz;

    /**
    * 批量删除人物
    * @param
    * @return
    */
    @ApiOperation("批量删除人物")
    @DeleteMapping("v1/person/personManage/deletePersons")
    public Boolean deletePersons(@Validated String ids ) {
        return personManageBiz.deletePersons(ids);
    }

    /**
    * 查看人物基本信息
    * @param
    * @return
    */
    @ApiOperation("查看人物基本信息")
    @GetMapping("v1/person/personManage/getPerson")
    public PersonInstanceResponse getPerson(@Validated String accountId) {
        return personManageBiz.getPerson(accountId);
    }

    /**
    * 编辑人物基本信息
    * @param
    * @return
    */
    @ApiOperation("编辑人物基本信息")
    @PutMapping("v1/person/personManage/editPerson")
    public Boolean editPerson(@Validated PersonInstanceRequest personInstance ) {
        return personManageBiz.editPerson(personInstance);
    }

    /**
    * 复制人物
    * @param
    * @return
    */
    @ApiOperation("复制人物")
    @PostMapping("v1/person/personManage/copyPerson")
    public Boolean copyPerson(@RequestBody @Validated String accountId ) {
        return personManageBiz.copyPerson(accountId);
    }


}