
-- 若库不存在创建一个
CREATE DATABASE IF NOT EXISTS `dows_hep`;
USE `dows_hep`;

drop table if exists `experiment_instance`;
CREATE TABLE IF NOT EXISTS `experiment_instance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_name` varchar(64) DEFAULT NULL COMMENT '案例名称[社区名]',
    `experiment_name` varchar(64) DEFAULT NULL COMMENT '实验名称',
    `experiment_descr` varchar(64) DEFAULT NULL COMMENT '实验说明',
    `model` integer(2) DEFAULT NULL COMMENT '实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `state` tinyint(4) DEFAULT NULL COMMENT '实验状态[默认未开始状态0~6步]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
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
    `group_alias` varchar(64) DEFAULT NULL COMMENT '小组别名',
    `account_id` varchar(64) DEFAULT NULL COMMENT '组员账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '组员账号名',
    `participator_type` integer(2) DEFAULT NULL COMMENT '参与者类型[0:教师，1:学生]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_experiment_participator_id` (`experiment_participator_id`)
) ENGINE=InnoDB COMMENT='实验组员（参与者）';

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

drop table if exists `operate_transfers`;
CREATE TABLE IF NOT EXISTS `operate_transfers`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_transfers_id` varchar(64) DEFAULT NULL COMMENT '操作机构转入转出记录ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `form_org_id` varchar(64) DEFAULT NULL COMMENT '转出机构ID',
    `form_org_name` varchar(64) DEFAULT NULL COMMENT '转出机构名称',
    `to_org_id` varchar(64) DEFAULT NULL COMMENT '转入机构ID',
    `to_org_name` varchar(64) DEFAULT NULL COMMENT '转入机构名称',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '案例人物ID',
    `case_account_name` varchar(64) DEFAULT NULL COMMENT '案例人物名',
    `account_id` varchar(64) DEFAULT NULL COMMENT '操作人员ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '操作人员名称',
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
    `account_id` varchar(64) DEFAULT NULL COMMENT '操作账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '操作人员名称',
    `operate_id` varchar(64) DEFAULT NULL COMMENT '操作ID',
    `operate_type` varchar(64) DEFAULT NULL COMMENT '操作类型[答题,考试,事件...]',
    `operate_answer` varchar(64) DEFAULT NULL COMMENT '操作答案',
    `score` varchar(64) DEFAULT NULL COMMENT '分数',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_result_id` (`operate_result_id`)
) ENGINE=InnoDB COMMENT='操作结果';

drop table if exists `operate_indictar`;
CREATE TABLE IF NOT EXISTS `operate_indictar`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `operate_indictar_id` varchar(64) DEFAULT NULL COMMENT '学生操作指标记录表ID',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实列ID',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组ID',
    `operate_account_id` varcahr DEFAULT NULL COMMENT '操作人ID',
    `operate_account_name` varcahr DEFAULT NULL COMMENT '操作人名',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '案例人物',
    `case_account_name` varcahr DEFAULT NULL COMMENT '案例人名',
    `operate_type` varchar(64) DEFAULT NULL COMMENT '操作[干预]类型',
    `operate_source_id` varchar(64) DEFAULT NULL COMMENT '干预或事件处理id',
    `indactor_instance_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `indactor_name` varchar(64) DEFAULT NULL COMMENT '指标名称',
    `indactor_code` varchar(64) DEFAULT NULL COMMENT '指标',
    `indactor_org_val` varchar(64) DEFAULT NULL COMMENT '记录原值',
    `indactor_inc_val` varchar(64) DEFAULT NULL COMMENT '记录变化值',
    `indactor_val` varchar(64) DEFAULT NULL COMMENT '记录最终值',
    `indactor_unit` varchar(64) DEFAULT NULL COMMENT '指标单位',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_indictar_id` (`operate_indictar_id`)
) ENGINE=InnoDB COMMENT='学生操作指标记录表';

drop table if exists `operate_intervene`;
CREATE TABLE IF NOT EXISTS `operate_intervene`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `operate_intervene_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `experiment_instance_id` varchar(64) DEFAULT NULL COMMENT '实验实例id',
    `experiment_group_id` varchar(64) DEFAULT NULL COMMENT '实验小组id',
    `operate_account_id` varcahr DEFAULT NULL COMMENT '操作人id',
    `operate_account_name` varcahr DEFAULT NULL COMMENT '操作人名',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '案例人物',
    `case_account_name` varcahr DEFAULT NULL COMMENT '案例人名',
    `periods` integer(2) DEFAULT NULL COMMENT '期数',
    `operate_type` varchar(64) DEFAULT NULL COMMENT '操作[干预]类型 1-饮食 2-运动 3-心理 4-治疗',
    `operate_value_json` varchar(64) DEFAULT NULL COMMENT '学生输入值json',
    `operate_context_json` blob DEFAULT NULL COMMENT '状态完整快照json',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_operate_intervene_id` (`operate_intervene_id`)
) ENGINE=InnoDB COMMENT='学生干预操作记录';

