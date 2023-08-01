<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>方案设计报告</title>
    <style>
        .pos{
            position:absolute;
            left:200px;
            top:5px;
            width: 200px;
            font-size: 10px;
        }
        #header-right {
            display: block;
            color: rgb(126,132,133);
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
        .pageNext{page-break-after: always;}
    </style>
    <style>
        body {
            font-family:SimHei;
        }
        .bold{
            font-weight:bold;
        }
        .text-center {
            text-align: center;
        }
        .text-right {
            text-align: right;
        }
        .wd-700 {
            width:700px;
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
            color:rgb(42, 147, 186)
        }
        .designContent {
            background: #fff;
        }
        .introTitle{
            padding: 0 16px;
            border-left: 4px solid #bbbfc4;
            margin-bottom: 24px;
        }
        .planQuestion .title{
            font-size: 25px;
            color: rgba(0, 0, 0, 0.85);
            line-height: 32px;
            margin-bottom: 24px;
        }
        .planQuestion .isSub{
            font-size: 22px;
            margin-bottom: 16px;
        }
        .planQuestion .isSubSub{
            font-size: 18px;
            margin-bottom: 20px;
        }
        .answerContent {
            border-radius: 2px;
            border: 1px solid rgba(0, 0, 0, 0.15);
            padding: 6px 12px;
            color: rgba(42, 46, 54, 0.75);
        }
        .groupDiv{
            text-align: left;
            font-size: 13px;
            margin-top: 30px;
            margin-left: 450px;
        }
    </style>
</head>
<body>
<div id="header-right" class="bold" style="font-size:16px;">${baseInfo.title!""}</div>
<div id="footer">
    <div align="center" style="font-size:14px;">${baseInfo.copyRight!""} <span id="pages"> / </span></div>
</div>
<div class="page">
    <img src="data:image/jpg;base64,${baseInfo.logoImg}" align="center" width="740px" style="width: 740px;margin-top:60px;margin-bottom: 200px"/>
    <div align="center">
        <div class="pageTitle " style="margin-top:60px">${baseInfo.title!""}</div>
        <div class="pageTitle " style="margin-top:38px">方案设计</div>
        <img src="data:image/jpg;base64,${baseInfo.coverImg!""}" style="width: 760px;margin-top:38px"/>
    </div>
    <div align="right">
        <div class="groupDiv">组数：第${groupInfo.groupNo!""}组</div>
        <div class="groupDiv">组名：${groupInfo.groupName!""}</div>
        <div class="groupDiv">成员：<#list groupInfo.groupMembers as item>${item}<#if item_has_next>、</#if></#list></div>
        <div class="groupDiv">实验社区：${groupInfo.caseName!""}</div>
        <div class="groupDiv">实验日期：${groupInfo.exptStartDate!""}</div>
    </div>
</div>
<div class="pageNext"></div>
    <div class="page">
        <#if scoreInfo.show>
            <div class="font-blue bold" style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">一、实验总得分</div>
            <table class="wd-700 text-center" align="center">
                <tr class="background-singular-blue">
                    <td width="350px" style="">方案设计得分</td>
                    <td>排名</td>
                </tr>
                <tr class="background-even-blue">
                    <td>${scoreInfo.score}</td>
                    <td>${scoreInfo.rank!0}</td>
                </tr>
            </table>
            <div class="font-blue bold" style="font-size:18px;margin-top:50px;margin-bottom:25px" align="center">二、方案设计报告</div>
        </#if>
        <div class="designContent">
            <div class="intro">
                <div class="introTitle">${schemeInfo.schemeName!""}</div>
                <div class="introTitle">${schemeInfo.schemeTips!""}</div>
                <div class="introContent">${schemeInfo.schemeDescr!""}</div>
            </div>
            <div class="quesiton">
                <#if questionInfos??>
                <#list questionInfos as item>
                <div>
                    <div class="planQuestion">
                        <div class="title">${item_index+1}. ${item.questionTitle!""}</div>
                        <div class="info">${item.questionDescr!""}</div>
                        <#if item.children?size == 0>
                             <div class="answerContent">${item.questionResult!"未作答"}</div>
                        </#if>
                    </div>
                    <#if item.children??>
                    <#list item.children as subItem>
                    <div>
                        <div class="planQuestion">
                            <div class="title isSub">${item_index+1}.${subItem_index+1} ${subItem.questionTitle!""}</div>
                            <div class="info">${subItem.questionDescr!""}</div>
                            <#if subItem.children?size==0>
                                <div class="answerContent">${subItem.questionResult!"未作答"}</div>
                            </#if>
                        </div>
                        <#if subItem.children??>
                        <#list subItem.children as subSubItem>
                            <div>
                                <div class="planQuestion">
                                    <div class="title isSubSub">${item_index+1}.${subItem_index+1}.${subSubItem_index+1} ${subSubItem.questionTitle!""}</div>
                                    <div class="info">${subSubItem.questionDescr!""}</div>
                                    <#if subSubItem.children?size==0>
                                        <div class="answerContent">${subSubItem.questionResult!"未作答"}</div>
                                    </#if>
                                </div>
                            </div>
                        </#list>
                        </#if>
                    </div>
                    </#list>
                    </#if>
                </div>
                </#list>
                </#if>
            </div>
        </div>
    </div>
</body>
</html>