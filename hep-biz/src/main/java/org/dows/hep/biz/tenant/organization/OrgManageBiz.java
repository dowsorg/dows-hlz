package org.dows.hep.biz.tenant.organization;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.hep.api.tenant.excel.BatchMemberInsertRequest;
import org.dows.hep.api.tenant.organization.request.AddOrgMemberRequest;
import org.dows.hep.api.tenant.organization.request.AddOrgRequest;
import org.dows.hep.api.tenant.organization.request.OrgFeeSettingRequest;
import org.dows.hep.api.tenant.organization.request.OrgFuncRequest;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.dows.hep.biz.tenant.excel.BatchInsertBiz;
import org.dows.hep.biz.util.AssertUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @description project descr:机构操作:机构管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class OrgManageBiz{

    private final BatchInsertBiz batchInsertBiz;
    private final PersonManageBiz personManageBiz;
    private final org.dows.hep.biz.base.org.OrgBiz orgManageBiz;
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
    * @关联表: account_identifier、rbac_role、account_org、account_instance、account_role、account_group、account_user、user_instance
    * @工时: 6H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月24日 上午09:00:00
    */
    @DSTransactional
    public Map<String, Object> importOrgMember(AccountInstanceRequest request, MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        //失败成员列表
        List<BatchMemberInsertRequest> memberList = new ArrayList<>();
        //导入成功成员数
        Integer finishCount = 0;
        //导入失败成员数
        Integer failCount = 0;
        InputStream fin = null;
        try {
//            fin = file.getInputStream();
            AssertUtil.trueThenThrow(file.isEmpty()).throwMessage("导入文件为空");
            //重新编译后读取文件
            File newFile=  batchInsertBiz.parseImportExcelStream(file);
            fin = new FileInputStream(newFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<BatchMemberInsertRequest> list = batchInsertBiz.batchInsert(fin, 2, 500, BatchMemberInsertRequest.class, "accountName");
        //2、插入用户信息
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                request.setAccountName(list.get(i).getAccountName());
                request.setUserName(list.get(i).getUserName());
                request.setPassword(list.get(i).getPassword());
                request.setIdentifier(orgManageBiz.createCode(7));
                String message = "";
                try {
                    personManageBiz.createTeacherOrStudent(request);
                } catch (Exception e) {
                    message = e.getMessage();
                } finally {
                    if (StringUtils.isEmpty(message)) {
                        finishCount++;
                    } else {
                        list.get(i).setTips(message);
                        memberList.add(list.get(i));
                        failCount++;
                    }
                }
            }
        }
        map.put("finishCount", finishCount);
        map.put("failCount", failCount);
        map.put("memberList", memberList);
        return map;
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