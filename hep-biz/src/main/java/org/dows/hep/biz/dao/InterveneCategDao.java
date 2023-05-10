package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.InterveneCategoryEntity;
import org.dows.hep.service.InterveneCategoryService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/4/21 11:37
 */
@Component
public class InterveneCategDao extends BaseDao<InterveneCategoryService,InterveneCategoryEntity> {

    public InterveneCategDao(){
        super("类别不存在或已删除，请刷新");
    }




    @Override
    protected SFunction<InterveneCategoryEntity, String> getColId() {
        return InterveneCategoryEntity::getInterveneCategoryId;
    }

    @Override
    protected SFunction<String, ?> setColId(InterveneCategoryEntity item) {
        return item::setInterveneCategoryId;
    }

    @Override
    protected SFunction<InterveneCategoryEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(InterveneCategoryEntity item) {
        return null;
    }


    public List<InterveneCategoryEntity> getAll(){
        return service.lambdaQuery()
                .orderByAsc(InterveneCategoryEntity::getFamily,InterveneCategoryEntity::getCategPid,
                        InterveneCategoryEntity::getSeq,InterveneCategoryEntity::getId)
                .list();
    }
}
