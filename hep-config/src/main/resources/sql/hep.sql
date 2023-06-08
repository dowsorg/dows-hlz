
-- 若库不存在创建一个
CREATE DATABASE IF NOT EXISTS `dows_hep`;
USE `dows_hep`;

drop table if exists `experiment_instance`;
CREATE TABLE IF NOT EXISTS `experiment_instance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_name` varchar(64) DEFAULT NULL COMMENT '案例名称[社区名]',
    `experiment_name` varchar(64) DEFAULT NULL COMMENT '实验名称',
    `experiment_descr` varchar(64) DEFAULT NULL COMMENT '实验说明',
    `model` integer(2) DEFAULT NULL COMMENT '实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]',
    `state` integer(2) DEFAULT NULL COMMENT '实验状态[默认未开始状态0~6步]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_instance_id` (`experiment_instance_id`)
    ) ENGINE=InnoDB COMMENT='实验实列';

drop table if exists `experiment_setting`;
CREATE TABLE IF NOT EXISTS `experiment_setting`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_setting_id` varchar(64) DEFAULT NULL COMMENT 'experimentSettingId',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `config_key` varchar(64) DEFAULT NULL COMMENT '配置key[标准模式，沙盘模式，方案设计...]',
    `config_json_vals` varchar(64) DEFAULT NULL COMMENT 'key对应的json配置',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_setting_id` (`experiment_setting_id`)
    ) ENGINE=InnoDB COMMENT='实验设置';

drop table if exists `experiment_group`;
CREATE TABLE IF NOT EXISTS `experiment_group`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `group_no` varchar(64) DEFAULT NULL COMMENT '小组序号',
    `group_name` varchar(64) DEFAULT NULL COMMENT '组名',
    `group_alias` varchar(64) DEFAULT NULL COMMENT '小组别名',
    `member_count` integer(2) DEFAULT NULL COMMENT '成员数量',
    `state` tinyint(4) DEFAULT NULL COMMENT '实验状态[默认未开始状态0~6步]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_group_id` (`experiment_group_id`)
    ) ENGINE=InnoDB COMMENT='实验小组';

drop table if exists `experiment_participator`;
CREATE TABLE IF NOT EXISTS `experiment_participator`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_participator_id` varchar(64) DEFAULT NULL COMMENT '实验组员ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `account_id` varchar(64) DEFAULT NULL COMMENT '组员账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '组员账号名',
    `group_alias` varchar(64) DEFAULT NULL COMMENT '小组别名',
    `group_no` integer(2) DEFAULT NULL COMMENT '组序号',
    `participator_type` integer(2) DEFAULT NULL COMMENT '参与者类型[0:教师，1:组长，2：学生]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_participator_id` (`experiment_participator_id`)
    ) ENGINE=InnoDB COMMENT='实验组员（参与者）';

drop table if exists `experiment_actor`;
CREATE TABLE IF NOT EXISTS `experiment_actor`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_actor_id` varchar(64) DEFAULT NULL COMMENT '实验扮演者ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `actor_id` varchar(64) DEFAULT NULL COMMENT '扮演关联ID[分析角色|机构角色]',
    `actor_name` varchar(64) DEFAULT NULL COMMENT '分配角色[分析角色名|机构角色名]',
    `actor_type` varchar(64) DEFAULT NULL COMMENT '扮演类型[0:问题，1:机构]',
    `group_alias` varchar(64) DEFAULT NULL COMMENT '小组别名',
    `account_id` varchar(64) DEFAULT NULL COMMENT '组员账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '组员账号名',
    `participator_type` integer(2) DEFAULT NULL COMMENT '参与者类型[0:教师，1:学生，2:组长]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_actor_id` (`experiment_actor_id`)
    ) ENGINE=InnoDB COMMENT='实验扮演者';

drop table if exists `experiment_scheme`;
CREATE TABLE IF NOT EXISTS `experiment_scheme`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_design_id` varchar(64) DEFAULT NULL COMMENT '实验方案设计ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `scheme_url` varchar(64) DEFAULT NULL COMMENT '方案地址',
    `state` tinyint(4) DEFAULT NULL COMMENT '方案状态[0:未提交,1:已提交]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_design_id` (`experiment_design_id`)
    ) ENGINE=InnoDB COMMENT='实验方案';

drop table if exists `experiment_timer`;
CREATE TABLE IF NOT EXISTS `experiment_timer`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_timer_id` varchar(64) DEFAULT NULL COMMENT '实验定时器ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `duration` bigint(19) DEFAULT NULL COMMENT '暂停时长[暂停结束时间-暂停起始时间]',
    `start_time` bigint(19) DEFAULT NULL COMMENT '实验开始时间',
    `end_time` bigint(19) DEFAULT NULL COMMENT '实验结束时间[如果有暂停，需加暂停时长]',
    `periods` integer(2) DEFAULT NULL COMMENT '期数[根据期数生成对应的计时记录]',
    `model` integer(2) DEFAULT NULL COMMENT '实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]',
    `pause_count` integer(2) DEFAULT NULL COMMENT '暂停次数[每次暂停++]',
    `state` integer(2) DEFAULT NULL COMMENT '状态[0:未开始，1:进行中，2:已结束]',
    `paused` tinyint(4) DEFAULT NULL COMMENT '是否暂停',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_timer_id` (`experiment_timer_id`)
    ) ENGINE=InnoDB COMMENT='实验计数计时器';

drop table if exists `experiment_report_schema`;
CREATE TABLE IF NOT EXISTS `experiment_report_schema`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_report_schema_id` varchar(64) DEFAULT NULL COMMENT '实验报告元数据ID',
    `report_name` varchar(64) DEFAULT NULL COMMENT '报表名称',
    `report_code` varchar(64) DEFAULT NULL COMMENT '报表code',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_report_schema_id` (`experiment_report_schema_id`)
    ) ENGINE=InnoDB COMMENT='实验报告元数据';

drop table if exists `experiment_report_item`;
CREATE TABLE IF NOT EXISTS `experiment_report_item`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_case_report_id` varchar(64) DEFAULT NULL COMMENT '实验案例报告ID',
    `field_name` varchar(64) DEFAULT NULL COMMENT '字段名称',
    `field_code` varchar(64) DEFAULT NULL COMMENT '字段code',
    `field_unit` varchar(64) DEFAULT NULL COMMENT '字段单位',
    `field_type` varchar(64) DEFAULT NULL COMMENT '字段的数据类型',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_case_report_id` (`experiment_case_report_id`)
    ) ENGINE=InnoDB COMMENT='报告项';

drop table if exists `experiment_case_report`;
CREATE TABLE IF NOT EXISTS `experiment_case_report`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_case_report_id` varchar(64) DEFAULT NULL COMMENT '实验案例报告ID',
    `account_id` varchar(64) DEFAULT NULL COMMENT '账号ID',
    `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
    `sex` varchar(64) DEFAULT NULL COMMENT '性别',
    `age` varchar(64) DEFAULT NULL COMMENT '年龄',
    `report_no` varchar(64) DEFAULT NULL COMMENT '报告编号',
    `report_time` datetime DEFAULT NULL COMMENT '报告时间',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_case_report_id` (`experiment_case_report_id`)
    ) ENGINE=InnoDB COMMENT='实验案例报告';

drop table if exists `experiment_grade_report`;
CREATE TABLE IF NOT EXISTS `experiment_grade_report`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_grade_report_id` varchar(64) DEFAULT NULL COMMENT '实验成绩报告ID',
    `experiment_case_report_id` varchar(64) DEFAULT NULL COMMENT '实验案例报告ID',
    `system_score` varchar(64) DEFAULT NULL COMMENT '系统评分',
    `teacher_score` varchar(64) DEFAULT NULL COMMENT '教师评分',
    `assess` varchar(64) DEFAULT NULL COMMENT '评价',
    `account_name` varchar(64) DEFAULT NULL COMMENT '账号名',
    `account_id` varchar(64) DEFAULT NULL COMMENT '账号ID',
    `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_grade_report_id` (`experiment_grade_report_id`)
    ) ENGINE=InnoDB COMMENT='实验成绩报告';

drop table if exists `experiment_periods_question`;
CREATE TABLE IF NOT EXISTS `experiment_periods_question`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_periods_question_id` varchar(64) DEFAULT NULL COMMENT '实验期数答题ID',
    `experiment_id` varchar(64) DEFAULT NULL COMMENT '实验ID',
    `periods` varchar(64) DEFAULT NULL COMMENT '实验期数|答题位置',
    `question_count` varchar(64) DEFAULT NULL COMMENT '题目数',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '试卷ID',
    `descr` varchar(64) DEFAULT NULL COMMENT '题型描述',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_periods_question_id` (`experiment_periods_question_id`)
    ) ENGINE=InnoDB COMMENT='实验期数答题';

drop table if exists `experiment_question_item`;
CREATE TABLE IF NOT EXISTS `experiment_question_item`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_question_item_id` varchar(64) DEFAULT NULL COMMENT '实验答题项目',
    `experiment_periods_question_id` varchar(64) DEFAULT NULL COMMENT '实验期数答题ID',
    `experiment_id` varchar(64) DEFAULT NULL COMMENT '实验ID',
    `question_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `periods` varchar(64) DEFAULT NULL COMMENT '实验期数|答题位置',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB COMMENT='实验答题项目';

drop table if exists `experiment_person`;
CREATE TABLE IF NOT EXISTS `experiment_person`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物id',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实例ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `case_org_name` varchar(64) DEFAULT NULL COMMENT '案例机构名称',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '账号ID',
    `case_account_name` varchar(64) DEFAULT NULL COMMENT '账号名称',
    `case_org_id_last` varchar(64) DEFAULT NULL COMMENT '上个案例机构id',
    `case_org_name_last` varchar(64) DEFAULT NULL COMMENT '上个案例机构名称',
    `flow_state` tinyint(4) DEFAULT NULL COMMENT '挂号状态 0-未挂号 1-已挂号',
    `insurance_state` tinyint(4) DEFAULT NULL COMMENT '保险状态 0-未购买 1-已购买',
    `asset` decimal(11,2) DEFAULT NULL COMMENT '剩余资金',
    `asset_init` decimal(11,2) DEFAULT NULL COMMENT '初始资金',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_person_id` (`experiment_person_id`)
    ) ENGINE=InnoDB COMMENT='实验机构人物';

drop table if exists `experiment_person_cost`;
CREATE TABLE IF NOT EXISTS `experiment_person_cost`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_person_cost_id` varchar(64) DEFAULT NULL COMMENT '实验花费ID',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物id',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '账号ID',
    `asset_name` varchar(64) DEFAULT NULL COMMENT '资产名称[医保,商保,养老]',
    `asset_amount` double(11,2) DEFAULT NULL COMMENT '资产额度[]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_person_cost_id` (`experiment_person_cost_id`)
    ) ENGINE=InnoDB COMMENT='实验人物资产花费';

