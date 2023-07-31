<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>实验报告总览</title>
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
    <div class="text-right"><img src="data:image/jpg;base64,${baseInfo.logoImg}"/></div>
    <div align="center">
        <div class="pageTitle " style="margin-top:60px">${baseInfo.title!""}</div>
        <div class="pageTitle " style="margin-top:38px">总报告</div>
        <img src="data:image/jpg;base64,${baseInfo.coverImg}" style="width: 760px;margin-top:38px"/>
    </div>
    <div align="right">
        <div class="groupDiv">实验社区：${exptInfo.experimentName}</div>
        <div class="groupDiv">实验日期：${exptInfo.exptStartDate?string("yyyy-MM-dd")}</div>
    </div>
</div>
<div class="pageNext"></div>
    <div class="page">
        <#if totalRankingList?has_content && (totalRankingList?size>0)>
            <div class="font-blue bold" style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">总排行榜</div>
            <table class="wd-700 text-center" align="center">
                <tr class="background-singular-blue">
                    <td>排名</td>
                    <td>组数</td>
                    <td>组名</td>
                    <td>方案设计</td>
                    <td>沙盘模拟</td>
                    <td>得分</td>
                </tr>
                <#list totalRankingList as item>
                    <tr class="background-even-blue">
                        <td>${item_index+1}</td>
                        <td>${item.groupNo}</td>
                        <td>${item.groupName}</td>
                        <td>${item.schemeScore}</td>
                        <td>${item.sandScore}</td>
                        <td>${item.totalScore}</td>
                    </tr>
                </#list>
            </table>
        </#if>
        <#if schemeRankingList?has_content && (schemeRankingList?size > 0)>
        <div class="font-blue bold" style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">方案设计排行榜</div>
        <table class="wd-700 text-center" align="center">
            <tr class="background-singular-blue">
                <td>排名</td>
                <td>组数</td>
                <td>组名</td>
                <td>方案设计得分</td>
            </tr>
            <#list schemeRankingList as item>
            <tr class="background-even-blue">
                <td>${item_index+1}</td>
                <td>${item.groupNo}</td>
                <td>${item.groupName!"未进入的小组"}</td>
                <td>${item.schemeScore}</td>
            </tr>
            </#list>
        </table>
        </#if>
        <#if sandGroupRankingList?has_content && (sandGroupRankingList?size > 0)>
        <div class="font-blue bold" style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">沙盘对抗排行榜</div>
        <table class="wd-700 text-center" align="center">
            <#list sandGroupRankingList as item>
            <tr class="background-singular-blue">
                <td>排名</td>
                <td>组数</td>
                <td>组名</td>
                    <#list item.periodGroupScoreList as itemItem>
                        <td>第${itemItem_index+1}期</td>
                    </#list>
                <td>得分</td>
            </tr>
            <tr class="background-even-blue">
                <td>${item_index+1}</td>
                <td>${item.groupNo}</td>
                <td>${item.groupName}</td>
                <#list item.periodGroupScoreList as item2>
                    <td>${item2.score}</td>
                </#list>
                <td>${item.groupScore}</td>
            </tr>
            </#list>
        </table>
        </#if>
        <#if sandPeriodRankingList?has_content && (sandPeriodRankingList?size > 0)>
        <#list sandPeriodRankingList as item>
        <div class="font-blue bold" style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">第${item_index+1}期排行榜</div>
        <table class="wd-700 text-center" align="center">
            <tr class="background-singular-blue">
                <td>排名</td>
                <td>组数</td>
                <td>组名</td>
                <td>健康指数</td>
                <td>知识考点</td>
                <td>医疗占比</td>
                <td>得分</td>
            </tr>
            <#list item as item2>
            <tr class="background-even-blue">
                <td>${item2_index+1}</td>
                <td>${item2.groupNo}</td>
                <td>${item2.groupName}</td>
                <td>${item2.healthIndexScore}</td>
                <td>${item2.knowledgeScore}</td>
                <td>${item2.treatmentPercentScore}</td>
                <td>${item2.totalScore}</td>
            </tr>
            </#list>
        </table>
        </#list>
        </#if>
    </div>
</body>
</html>