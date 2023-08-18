<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>健康沙盘报告</title>
    <style>
        .pos {
            position: absolute;
            left: 200px;
            top: 5px;
            width: 200px;
            font-size: 10px;
        }

        #header-right {
            display: block;
            color: rgb(126, 132, 133);
            position: running(header-right);
        }

        @page {
            size: 8.5in 11in;
            @top-right {
                content: element(header-right);
            };
            /*@bottom-center {
                content : "Page " counter(page) " of " counter(pages);
            };	 */
            @bottom-center {
                content: element(footer);
            }
        }

        #footer {
            position: running(footer);
        }

        #pages:before {
            content: counter(page);
        }

        #pages:after {
            content: counter(pages);
        }

        .pageNext {
            page-break-after: always;
        }
    </style>
    <style>
        body {
            font-family: SimHei;
        }

        .bold {
            font-weight: bold;
        }

        .text-center {
            text-align: center;
        }

        .text-right {
            text-align: right;
        }

        .wd-700 {
            width: 700px;
        }

        .font-blue {
            color: rgb(42, 146, 185);
        }

        .background-singular-blue {
            background-color: rgb(187, 221, 233);
        }

        .background-even-blue {
            background-color: rgb(218, 226, 240);
        }

        .pageTitle {
            font-size: 32px;
            color: rgb(42, 147, 186)
        }

        .designContent {
            background: #fff;
        }

        .introTitle {
            padding: 0 16px;
            border-left: 4px solid #bbbfc4;
            margin-bottom: 24px;
        }

        .planQuestion .title {
            font-size: 25px;
            color: rgba(0, 0, 0, 0.85);
            line-height: 32px;
            margin-bottom: 24px;
        }

        .planQuestion .isSub {
            font-size: 22px;
            margin-bottom: 16px;
        }

        .planQuestion .isSubSub {
            font-size: 18px;
            margin-bottom: 20px;
        }

        .answerContent {
            border-radius: 2px;
            border: 1px solid rgba(0, 0, 0, 0.15);
            padding: 6px 12px;
            color: rgba(42, 46, 54, 0.75);
        }

        .groupDiv {
            text-align: left;
            font-size: 13px;
            margin-top: 30px;
            margin-left: 450px;
        }
    </style>
</head>
<body>
<div id="header-right" class="bold" style="font-size:16px;">${baseInfo.title}</div>
<div id="footer">
    <div align="center" style="font-size:14px;">${baseInfo.copyRight} <span id="pages"> / </span></div>
</div>
<div class="page">
    <img src="data:image/jpg;base64,${baseInfo.logoImg}" align="center" width="740px" style="width: 740px;margin-top:60px;margin-bottom: 200px"/>
    <div align="center">
        <div class="pageTitle " style="margin-top:60px">${baseInfo.title}</div>
        <div class="pageTitle " style="margin-top:38px">健康沙盘</div>
        <img src="data:image/jpg;base64,${baseInfo.coverImg}" style="width: 760px;margin-top:38px"/>
    </div>
    <div align="right">
        <div class="groupDiv">组数：第${groupInfo.groupNo!""}组</div>
        <div class="groupDiv">组名：${groupInfo.groupName!""}</div>
        <div class="groupDiv">成员：<#list groupInfo.groupMembers as item>${item}<#if item_has_next>、</#if></#list></div>
        <div class="groupDiv">实验社区：${groupInfo.caseName!""}</div>
        <div class="groupDiv">
            实验日期：${groupInfo.exptStartDate!""}
        </div>
        <div class="groupDiv">案例数量：${groupInfo.caseNum!0}</div>
    </div>