drop table if exists `experiment_org_notice`;
CREATE TABLE IF NOT EXISTS `experiment_org_notice`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_org_notice_id` varchar(64) DEFAULT NULL COMMENT '机构通知id',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实例id',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组id',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物id',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '案例账号ID',
    `case_account_name` varchar(64) DEFAULT NULL COMMENT '账号名称',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `notice_type` tinyint(4) DEFAULT NULL COMMENT '通知类型 1-人物转移 2-检测随访 3-突发事件',
    `notice_src_id` varchar(64) DEFAULT NULL COMMENT '通知来源id，转移，随访操作id，事件id',
    `content` varchar(512) DEFAULT NULL COMMENT '通知内容',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_org_notice_id` (`experiment_org_notice_id`)
    ) ENGINE=InnoDB COMMENT='实验机构通知';

drop table if exists `operate_transfers`;
CREATE TABLE IF NOT EXISTS `operate_transfers`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_transfers_id` varchar(64) DEFAULT NULL COMMENT '操作机构转入转出记录ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物id',
    `form_org_id` varchar(64) DEFAULT NULL COMMENT '转出机构ID',
    `form_org_name` varchar(64) DEFAULT NULL COMMENT '转出机构名称',
    `to_org_id` varchar(64) DEFAULT NULL COMMENT '转入机构ID',
    `to_org_name` varchar(64) DEFAULT NULL COMMENT '转入机构名称',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '案例人物ID',
    `case_account_name` varchar(64) DEFAULT NULL COMMENT '案例人物名',
    `operate_account_id` varchar(64) DEFAULT NULL COMMENT '操作人员ID',
    `operate_account_name` varchar(64) DEFAULT NULL COMMENT '操作人员名称',
    `descr` varchar(64) DEFAULT NULL COMMENT '转入说明',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_transfers_id` (`operate_transfers_id`)
    ) ENGINE=InnoDB COMMENT='操作机构转入转出记录';

drop table if exists `operate_event`;
CREATE TABLE IF NOT EXISTS `operate_event`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_event_id` varchar(64) DEFAULT NULL COMMENT '操作事件ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物id',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `event_name` varchar(64) DEFAULT NULL COMMENT '事件名称',
    `event_time` datetime DEFAULT NULL COMMENT '事件时间',
    `account_id` varchar(64) DEFAULT NULL COMMENT '操作账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '操作人员名称',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_event_id` (`operate_event_id`)
    ) ENGINE=InnoDB COMMENT='操作事件记录';

drop table if exists `operate_exam`;
CREATE TABLE IF NOT EXISTS `operate_exam`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_exam_id` varchar(64) DEFAULT NULL COMMENT '操作考试[题目]记录ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `question_section_result_id` varchar(64) DEFAULT NULL COMMENT '答题记录ID',
    `question_title` varchar(64) DEFAULT NULL COMMENT '问题title',
    `account_id` varchar(64) DEFAULT NULL COMMENT '操作账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '操作人员名称',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_exam_id` (`operate_exam_id`)
    ) ENGINE=InnoDB COMMENT='操作考试[题目]记录';

drop table if exists `operate_result`;
CREATE TABLE IF NOT EXISTS `operate_result`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_result_id` varchar(64) DEFAULT NULL COMMENT '操作结果ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `account_id` varchar(64) DEFAULT NULL COMMENT '操作账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '操作人员名称',
    `operate_id` varchar(64) DEFAULT NULL COMMENT '操作ID',
    `operate_type` varchar(64) DEFAULT NULL COMMENT '操作类型[答题,考试,事件...]',
    `operate_result` varchar(64) DEFAULT NULL COMMENT '操作结果[答案]',
    `score` varchar(64) DEFAULT NULL COMMENT '分数',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_result_id` (`operate_result_id`)
    ) ENGINE=InnoDB COMMENT='操作结果';

drop table if exists `operate_indicator`;
CREATE TABLE IF NOT EXISTS `operate_indicator`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_indicator_id` varchar(64) DEFAULT NULL COMMENT '学生操作指标记录表ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物ID',
    `experiment_org_id` varchar(64) DEFAULT NULL COMMENT '实验机构ID',
    `operate_account_id` varchar(64) DEFAULT NULL COMMENT '操作人ID',
    `operate_account_name` varchar(64) DEFAULT NULL COMMENT '操作人名',
    `operate_type` integer(2) DEFAULT NULL COMMENT '操作[干预]类型',
    `operate_source_id` varchar(64) DEFAULT NULL COMMENT '干预或事件处理id',
    `indactor_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `indactor_name` varchar(64) DEFAULT NULL COMMENT '指标名称',
    `indactor_code` varchar(64) DEFAULT NULL COMMENT '指标',
    `indactor_org_val` varchar(64) DEFAULT NULL COMMENT '记录原值',
    `indactor_inc_val` varchar(64) DEFAULT NULL COMMENT '记录变化值',
    `indactor_val` varchar(64) DEFAULT NULL COMMENT '记录最终值',
    `indactor_unit` varchar(64) DEFAULT NULL COMMENT '指标单位',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `game_day` integer(2) DEFAULT NULL COMMENT '游戏内天数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_indicator_id` (`operate_indicator_id`)
    ) ENGINE=InnoDB COMMENT='学生操作指标记录表';

drop table if exists `operate_flow`;
CREATE TABLE IF NOT EXISTS `operate_flow`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_flow_id` varchar(64) DEFAULT NULL COMMENT '实验操作流程id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实例id',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组id',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物id',
    `experiment_org_id` varchar(64) DEFAULT NULL COMMENT '实验机构ID',
    `operate_account_id` varchar(64) DEFAULT NULL COMMENT '操作人ID',
    `operate_account_name` varchar(64) DEFAULT NULL COMMENT '操作人名',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `flow_name` varchar(64) DEFAULT NULL COMMENT '流程名称',
    `flow_sequence` varchar(64) DEFAULT NULL COMMENT '流程顺序',
    `report_flag` integer(2) DEFAULT NULL COMMENT '展示类型 0-不展示 1-用户端展示',
    `report_label` varchar(64) DEFAULT NULL COMMENT '标签',
    `report_descr` varchar(64) DEFAULT NULL COMMENT '操作描述',
    `asset` decimal(11,2) DEFAULT NULL COMMENT '剩余资金',
    `fee` decimal(11,2) DEFAULT NULL COMMENT '消耗资金',
    `refund` decimal(11,2) DEFAULT NULL COMMENT '报销资金',
    `score` varchar(64) DEFAULT NULL COMMENT '操作得分',
    `total_steps` integer(2) DEFAULT NULL COMMENT '总操作数',
    `done_steps` integer(2) DEFAULT NULL COMMENT '完成操作数',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `operate_org_func_id` varchar(64) DEFAULT NULL COMMENT '机构操作id',
    `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
    `operate_game_day` integer(2) DEFAULT NULL COMMENT '操作所在游戏内天数',
    `state` integer(2) DEFAULT NULL COMMENT '状态',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_flow_id` (`operate_flow_id`)
    ) ENGINE=InnoDB COMMENT='实验操作流程';

drop table if exists `operate_flow_snap`;
CREATE TABLE IF NOT EXISTS `operate_flow_snap`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_flow_snap_id` varchar(64) DEFAULT NULL COMMENT '实验操作流程快照id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `operate_flow_id` varchar(64) DEFAULT NULL COMMENT '实验操作流程id',
    `snap_time` datetime DEFAULT NULL COMMENT '快照时间',
    `record_json` text(65535) DEFAULT NULL COMMENT '操作记录',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_flow_snap_id` (`operate_flow_snap_id`)
    ) ENGINE=InnoDB COMMENT='实验操作流程快照';

drop table if exists `operate_org_func`;
CREATE TABLE IF NOT EXISTS `operate_org_func`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `operate_org_func_id` varchar(64) DEFAULT NULL COMMENT '机构操作id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `operate_flow_id` varchar(64) DEFAULT NULL COMMENT '实验操作流程id',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实例id',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组id',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物id',
    `experiment_org_id` varchar(64) DEFAULT NULL COMMENT '实验机构ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '功能点类别ID',
    `indicator_func_id` varchar(64) DEFAULT NULL COMMENT '指标功能点id',
    `operate_account_id` varchar(64) DEFAULT NULL COMMENT '操作人id',
    `operate_account_name` varchar(64) DEFAULT NULL COMMENT '操作人名',
    `operate_type` integer(2) DEFAULT NULL COMMENT '功能类型  1-基本信息 2-设置随访  3-开始随访 4-一般检查 11-健康问题 12-健康指导 13-疾病问题 14-健管目标 21-饮食干预 22-运动干预  23-自定义干预',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `report_flag` integer(2) DEFAULT NULL COMMENT '展示类型 0-不展示 1-用户端展示',
    `report_label` varchar(64) DEFAULT NULL COMMENT '标签',
    `report_descr` varchar(64) DEFAULT NULL COMMENT '操作描述',
    `asset` decimal(11,2) DEFAULT NULL COMMENT '剩余资金',
    `fee` decimal(11,2) DEFAULT NULL COMMENT '消耗资金',
    `refund` decimal(11,2) DEFAULT NULL COMMENT '报销资金',
    `score` varchar(64) DEFAULT NULL COMMENT '操作得分',
    `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
    `operate_game_day` integer(2) DEFAULT NULL COMMENT '操作所在游戏内天数',
    `deal_time` datetime DEFAULT NULL COMMENT '结算处理时间',
    `deal_game_day` integer(2) DEFAULT NULL COMMENT '结算所在游戏内天数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_org_func_id` (`operate_org_func_id`)
    ) ENGINE=InnoDB COMMENT='学生机构操作记录';

drop table if exists `operate_org_func_snap`;
CREATE TABLE IF NOT EXISTS `operate_org_func_snap`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_org_func_snap_id` varchar(64) DEFAULT NULL COMMENT '实验操作流程快照id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `operate_org_func_id` varchar(64) DEFAULT NULL COMMENT '机构操作id',
    `snap_time` datetime DEFAULT NULL COMMENT '快照时间',
    `input_json` text(65535) DEFAULT NULL COMMENT '输入记录',
    `result_json` text(65535) DEFAULT NULL COMMENT '结果记录',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_org_func_snap_id` (`operate_org_func_snap_id`)
    ) ENGINE=InnoDB COMMENT='学生机构操作快照';

