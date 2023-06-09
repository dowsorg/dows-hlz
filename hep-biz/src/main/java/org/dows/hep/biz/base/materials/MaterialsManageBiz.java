package org.dows.hep.biz.base.materials;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.oss.api.OssInfo;
import org.dows.hep.api.base.materials.MaterialsAccessAuthEnum;
import org.dows.hep.api.base.materials.MaterialsESCEnum;
import org.dows.hep.api.base.materials.MaterialsEnabledEnum;
import org.dows.hep.api.base.materials.request.MaterialsAttachmentRequest;
import org.dows.hep.api.base.materials.request.MaterialsPageRequest;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.base.materials.request.MaterialsSearchRequest;
import org.dows.hep.api.base.materials.response.MaterialsAttachmentResponse;
import org.dows.hep.api.base.materials.response.MaterialsPageResponse;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.dows.hep.biz.base.oss.OSSBiz;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.dows.hep.entity.MaterialsAttachmentEntity;
import org.dows.hep.entity.MaterialsEntity;
import org.dows.hep.service.MaterialsAttachmentService;
import org.dows.hep.service.MaterialsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:资料中心:资料信息管理
 * @date 2023年4月18日 上午10:45:07
 */
@RequiredArgsConstructor
@Service
public class MaterialsManageBiz {
    private final MaterialsBaseBiz baseBiz;
    private final MaterialsService materialsService;
    private final MaterialsAttachmentService materialsAttachmentService;
    private final OSSBiz ossBiz;
    private final PersonManageBiz personManageBiz;

