package org.dows.hep.biz.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.constant.RsPageConstant;

import java.util.Objects;

/**
 * @author runsix
 */
public class RsPageUtil {
  public static Page getRsPage(Long pageNo, Long pageSize, String order, Boolean asc) {
    Page page = new Page<>(pageNo, pageSize);
    if (StringUtils.isBlank(order)) {
      order = RsPageConstant.ORDER;
    } else {
      order = order.trim();
    }
    if (Objects.isNull(asc)) {
      asc = Boolean.valueOf(RsPageConstant.ASC);
    }
    OrderItem orderItem = new OrderItem(order, asc);
    page.addOrder(orderItem);
    return page;
  }

  public static Page convertFromAnother(Page page) {
    Page page1 = new Page();
    page1.setTotal(page.getTotal());
    page1.setSize(page.getSize());
    page1.setCurrent(page.getCurrent());
    page1.setOrders(page.getOrders());
    page1.setOptimizeCountSql(page.optimizeJoinOfCountSql());
    page1.setSearchCount(page.searchCount());
    page1.setOptimizeJoinOfCountSql(page.optimizeJoinOfCountSql());
    page1.setMaxLimit(page.maxLimit());
    page1.setCountId(page.countId());
    return page1;
  }
}