drop table if exists `operate_followup_timer`;
CREATE TABLE IF NOT EXISTS `operate_followup_timer`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_followup_timer_id` varchar(64) DEFAULT NULL COMMENT '随访操作计时器id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实例id',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组id',
    `experiment_person_id` varchar(64) DEFAULT NULL COMMENT '实验人物id',
    `experiment_org_id` varchar(64) DEFAULT NULL COMMENT '实验机构ID',
    `indicator_func_id` varchar(64) DEFAULT NULL COMMENT '指标功能点id',
    `operate_account_id` varchar(64) DEFAULT NULL COMMENT '操作人id',
    `operate_account_name` varchar(64) DEFAULT NULL COMMENT '操作人名',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `indicator_view_monitor_followup_id` varchar(64) DEFAULT NULL COMMENT '随访表id',
    `indicator_followup_name` varchar(64) DEFAULT NULL COMMENT '随访表名称',
    `set_at_day` integer(2) DEFAULT NULL COMMENT '游戏内起始天数',
    `due_days` integer(2) DEFAULT NULL COMMENT '随访间隔天数',
    `todo_day` integer(2) DEFAULT NULL COMMENT '可以随访时间',
    `done_day` integer(2) DEFAULT NULL COMMENT '上次随访时间',
    `set_at_time` datetime DEFAULT NULL COMMENT '最近保存时间',
    `followup_time` datetime DEFAULT NULL COMMENT '最近随访时间',
    `followup_times` integer(2) DEFAULT NULL COMMENT '本期随访次数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_followup_timer_id` (`operate_followup_timer_id`)
    ) ENGINE=InnoDB COMMENT='学生随访操作计时器';

drop table if exists `case_category`;
CREATE TABLE IF NOT EXISTS `case_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `pid` bigint(19) DEFAULT NULL COMMENT '父ID',
    `case_categ_pid` varchar(64) DEFAULT NULL COMMENT '类别父id',
    `case_categ_id` varchar(64) DEFAULT NULL COMMENT '类别ID',
    `case_categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `case_categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `case_categ_name_path` varchar(512) DEFAULT NULL COMMENT '类别name路径',
    `case_categ_group` varchar(64) DEFAULT NULL COMMENT '类别组',
    `sequence` integer(2) DEFAULT NULL COMMENT '序列号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_categ_pid` (`case_categ_pid`)
    ) ENGINE=InnoDB COMMENT='案例类目';

drop table if exists `case_instance`;
CREATE TABLE IF NOT EXISTS `case_instance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_name` varchar(64) DEFAULT NULL COMMENT '案例名称',
    `case_pic` varchar(64) DEFAULT NULL COMMENT '案例图片',
    `case_type` varchar(64) DEFAULT NULL COMMENT '案例类型',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号Id',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者姓名',
    `descr` text(65535) DEFAULT NULL COMMENT '背景描述',
    `guide` text(65535) DEFAULT NULL COMMENT '指导描述',
    `state` integer(2) DEFAULT NULL COMMENT '案例状态[0:未发布|1:发布]',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例唯一标示',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_instance_id` (`case_instance_id`)
    ) ENGINE=InnoDB COMMENT='案例实例';

drop table if exists `case_org_questionnaire`;
CREATE TABLE IF NOT EXISTS `case_org_questionnaire`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_org_questionnaire` varchar(64) DEFAULT NULL COMMENT '案例机构问卷ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `case_questionnaire_id` varchar(64) DEFAULT NULL COMMENT '案例问卷ID',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_org_questionnaire` (`case_org_questionnaire`)
    ) ENGINE=InnoDB COMMENT='案例机构问卷';

drop table if exists `case_notice`;
CREATE TABLE IF NOT EXISTS `case_notice`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_notice_id` varchar(64) DEFAULT NULL COMMENT '案例通知ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `notice_name` varchar(64) DEFAULT NULL COMMENT '公告名称',
    `notice_content` varchar(64) DEFAULT NULL COMMENT '公告内容',
    `periods` varchar(64) DEFAULT NULL COMMENT '期数',
    `period_sequence` tinyint(4) DEFAULT NULL COMMENT '期数排序',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_notice_id` (`case_notice_id`)
    ) ENGINE=InnoDB COMMENT='案例公告';

drop table if exists `case_scheme`;
CREATE TABLE IF NOT EXISTS `case_scheme`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_scheme_id` varchar(64) DEFAULT NULL COMMENT '方案ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_categ_id` varchar(64) DEFAULT NULL COMMENT '类别ID',
    `case_categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `case_categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `case_categ_name_path` varchar(512) DEFAULT NULL COMMENT '类别name路径',
    `scheme_name` varchar(64) DEFAULT NULL COMMENT '方案名称',
    `tips` varchar(64) DEFAULT NULL COMMENT '方案提示',
    `scheme_descr` varchar(64) DEFAULT NULL COMMENT '方案说明',
    `contains_video` tinyint(4) DEFAULT NULL COMMENT '是否包含视频[0-否|1-是]',
    `enabled` tinyint(4) DEFAULT NULL COMMENT '状态[0-关闭|1-开启]',
    `source` varchar(64) DEFAULT NULL COMMENT '来源[admin|tenant]',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者Name',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_count` integer(2) DEFAULT NULL COMMENT '题数',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_scheme_id` (`case_scheme_id`)
    ) ENGINE=InnoDB COMMENT='案例方案';

drop table if exists `case_scheme_result`;
CREATE TABLE IF NOT EXISTS `case_scheme_result`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_scheme_result_id` varchar(64) DEFAULT NULL COMMENT '案例方案结果ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_scheme_id` varchar(64) DEFAULT NULL COMMENT '方案ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_section_result_id` varchar(64) DEFAULT NULL COMMENT '答题记录ID',
    `account_id` varchar(64) DEFAULT NULL COMMENT '答题者账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '答题者Name',
    `question_instance_ids` text(65535) DEFAULT NULL COMMENT '问题ids[1,2]',
    `status` tinyint(4) DEFAULT NULL COMMENT '状态[0-未开始|1-进行中|2-已完成]',
    `duration` integer(2) DEFAULT NULL COMMENT '持续时间[min]',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_scheme_result_id` (`case_scheme_result_id`)
    ) ENGINE=InnoDB COMMENT='案例方案结果';

drop table if exists `case_questionnaire`;
CREATE TABLE IF NOT EXISTS `case_questionnaire`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_questionnaire_id` varchar(64) DEFAULT NULL COMMENT '案例问卷ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `periods` varchar(64) DEFAULT NULL COMMENT '期数',
    `period_sequence` tinyint(4) DEFAULT NULL COMMENT '期数排序',
    `allot_mode` varchar(64) DEFAULT NULL COMMENT '分配方式',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_count` integer(2) DEFAULT NULL COMMENT '题数',
    `question_section_structure` varchar(64) DEFAULT NULL COMMENT '题型结构',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_questionnaire_id` (`case_questionnaire_id`)
    ) ENGINE=InnoDB COMMENT='案例问卷';

drop table if exists `case_questionnaire_result`;
CREATE TABLE IF NOT EXISTS `case_questionnaire_result`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_questionnaire_result_id` varchar(64) DEFAULT NULL COMMENT '案例问卷结果ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_questionnaire_id` varchar(64) DEFAULT NULL COMMENT '案例问卷ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_section_result_id` varchar(64) DEFAULT NULL COMMENT '答题记录ID',
    `account_id` varchar(64) DEFAULT NULL COMMENT '答题者账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '答题者Name',
    `status` tinyint(4) DEFAULT NULL COMMENT '状态[0-未开始|1-进行中|2-已完成]',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_questionnaire_result_id` (`case_questionnaire_result_id`)
    ) ENGINE=InnoDB COMMENT='案例问卷结果';

drop table if exists `case_setting`;
CREATE TABLE IF NOT EXISTS `case_setting`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_setting_id` varchar(64) DEFAULT NULL COMMENT '案例问卷设置ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `score_mode` varchar(64) DEFAULT NULL COMMENT '记分方式[少选不得分|少选得一半分]',
    `allot_mode` varchar(64) DEFAULT NULL COMMENT '分配方式',
    `ext` varchar(64) DEFAULT NULL COMMENT '额外配置[JSON]',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_setting_id` (`case_setting_id`)
    ) ENGINE=InnoDB COMMENT='案例问卷设置';

drop table if exists `case_org`;
CREATE TABLE IF NOT EXISTS `case_org`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `org_id` varchar(64) DEFAULT NULL COMMENT '机构ID[uim域]',
    `org_name` varchar(64) DEFAULT NULL COMMENT '机构名称',
    `scene` varchar(64) DEFAULT NULL COMMENT '场景',
    `handbook` varchar(64) DEFAULT NULL COMMENT '操作手册',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_org_id` (`case_org_id`)
    ) ENGINE=InnoDB COMMENT='案例机构';

drop table if exists `case_org_function`;
CREATE TABLE IF NOT EXISTS `case_org_function`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_org_function_id` varchar(64) DEFAULT NULL COMMENT '机构功能ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `function_name` varchar(64) DEFAULT NULL COMMENT '功能|菜单名称',
    `function_icon` varchar(64) DEFAULT NULL COMMENT '功能图标',
    `org_name` varchar(64) DEFAULT NULL COMMENT '机构名称',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_org_function_id` (`case_org_function_id`)
    ) ENGINE=InnoDB COMMENT='机构功能';

