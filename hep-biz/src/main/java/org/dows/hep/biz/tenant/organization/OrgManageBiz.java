package org.dows.hep.biz.tenant.organization;

import org.dows.hep.api.tenant.organization.request.*;
import org.springframework.stereotype.Service;

/**
* @description project descr:机构操作:机构管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class OrgManageBiz{
    /**
    * @param
    * @return
    * @说明: 保存机构基本信息
    * @关联表: CaseOrgFee、AccounOrg、AccountOrgInfo
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String addOrg(AddOrgRequest addOrg ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 绑定机构功能
    * @关联表: CaseOrgFunction
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean bindOrgFunc(OrgFuncRequest orgFunc ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 设置机构相关费用
    * @关联表: CaseOrgFee
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean settingOrgFee(OrgFeeSettingRequest orgFeeSetting ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 增加机构组员
    * @关联表: AccountOrg、AccountGroup、AccountInstance
    * @工时: 3H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean addMember(AddOrgMemberRequest addOrgMember ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 导出机构组员数据
    * @关联表: 
    * @工时: 0H
    * @开发者: 
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void exportMember() {
        
    }
    /**
    * @param
    * @return
    * @说明: 导入机构组员数据
    * @关联表: AccountOrg、AccountGroup、AccountInstance
    * @工时: 6H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean importOrgMember(MultipartFileRequest file ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 下载模板
    * @关联表: 
    * @工时: 5H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void dowsnloadTemplate() {
        
    }
}