drop table if exists `question_instance`;
CREATE TABLE IF NOT EXISTS `question_instance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `pid` bigint(19) DEFAULT NULL COMMENT '父ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `question_section_dimension_id` varchar(64) DEFAULT NULL COMMENT '问题集维度ID',
    `categ_id` varchar(64) DEFAULT NULL COMMENT '类别ID',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `categ_name_path` varchar(512) DEFAULT NULL COMMENT '类别name路径',
    `input_type` varchar(64) DEFAULT NULL COMMENT '题目答题输入类型[input,select,text]',
    `question_type` varchar(64) DEFAULT NULL COMMENT '题目答题类型[单选|多选|判断|主观|材料]',
    `question_title` varchar(64) DEFAULT NULL COMMENT '问题标题',
    `question_descr` text(65535) DEFAULT NULL COMMENT '问题描述',
    `enabled` tinyint(4) DEFAULT NULL COMMENT '状态',
    `sequence` integer(2) DEFAULT NULL COMMENT '排序',
    `source` varchar(64) DEFAULT NULL COMMENT '来源',
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
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `q` varchar(64) DEFAULT NULL COMMENT '',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `option_title` varchar(64) DEFAULT NULL COMMENT '选项标题',
    `option_value` varchar(64) DEFAULT NULL COMMENT '选项值',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_q` (`q`)
) ENGINE=InnoDB COMMENT='问题-选项';

drop table if exists `question_answers`;
CREATE TABLE IF NOT EXISTS `question_answers`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `q` varchar(64) DEFAULT NULL COMMENT '',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `q` varchar(64) DEFAULT NULL COMMENT '',
    `option_title` varchar(64) DEFAULT NULL COMMENT '选项标题',
    `option_value` varchar(64) DEFAULT NULL COMMENT '问题的答案',
    `right` tinyint(4) DEFAULT NULL COMMENT '是否是正确答案[0:错误，1:正确]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_q` (`q`)
) ENGINE=InnoDB COMMENT='问题-答案';

drop table if exists `question_score`;
CREATE TABLE IF NOT EXISTS `question_score`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_score_id` varchar(64) DEFAULT NULL COMMENT '问题-分数ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `biz_id` varchar(64) DEFAULT NULL COMMENT '业务ID',
    `question_instance_id` varchar(64) DEFAULT NULL COMMENT '问题ID',
    `q` varchar(64) DEFAULT NULL COMMENT '',
    `q` varchar(64) DEFAULT NULL COMMENT '',
    `score` float(11,2) DEFAULT NULL COMMENT '分数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_question_score_id` (`question_score_id`)
) ENGINE=InnoDB COMMENT='';

drop table if exists `question_section`;
CREATE TABLE IF NOT EXISTS `question_section`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `categ_id` varchar(64) DEFAULT NULL COMMENT '类别Id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别Id路径',
    `categ_name_path` varchar(256) DEFAULT NULL COMMENT '类别名路径',
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
    `question_section_dimension_id` varchar(64) DEFAULT NULL COMMENT '维度ID',
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
) ENGINE=InnoDB COMMENT='问题集-维度';

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
) ENGINE=InnoDB COMMENT='问题集-答题记录';

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
) ENGINE=InnoDB COMMENT='问题集-答题记录项';

drop table if exists `case_instance`;
CREATE TABLE IF NOT EXISTS `case_instance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchcar DEFAULT NULL COMMENT '案例ID',
    `case_name` varchar(64) DEFAULT NULL COMMENT '案例名称',
    `case_pic` varchar(64) DEFAULT NULL COMMENT '案例图片',
    `case_type` varchar(64) DEFAULT NULL COMMENT '案例类型',
    `account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号Id',
    `account_name` varchar(64) DEFAULT NULL COMMENT '创建者姓名',
    `descr` text(65535) DEFAULT NULL COMMENT '背景描述',
    `guide` text(65535) DEFAULT NULL COMMENT '指导描述',
    `state` integer(2) DEFAULT NULL COMMENT '案例状态[0:未发布|1:发布]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_instance_id` (`case_instance_id`)
) ENGINE=InnoDB COMMENT='案例实例';

