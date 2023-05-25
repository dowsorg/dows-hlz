package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.EventCategEntity;
import org.dows.hep.service.EventCategService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/4/21 11:37
 */
@Component
public class EventCategDao extends BaseDao<EventCategService, EventCategEntity> {

    public EventCategDao(){
        super("类别不存在或已删除，请刷新");
    }




    @Override
    protected SFunction<EventCategEntity, String> getColId() {
        return EventCategEntity::getEventCategId;
    }

    @Override
    protected SFunction<String, ?> setColId(EventCategEntity item) {
        return item::setEventCategId;
    }

    @Override
    protected SFunction<EventCategEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(EventCategEntity item) {
        return null;
    }

    @Override
    protected SFunction<EventCategEntity, Integer> getColSeq() {
        return EventCategEntity::getSeq;
    }

    @Override
    protected SFunction<Integer, ?> setColSeq(EventCategEntity item) {
        return item::setSeq;
    }

    public List<EventCategEntity> getAll(){
        return service.lambdaQuery()
                .orderByAsc(EventCategEntity::getFamily,EventCategEntity::getCategPid,
                        EventCategEntity::getSeq,EventCategEntity::getId)
                .list();
    }
}
