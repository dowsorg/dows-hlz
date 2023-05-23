package org.dows.hep.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : wuzl
 * @date : 2023/5/16 10:20
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginContextVO implements Serializable {

    /**
     * 登录用户id
     */
    private String accountId="";

    /**
     * 登录用户名称
     */
    private String accountName="";


}
