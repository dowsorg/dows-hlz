package org.dows.hep.biz.organization.tenant;

import org.dows.framework.api.Response;
import org.dows.hep.api.organization.tenant.request.AddOrgRequest;
import org.dows.hep.api.organization.tenant.request.OrgFuncRequest;
import org.dows.hep.api.organization.tenant.request.OrgFeeSettingRequest;
import org.dows.hep.api.organization.tenant.request.AddOrgMemberRequest;
import org.dows.hep.api.organization.tenant.request.MultipartFileRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:机构操作:机构管理
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
public class OrgManageBiz{
    /**
    * @param
    * @return
    * @说明: 保存机构基本信息
    * @关联表: CaseOrgFee、AccounOrg、AccountOrgInfo
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public void dowsnloadTemplate() {
        
    }
}