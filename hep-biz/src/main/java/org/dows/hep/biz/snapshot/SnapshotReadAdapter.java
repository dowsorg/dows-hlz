package org.dows.hep.biz.snapshot;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dows.hep.ExperimentCrudEntity;
import org.dows.hep.biz.util.ShareUtil;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author : wuzl
 * @date : 2023/7/2 16:29
 */
public class SnapshotReadAdapter<T ,ET extends T > extends LambdaQueryChainWrapper<T> {

    public SnapshotReadAdapter(EnumSnapshotType snapshotType, IService<T> srcSvc, IService<ET> snapSvc,ISnapshotLambdaConverter<ET> converter) {
        super(srcSvc.getBaseMapper(), srcSvc.getEntityClass());
        notSnapshot = !SnapshotRequestHolder.hasSnapshotRequest();
        if(!notSnapshot) {
            snapLambdaQuery = snapLambdaQuery(snapshotType, snapSvc,converter.convertFrom(ExperimentCrudEntity::getExperimentInstanceId));
        }
        this.converter=converter;
    }

    public static <T ,ET extends T > SnapshotReadAdapter<T,ET> create(EnumSnapshotType snapshotType, IService<T> srcSvc, IService<ET> snapSvc,ISnapshotLambdaConverter<ET> converter){
        return new SnapshotReadAdapter(snapshotType,srcSvc,snapSvc,converter);
    }
    protected final boolean notSnapshot;
    protected final ISnapshotLambdaConverter<ET> converter;

    protected LambdaQueryChainWrapper<ET> snapLambdaQuery=null;

    public static <T extends ExperimentCrudEntity> LambdaQueryChainWrapper<T> snapLambdaQuery(EnumSnapshotType snapshotType,IService<T> svc) {
        return snapLambdaQuery(snapshotType,svc, T::getExperimentInstanceId);
    }

    public static <T> LambdaQueryChainWrapper<T> snapLambdaQuery(EnumSnapshotType snapshotType,IService<T> svc, SFunction<T, ?> getExperimentIdFunc) {
        LambdaQueryChainWrapper<T> wrapper = svc.lambdaQuery();
        if (!SnapshotRequestHolder.hasSnapshotRequest()) {
            return wrapper;
        }
        return wrapper.eq(getExperimentIdFunc, SnapshotRequestHolder.getRefExperimentId(snapshotType));
    }

    //region execute

    @Override
    public List<T> list() {
        if (notSnapshot) {
            return super.list();
        }
        return ShareUtil.XCollection.map(snapLambdaQuery.list(), false, Function.identity());
    }

    @Override
    public T one() {
        if (notSnapshot) {
            return super.one();
        }
        return snapLambdaQuery.one();
    }

    @Override
    public Optional<T> oneOpt() {
        if (notSnapshot) {
            return super.oneOpt();
        }
        return Optional.ofNullable(snapLambdaQuery.oneOpt().orElse(null));
    }

    @Override
    public Long count() {
        if (notSnapshot) {
            return super.count();
        }
        return snapLambdaQuery.count();
    }

    @Override
    public boolean exists() {
        if (notSnapshot) {
            return super.exists();
        }
        return snapLambdaQuery.exists();
    }

    @Override
    public <E extends IPage<T>> E page(E page) {
        if (notSnapshot) {
            return super.page(page);
        }
        Page<ET> snapPage = Page.of(page.getCurrent(), page.getSize(), page.getTotal(), page.searchCount());
        snapPage.setOrders(page.orders());
        snapPage=snapLambdaQuery.page(snapPage);
        page.setRecords(ShareUtil.XCollection.map(snapPage.getRecords(),false, Function.identity()));
        return page;
    }


    public LambdaQueryChainWrapper<T> selectAs(SFunction<T, ?>... columns) {
        if(notSnapshot){
            return super.select(columns);
        }
        snapLambdaQuery=snapLambdaQuery.select(converter.convertFrom(columns));
        return this;
    }
    //endregion

