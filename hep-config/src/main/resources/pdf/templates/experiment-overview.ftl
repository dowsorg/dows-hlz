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
<div id="header-right" class="bold" style="font-size:16px;">${title}</div>
<div id="footer">
    <div align="center" style="font-size:14px;">${copyRight} <span id="pages"> / </span></div>
</div>
<div class="page">
    <div class="text-right"><img src="data:image/jpg;base64,${logoImg}"/></div>
    <div align="center">
        <div class="pageTitle " style="margin-top:60px">${title}</div>
        <div class="pageTitle " style="margin-top:38px">总报告</div>
        <img src="data:image/jpg;base64,${coverImg}" style="width: 760px;margin-top:38px"/>
    </div>
    <div align="right">
        <div class="groupDiv">实验社区：${areaName}</div>
        <div class="groupDiv">实验日期：${startTime?string("yyyy-MM-dd")}</div>
    </div>
</div>
<div class="pageNext"></div>
    <div class="page">
        <#if totalRank??>
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
                <#list totalRank as item>
                    <tr class="background-even-blue">
                        <td>${item_index+1}</td>
                        <td>第${item.groupSeq}组</td>
                        <td>${item.groupName}</td>
                        <td>${item.planScore?string("0.##")}</td>
                        <td>${item.sandScore?string("0.##")}</td>
                        <td>${item.totalScore?string("0.##")}</td>
                    </tr>
                </#list>
            </table>
        </#if>
        <#if planRank??>
        <div class="font-blue bold" style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">方案设计排行榜</div>
        <table class="wd-700 text-center" align="center">
            <tr class="background-singular-blue">
                <td>排名</td>
                <td>组数</td>
                <td>组名</td>
                <td>方案设计得分</td>
            </tr>
            <#list planRank as item>
            <tr class="background-even-blue">
                <td>${item.rank}</td>
                <td>第${item.groupSeq}组</td>
                <td>${item.groupName!"未进入的小组"}</td>
                <td>${item.planScore?string("0.##")}</td>
            </tr>
            </#list>
        </table>
        </#if>
        <#if groupScore??>
        <div class="font-blue bold" style="font-size:18px;margin-top:25px;margin-bottom:25px" align="center">沙盘对抗排行榜</div>
        <table class="wd-700 text-center" align="center">
            <tr class="background-singular-blue">
                <td>排名</td>
                <td>组数</td>
                <td>组名</td>
                <#list periodScore as item>
                <td>第${item_index+1}期</td>
                </#list>
                <td>得分</td>
            </tr>
            <#list groupScore as item>
            <tr class="background-even-blue">
                <td>${item_index+1}</td>
                <td>第${item.seq}组</td>
                <td>${item.stuGroupName}</td>
                <#list item.periodScore as item2>
                    <td>${item2?string("0.##")}</td>
                </#list>
                <td>${item.total?string("0.##")}</td>
            </tr>
            </#list>
        </table>
        </#if>
        <#if periodScore??>
        <#list periodScore as item>
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
                <td>第${item2.seq}组</td>
                <td>${item2.stuGroupName}</td>
                <td>${item2.healthIndex?string("0.##")}</td>
                <td>${item2.knowledge?string("0.##")}</td>
                <td>${item2.medical?string("0.##")}</td>
                <td>${item2.total?string("0.##")}</td>
            </tr>
            </#list>
        </table>
        </#list>
        </#if>
    </div>
</body>
</html>