drop table if exists `case_notice`;
CREATE TABLE IF NOT EXISTS `case_notice`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_notice_id` varchar(64) DEFAULT NULL COMMENT '案例通知ID',
    `case_instance_id` varchcar DEFAULT NULL COMMENT '案例ID',
    `notice_name` varchar(64) DEFAULT NULL COMMENT '公告名称',
    `notice_content` varchar(64) DEFAULT NULL COMMENT '公告内容',
    `periods` varchar(64) DEFAULT NULL COMMENT '期数',
    `period_sequence` tinyint(4) DEFAULT NULL COMMENT '期数排序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_notice_id` (`case_notice_id`)
) ENGINE=InnoDB COMMENT='案例公告';

drop table if exists `case_scheme`;
CREATE TABLE IF NOT EXISTS `case_scheme`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchcar DEFAULT NULL COMMENT '案例ID',
    `case_scheme_id` varchar(64) DEFAULT NULL COMMENT '方案ID',
    `categ_id` varchar(64) DEFAULT NULL COMMENT '类别ID',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '类别名',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '类别ID路径',
    `categ_name_path` varchar(256) DEFAULT NULL COMMENT '类别name路径',
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
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_scheme_id` (`case_scheme_id`)
) ENGINE=InnoDB COMMENT='案例方案';

drop table if exists `case_scheme_result`;
CREATE TABLE IF NOT EXISTS `case_scheme_result`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchcar DEFAULT NULL COMMENT '案例ID',
    `case_scheme_result_id` varchar(64) DEFAULT NULL COMMENT '案例方案结果ID',
    `case_scheme_id` varchar(64) DEFAULT NULL COMMENT '方案ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_section_result_id` varchar(64) DEFAULT NULL COMMENT '答题记录ID',
    `account_id` varchar(64) DEFAULT NULL COMMENT '答题者账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '答题者Name',
    `question_instance_ids` text(65535) DEFAULT NULL COMMENT '问题ids[1,2]',
    `status` tinyint(4) DEFAULT NULL COMMENT '状态[0-未开始|1-进行中|2-已完成]',
    `duration` integer(2) DEFAULT NULL COMMENT '持续时间[min]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_scheme_result_id` (`case_scheme_result_id`)
) ENGINE=InnoDB COMMENT='案例方案结果';

drop table if exists `case_questionnaire`;
CREATE TABLE IF NOT EXISTS `case_questionnaire`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_questionnaire_id` varchar(64) DEFAULT NULL COMMENT '案例问卷ID',
    `periods` varchar(64) DEFAULT NULL COMMENT '期数',
    `period_sequence` tinyint(4) DEFAULT NULL COMMENT '期数排序',
    `allot_mode` varchar(64) DEFAULT NULL COMMENT '分配方式',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_count` integer(2) DEFAULT NULL COMMENT '题数',
    `question_section_structure` varchar(64) DEFAULT NULL COMMENT '题型结构',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_questionnaire_id` (`case_questionnaire_id`)
) ENGINE=InnoDB COMMENT='案例问卷';

drop table if exists `case_questionnaire_result`;
CREATE TABLE IF NOT EXISTS `case_questionnaire_result`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_questionnaire_result_id` varchar(64) DEFAULT NULL COMMENT '案例问卷结果ID',
    `case_questionnaire_id` varchar(64) DEFAULT NULL COMMENT '案例问卷ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集ID',
    `question_section_result_id` varchar(64) DEFAULT NULL COMMENT '答题记录ID',
    `account_id` varchar(64) DEFAULT NULL COMMENT '答题者账号ID',
    `account_name` varchar(64) DEFAULT NULL COMMENT '答题者Name',
    `status` tinyint(4) DEFAULT NULL COMMENT '状态[0-未开始|1-进行中|2-已完成]',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_questionnaire_result_id` (`case_questionnaire_result_id`)
) ENGINE=InnoDB COMMENT='案例问卷结果';

drop table if exists `case_setting`;
CREATE TABLE IF NOT EXISTS `case_setting`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_instance_id` varchar(64) DEFAULT NULL COMMENT '案例ID',
    `case_setting_id` varchar(64) DEFAULT NULL COMMENT '案例问卷设置ID',
    `score_mode` varchar(64) DEFAULT NULL COMMENT '记分方式[少选不得分|少选得一半分]',
    `allot_mode` varchar(64) DEFAULT NULL COMMENT '分配方式',
    `ext` varchar(64) DEFAULT NULL COMMENT '额外配置[JSON]',
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
    `case_instance_id` varchcar DEFAULT NULL COMMENT '案例ID',
    `org_id` varchar(64) DEFAULT NULL COMMENT '机构ID[uim域]',
    `org_name` varchar(64) DEFAULT NULL COMMENT '机构名称',
    `scene` varchar(64) DEFAULT NULL COMMENT '场景',
    `handbook` varchar(64) DEFAULT NULL COMMENT '操作手册',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='案例机构';