    //region condition
    @Override
    public LambdaQueryChainWrapper<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot){
            return super.eq(condition,column,val);
        }
        snapLambdaQuery=snapLambdaQuery.eq(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> ne(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.ne(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.ne(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.gt(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.gt(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.ge(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.ge(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.lt(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.lt(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.le(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.le(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        if(notSnapshot) {
            return super.between(condition, column, val1, val2);
        }
        snapLambdaQuery=snapLambdaQuery.between(condition,converter.convertFrom(column),val1, val2);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        if(notSnapshot) {
            return super.notBetween(condition, column, val1, val2);
        }
        snapLambdaQuery=snapLambdaQuery.notBetween(condition,converter.convertFrom(column),val1, val2);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.like(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.like(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.likeLeft(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.likeLeft(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.likeRight(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.likeRight(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.notLike(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.notLike(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> notLikeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.notLikeLeft(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.notLikeLeft(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> notLikeRight(boolean condition, SFunction<T, ?> column, Object val) {
        if(notSnapshot) {
            return super.notLikeRight(condition, column, val);
        }
        snapLambdaQuery=snapLambdaQuery.notLikeRight(condition,converter.convertFrom(column),val);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> first(boolean condition, String firstSql) {
        if(notSnapshot) {
            return super.first(condition, firstSql);
        }
        snapLambdaQuery=snapLambdaQuery.first(condition,firstSql);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> last(boolean condition, String lastSql) {
        if(notSnapshot) {
            return super.last(condition, lastSql);
        }
        snapLambdaQuery=snapLambdaQuery.last(condition,lastSql);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> isNull(boolean condition, SFunction<T, ?> column) {
        if(notSnapshot) {
            return super.isNull(condition, column);
        }
        snapLambdaQuery=snapLambdaQuery.isNull(condition,converter.convertFrom(column));
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        if(notSnapshot) {
            return super.isNotNull(condition, column);
        }
        snapLambdaQuery=snapLambdaQuery.isNotNull(condition,converter.convertFrom(column));
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> in(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
        if(notSnapshot) {
            return super.in(condition, column, coll);
        }
        snapLambdaQuery=snapLambdaQuery.in(condition,converter.convertFrom(column),coll);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> in(boolean condition, SFunction<T, ?> column, Object... values) {
        if(notSnapshot) {
            return super.in(condition, column, values);
        }
        snapLambdaQuery=snapLambdaQuery.in(condition,converter.convertFrom(column),values);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> notIn(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
        if(notSnapshot) {
            return super.notIn(condition, column, coll);
        }
        snapLambdaQuery=snapLambdaQuery.notIn(condition,converter.convertFrom(column),coll);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> notIn(boolean condition, SFunction<T, ?> column, Object... values) {
        if(notSnapshot) {
            return super.notIn(condition, column, values);
        }
        snapLambdaQuery=snapLambdaQuery.notIn(condition,converter.convertFrom(column),values);
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> groupBy(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        if(notSnapshot) {
            return super.groupBy(condition, column, columns);
        }
        snapLambdaQuery=snapLambdaQuery.groupBy(condition,converter.convertFrom(column),converter.convertFrom(columns));
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> groupBy(boolean condition, SFunction<T, ?> column) {
        if(notSnapshot) {
            return super.groupBy(condition, column);
        }
        snapLambdaQuery=snapLambdaQuery.groupBy(condition,converter.convertFrom(column));
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> groupBy(boolean condition, List<SFunction<T, ?>> columns) {
        if(notSnapshot) {
            return super.groupBy(condition, columns);
        }
        snapLambdaQuery=snapLambdaQuery.groupBy(condition,converter.convertFrom(columns));
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        if(notSnapshot) {
            return super.orderBy(condition, isAsc, column, columns);
        }
        snapLambdaQuery=snapLambdaQuery.orderBy(condition, isAsc,converter.convertFrom(column),converter.convertFrom(columns));
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column) {
        if(notSnapshot) {
            return super.orderBy(condition, isAsc, column);
        }
        snapLambdaQuery=snapLambdaQuery.orderBy(condition, isAsc,converter.convertFrom(column));
        return this;
    }

    @Override
    public LambdaQueryChainWrapper<T> orderBy(boolean condition, boolean isAsc, List<SFunction<T, ?>> columns) {
        if(notSnapshot) {
            return super.orderBy(condition, isAsc, columns);
        }
        snapLambdaQuery=snapLambdaQuery.orderBy(condition, isAsc,converter.convertFrom(columns));
        return this;
    }

    //endregion
}