    /**
     * @param
     * @return
     * @说明: 新增和更新
     * @关联表: Materials, MaterialsAttachment
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @DSTransactional
    public String saveOrUpdMaterials(MaterialsRequest materialsRequest) {
        if (BeanUtil.isEmpty(materialsRequest)) {
            return "";
        }

        // check and saveOrUpd
        MaterialsEntity materialsEntity = convertRequest2Entity(materialsRequest);
        materialsService.saveOrUpdate(materialsEntity);

        // handle materials' attachments
        List<MaterialsAttachmentEntity> attachmentEntityList = convertAttachmentRequest2Entity(materialsRequest.getMaterialsAttachments(), materialsEntity.getMaterialsId());
        materialsAttachmentService.saveOrUpdateBatch(attachmentEntityList);

        return materialsEntity.getMaterialsId();
    }

    /**
     * @param
     * @return
     * @说明: 分页
     * @关联表: Materials, MaterialsAttachment
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public IPage<MaterialsPageResponse> pageMaterials(MaterialsPageRequest request) {
        Page<MaterialsPageResponse> result = new Page<>();
        if (BeanUtil.isEmpty(request)) {
            return result;
        }
        boolean isTeacher = baseBiz.isTeacher(request.getAccountId());

        Page<MaterialsEntity> pageRequest = new Page<>(request.getPageNo(), request.getPageSize());
        Page<MaterialsEntity> pageResult = materialsService.lambdaQuery()
                .eq(MaterialsEntity::getAppId, request.getAppId())
                .eq(MaterialsEntity::getBizCode, request.getBizCode())
                .eq(MaterialsEntity::getEnabled, MaterialsEnabledEnum.ENABLED.getCode())
                .and(isTeacher,
                        i -> i.eq(MaterialsEntity::getAccountId, request.getAccountId())
                                .or()
                                .eq(MaterialsEntity::getAccessAuth, MaterialsAccessAuthEnum.ACCESS_AUTH_PUBLIC.name()))
                .like(BeanUtil.isNotEmpty(request) && StrUtil.isNotBlank(request.getKeyword()), MaterialsEntity::getTitle, request.getKeyword())
                .orderBy(true, true, MaterialsEntity::getSequence)
                .page(pageRequest);
        result = baseBiz.convertPage(pageResult, MaterialsPageResponse.class);
        fillResult(result);
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 条件查询-无分页
     * @关联表: Materials, MaterialsAttachment
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public List<MaterialsResponse> listMaterials(MaterialsSearchRequest request) {
        List<MaterialsResponse> result = new ArrayList<>();

        // list materials
        List<MaterialsEntity> materialsEntityList = materialsService.lambdaQuery()
                .eq(MaterialsEntity::getAppId, request.getAppId())
                .eq(MaterialsEntity::getBizCode, request.getBizCode())
                .like(BeanUtil.isNotEmpty(request) && StrUtil.isNotBlank(request.getKeyword()), MaterialsEntity::getTitle, request.getKeyword())
                .list();
        if (materialsEntityList == null || materialsEntityList.isEmpty()) {
            return result;
        }

        // list attachmentResponse
        List<String> materialsIds = materialsEntityList.stream()
                .map(MaterialsEntity::getMaterialsId)
                .collect(Collectors.toList());
        Map<String, List<MaterialsAttachmentResponse>> attachmentMap = collectAttachmentResponse(materialsIds);
        return materialsEntityList.stream()
                .map(materialsEntity -> {
                    MaterialsResponse materialsResponse = BeanUtil.copyProperties(materialsEntity, MaterialsResponse.class);
                    String materialsId = materialsResponse.getMaterialsId();
                    List<MaterialsAttachmentResponse> att = attachmentMap.get(materialsId);
                    materialsResponse.setMaterialsAttachments(att);
                    return materialsResponse;
                })
                .collect(Collectors.toList());
    }

    /**
     * @param
     * @return
     * @说明: 根据ID获取详情
     * @关联表: Materials, MaterialsAttachment
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public MaterialsResponse getMaterials(String materialsId) {
        if (StrUtil.isBlank(materialsId)) {
            return new MaterialsResponse();
        }

        // get materials
        MaterialsEntity materialsEntity = getById(materialsId);
        if (BeanUtil.isEmpty(materialsEntity)) {
            return new MaterialsResponse();
        }
        MaterialsResponse result = BeanUtil.copyProperties(materialsEntity, MaterialsResponse.class);
        // list attachment of materials
        List<MaterialsAttachmentResponse> attachmentResponseList = listAttachmentResponses(List.of(materialsId));

        result.setMaterialsAttachments(attachmentResponseList);
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 启用
     * @关联表: Materials, MaterialsAttachment
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean enabledMaterials(String materialsId) {
        if (StrUtil.isBlank(materialsId)) {
            return Boolean.FALSE;
        }

        return changeEnabled(materialsId, MaterialsEnabledEnum.ENABLED);
    }

    /**
     * @param
     * @return
     * @说明: 禁用
     * @关联表: Materials, MaterialsAttachment
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean disabledMaterials(String materialsId) {
        if (StrUtil.isBlank(materialsId)) {
            return Boolean.FALSE;
        }

        return changeEnabled(materialsId, MaterialsEnabledEnum.DISABLED);
    }

    /**
     * Download string.
     *
     * @param materialsId the materials id
     * @return the string
     */
    public String download(String materialsId) {
        return batchDownload(CollUtil.newArrayList(materialsId));
    }

    /**
     * Batch download string.
     *
     * @param materialsIds the materials ids
     * @return the string
     */
    public String batchDownload(List<String> materialsIds) {
        List<MaterialsAttachmentEntity> attachments = listAttachmentEntity(materialsIds);
        Validator.validateNotEmpty(attachments, "资料不存在");

        String fileName = "我的资料";
        if (attachments.size() == 1) {
            MaterialsAttachmentEntity materialsAttachmentEntity = attachments.get(0);
            fileName = materialsAttachmentEntity.getFileName();
        }

        OssInfo oss = ossBiz.zip(attachments, fileName);
        return oss.getPath();
    }