drop table if exists `case_org_function`;
CREATE TABLE IF NOT EXISTS `case_org_function`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `case_org_function_id` varchar(64) DEFAULT NULL COMMENT '机构功能ID',
    `function_name` varchar(64) DEFAULT NULL COMMENT '功能|菜单名称',
    `function_icon` varchar(64) DEFAULT NULL COMMENT '功能图标',
    `org_name` varchar(64) DEFAULT NULL COMMENT '机构名称',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
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
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='机构功能指标点';

drop table if exists `case_org_fee`;
CREATE TABLE IF NOT EXISTS `case_org_fee`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `case_org_indicator_id` varchar(64) DEFAULT NULL COMMENT '机构功能指标点ID',
    `case_org_fee_id` varchar(64) DEFAULT NULL COMMENT '案例机构费用ID',
    `case_instance_id` varchcar DEFAULT NULL COMMENT '案例ID',
    `case_org_id` varchar(64) DEFAULT NULL COMMENT '案例机构ID',
    `org_function_id` varchar(64) DEFAULT NULL COMMENT '机构功能ID',
    `function_name` varchar(64) DEFAULT NULL COMMENT '功能|菜单名称',
    `reimburse_ratio` double(11,2) DEFAULT NULL COMMENT '报销比例',
    `fee` decimal(11,2) DEFAULT NULL COMMENT '费用',
    `fee_code` varchar(64) DEFAULT NULL COMMENT '费用Code',
    `fee_name` varchar(64) DEFAULT NULL COMMENT '费用名称',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='案例机构费用';

drop table if exists `case_event`;
CREATE TABLE IF NOT EXISTS `case_event`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `case_event_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `case_instance_id` varchcar DEFAULT NULL COMMENT '案例ID',
    `case_account_id` varchar(64) DEFAULT NULL COMMENT '人物id',
    `case_account_name` varchar(64) DEFAULT NULL COMMENT '人物名称',
    `event_id` varchar(64) DEFAULT NULL COMMENT '数据库事件id',
    `case_event_name` varchar(64) DEFAULT NULL COMMENT '事件名称',
    `pic` varchar(64) DEFAULT NULL COMMENT '图片',
    `event_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
    `descr` varchar(64) DEFAULT NULL COMMENT '事件说明',
    `create_account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号',
    `create_account_name` varchar(64) DEFAULT NULL COMMENT '创建者名称',
    `trigger_period` varchar(64) DEFAULT NULL COMMENT '触发期数',
    `trigger_span` varchar(64) DEFAULT NULL COMMENT '触发时间段 1-前期 2-中期 3-后期',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `trigger_type` tinyint(4) DEFAULT NULL COMMENT '触发类型 1-事件触发 2-条件触发',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_event_id` (`case_event_id`)
) ENGINE=InnoDB COMMENT='案例人物事件';

drop table if exists `case_event_eval`;
CREATE TABLE IF NOT EXISTS `case_event_eval`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `case_event_eval_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `case_event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_descr` varchar(64) DEFAULT NULL COMMENT '触发条件描述',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_event_eval_id` (`case_event_eval_id`)
) ENGINE=InnoDB COMMENT='案例人物事件触发条件';

