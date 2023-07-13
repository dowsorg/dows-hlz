package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jx
 * @date 2023/7/13 9:51
 */
@Data
@NoArgsConstructor
@Schema(name = "EchartsDataResonse 对象", title = "图表数据")
public class EchartsDataResonse {
    private static final long serialVersionUID = -3972286994024019236L;
    private String id;          //标题id
    private String name;        //标题名
    private List<Object> dataList; //数据
    private Long count;    //数量
    private String per;       //百分比

    public EchartsDataResonse(String name, List<Object> dataList) {
        this.name = name;
        this.dataList = dataList;
    }

    public EchartsDataResonse(String name, Long count, String per) {
        this.name = name;
        this.count = count;
        this.per = per;
    }

    public EchartsDataResonse(String name, String per) {
        this.name = name;
        this.per = per;
    }

    public EchartsDataResonse(String name, Long count) {
        this.name = name;
        this.count = count;
    }
}