drop table if exists `case_org_indicator`;
CREATE TABLE IF NOT EXISTS `case_org_indicator`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_org_indicator_id` varchar(64) DEFAULT NULL COMMENT '机构功能指标点ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_org_function_id` varchar(64) DEFAULT NULL COMMENT '机构功能ID',
    `indicator_categ_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `indicator_name` varchar(64) DEFAULT NULL COMMENT '指标名称',
    `indicator_code` varchar(64) DEFAULT NULL COMMENT '指标Code',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB COMMENT='机构功能指标点';

drop table if exists `case_org_fee`;
CREATE TABLE IF NOT EXISTS `case_org_fee`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_org_fee_id` varchar(64) DEFAULT NULL COMMENT '案例机构费用ID',
    `case_org_indicator_id` varchar(64) DEFAULT NULL COMMENT '机构功能指标点ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `org_function_id` varchar(64) DEFAULT NULL COMMENT '机构功能ID',
    `function_name` varchar(64) DEFAULT NULL COMMENT '功能|菜单名称',
    `reimburse_ratio` double(11,2) DEFAULT NULL COMMENT '报销比例',
    `fee` decimal(11,2) DEFAULT NULL COMMENT '费用',
    `fee_code` varchar(64) DEFAULT NULL COMMENT '费用Code',
    `fee_name` varchar(64) DEFAULT NULL COMMENT '费用名称',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_org_fee_id` (`case_org_fee_id`)
    ) ENGINE=InnoDB COMMENT='案例机构费用';

drop table if exists `case_person`;
CREATE TABLE IF NOT EXISTS `case_person`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `case_person_id` varchar(64) DEFAULT NULL COMMENT '案例人物ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '案例|病人账号ID[uim]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_person_ID` (`case_person_ID`)
    ) ENGINE=InnoDB COMMENT='案例人物';

drop table if exists `case_event`;
CREATE TABLE IF NOT EXISTS `case_event`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `case_event_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `person_id` varchar(64) DEFAULT NULL COMMENT '人物id',
    `person_name` varchar(64) DEFAULT NULL COMMENT '人物名称',
    `event_id` varchar(64) DEFAULT NULL COMMENT '数据库事件id',
    `case_event_name` varchar(64) DEFAULT NULL COMMENT '事件名称',
    `pic` varchar(64) DEFAULT NULL COMMENT '图片',
    `event_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '分类名称路径',
    `descr` varchar(64) DEFAULT NULL COMMENT '事件说明',
    `tips` varchar(64) DEFAULT NULL COMMENT '事件提示',
    `create_account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号',
    `create_account_name` varchar(64) DEFAULT NULL COMMENT '创建者名称',
    `trigger_type` integer(2) DEFAULT NULL COMMENT '触发类型 0-条件触发 1-第一期 2-第二期...5-第5期',
    `trigger_period` varchar(64) DEFAULT NULL COMMENT '触发期数',
    `trigger_span` varchar(64) DEFAULT NULL COMMENT '触发时间段 1-前期 2-中期 3-后期',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标识',
    `state` integer(2) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_event_id` (`case_event_id`)
    ) ENGINE=InnoDB COMMENT='案例人物事件';

drop table if exists `case_event_eval`;
CREATE TABLE IF NOT EXISTS `case_event_eval`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `case_event_eval_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_descr` varchar(64) DEFAULT NULL COMMENT '触发条件描述',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_event_eval_id` (`case_event_eval_id`)
    ) ENGINE=InnoDB COMMENT='案例人物事件触发条件';

drop table if exists `case_event_action`;
CREATE TABLE IF NOT EXISTS `case_event_action`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `case_event_action_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `action_desc` varchar(64) DEFAULT NULL COMMENT '处理描述',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `score` integer(2) DEFAULT NULL COMMENT '=0错误选项 >0得分',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_event_action_id` (`case_event_action_id`)
    ) ENGINE=InnoDB COMMENT='案例人物事件处理选项';

drop table if exists `case_event_action_indicator`;
CREATE TABLE IF NOT EXISTS `case_event_action_indicator`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `case_event_action_indicator_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `init_flag` tinyint(4) DEFAULT NULL COMMENT '初始指标影响标记，0-否 1-是',
    `case_event_action_id` varchar(64) DEFAULT NULL COMMENT '事件选项id',
    `indicator_instance_id`          varchar(64)   null comment '指标id',
    `indicator_category_id`          varchar(255)  null comment '指标分类id',
    `indicator_expression_id`        varchar(64)  null comment '指标公式id',
    `expression`                     varchar(1500) null comment '表达式',
    `expression_descr`               varchar(1500) null comment '公式描述',
    `expression_vars`                varchar(1500) null comment '参数ID列表',
    `expression_names`               varchar(1500) null comment '参数名列表',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `case_identifier` varchar(64) DEFAULT NULL COMMENT '案例标示',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_event_action_indicator_id` (`case_event_action_indicator_id`)
    ) ENGINE=InnoDB COMMENT='人物事件处理选项影响指标';

drop table if exists `event_categ`;
CREATE TABLE IF NOT EXISTS `event_categ`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `event_categ_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `categ_pid` varchar(64) DEFAULT NULL COMMENT '父类别id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '父类别id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '父类别名称路径',
    `family` varchar(64) DEFAULT NULL COMMENT '根类别',
    `extend` varchar(512) DEFAULT NULL COMMENT '扩展属性',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `mark` tinyint(4) DEFAULT NULL COMMENT '标记',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_event_categ_id` (`event_categ_id`)
    ) ENGINE=InnoDB COMMENT='事件类别管理';

drop table if exists `event`;
CREATE TABLE IF NOT EXISTS `event`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `event_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `event_name` varchar(64) DEFAULT NULL COMMENT '突发事件名称',
    `pic` varchar(64) DEFAULT NULL COMMENT '图片',
    `event_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '分类名称路径',
    `descr` varchar(64) DEFAULT NULL COMMENT '事件说明',
    `tips` varchar(64) DEFAULT NULL COMMENT '事件提示',
    `create_account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号',
    `create_account_name` varchar(64) DEFAULT NULL COMMENT '创建者名称',
    `trigger_type` integer(2) DEFAULT NULL COMMENT '触发类型 0-条件触发 1-第一期 2-第二期...5-第5期',
    `trigger_period` varchar(64) DEFAULT NULL COMMENT '触发期数',
    `trigger_span` varchar(64) DEFAULT NULL COMMENT '触发时间段 1-前期 2-中期 3-后期',
    `state` integer(2) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_event_id` (`event_id`)
    ) ENGINE=InnoDB COMMENT='突发事件';

drop table if exists `event_eval`;
CREATE TABLE IF NOT EXISTS `event_eval`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `event_eval_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_descr` varchar(64) DEFAULT NULL COMMENT '触发条件描述',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_event_eval_id` (`event_eval_id`)
    ) ENGINE=InnoDB COMMENT='突发事件触发条件';

drop table if exists `event_action`;
CREATE TABLE IF NOT EXISTS `event_action`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `event_action_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `action_desc` varchar(64) DEFAULT NULL COMMENT '处理描述',
    `score` integer(2) DEFAULT NULL COMMENT '=0错误选项 >0得分',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_event_action_id` (`event_action_id`)
    ) ENGINE=InnoDB COMMENT='突发事件处理选项';

drop table if exists `event_action_indicator`;
CREATE TABLE IF NOT EXISTS `event_action_indicator`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `event_action_indicator_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `init_flag` tinyint(4) DEFAULT NULL COMMENT '初始指标影响标记，0-否 1-是',
    `event_action_id` varchar(64) DEFAULT NULL COMMENT '事件选项id',
    `indicator_instance_id`          varchar(64)   null comment '指标id',
    `indicator_category_id`          varchar(255)  null comment '指标分类id',
    `indicator_expression_id`          varchar(64)  null comment '指标公式id',
    `expression`                     varchar(1500) null comment '表达式',
    `expression_descr`               varchar(1500) null comment '公式描述',
    `expression_vars`                varchar(1500) null comment '参数ID列表',
    `expression_names`               varchar(1500) null comment '参数名列表',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_event_action_indicator_id` (`event_action_indicator_id`)
    ) ENGINE=InnoDB COMMENT='事件处理选项影响指标';

drop table if exists `intervene_category`;
CREATE TABLE IF NOT EXISTS `intervene_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `intervene_category_id` varchar(64) DEFAULT NULL COMMENT '类别id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `categ_pid` varchar(64) DEFAULT NULL COMMENT '父类别id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '父类别id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '父类别名称路径',
    `family` varchar(64) DEFAULT NULL COMMENT '根类别',
    `extend` varchar(512) DEFAULT NULL COMMENT '扩展属性，饮食推荐量',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `mark` tinyint(4) DEFAULT NULL COMMENT '标记，0-普通 1-膳食主要分类',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_intervene_category_id` (`intervene_category_id`)
    ) ENGINE=InnoDB COMMENT='干预类别管理';

drop table if exists `food_recommend`;
CREATE TABLE IF NOT EXISTS `food_recommend`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_recommend_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `instance_type` tinyint(4) DEFAULT NULL COMMENT '主体类型,1-营养成分 2-食材一级分类',
    `instance_id` varchar(64) DEFAULT NULL COMMENT '主体id ',
    `instance_name` varchar(64) DEFAULT NULL COMMENT '主体名称',
    `unit` varchar(64) DEFAULT NULL COMMENT '单位',
    `min` varchar(64) DEFAULT NULL COMMENT '推荐量下限',
    `max` varchar(64) DEFAULT NULL COMMENT '推荐量上限',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_recommend_id` (`food_recommend_id`)
    ) ENGINE=InnoDB COMMENT='食物推荐量配置';

drop table if exists `food_nutrient`;
CREATE TABLE IF NOT EXISTS `food_nutrient`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_nutrient_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '营养指标',
    `nutrient_name` varchar(64) DEFAULT NULL COMMENT '营养成分名称',
    `nutrient_type` tinyint(4) DEFAULT NULL COMMENT '特定成分标识，1-蛋白质2-碳水3-脂肪0-其它',
    `unit` varchar(64) DEFAULT NULL COMMENT '成分单位',
    `amt` varchar(64) DEFAULT NULL COMMENT '初始值',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_nutrient_id` (`food_nutrient_id`)
    ) ENGINE=InnoDB COMMENT='食物成分';