drop table if exists `case_event_action`;
CREATE TABLE IF NOT EXISTS `case_event_action`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `case_event_action_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `case_event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `action_desc` varchar(64) DEFAULT NULL COMMENT '处理描述',
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
    `case_event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `init_flag` tinyint(4) DEFAULT NULL COMMENT '初始指标影响标记，0-否 1-是',
    `case_event_action_id` varchar(64) DEFAULT NULL COMMENT '事件选项id',
    `indicator_instace_id` varchar(64) DEFAULT NULL COMMENT '指标id',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_descr` varchar(64) DEFAULT NULL COMMENT '公式描述',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_case_event_action_indicator_id` (`case_event_action_indicator_id`)
) ENGINE=InnoDB COMMENT='人物事件处理选项影响指标';

drop table if exists `indicator_category`;
CREATE TABLE IF NOT EXISTS `indicator_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `pid` bigint(19) DEFAULT NULL COMMENT '父ID',
    `category_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `category_code` varchar(64) DEFAULT NULL COMMENT '分类code',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标类别';

drop table if exists `indicator_instance`;
CREATE TABLE IF NOT EXISTS `indicator_instance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `indicator_name` varchar(64) DEFAULT NULL COMMENT '指标名称',
    `indicator_code` varchar(64) DEFAULT NULL COMMENT '指标code',
    `unit` varchar(64) DEFAULT NULL COMMENT '单位',
    `core` tinyint(4) DEFAULT NULL COMMENT '0-非核心指标，1-核心指标',
    `experssion` varchar(64) DEFAULT NULL COMMENT '指标表达式[拆包]',
    `raw_experssion` varchar(64) DEFAULT NULL COMMENT '未拆包指标表达式',
    `descr` varchar(64) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标';

drop table if exists `indicator_category_ref`;
CREATE TABLE IF NOT EXISTS `indicator_category_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_category_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标分类与指标关联关系';

drop table if exists `indicator_ref`;
CREATE TABLE IF NOT EXISTS `indicator_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `ref_indicator_id` varchar(64) DEFAULT NULL COMMENT '引用这个指标的指标ID',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标-引用';

drop table if exists `indicator_var`;
CREATE TABLE IF NOT EXISTS `indicator_var`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_var_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `db_name` varchar(64) DEFAULT NULL COMMENT '数据库名',
    `tb_name` varchar(64) DEFAULT NULL COMMENT '表名',
    `var_name` varchar(64) DEFAULT NULL COMMENT '变量名',
    `var_code` varchar(64) DEFAULT NULL COMMENT '变量code',
    `periods` varchar(64) DEFAULT NULL COMMENT '期数，如果多期用[,]分割',
    `descr` varchar(64) DEFAULT NULL COMMENT '描述',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标|变量规则[校验]';

drop table if exists `indicator_val`;
CREATE TABLE IF NOT EXISTS `indicator_val`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_val_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `current_val` varchar(64) DEFAULT NULL COMMENT '当前值',
    `min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `def` varchar(64) DEFAULT NULL COMMENT '默认值',
    `descr` varchar(64) DEFAULT NULL COMMENT '描述',
    `periods` varchar(64) DEFAULT NULL COMMENT '期数',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='查看指标基本信息类';

drop table if exists `indicator_view_base_info_desc`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_desc`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_desc_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `name` varchar(64) DEFAULT NULL COMMENT '指标基本信息描述表名称',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标基本信息描述表';

drop table if exists `indicator_view_base_info_desc_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_desc_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_desc_ref_id` varchar(64) DEFAULT NULL COMMENT '',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_view_base_info_desc_id` varchar(64) DEFAULT NULL COMMENT '指标描述表ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标';

drop table if exists `indicator_view_base_info_monitor`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_monitor`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_monitor_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `name` varchar(64) DEFAULT NULL COMMENT '指标基本信息监测表名称',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标基本信息监测表';

drop table if exists `indicator_view_base_info_monitor_content`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_monitor_content`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_monitor_content_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `name` varchar(64) DEFAULT NULL COMMENT '监测内容名称',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标基本信息监测内容表';

drop table if exists `indicator_view_base_info_monitor_content_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_monitor_content_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_monitor_content_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标基本信息监测内容表与指标关联关系';

drop table if exists `indicator_view_base_info_single`;
CREATE TABLE IF NOT EXISTS `indicator_view_base_info_single`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_base_info_single_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='查看指标监测随访类';

drop table if exists `indicator_view_monitor_followup_followup_content`;
CREATE TABLE IF NOT EXISTS `indicator_view_monitor_followup_followup_content`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_monitor_followup_followup_content_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_view_monitor_followup_id` varchar(64) DEFAULT NULL COMMENT '查看指标监测随访类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '随访内容名称',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='查看指标监测随访内容';

drop table if exists `indicator_view_monitor_followup_content_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_monitor_followup_content_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_monitor_followup_content_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_view_monitor_followup_followup_content_id` varchar(64) DEFAULT NULL COMMENT '指标监测随访内容ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='指标监测随访随访内容表与指标关联关系';

drop table if exists `indicator_view_physical_exam`;
CREATE TABLE IF NOT EXISTS `indicator_view_physical_exam`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_physical_exam_id` varchar(64) DEFAULT NULL COMMENT '',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_category_id` varchar(64) DEFAULT NULL COMMENT '指标分类ID',
    `name` varchar(64) DEFAULT NULL COMMENT '体格检查名称',
    `type` varchar(64) DEFAULT NULL COMMENT '体格检查类别',
    `fee` double(11,2) DEFAULT NULL COMMENT '费用',
    `result_analysis` varchar(64) DEFAULT NULL COMMENT '结果解析',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='查看指标体格检查类';

drop table if exists `indicator_view_physical_exam_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_physical_exam_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_physical_exam_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='查看指标辅助检查类';