    /**
     * @param
     * @return
     * @说明: 删除or批量删除
     * @关联表: Materials, MaterialsAttachment
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean delMaterials(List<String> materialsIds) {
        if (CollUtil.isEmpty(materialsIds)) {
            throw new BizException(MaterialsESCEnum.PARAMS_NON_NULL);
        }

        // remove materials
        LambdaQueryWrapper<MaterialsEntity> queryWrapper1 = new LambdaQueryWrapper<MaterialsEntity>()
                .in(MaterialsEntity::getMaterialsId, materialsIds);
        boolean remRes1 = materialsService.remove(queryWrapper1);

        // remove attachment
        LambdaQueryWrapper<MaterialsAttachmentEntity> queryWrapper2 = new LambdaQueryWrapper<MaterialsAttachmentEntity>()
                .in(MaterialsAttachmentEntity::getMaterialsId, materialsIds);
        boolean remRes2 = materialsAttachmentService.remove(queryWrapper2);

        return remRes1 && remRes2;
    }

    /**
     * @param
     * @return
     * @说明: 删除or批量删除附件
     * @关联表: MaterialsAttachment
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean delMaterialsAttachment(List<String> attachmentIds) {
        if (CollUtil.isEmpty(attachmentIds)) {
            throw new BizException(MaterialsESCEnum.PARAMS_NON_NULL);
        }

        // remove attachment
        LambdaQueryWrapper<MaterialsAttachmentEntity> queryWrapper2 = new LambdaQueryWrapper<MaterialsAttachmentEntity>()
                .in(MaterialsAttachmentEntity::getMaterialsAttachmentId, attachmentIds);
        return materialsAttachmentService.remove(queryWrapper2);
    }

    private MaterialsEntity convertRequest2Entity(MaterialsRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(MaterialsESCEnum.PARAMS_NON_NULL);
        }

        String accountId = request.getAccountId();
        String accessAuth = getAccessAuth(accountId);

        MaterialsEntity result = MaterialsEntity.builder()
                .appId(baseBiz.getAppId())
                .materialsId(request.getMaterialsId())
                .bizCode(request.getBizCode())
                .categoryId(request.getCategoryId())
                .title(request.getTitle())
                .descr(request.getDescr())
                .sequence(request.getSequence())
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .accessAuth(accessAuth)
                .build();

        String uniqueId = result.getMaterialsId();
        if (StrUtil.isBlank(uniqueId)) {
            result.setMaterialsId(baseBiz.getIdStr());
            result.setEnabled(MaterialsEnabledEnum.ENABLED.getCode());
        } else {
            MaterialsEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException(MaterialsESCEnum.DATA_NULL);
            }
            // check auth
            String oriAccountId = entity.getAccountId();
            String curAccountId = request.getAccountId();
            if (!Objects.equals(oriAccountId, curAccountId)) {
                if (!baseBiz.isAdministrator(curAccountId)) {
                    throw new BizException(MaterialsESCEnum.NO_AUTH);
                }
            }
            result.setId(entity.getId());
            // 更新不能改变创建者以及访问权限
            result.setAccountId(null);
            result.setAccountName(null);
            result.setAccessAuth(null);
        }

        return result;
    }

    private List<MaterialsAttachmentEntity> convertAttachmentRequest2Entity(List<MaterialsAttachmentRequest> requests, String materialsId) {
        if (Objects.isNull(requests) || requests.isEmpty() || StrUtil.isBlank(materialsId)) {
            throw new BizException(MaterialsESCEnum.PARAMS_NON_NULL);
        }

        List<MaterialsAttachmentEntity> attachmentList = listAttachmentEntity(List.of(materialsId));
        Map<String, MaterialsAttachmentEntity> attachmentCollect;
        if (Objects.nonNull(attachmentList) && !attachmentList.isEmpty()) {
            attachmentCollect = attachmentList.stream().collect(Collectors.toMap(MaterialsAttachmentEntity::getMaterialsAttachmentId, v -> v, (v1, v2) -> v1));
        } else {
            attachmentCollect = new HashMap<>();
        }

        List<MaterialsAttachmentEntity> resultList = new ArrayList<>();
        requests.forEach(request -> {
            MaterialsAttachmentEntity result = MaterialsAttachmentEntity.builder()
                    .appId(baseBiz.getAppId())
                    .materialsAttachmentId(request.getMaterialsAttachmentId())
                    .materialsId(materialsId)
                    .fileName(request.getFileName())
                    .fileUri(request.getFileUri())
                    .fileType(request.getFileType())
                    .sequence(request.getSequence())
                    .build();

            String uniqueId = result.getMaterialsAttachmentId();
            if (StrUtil.isBlank(uniqueId)) {
                result.setMaterialsAttachmentId(baseBiz.getIdStr());
            } else {
                MaterialsAttachmentEntity entity = attachmentCollect.get(uniqueId);
                if (BeanUtil.isEmpty(entity)) {
                    throw new BizException(MaterialsESCEnum.DATA_NULL);
                }
                result.setId(entity.getId());
            }

            resultList.add(result);
        });

        return resultList;
    }

    private MaterialsEntity getById(String materialsId) {
        LambdaQueryWrapper<MaterialsEntity> queryWrapper = new LambdaQueryWrapper<MaterialsEntity>()
                .eq(MaterialsEntity::getMaterialsId, materialsId);
        return materialsService.getOne(queryWrapper);
    }

    private List<MaterialsAttachmentEntity> listAttachmentEntity(List<String> materialsIds) {
        return materialsAttachmentService.lambdaQuery()
                .in(MaterialsAttachmentEntity::getMaterialsId, materialsIds)
                .list();
    }

    private List<MaterialsAttachmentResponse> listAttachmentResponses(List<String> materialsIds) {
        List<MaterialsAttachmentResponse> result = new ArrayList<>();
        if (materialsIds == null || materialsIds.isEmpty()) {
            return result;
        }

        List<MaterialsAttachmentEntity> attachmentEntityList = listAttachmentEntity(materialsIds);
        if (attachmentEntityList == null || attachmentEntityList.isEmpty()) {
            return result;
        }

        return attachmentEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, MaterialsAttachmentResponse.class))
                .collect(Collectors.toList());
    }

    private Map<String, List<MaterialsAttachmentResponse>> collectAttachmentResponse(List<String> materialsIds) {
        List<MaterialsAttachmentResponse> attachmentResponseList = listAttachmentResponses(materialsIds);

        return attachmentResponseList.stream().collect(Collectors.groupingBy(MaterialsAttachmentResponse::getMaterialsId));
    }

    private boolean changeEnabled(String materialsId, MaterialsEnabledEnum enable) {
        LambdaUpdateWrapper<MaterialsEntity> updateWrapper = new LambdaUpdateWrapper<MaterialsEntity>()
                .eq(MaterialsEntity::getMaterialsId, materialsId)
                .set(MaterialsEntity::getEnabled, enable.getCode());
        return materialsService.update(updateWrapper);
    }

    private void fillResult(Page<MaterialsPageResponse> result) {
        if (BeanUtil.isEmpty(result)) {
            throw new BizException(MaterialsESCEnum.DATA_NULL);
        }

        List<MaterialsPageResponse> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }

        for (MaterialsPageResponse record: records) {
            Date dt = record.getDt();
            String uploadTime = baseBiz.convertDate2String(dt);
            record.setUploadTime(uploadTime);
            AccountInstanceResponse personalInformation = personManageBiz.getPersonalInformation(record.getAccountId(), baseBiz.getAppId());
            String userName = Optional.ofNullable(personalInformation)
                    .map(AccountInstanceResponse::getUserName)
                    .orElse("");
            record.setUserName(userName);
            record.setAccountName(userName);
        }

    }

    private String getAccessAuth(String accountId) {
        if (baseBiz.isAdministrator(accountId)) {
            return MaterialsAccessAuthEnum.ACCESS_AUTH_PUBLIC.name();
        }
        return MaterialsAccessAuthEnum.ACCESS_AUTH_PRIVATE.name();
    }

}