drop table if exists `food_material`;
CREATE TABLE IF NOT EXISTS `food_material`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_material_id` varchar(64) DEFAULT NULL COMMENT '食材id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_material_name` varchar(64) DEFAULT NULL COMMENT '食材名称',
    `pic` varchar(64) DEFAULT NULL COMMENT '图片',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '分类名称路径',
    `protein` varchar(64) DEFAULT NULL COMMENT '蛋白质每100g',
    `cho` varchar(64) DEFAULT NULL COMMENT '碳水每100g',
    `fat` varchar(64) DEFAULT NULL COMMENT '脂肪每100g',
    `energy` varchar(64) DEFAULT NULL COMMENT '总能量每100g',
    `protein_energy` varchar(64) DEFAULT NULL COMMENT '蛋白质能量占比',
    `cho_energy` varchar(64) DEFAULT NULL COMMENT '碳水能量占比',
    `fat_energy` varchar(64) DEFAULT NULL COMMENT '脂肪能量占比',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `descr` varchar(64) DEFAULT NULL COMMENT '说明',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_material_id` (`food_material_id`)
    ) ENGINE=InnoDB COMMENT='食材';

drop table if exists `food_material_indicator`;
CREATE TABLE IF NOT EXISTS `food_material_indicator`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_material_indicator_id` varchar(64) DEFAULT NULL COMMENT '关联id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_material_id` varchar(64) DEFAULT NULL COMMENT '食材id',
    `indicator_instance_id`          varchar(64)   null comment '指标id',
    `indicator_category_id`          varchar(255)  null comment '指标分类id',
    `indicator_expression_id`          varchar(64)  null comment '指标公式id',
    `expression`                     varchar(1500) null comment '表达式',
    `expression_descr`               varchar(1500) null comment '公式描述',
    `expression_vars`                varchar(1500) null comment '参数ID列表',
    `expression_names`               varchar(1500) null comment '参数名列表',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_material_indicator_id` (`food_material_indicator_id`)
    ) ENGINE=InnoDB COMMENT='食材关联指标';

drop table if exists `food_material_nutrient`;
CREATE TABLE IF NOT EXISTS `food_material_nutrient`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_material_nutrient_id` varchar(64) DEFAULT NULL COMMENT '关联id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_material_id` varchar(64) DEFAULT NULL COMMENT '食材id',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '营养指标',
    `nutrient_name` varchar(64) DEFAULT NULL COMMENT '营养成分名称',
    `unit` varchar(64) DEFAULT NULL COMMENT '单位',
    `weight` varchar(64) DEFAULT NULL COMMENT '重量',
    `energy` varchar(64) DEFAULT NULL COMMENT '能量',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_material_nutrient_id` (`food_material_nutrient_id`)
    ) ENGINE=InnoDB COMMENT='食材成分';

drop table if exists `food_dishes`;
CREATE TABLE IF NOT EXISTS `food_dishes`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_dishes_id` varchar(64) DEFAULT NULL COMMENT '菜肴id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_dishes_name` varchar(64) DEFAULT NULL COMMENT '菜肴名称',
    `materials_desc` varchar(512) DEFAULT NULL COMMENT '食材含量描述',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '分类名称路径',
    `protein` varchar(64) DEFAULT NULL COMMENT '蛋白质',
    `cho` varchar(64) DEFAULT NULL COMMENT '碳水',
    `fat` varchar(64) DEFAULT NULL COMMENT '脂肪',
    `energy` varchar(64) DEFAULT NULL COMMENT '总能量',
    `protein_scale` varchar(64) DEFAULT NULL COMMENT '蛋白质每100g',
    `cho_scale` varchar(64) DEFAULT NULL COMMENT '碳水每100g',
    `fat_scale` varchar(64) DEFAULT NULL COMMENT '脂肪每100g',
    `energy_scale` varchar(64) DEFAULT NULL COMMENT '总能量每100g',
    `protein_energy` varchar(64) DEFAULT NULL COMMENT '蛋白质能量占比',
    `cho_energy` varchar(64) DEFAULT NULL COMMENT '碳水能量占比',
    `fat_energy` varchar(64) DEFAULT NULL COMMENT '脂肪能量占比',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `descr` varchar(64) DEFAULT NULL COMMENT '说明',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_dishes_id` (`food_dishes_id`)
    ) ENGINE=InnoDB COMMENT='菜肴';

drop table if exists `food_dishes_material`;
CREATE TABLE IF NOT EXISTS `food_dishes_material`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_dishes_material_id` varchar(64) DEFAULT NULL COMMENT '关联id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_dishes_id` varchar(64) DEFAULT NULL COMMENT '菜肴id',
    `food_material_id` varchar(64) DEFAULT NULL COMMENT '食材id',
    `food_material_name` varchar(64) DEFAULT NULL COMMENT '食材名称',
    `weight` varchar(64) DEFAULT NULL COMMENT '重量',
    `energy` varchar(64) DEFAULT NULL COMMENT '能量',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_dishes_material_id` (`food_dishes_material_id`)
    ) ENGINE=InnoDB COMMENT='菜肴食材';

drop table if exists `food_dishes_nutrient`;
CREATE TABLE IF NOT EXISTS `food_dishes_nutrient`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_dishes_nutrient_id` varchar(64) DEFAULT NULL COMMENT '关联id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_dishes_id` varchar(64) DEFAULT NULL COMMENT '菜肴id',
    `instance_type`           integer(2) DEFAULT NULL comment '主体类型 1-营养成分 2-食材分类',
    `instance_id`             varchar(64) DEFAULT NULL comment '主体(营养指标，食材分类)id ',
    `instance_name`           varchar(64) DEFAULT NULL comment '主体名称',
    `unit`                    varchar(64) DEFAULT NULL comment '单位',
    `weight`                  varchar(64) DEFAULT NULL comment '重量',
    `energy`                  varchar(64) DEFAULT NULL comment '能量',
    `min`                     varchar(64) DEFAULT NULL comment '推荐量下限',
    `max`                     varchar(64) DEFAULT NULL comment '推荐量上限',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_dishes_nutrient_id` (`food_dishes_nutrient_id`)
    ) ENGINE=InnoDB COMMENT='菜肴成分';

drop table if exists `food_cookbook`;
CREATE TABLE IF NOT EXISTS `food_cookbook`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_cookbook_id` varchar(64) DEFAULT NULL COMMENT '食谱id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_cookbook_name` varchar(64) DEFAULT NULL COMMENT '食谱名称',
    `materials_desc` varchar(512) DEFAULT NULL COMMENT '食材含量描述',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '分类名称路径',
    `protein` varchar(64) DEFAULT NULL COMMENT '蛋白质每100g',
    `cho` varchar(64) DEFAULT NULL COMMENT '碳水每100g',
    `fat` varchar(64) DEFAULT NULL COMMENT '脂肪每100g',
    `energy` varchar(64) DEFAULT NULL COMMENT '总能量每100g',
    `protein_energy` varchar(64) DEFAULT NULL COMMENT '蛋白质能量占比',
    `cho_energy` varchar(64) DEFAULT NULL COMMENT '碳水能量占比',
    `fat_energy` varchar(64) DEFAULT NULL COMMENT '脂肪能量占比',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `descr` varchar(64) DEFAULT NULL COMMENT '说明',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_cookbook_id` (`food_cookbook_id`)
    ) ENGINE=InnoDB COMMENT='食谱';

drop table if exists `food_cookbook_detail`;
CREATE TABLE IF NOT EXISTS `food_cookbook_detail`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_cookbook_detail_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_cookbook_id` varchar(64) DEFAULT NULL COMMENT '食谱id',
    `meal_time` integer(2) DEFAULT NULL COMMENT '进餐时间，1-早|2-早加|午|午加|晚|晚加',
    `instance_type` integer(2) DEFAULT NULL COMMENT '明细类型，1-菜肴 2-食材',
    `instance_id` varchar(64) DEFAULT NULL COMMENT '菜肴、食材id',
    `instance_name` varchar(64) DEFAULT NULL COMMENT '菜肴、食材名称',
    `materials_desc` varchar(512) DEFAULT NULL COMMENT '食材含量描述',
    `weight` varchar(64) DEFAULT NULL COMMENT '重量',
    `energy` varchar(64) DEFAULT NULL COMMENT '能量',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_cookbook_detail_id` (`food_cookbook_detail_id`)
    ) ENGINE=InnoDB COMMENT='食谱食材';

drop table if exists `food_cookbook_nutrient`;
CREATE TABLE IF NOT EXISTS `food_cookbook_nutrient`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_cookbook_nutrient_id` varchar(64) DEFAULT NULL COMMENT '关联id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `food_cookbook_id` varchar(64) DEFAULT NULL COMMENT '食谱id',
    `instance_type`           integer(2) DEFAULT NULL comment '主体类型 1-营养成分 2-食材分类',
    `instance_id`             varchar(64) DEFAULT NULL comment '主体(营养指标，食材分类)id ',
    `instance_name`           varchar(64) DEFAULT NULL comment '主体名称',
    `unit`                    varchar(64) DEFAULT NULL comment '单位',
    `weight`                  varchar(64) DEFAULT NULL comment '重量',
    `energy`                  varchar(64) DEFAULT NULL comment '能量',
    `min`                     varchar(64) DEFAULT NULL comment '推荐量下限',
    `max`                     varchar(64) DEFAULT NULL comment '推荐量上限',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_cookbook_nutrient_id` (`food_cookbook_nutrient_id`)
    ) ENGINE=InnoDB COMMENT='食谱成分';

drop table if exists `sport_plan`;
CREATE TABLE IF NOT EXISTS `sport_plan`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `sport_plan_id` varchar(64) DEFAULT NULL COMMENT '运动方案id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `sport_plan_name` varchar(64) DEFAULT NULL COMMENT '运动方案名称',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '分类名称路径',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `descr` varchar(64) DEFAULT NULL COMMENT '说明',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_sport_plan_id` (`sport_plan_id`)
    ) ENGINE=InnoDB COMMENT='运动方案';

drop table if exists `sport_plan_items`;
CREATE TABLE IF NOT EXISTS `sport_plan_items`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `sport_plan_items_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `sport_plan_id` varchar(64) DEFAULT NULL COMMENT '运动方案id',
    `sport_item_id` varchar(64) DEFAULT NULL COMMENT '运动项目id',
    `sport_item_name` varchar(64) DEFAULT NULL COMMENT '运动项目名称',
    `frequency` varchar(64) DEFAULT NULL COMMENT '运动频次',
    `last_time` varchar(64) DEFAULT NULL COMMENT '运动时长',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_sport_plan_items_id` (`sport_plan_items_id`)
    ) ENGINE=InnoDB COMMENT='运动方案项目列表';

