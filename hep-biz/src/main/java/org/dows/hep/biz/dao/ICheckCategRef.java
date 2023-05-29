package org.dows.hep.biz.dao;

/**
 * @author : wuzl
 * @date : 2023/5/11 9:18
 */
public interface ICheckCategRef {
    /**
     * 判断是否引用到类别
     * @param categId
     * @return
     */
    Boolean checkCategRef(String categId);
}
