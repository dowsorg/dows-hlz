package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.framework.crud.api.CrudEntity;
import org.dows.framework.crud.mybatis.MybatisCrudService;
import org.dows.hep.api.enums.EnumString;

/**
 * @author : wuzl
 * @date : 2023/5/2 9:22
 */
public abstract class BaseCategDao<S extends MybatisCrudService<E>,E extends CrudEntity> extends BaseDao<S,E>
    implements ICheckCategRef {

    protected BaseCategDao(String notExistsMessage) {
        super(notExistsMessage);
    }

    protected BaseCategDao(String notExistsMessage,String failSaveMessage){
        super(notExistsMessage,failSaveMessage);
    }

    protected abstract SFunction<E,String> getColCateg();

    /**
     * 检测类别引用
     * @param categId 类别id
     * @return
     */
    @Override
    public Boolean checkCategRef(String categId) {
        if(null==getColCateg()){
            return false;
        }
        return service.lambdaQuery()
                .eq(getColCateg(),categId)
                .last(EnumString.LIMIT_1.getStr())
                .count()>0;
    }
}