drop table if exists `sport_item`;
CREATE TABLE IF NOT EXISTS `sport_item`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `sport_item_id` varchar(64) DEFAULT NULL COMMENT '运动项目id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `sport_item_name` varchar(64) DEFAULT NULL COMMENT '运动项目名称',
    `pic` varchar(64) DEFAULT NULL COMMENT '图片',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '分类名称路径',
    `strength_met` varchar(64) DEFAULT NULL COMMENT '运动强度(MET)',
    `strength_type` varchar(64) DEFAULT NULL COMMENT '运动强度类别',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `descr` varchar(64) DEFAULT NULL COMMENT '说明',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_sport_item_id` (`sport_item_id`)
    ) ENGINE=InnoDB COMMENT='运动项目';

drop table if exists `sport_item_indicator`;
CREATE TABLE IF NOT EXISTS `sport_item_indicator`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `sport_item_indicator_id` varchar(64) DEFAULT NULL COMMENT '关联id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `sport_item_id` varchar(64) DEFAULT NULL COMMENT '运动项目id',
    `indicator_instance_id`          varchar(64)   null comment '指标id',
    `indicator_category_id`          varchar(255)  null comment '指标分类id',
    `indicator_expression_id`          varchar(64)  null comment '指标公式id',
    `expression`                     varchar(1500) null comment '表达式',
    `expression_descr`               varchar(1500) null comment '公式描述',
    `expression_vars`                varchar(1500) null comment '参数ID列表',
    `expression_names`               varchar(1500) null comment '参数名列表',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_sport_item_indicator_id` (`sport_item_indicator_id`)
    ) ENGINE=InnoDB COMMENT='运动项目关联指标';

drop table if exists `treat_item`;
CREATE TABLE IF NOT EXISTS `treat_item`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `treat_item_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `treat_item_name` varchar(64) DEFAULT NULL COMMENT '治疗名称',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '功能点类别',
    `indicator_func_id` varchar(64) DEFAULT NULL COMMENT '功能点id',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(512) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '分类名称路径',
    `unit` varchar(64) DEFAULT NULL COMMENT '单位',
    `fee` varchar(64) DEFAULT NULL COMMENT '费用',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_treat_item_id` (`treat_item_id`)
    ) ENGINE=InnoDB COMMENT='治疗项目';

drop table if exists `treat_item_indicator`;
CREATE TABLE IF NOT EXISTS `treat_item_indicator`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `treat_item_indicator_id` varchar(64) DEFAULT NULL COMMENT '关联id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `treat_item_id` varchar(64) DEFAULT NULL COMMENT '治疗项目id',
    `indicator_instance_id`          varchar(64)   null comment '指标id',
    `indicator_category_id`          varchar(255)  null comment '指标分类id',
    `indicator_expression_id`          varchar(64)  null comment '指标公式id',
    `expression`                     varchar(1500) null comment '表达式',
    `expression_descr`               varchar(1500) null comment '公式描述',
    `expression_vars`                varchar(1500) null comment '参数ID列表',
    `expression_names`               varchar(1500) null comment '参数名列表',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_treat_item_indicator_id` (`treat_item_indicator_id`)
    ) ENGINE=InnoDB COMMENT='治疗项目关联指标';

drop table if exists `question_category`;
CREATE TABLE IF NOT EXISTS `question_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `pid` bigint(19) DEFAULT NULL COMMENT '父ID',
    `question_categ_pid` varchar(64) DEFAULT NULL COMMENT '类别父id',
    `question_categ_id` varchar(64) DEFAULT NULL COMMENT '类别ID',
    `question_categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `question_categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `question_categ_name_path` varchar(512) DEFAULT NULL COMMENT '类别name路径',
    `question_categ_group` varchar(64) DEFAULT NULL COMMENT '类别组',
    `sequence` integer(2) DEFAULT NULL COMMENT '序列号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_categ_pid` (`question_categ_pid`)
    ) ENGINE=InnoDB COMMENT='问题类目';

drop table if exists `question_instance`;
CREATE TABLE IF NOT EXISTS `question_instance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `pid` bigint(19) DEFAULT NULL COMMENT '父ID',
    `question_instance_pid` varchar(64) DEFAULT NULL COMMENT '问题pid',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_categ_id` varchar(64) DEFAULT NULL COMMENT '类别ID',
    `question_categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `question_categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `question_categ_name_path` varchar(512) DEFAULT NULL COMMENT '类别name路径',
    `input_type` varchar(64) DEFAULT NULL COMMENT '题目答题输入类型[input,select,text]',
    `question_type` varchar(64) DEFAULT NULL COMMENT '题目答题类型[单选|多选|判断|主观|材料]',
    `question_title` varchar(64) DEFAULT NULL COMMENT '问题标题',
    `question_descr` text(65535) DEFAULT NULL COMMENT '问题描述',
    `enabled` tinyint(4) DEFAULT NULL COMMENT '状态',
    `sequence` integer(2) DEFAULT NULL COMMENT '排序',
    `source` varchar(64) DEFAULT NULL COMMENT '来源',
    `ref_count` integer(2) DEFAULT NULL COMMENT '引用计数',
    `question_identifier` varchar(64) DEFAULT NULL COMMENT '问题标识',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者Name',
    `permissions` varchar(64) DEFAULT NULL COMMENT '权限[000001]',
    `detailed_answer` text(65535) DEFAULT NULL COMMENT '答案解析',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_instance_id` (`question_instance_id`)
    ) ENGINE=InnoDB COMMENT='问题实例';

drop table if exists `question_options`;
CREATE TABLE IF NOT EXISTS `question_options`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_options_id` varchar(64) DEFAULT NULL COMMENT '选项ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `option_title` varchar(64) DEFAULT NULL COMMENT '选项标题',
    `option_value` varchar(64) DEFAULT NULL COMMENT '选项值',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_options_id` (`question_options_id`)
    ) ENGINE=InnoDB COMMENT='问题-选项';

drop table if exists `question_answers`;
CREATE TABLE IF NOT EXISTS `question_answers`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_answer_id` varchar(64) DEFAULT NULL COMMENT '答案的ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `question_options_id` varchar(64) DEFAULT NULL COMMENT '选项ID',
    `option_title` varchar(64) DEFAULT NULL COMMENT '选项标题',
    `option_value` varchar(64) DEFAULT NULL COMMENT '问题的答案',
    `right` tinyint(4) DEFAULT NULL COMMENT '是否是正确答案[0:错误，1:正确]',
    `question_identifier` varchar(64) DEFAULT NULL COMMENT '问题标识',
    `ver` varchar(64) DEFAULT NULL COMMENT '版本号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_answer_id` (`question_answer_id`)
    ) ENGINE=InnoDB COMMENT='问题-答案';

drop table if exists `question_score`;
CREATE TABLE IF NOT EXISTS `question_score`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_score_id` varchar(64) DEFAULT NULL COMMENT '问题-分数ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `biz_id` varchar(64) DEFAULT NULL COMMENT '业务ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `question_options_id` varchar(64) DEFAULT NULL COMMENT '选项ID',
    `question_answer_id` varchar(64) DEFAULT NULL COMMENT '答案的ID',
    `score` float(11,2) DEFAULT NULL COMMENT '分数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_score_id` (`question_score_id`)
    ) ENGINE=InnoDB COMMENT='问题-得分';

drop table if exists `question_dimension`;
CREATE TABLE IF NOT EXISTS `question_dimension`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_dimension_id` varchar(64) DEFAULT NULL COMMENT '问题维度ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `demension_name` varchar(64) DEFAULT NULL COMMENT '维度名称',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_dimension_id` (`question_dimension_id`)
    ) ENGINE=InnoDB COMMENT='问题-维度';

drop table if exists `question_section_category`;
CREATE TABLE IF NOT EXISTS `question_section_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `pid` bigint(19) DEFAULT NULL COMMENT '父ID',
    `question_section_categ_pid` varchar(64) DEFAULT NULL COMMENT '类别父id',
    `question_section_categ_id` varchar(64) DEFAULT NULL COMMENT '类别ID',
    `question_section_categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `question_section_categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `question_section_categ_name_path` varchar(512) DEFAULT NULL COMMENT '类别name路径',
    `question_categ_group` varchar(64) DEFAULT NULL COMMENT '类别组',
    `sequence` integer(2) DEFAULT NULL COMMENT '序列号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_section_categ_pid` (`question_section_categ_pid`)
    ) ENGINE=InnoDB COMMENT='问题集类目';

drop table if exists `question_section`;
CREATE TABLE IF NOT EXISTS `question_section`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_section_categ_id` varchar(64) DEFAULT NULL COMMENT '类别ID',
    `question_section_categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `question_section_categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `question_section_categ_name_path` varchar(512) DEFAULT NULL COMMENT '类别name路径',
    `name` varchar(64) DEFAULT NULL COMMENT '问题集名称',
    `tips` varchar(64) DEFAULT NULL COMMENT '问题集提示',
    `descr` varchar(512) DEFAULT NULL COMMENT '问题集说明',
    `sequence` integer(2) DEFAULT NULL COMMENT '排序',
    `source` varchar(64) DEFAULT NULL COMMENT '来源',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号Id',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者姓名',
    `permissions` varchar(64) DEFAULT NULL COMMENT '权限[000001]',
    `question_count` integer(2) DEFAULT NULL COMMENT '题数',
    `question_section_structure` varchar(64) DEFAULT NULL COMMENT '题型结构',
    `enabled` tinyint(4) DEFAULT NULL COMMENT '状态',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_section_id` (`question_section_id`)
    ) ENGINE=InnoDB COMMENT='问题集[试卷]';

drop table if exists `question_section_item`;
CREATE TABLE IF NOT EXISTS `question_section_item`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_section_item_id` varchar(64) DEFAULT NULL COMMENT 'itemID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_section_name` varchar(64) DEFAULT NULL COMMENT '问题集名称',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `question_title` varchar(64) DEFAULT NULL COMMENT '问题标题',
    `question_descr` varchar(512) DEFAULT NULL COMMENT '问题描述',
    `enabled` tinyint(4) DEFAULT NULL COMMENT '状态',
    `required` tinyint(4) DEFAULT NULL COMMENT '是否必填',
    `sequence` integer(2) DEFAULT NULL COMMENT '排序',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号Id',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者姓名',
    `permissions` varchar(64) DEFAULT NULL COMMENT '权限[000001]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_section_item_id` (`question_section_item_id`)
    ) ENGINE=InnoDB COMMENT='问题集[试卷]-题目';

drop table if exists `question_section_dimension`;
CREATE TABLE IF NOT EXISTS `question_section_dimension`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_section_dimension_id` varchar(64) DEFAULT NULL COMMENT '问题集维度ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `demension_name` varchar(64) DEFAULT NULL COMMENT '维度名称',
    `demension_content` varchar(64) DEFAULT NULL COMMENT '内容',
    `score` float(11,2) DEFAULT NULL COMMENT '分数',
    `source` varchar(64) DEFAULT NULL COMMENT '来源',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号Id',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者姓名',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_section_dimension_id` (`question_section_dimension_id`)
    ) ENGINE=InnoDB COMMENT='问题集[试卷]-维度';