</div>
<div class="pageNext"></div>
<div class="page">

    <div class="font-blue bold" style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">一 实验总得分
    </div>
    <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">总得分</div>
    <table class="wd-700 text-center" align="center">
        <tr class="background-singular-blue">
            <td width="20%">健康指数得分</td>
            <td width="20%">知识考点得分</td>
            <td width="20%">医疗占比得分</td>
            <td width="20%">总分</td>
            <td width="20%">总排名</td>
        </tr>
        <tr class="background-even-blue">
            <td>${scoreInfo.totalScore.healthIndexScore}</td>
            <td>${scoreInfo.totalScore.knowledgeScore}</td>
            <td>${scoreInfo.totalScore.treatmentPercentScore}</td>
            <td>${scoreInfo.totalScore.totalScore}</td>
            <td>${scoreInfo.totalScore.totalRanking}</td>
        </tr>
    </table>
    <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">每期得分</div>
    <table class="wd-700 text-center" align="center">
        <tr class="background-singular-blue">
            <td width="20%">期数</td>
            <td width="16%">健康指数得分</td>
            <td width="16%">知识考点得分</td>
            <td width="16%">医疗占比得分</td>
            <td width="16%">总分</td>
            <td width="16%">排名</td>
        </tr>
        <#list scoreInfo.periodScores as periodScore>
        <tr class="background-even-blue">
            <td>${periodScore_index + 1}</td>
            <td>${periodScore.scoreInfo.healthIndexScore}</td>
            <td>${periodScore.scoreInfo.knowledgeScore}</td>
            <td>${periodScore.scoreInfo.treatmentPercentScore}</td>
            <td>${periodScore.scoreInfo.totalScore}</td>
            <td>${periodScore.scoreInfo.totalRanking!0}</td>
        </tr>
        </#list>
    </table>
    <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">每期权重</div>
    <table class="wd-700 text-center" align="center">
        <tr class="background-singular-blue">
        <#list scoreInfo.periodWeights as periodWeight>
            <td width="20%">第 ${periodWeight?index+1} 期</td>
        </#list>
        </tr>
        <tr
        <#list scoreInfo.periodWeights as periodWeight>
            <td class="background-even-blue">${periodWeight.weight!""}</td>
        </#list>
        </tr>
    </table>
    <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">评分权重</div>
    <table class="wd-700 text-center" align="center">
        <tr class="background-singular-blue">
            <td>健康指数</td>
            <td>知识考点</td>
            <td>医疗占比</td>
        </tr>
        <tr class="background-even-blue">
            <td>${scoreInfo.scoreWeight.healthIndexWeight}</td>
            <td>${scoreInfo.scoreWeight.knowledgeWeight}</td>
            <td>${scoreInfo.scoreWeight.treatmentPercentWeight}</td>
        </tr>
    </table>

    <div class="font-blue bold" style="font-size:18px;margin-top:50px;margin-bottom:25px" align="center">二 实验详情
    </div>
    <#list npcDatas as npc>
        <div style="font-size:18px;margin-top:25px;margin-bottom:25px">案例: ${npc.personName!""}</div>
        <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">基本信息</div>
        <table class="wd-700 text-center" align="center">
            <tr class="background-singular-blue">
                <td style="">案例名称</td>
                <td>性别</td>
                <td>年龄</td>
                <td>疾病类别</td>
            </tr>
            <tr class="background-even-blue">
                <td>${npc.interveneBefores.personName!""}</td>
                <td>${npc.interveneBefores.sex!""}</td>
                <td>${npc.interveneBefores.age!""}</td>
                <td>${npc.interveneBefores.diseaseCateg!""}</td>
            </tr>
        </table>
        <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">危险因素评价</div>
        <div style="font-size:18px;margin-top:25px;margin-bottom:25px">干预前</div>
        <table class="wd-700 text-center" align="center">
            <tr class="background-singular-blue">
                <td style="">死亡原因</td>
                <td>死亡概率（1/10万）</td>
                <td>健康危险因素</td>
                <td>指标值</td>
                <td>危险分数</td>
                <td>组合危险分数</td>
                <td>存在死亡危险</td>
            </tr>
            <#if npc.interveneBefores.riskFactors??>
                <#list npc.interveneBefores.riskFactors as ibrf>
                    <#if ibrf.riskItems??>
                        <#list ibrf.riskItems as ri >
            <tr class="background-even-blue">
                <#if ri?is_first>
                <td rowspan="${ibrf.riskItems?size}">${ibrf.riskName}</td>
                <td rowspan="${ibrf.riskItems?size}">${ibrf.riskDeathProbability}</td>
                <#else>
                <td></td>
                <td></td>
                </#if>
                <td>${ri.itemName}</td>
                <td>${ri.itemValue}</td>
                <td>${ri.riskScore}</td>
                <#if ri?is_first>
                <td rowspan="${ibrf.riskItems?size}">${ibrf.riskScore}</td>
                <td rowspan="${ibrf.riskItems?size}">${ibrf.deathRiskScore}</td>
                <#else>
                <td></td>
                <td></td>
                </#if>
            </tr>
                        </#list>
                    </#if>
                </#list>
            </#if>
        </table>
        <div style="font-size:18px;margin-top:25px;margin-bottom:25px">干预后</div>
        <table class="wd-700 text-center" align="center">
            <tr class="background-singular-blue">
                <td style="">死亡原因</td>
                <td>死亡概率（1/10万）</td>
                <td>健康危险因素</td>
                <td>指标值</td>
                <td>危险分数</td>
                <td>组合危险分数</td>
                <td>存在死亡危险</td>
            </tr>
            <#if npc.interveneAfters.riskFactors??>
                <#list npc.interveneBefores.riskFactors as iarf>
                    <#if iarf.riskItems??>
                        <#list iarf.riskItems as ri >
            <tr class="background-even-blue">
                <#if ri?is_first>
                <td rowspan="${iarf.riskItems?size}">${iarf.riskName}</td>
                <td rowspan="${iarf.riskItems?size}">${iarf.riskDeathProbability}</td>
                <#else>
                <td></td>
                <td></td>
                </#if>
                <td>${ri.itemName}</td>
                <td>${ri.itemValue}</td>
                <td>${ri.riskScore}</td>
                <#if ri?is_first>
                <td rowspan="${iarf.riskItems?size}">${iarf.riskScore}</td>
                <td rowspan="${iarf.riskItems?size}">${iarf.deathRiskScore}</td>
                <#else>
                <td></td>
                <td></td>
                </#if>
            </tr>
                        </#list>
                    </#if>
                </#list>
            </#if>
        </table>

        <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">服务记录</div>
        <table class="wd-700 text-center" align="center">
            <tr class="background-singular-blue">
                <td style="">时间</td>
                <td>服务记录</td>
                <td>标签</td>
            </tr>
            <#if npc.serviceLogs??>
            <#list npc.serviceLogs as serviceLog>
                <tr class="background-even-blue">
                    <td>${serviceLog.dt!""}</td>
                    <td>${serviceLog.descr!""}</td>
                    <td>${serviceLog.lable!""}</td>
                </tr>
            </#list>
            </#if>
        </table>
    </#list>

    <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">三 知识考点</div>
    <#list periodQuestions as pq>
        <div style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">第${pq_index+1}期</div>
        <#list pq as categQuestionnaire>
            <div>${categQuestionnaire.categName}</div>
            <#list categQuestionnaire.questionInfos as questionInfo>
                <div>
                    <div style="font-size:18px;margin-top:25px;margin-bottom:25px;">${questionInfo.questionTitle}</div>
                    <#if questionInfo.children?size==0>
                        <#list questionInfo.questionOptions as option>
                            <div>${option}</div>
                        </#list>
                        <div>你的答案</div>
                        <div>${questionInfo.userAnswer!"无"}</div>
                        <div>参考答案</div>
                        <div>${questionInfo.rightAnswer!"无"}</div>
                        <div>解析</div>
                        <div>${questionInfo.analysis!"无"}</div>
                    <#else >
                        <#list questionInfo.children as child>
                            <div style="font-size:18px;margin-top:25px;margin-bottom:25px;">${child.questionTitle}</div>
                            <#list child.questionOptions as option>
                                <div>${option}</div>
                            </#list>
                            <div>你的答案</div>
                            <div>${child.userAnswer!"无"}</div>
                            <div>参考答案</div>
                            <div>${child.rightAnswer!"无"}</div>
                            <div>解析</div>
                            <div>${child.analysis!"无"}</div>
                        </#list>
                    </#if>
                </div>
            </#list>
        </#list>
    </#list>

</div>
</body>
</html>