drop table if exists `indicator_view_support_exam_ref`;
CREATE TABLE IF NOT EXISTS `indicator_view_support_exam_ref`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_view_support_exam_ref_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `indicator_id` varchar(64) DEFAULT NULL COMMENT '指标ID',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='查看指标辅助检查关联指标';

drop table if exists `indicator_judge_risk_factor`;
CREATE TABLE IF NOT EXISTS `indicator_judge_risk_factor`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_judge_risk_factor_id` varchar(64) DEFAULT NULL COMMENT '',
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
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='判断指标健康问题';

drop table if exists `indicator_judge_health_guidance`;
CREATE TABLE IF NOT EXISTS `indicator_judge_health_guidance`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `indicator_judge_health_guidance_id` varchar(64) DEFAULT NULL COMMENT '',
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
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='判断指标疾病问题';

drop table if exists `risk_category`;
CREATE TABLE IF NOT EXISTS `risk_category`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `risk_category_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `risk_category_name` varchar(64) DEFAULT NULL COMMENT '风控',
    `order` integer(2) DEFAULT NULL COMMENT '展示顺序',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='风控类别';

drop table if exists `risk_model`;
CREATE TABLE IF NOT EXISTS `risk_model`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `risk_model_id` varchar(64) DEFAULT NULL COMMENT '风控模型ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `risk_category_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `name` varchar(64) DEFAULT NULL COMMENT '模型名称',
    `status` tinyint(4) DEFAULT NULL COMMENT '0-禁用，1-启用',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='风控模型';

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
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='危险分数';

drop table if exists `survey`;
CREATE TABLE IF NOT EXISTS `survey`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `survey_id` archar DEFAULT NULL COMMENT '分布式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `survey_name` varchar(64) DEFAULT NULL COMMENT '问卷名称',
    `categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '题库id',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `descr` varchar(64) DEFAULT NULL COMMENT '问卷说明',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='评估问卷';

drop table if exists `survey_eval`;
CREATE TABLE IF NOT EXISTS `survey_eval`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `survey_eval_id` varchar(64) DEFAULT NULL COMMENT '公式id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `survey_id` varchar(64) DEFAULT NULL COMMENT '评估id',
    `dimension_id` varchar(64) DEFAULT NULL COMMENT '评估维度id',
    `descr` varchar(64) DEFAULT NULL COMMENT '公式描述',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='问卷评估公式';

drop table if exists `survey_report`;
CREATE TABLE IF NOT EXISTS `survey_report`(
    `bigint` varchar(64) DEFAULT NULL COMMENT '',
    `survey_report_id` varchar(64) DEFAULT NULL COMMENT '报告id',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `survey_id` varchar(64) DEFAULT NULL COMMENT '问卷id',
    `report_name` varchar(64) DEFAULT NULL COMMENT '报告名称',
    `min_score` integer(2) DEFAULT NULL COMMENT '分数段[最小]',
    `max_score` integer(2) DEFAULT NULL COMMENT '分数段[最大]',
    `descr` varchar(64) DEFAULT NULL COMMENT '报告说明',
    `result` varchar(64) DEFAULT NULL COMMENT '评估结果',
    `suggestion` varchar(64) DEFAULT NULL COMMENT '相关建议',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
) ENGINE=InnoDB COMMENT='问卷报告';

drop table if exists `evaluate_questionnaire`;
CREATE TABLE IF NOT EXISTS `evaluate_questionnaire`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库ID',
    `evaluate_questionnaire_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `app_id` varchar(64) DEFAULT NULL COMMENT '应用ID',
    `question_section_id` varchar(64) DEFAULT NULL COMMENT '问题集',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
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
    PRIMARY KEY (`id`) 
) ENGINE=InnoDB COMMENT='评估报告管理';

