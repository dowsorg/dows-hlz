package org.dows.hep.biz.person;

import org.dows.framework.api.Response;
import org.dows.hep.api.person.response.PersonInstanceResponse;
import org.dows.hep.api.person.request.PersonInstanceRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:人物:人物管理
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
public class PersonManageBiz{
    /**
    * @param
    * @return
    * @说明: 批量删除人物
    * @关联表: AccountInstance、AccountUser、AccountRole、UserInstance、UserExtinfo、IndicatorInstance、IndicatorPrincipalRef、CaseEvent、CaseEventEval、CaseEventAction
    * @工时: 3H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public Boolean deletePersons(String ids ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 查看人物基本信息
    * @关联表: AccountInstance、AccountUser、UserInstance、UserExtinfo
    * @工时: 3H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public PersonInstanceResponse getPerson(String accountId ) {
        return new PersonInstanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 编辑人物基本信息
    * @关联表: AccountInstance、AccountUser、UserInstance、UserExtinfo
    * @工时: 3H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public Boolean editPerson(PersonInstanceRequest personInstance ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 复制人物
    * @关联表: AccountInstance、AccountUser、AccountRole、UserInstance、UserExtinfo、IndicatorInstance、IndicatorPrincipalRef、CaseEvent、CaseEventEval、CaseEventAction
    * @工时: 6H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public Boolean copyPerson(String accountId ) {
        return Boolean.FALSE;
    }
}