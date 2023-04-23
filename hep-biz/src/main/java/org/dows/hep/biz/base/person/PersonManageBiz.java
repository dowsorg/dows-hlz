package org.dows.hep.biz.base.person;

import org.dows.hep.api.base.person.request.PersonInstanceRequest;
import org.dows.hep.api.base.person.response.PersonInstanceResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:人物:人物管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class PersonManageBiz{
    /**
    * @param
    * @return
    * @说明: 批量删除人物
    * @关联表: AccountInstance、AccountUser、AccountRole、UserInstance、UserExtinfo、IndicatorInstance、IndicatorPrincipalRef、CaseEvent、CaseEventEval、CaseEventAction
    * @工时: 3H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean copyPerson(String accountId ) {
        return Boolean.FALSE;
    }
}