drop table if exists `question_section_result`;
CREATE TABLE IF NOT EXISTS `question_section_result`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_section_result_id` varchar(64) DEFAULT NULL COMMENT '答题记录ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_section_name` varchar(64) DEFAULT NULL COMMENT '问题集名称',
    `question_count` integer(2) DEFAULT NULL COMMENT '题数',
    `question_section_structure` varchar(64) DEFAULT NULL COMMENT '题型结构',
    `right_count` integer(2) DEFAULT NULL COMMENT '正确题数',
    `score_structure` varchar(64) DEFAULT NULL COMMENT '得分结构',
    `score` float(11,2) DEFAULT NULL COMMENT '分数',
    `account_id` varchar(64) DEFAULT NULL COMMENT '答题者账号Id',
    `account_name` varchar(64) DEFAULT NULL COMMENT '答题者姓名',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_section_result_id` (`question_section_result_id`)
    ) ENGINE=InnoDB COMMENT='问题集[试卷]-答题记录';

drop table if exists `question_section_result_item`;
CREATE TABLE IF NOT EXISTS `question_section_result_item`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_section_result_item_id` varchar(64) DEFAULT NULL COMMENT '记录项ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `question_section_dimension_id` varchar(64) DEFAULT NULL COMMENT '问题集维度ID',
    `question_title` varchar(64) DEFAULT NULL COMMENT '问题标题',
    `right_value` varchar(64) DEFAULT NULL COMMENT '正确答案[JSON]',
    `answer_id` varchar(64) DEFAULT NULL COMMENT '答案值ID[JSON]',
    `answer_value` varchar(64) DEFAULT NULL COMMENT '答题值[JSON]',
    `right` tinyint(4) DEFAULT NULL COMMENT '是否正确[0：错误|1：一半|2：完成正确]',
    `score` float(11,2) DEFAULT NULL COMMENT '分数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_section_result_item_id` (`question_section_result_item_id`)
    ) ENGINE=InnoDB COMMENT='问题集[试卷]-答题记录Item';

drop table if exists `materials_category`;
CREATE TABLE IF NOT EXISTS `materials_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `materials_category_id` varchar(64) DEFAULT NULL COMMENT '资料分类ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `category_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `materials_categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `materials_categ_name_path` varchar(512) DEFAULT NULL COMMENT '类别name路径',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号Id',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者姓名',
    `sequence` integer(2) DEFAULT NULL COMMENT '序号',
    `state` integer(2) DEFAULT NULL COMMENT '状态',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_materials_category_id` (`materials_category_id`)
    ) ENGINE=InnoDB COMMENT='资料-分类';

drop table if exists `materials`;
CREATE TABLE IF NOT EXISTS `materials`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `materials_id` varchar(64) DEFAULT NULL COMMENT '资料ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `category_id` varchar(64) DEFAULT NULL COMMENT '资料分类Id',
    `category_name` varchar(64) DEFAULT NULL COMMENT '资料分类名称',
    `title` varchar(64) DEFAULT NULL COMMENT '标题',
    `descr` varchar(64) DEFAULT NULL COMMENT '资料简介',
    `type` varchar(64) DEFAULT NULL COMMENT '资料类型',
    `sequence` integer(2) DEFAULT NULL COMMENT '序号',
    `enabled` tinyint(4) DEFAULT NULL COMMENT '状态',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号Id',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者姓名',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_materials_id` (`materials_id`)
    ) ENGINE=InnoDB COMMENT='资料';

drop table if exists `materials_attachment`;
CREATE TABLE IF NOT EXISTS `materials_attachment`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `materials_attachment_id` varchar(64) DEFAULT NULL COMMENT '资料附件ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `materials_id` varchar(64) DEFAULT NULL COMMENT '资料ID',
    `file_name` varchar(64) DEFAULT NULL COMMENT '文件名称',
    `file_uri` varchar(64) DEFAULT NULL COMMENT '文件路径',
    `file_type` varchar(64) DEFAULT NULL COMMENT '文件类型',
    `sequence` integer(2) DEFAULT NULL COMMENT '序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_materials_attachment_id` (`materials_attachment_id`)
    ) ENGINE=InnoDB COMMENT='资料-附件';

drop table if exists `indicator_category`;
CREATE TABLE IF NOT EXISTS `indicator_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标类别分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `pid` varchar(64) DEFAULT NULL COMMENT '父ID',
    `category_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `category_code` varchar(64) DEFAULT NULL COMMENT '分类code',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_category_id` (`indicator_category_id`)
    ) ENGINE=InnoDB COMMENT='指标类别';

drop table if exists `indicator_instance`;
CREATE TABLE IF NOT EXISTS `indicator_instance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `indicator_name` varchar(64) DEFAULT NULL COMMENT '指标名称',
    `indicator_code` varchar(64) DEFAULT NULL COMMENT '指标code',
    `unit` varchar(64) DEFAULT NULL COMMENT '单位',
    `core` tinyint(4) DEFAULT NULL COMMENT '0-非关键指标，1-关键指标',
    `food` tinyint(4) DEFAULT NULL COMMENT '0-非饮食关键指标，1-饮食关键指标',
    `experssion` varchar(64) DEFAULT NULL COMMENT '指标表达式[拆包]',
    `raw_experssion` varchar(64) DEFAULT NULL COMMENT '未拆包指标表达式',
    `descr` varchar(64) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_instance_id` (`indicator_instance_id`)
    ) ENGINE=InnoDB COMMENT='指标';

drop table if exists `indicator_category_ref`;
CREATE TABLE IF NOT EXISTS `indicator_category_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_category_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标类别分布式ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_category_ref_id` (`indicator_category_ref_id`)
    ) ENGINE=InnoDB COMMENT='指标分类与指标关联关系';

drop table if exists `indicator_func`;
CREATE TABLE IF NOT EXISTS `indicator_func`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_func` varchar(64) DEFAULT NULL COMMENT '指标功能分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标类别分布式ID',
    `name` varchar(64) DEFAULT NULL COMMENT '功能名称',
    `operation_tip` varchar(64) DEFAULT NULL COMMENT '操作提示',
    `dialog_tip` varchar(64) DEFAULT NULL COMMENT '对话提示',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_func` (`indicator_func`)
    ) ENGINE=InnoDB COMMENT='指标功能';

drop table if exists `indicator_ref`;
CREATE TABLE IF NOT EXISTS `indicator_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `ref_indicator_id` varchar(64) DEFAULT NULL COMMENT '引用这个指标的指标ID',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_ref_id` (`indicator_ref_id`)
    ) ENGINE=InnoDB COMMENT='指标-引用';

drop table if exists `indicator_var`;
CREATE TABLE IF NOT EXISTS `indicator_var`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_var_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `db_name` varchar(64) DEFAULT NULL COMMENT '数据库名',
    `tb_name` varchar(64) DEFAULT NULL COMMENT '表名',
    `var_name` varchar(64) DEFAULT NULL COMMENT '变量名',
    `var_code` varchar(64) DEFAULT NULL COMMENT '变量code',
    `periods` varchar(64) DEFAULT NULL COMMENT '期数，如果多期用[,]分割',
    `descr` varchar(64) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_var_id` (`indicator_var_id`)
    ) ENGINE=InnoDB COMMENT='指标变量';

drop table if exists `indicator_rule`;
CREATE TABLE IF NOT EXISTS `indicator_rule`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_rule_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `variable_id` varchar(64) DEFAULT NULL COMMENT '指标或变量ID',
    `rule_type` integer(2) DEFAULT NULL COMMENT '变量类型[0:指标，1:变量]',
    `min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `def` varchar(64) DEFAULT NULL COMMENT '默认值',
    `descr` varchar(64) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_rule_id` (`indicator_rule_id`)
    ) ENGINE=InnoDB COMMENT='指标|变量规则[校验]';

drop table if exists `indicator_val`;
CREATE TABLE IF NOT EXISTS `indicator_val`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_val_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `current_val` varchar(64) DEFAULT NULL COMMENT '当前值',
    `min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `def` varchar(64) DEFAULT NULL COMMENT '默认值',
    `descr` varchar(64) DEFAULT NULL COMMENT '描述',
    `periods` varchar(64) DEFAULT NULL COMMENT '期数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_val_id` (`indicator_val_id`)
    ) ENGINE=InnoDB COMMENT='指标值';

drop table if exists `indicator_principal_ref`;
CREATE TABLE IF NOT EXISTS `indicator_principal_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_principal_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_val_id` varchar(64) DEFAULT NULL COMMENT '指标值ID',
    `principal_id` varchar(64) DEFAULT NULL COMMENT '主体ID',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_principal_ref_id` (`indicator_principal_ref_id`)
    ) ENGINE=InnoDB COMMENT='指标主体关联关系';

drop table if exists `indicator_view_base_info`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '指标基本信息类名称',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_base_info_id` (`indicator_view_base_info_id`)
    ) ENGINE=InnoDB COMMENT='查看指标基本信息类';

drop table if exists `indicator_view_base_info_descr`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_descr`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_desc_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `name` varchar(64) DEFAULT NULL COMMENT '指标基本信息描述表名称',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_base_info_desc_id` (`indicator_view_base_info_desc_id`)
    ) ENGINE=InnoDB COMMENT='指标基本信息描述表';

drop table if exists `indicator_view_base_info_descr_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_descr_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_desc_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_view_base_info_desc_id` varchar(64) DEFAULT NULL COMMENT '指标描述表ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_base_info_desc_ref_id` (`indicator_view_base_info_desc_ref_id`)
    ) ENGINE=InnoDB COMMENT='指标基本信息描述表与指标关联关系';

drop table if exists `indicator_view_base_info_monitor`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_monitor`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_monitor_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `name` varchar(64) DEFAULT NULL COMMENT '指标基本信息监测表名称',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_base_info_monitor_id` (`indicator_view_base_info_monitor_id`)
    ) ENGINE=InnoDB COMMENT='指标基本信息监测表';

drop table if exists `indicator_view_base_info_monitor_content`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_monitor_content`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_monitor_content_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `name` varchar(64) DEFAULT NULL COMMENT '监测内容名称',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_base_info_monitor_content_id` (`indicator_view_base_info_monitor_content_id`)
    ) ENGINE=InnoDB COMMENT='指标基本信息监测内容表';

drop table if exists `indicator_view_base_info_monitor_content_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_monitor_content_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_monitor_content_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_base_info_monitor_content_ref_id` (`indicator_view_base_info_monitor_content_ref_id`)
    ) ENGINE=InnoDB COMMENT='指标基本信息监测内容表与指标关联关系';