drop table if exists `event_categ`;
CREATE TABLE IF NOT EXISTS `event_categ`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `event_categ_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `categ_pid` varchar(64) DEFAULT NULL COMMENT '分布式父id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '名称',
    `section` varchar(64) DEFAULT NULL COMMENT '类别key',
    `extend` varchar(64) DEFAULT NULL COMMENT '扩展属性',
    `depth` tinyint(4) DEFAULT NULL COMMENT '层级',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布式id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '名称路径',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_event_categ_id` (`event_categ_id`)
) ENGINE=InnoDB COMMENT='事件类别管理';

drop table if exists `event`;
CREATE TABLE IF NOT EXISTS `event`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `event_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `event_name` varchar(64) DEFAULT NULL COMMENT '突发事件名称',
    `pic` varchar(64) DEFAULT NULL COMMENT '图片',
    `event_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
    `state` tinyint(4) DEFAULT NULL COMMENT '状态 0-启用 1-停用',
    `descr` varchar(64) DEFAULT NULL COMMENT '事件说明',
    `create_account_id` varchar(64) DEFAULT NULL COMMENT '创建者账号',
    `create_account_name` varchar(64) DEFAULT NULL COMMENT '创建者名称',
    `trigger_type` tinyint(4) DEFAULT NULL COMMENT '触发类型 1-事件触发 2-条件触发',
    `trigger_period` varchar(64) DEFAULT NULL COMMENT '触发期数',
    `trigger_span` varchar(64) DEFAULT NULL COMMENT '触发时间段 1-前期 2-中期 3-后期',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_event_id` (`event_id`)
) ENGINE=InnoDB COMMENT='突发事件';

drop table if exists `event_eval`;
CREATE TABLE IF NOT EXISTS `event_eval`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `event_eval_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
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
    `event_id` varchar(64) DEFAULT NULL COMMENT '事件id',
    `init_flag` tinyint(4) DEFAULT NULL COMMENT '初始指标影响标记，0-否 1-是',
    `event_action_id` varchar(64) DEFAULT NULL COMMENT '事件选项id',
    `indicator_instace_id` varchar(64) DEFAULT NULL COMMENT '指标id',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_descr` varchar(64) DEFAULT NULL COMMENT '公式描述',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
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
    `intervene_category_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `categ_pid` varchar(64) DEFAULT NULL COMMENT '分布式父id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '名称',
    `section` varchar(64) DEFAULT NULL COMMENT '类别key',
    `extend` varchar(64) DEFAULT NULL COMMENT '扩展属性',
    `depth` tinyint(4) DEFAULT NULL COMMENT '层级',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布式id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '名称路径',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_intervene_category_id` (`intervene_category_id`)
) ENGINE=InnoDB COMMENT='干预类别管理';

drop table if exists `food_recommend`;
CREATE TABLE IF NOT EXISTS `food_recommend`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_recommend_id` varchar(64) DEFAULT NULL COMMENT '分布式id',
    `instance_type` tinyint(4) DEFAULT NULL COMMENT '主体类型,1-营养成分 2-食材一级分类',
    `instance_id` varchar(64) DEFAULT NULL COMMENT '主体id ',
    `instance_name` varchar(64) DEFAULT NULL COMMENT '主体名称',
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
    `food_material_name` varchar(64) DEFAULT NULL COMMENT '食材名称',
    `pic` varchar(64) DEFAULT NULL COMMENT '图片',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
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
    `food_material_id` varchar(64) DEFAULT NULL COMMENT '食材id',
    `indicator_instace_id` varchar(64) DEFAULT NULL COMMENT '指标id',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_descr` varchar(64) DEFAULT NULL COMMENT '公式描述',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
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
    `food_material_id` varchar(64) DEFAULT NULL COMMENT '食材id',
    `indicator_instace_id` varchar(64) DEFAULT NULL COMMENT '营养指标',
    `nutrient_name` varchar(64) DEFAULT NULL COMMENT '营养成分名称',
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
    `food_dishes_name` varchar(64) DEFAULT NULL COMMENT '菜肴名称',
    `materials_desc` varchar(64) DEFAULT NULL COMMENT '食材含量描述',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
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
    UNIQUE KEY `unique_food_dishes_id` (`food_dishes_id`)
) ENGINE=InnoDB COMMENT='菜肴';

drop table if exists `food_dishes_material`;
CREATE TABLE IF NOT EXISTS `food_dishes_material`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_dishes_material_id` varchar(64) DEFAULT NULL COMMENT '关联id',
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
    `food_dishes_id` varchar(64) DEFAULT NULL COMMENT '菜肴id',
    `indicator_instace_id` varchar(64) DEFAULT NULL COMMENT '营养指标',
    `nutrient_name` varchar(64) DEFAULT NULL COMMENT '营养成分名称',
    `weight` varchar(64) DEFAULT NULL COMMENT '重量',
    `energy` varchar(64) DEFAULT NULL COMMENT '能量',
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
    `food_cookbook_name` varchar(64) DEFAULT NULL COMMENT '食谱名称',
    `materials_desc` varchar(64) DEFAULT NULL COMMENT '食材含量描述',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
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
    `food_cookbook_detail_id 分布式id` varchar(64) DEFAULT NULL COMMENT '',
    `food_cookbook_id` varchar(64) DEFAULT NULL COMMENT '食谱id',
    `meal_time` varchar(64) DEFAULT NULL COMMENT '进餐时间，早|早加|午|午加|晚|晚加',
    `instance_type` varchar(64) DEFAULT NULL COMMENT '明细类型，1-菜肴 2-食材',
    `instance_id` varchar(64) DEFAULT NULL COMMENT '菜肴、食材id',
    `instance_name` varchar(64) DEFAULT NULL COMMENT '菜肴、食材名称',
    `weight` varchar(64) DEFAULT NULL COMMENT '重量',
    `energy` varchar(64) DEFAULT NULL COMMENT '能量',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_food_cookbook_detail_id 分布式id` (`food_cookbook_detail_id 分布式id`)
) ENGINE=InnoDB COMMENT='食谱食材';

drop table if exists `food_cookbook_nutrient`;
CREATE TABLE IF NOT EXISTS `food_cookbook_nutrient`(
    `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '数据库id',
    `food_cookbook_nutrient_id` varchar(64) DEFAULT NULL COMMENT '关联id',
    `food_cookbook_id` varchar(64) DEFAULT NULL COMMENT '食谱id',
    `indicator_instace_id` varchar(64) DEFAULT NULL COMMENT '营养指标',
    `nutrient_name` varchar(64) DEFAULT NULL COMMENT '营养成分名称',
    `weight` varchar(64) DEFAULT NULL COMMENT '重量',
    `energy` varchar(64) DEFAULT NULL COMMENT '能量',
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
    `sport_plan_name` varchar(64) DEFAULT NULL COMMENT '运动方案名称',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
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
    `sport_item_name` varchar(64) DEFAULT NULL COMMENT '运动项目名称',
    `pic` varchar(64) DEFAULT NULL COMMENT '图片',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
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
    `sport_item_id` varchar(64) DEFAULT NULL COMMENT '运动项目id',
    `indicator_instance_id` varchar(64) DEFAULT NULL COMMENT '分布式ID',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_descr` varchar(64) DEFAULT NULL COMMENT '公式描述',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
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
    `treat_item_name` varchar(64) DEFAULT NULL COMMENT '治疗名称',
    `treat_item_type` tinyint(4) DEFAULT NULL COMMENT '治疗类型 1-心理治疗 2-医学治疗',
    `intervene_categ_id` varchar(64) DEFAULT NULL COMMENT '分类id',
    `categ_name` varchar(64) DEFAULT NULL COMMENT '分类名称',
    `categ_id_path` varchar(64) DEFAULT NULL COMMENT '分布id路径',
    `categ_name_path` varchar(64) DEFAULT NULL COMMENT '分类名称路径',
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
    `treat_item_id` varchar(64) DEFAULT NULL COMMENT '治疗项目id',
    `indicator_instace_id` varchar(64) DEFAULT NULL COMMENT '指标id',
    `expression_descr` varchar(64) DEFAULT NULL COMMENT '公式描述',
    `expression` varchar(64) DEFAULT NULL COMMENT '表达式',
    `expression_vars` varchar(64) DEFAULT NULL COMMENT '表达式涉及变量',
    `expression_min` varchar(64) DEFAULT NULL COMMENT '最小值',
    `expression_max` varchar(64) DEFAULT NULL COMMENT '最大值',
    `seq` integer(2) DEFAULT NULL COMMENT '排序号',
    `deleted` tinyint(4) DEFAULT NULL COMMENT '逻辑删除',
    `dt` datetime DEFAULT NULL COMMENT '时间戳',
    PRIMARY KEY (`id`) ,
    UNIQUE KEY `unique_treat_item_indicator_id` (`treat_item_indicator_id`)
) ENGINE=InnoDB COMMENT='治疗项目关联指标';


