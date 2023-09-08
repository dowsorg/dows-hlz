package org.dows.hep.config.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("User")
public class User {

    @Id// id生成策略
    private String id;
    private String name;
    private Integer age;
    private String email;
    private String createDate;
}