drop table if exists `indicator_view_base_info_single`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_single`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_single_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_base_info_single_id` (`indicator_view_base_info_single_id`)
    ) ENGINE=InnoDB COMMENT='指标基本信息与单一指标关系表';

drop table if exists `indicator_view_monitor_followup`;
CREATE TABLE IF NOT EXISTS `indicator_view_monitor_followup`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_monitor_followup_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '指标监测随访类表名称',
    `type` varchar(64) DEFAULT NULL COMMENT '监测随访表类别',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_monitor_followup_id` (`indicator_view_monitor_followup_id`)
    ) ENGINE=InnoDB COMMENT='查看指标监测随访类';

drop table if exists `indicator_view_monitor_followup_followup_content`;
CREATE TABLE IF NOT EXISTS `indicator_view_monitor_followup_followup_content`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_monitor_followup_followup_content_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_view_monitor_followup_id` varchar(64) DEFAULT NULL COMMENT '查看指标监测随访类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '随访内容名称',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_monitor_followup_followup_content_id` (`indicator_view_monitor_followup_followup_content_id`)
    ) ENGINE=InnoDB COMMENT='查看指标监测随访内容';

drop table if exists `indicator_view_monitor_followup_content_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_monitor_followup_content_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_monitor_followup_content_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_view_monitor_followup_followup_content_id` varchar(64) DEFAULT NULL COMMENT '指标监测随访内容ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_monitor_followup_content_ref_id` (`indicator_view_monitor_followup_content_ref_id`)
    ) ENGINE=InnoDB COMMENT='指标监测随访随访内容表与指标关联关系';

drop table if exists `indicator_view_physical_exam`;
CREATE TABLE IF NOT EXISTS `indicator_view_physical_exam`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_physical_exam_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '体格检查名称',
    `type` varchar(64) DEFAULT NULL COMMENT '体格检查类别',
    `fee` double(11,2) DEFAULT NULL COMMENT '费用',
    `result_analysis` varchar(64) DEFAULT NULL COMMENT '结果解析',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_physical_exam_id` (`indicator_view_physical_exam_id`)
    ) ENGINE=InnoDB COMMENT='查看指标体格检查类';

drop table if exists `indicator_view_physical_exam_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_physical_exam_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_physical_exam_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_physical_exam_ref_id` (`indicator_view_physical_exam_ref_id`)
    ) ENGINE=InnoDB COMMENT='查看指标体格检查关联指标';

drop table if exists `indicator_view_support_exam`;
CREATE TABLE IF NOT EXISTS `indicator_view_support_exam`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_support_exam_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '辅助检查名称',
    `type` varchar(64) DEFAULT NULL COMMENT '辅助检查类别',
    `fee` double(11,2) DEFAULT NULL COMMENT '费用',
    `result_analysis` varchar(64) DEFAULT NULL COMMENT '结果解析',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_support_exam_id` (`indicator_view_support_exam_id`)
    ) ENGINE=InnoDB COMMENT='查看指标辅助检查类';

drop table if exists `indicator_view_support_exam_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_support_exam_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_support_exam_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_view_support_exam_ref_id` (`indicator_view_support_exam_ref_id`)
    ) ENGINE=InnoDB COMMENT='查看指标辅助检查关联指标';

drop table if exists `indicator_judge_risk_factor`;
CREATE TABLE IF NOT EXISTS `indicator_judge_risk_factor`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_judge_risk_factor_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '危险因素名称',
    `type` varchar(64) DEFAULT NULL COMMENT '危险因素类别',
    `point` double(11,2) DEFAULT NULL COMMENT '分数',
    `expression` varchar(64) DEFAULT NULL COMMENT '判断规则',
    `result_explain` varchar(64) DEFAULT NULL COMMENT '结果说明',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_judge_risk_factor_id` (`indicator_judge_risk_factor_id`)
    ) ENGINE=InnoDB COMMENT='判断指标危险因素';

drop table if exists `indicator_judge_health_problem`;
CREATE TABLE IF NOT EXISTS `indicator_judge_health_problem`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_judge_health_problem_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '健康问题名称',
    `type` varchar(64) DEFAULT NULL COMMENT '健康问题类别',
    `point` double(11,2) DEFAULT NULL COMMENT '分数',
    `expression` varchar(64) DEFAULT NULL COMMENT '判断规则',
    `result_explain` varchar(64) DEFAULT NULL COMMENT '结果说明',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_judge_health_problem_id` (`indicator_judge_health_problem_id`)
    ) ENGINE=InnoDB COMMENT='判断指标健康问题';

drop table if exists `indicator_judge_health_guidance`;
CREATE TABLE IF NOT EXISTS `indicator_judge_health_guidance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_judge_health_guidance_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '健康指导名称',
    `type` varchar(64) DEFAULT NULL COMMENT '健康指导类别',
    `point` double(11,2) DEFAULT NULL COMMENT '分数',
    `expression` varchar(64) DEFAULT NULL COMMENT '判断规则',
    `result_explain` varchar(64) DEFAULT NULL COMMENT '结果说明',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_judge_health_guidance_id` (`indicator_judge_health_guidance_id`)
    ) ENGINE=InnoDB COMMENT='判断指标健康指导';

drop table if exists `indicator_judge_disease_problem`;
CREATE TABLE IF NOT EXISTS `indicator_judge_disease_problem`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_judge_disease_problem_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '疾病问题名称',
    `type` varchar(64) DEFAULT NULL COMMENT '疾病问题类别',
    `point` double(11,2) DEFAULT NULL COMMENT '分数',
    `expression` varchar(64) DEFAULT NULL COMMENT '判断规则',
    `result_explain` varchar(64) DEFAULT NULL COMMENT '结果说明',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_judge_disease_problem_id` (`indicator_judge_disease_problem_id`)
    ) ENGINE=InnoDB COMMENT='判断指标疾病问题';

drop table if exists `indicator_judge_health_management_goal`;
CREATE TABLE IF NOT EXISTS `indicator_judge_health_management_goal`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_judge_health_management_goal_id` varchar(64) DEFAULT NULL COMMENT '判断指标健管目标分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `point` double(11,2) DEFAULT NULL COMMENT '分数',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_judge_health_management_goal_id` (`indicator_judge_health_management_goal_id`)
    ) ENGINE=InnoDB COMMENT='判断指标健管目标';

drop table if exists `indicator_judge_health_management_goal_ref`;
CREATE TABLE IF NOT EXISTS `indicator_judge_health_management_goal_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_judge_health_management_goal_ref_id` varchar(64) DEFAULT NULL COMMENT '判断指标健管目标关联指标分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_judge_health_management_goal_id` varchar(64) DEFAULT NULL COMMENT '判断指标健管目标分布式ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '指标实例分布式ID',
    `expression` varchar(64) DEFAULT NULL COMMENT '判断规则',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_indicator_judge_health_management_goal_ref_id` (`indicator_judge_health_management_goal_ref_id`)
    ) ENGINE=InnoDB COMMENT='判断指标健管目标关联指标';

drop table if exists `risk_category`;
CREATE TABLE IF NOT EXISTS `risk_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `risk_category_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `risk_category_name` varchar(64) DEFAULT NULL COMMENT '风险类别名称',
    `seq` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_risk_category_id` (`risk_category_id`)
    ) ENGINE=InnoDB COMMENT='风险类别';

drop table if exists `risk_model`;
CREATE TABLE IF NOT EXISTS `risk_model`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `risk_model_id` varchar(64) DEFAULT NULL COMMENT '风险模型ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `risk_category_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `name` varchar(64) DEFAULT NULL COMMENT '模型名称',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_risk_model_id` (`risk_model_id`)
    ) ENGINE=InnoDB COMMENT='风险模型';

drop table if exists `risk_death_model`;
CREATE TABLE IF NOT EXISTS `risk_death_model`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `risk_death_model_id` varchar(64) DEFAULT NULL COMMENT '死亡模型ID',
    `risk_model_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `risk_death_reason_name` varchar(64) DEFAULT NULL COMMENT '死亡原因名称',
    `risk_death_probability` integer(2) DEFAULT NULL COMMENT '死亡概率',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_risk_death_model_id` (`risk_death_model_id`)
    ) ENGINE=InnoDB COMMENT='死亡模型';

drop table if exists `risk_danger_point`;
CREATE TABLE IF NOT EXISTS `risk_danger_point`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `risk_danger_point_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `risk_death_model_id` varchar(64) DEFAULT NULL COMMENT '死亡模型ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `expression` varchar(64) DEFAULT NULL COMMENT '公式',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_risk_danger_point_id` (`risk_danger_point_id`)
    ) ENGINE=InnoDB COMMENT='危险分数';

drop table if exists `evaluate_questionnaire`;
CREATE TABLE IF NOT EXISTS `evaluate_questionnaire`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `evaluate_questionnaire_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_evaluate_questionnaire_id` (`evaluate_questionnaire_id`)
    ) ENGINE=InnoDB COMMENT='评估问卷';

drop table if exists `evaluate_dimension_expression`;
CREATE TABLE IF NOT EXISTS `evaluate_dimension_expression`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `evaluate_dimension_expression_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `questionnaire_id` varchar(64) DEFAULT NULL COMMENT '评估问卷ID',
    `dimension_id` varchar(64) DEFAULT NULL COMMENT '维度id',
    `expression` varchar(64) DEFAULT NULL COMMENT '维度公式',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_evaluate_dimension_expression_id` (`evaluate_dimension_expression_id`)
    ) ENGINE=InnoDB COMMENT='评估维度公式';

drop table if exists `evaluate_report_management`;
CREATE TABLE IF NOT EXISTS `evaluate_report_management`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `evaluate_report_management_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `evaluate_questionnaire_id` varchar(64) DEFAULT NULL COMMENT '评估问卷分布式ID',
    `report_name` varchar(64) DEFAULT NULL COMMENT '报告名称',
    `report_descr` varchar(64) DEFAULT NULL COMMENT '报告说明',
    `assessment_result` varchar(64) DEFAULT NULL COMMENT '评估结果',
    `suggestion` varchar(64) DEFAULT NULL COMMENT '相关建议',
    `min_score` integer(2) DEFAULT NULL COMMENT '分数段[最小]',
    `max_score` integer(2) DEFAULT NULL COMMENT '分数段[最大]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_evaluate_report_management_id` (`evaluate_report_management_id`)
    ) ENGINE=InnoDB COMMENT='评估报告管理';