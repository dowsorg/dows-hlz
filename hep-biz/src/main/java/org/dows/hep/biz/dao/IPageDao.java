package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * @author : wuzl
 * @date : 2023/5/4 11:56
 */
public interface IPageDao <E,R>{
    IPage<E> pageByCondition(R req, SFunction<E,?>... cols);
}
