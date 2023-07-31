package org.dows.hep.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description 分页Base类
 * @date 2023/6/28 16:17
 **/
@Data
@NoArgsConstructor
@Schema(name = "BasePageRequest 对象", title = "分页Base类")
public class BasePageRequest {
    // default-constant
    @JsonIgnore
    private final Long DEFAULT_PAGE_NO = 1L;

    @JsonIgnore
    private final Long DEFAULT_PAGE_SIZE = 10L;

    @JsonIgnore
    private final String DEFAULT_ORDER = "dt";

    @JsonIgnore
    private final boolean DEFAULT_ASC = Boolean.FALSE;

    @Schema(title = "pageNo")
    @NotNull
    private Long pageNo;

    @Schema(title = "pageSize")
    @NotNull
    private Long pageSize;

    @Schema(title = "排序字段")
    private String order;

    @Schema(title = "是否升序")
    private Boolean asc;

    public <T> Page<T> getPage() {
        Long pageNo = this.pageNo == null ? DEFAULT_PAGE_NO : this.pageNo;
        Long pageSize = this.pageSize == null ? DEFAULT_PAGE_SIZE : this.pageSize;
        String order = StrUtil.isBlank(this.order) ? DEFAULT_ORDER : this.order;
        boolean asc = this.asc == null ? DEFAULT_ASC : this.asc;

        Page<T> page = new Page<>(pageNo, pageSize);
        OrderItem orderItem = new OrderItem(order, asc);
        page.addOrder(orderItem);
        return page;
    }
}
