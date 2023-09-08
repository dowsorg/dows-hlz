package org.dows.hep.config.mongo;


import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.config.WebMvcConfig;
import org.junit.jupiter.api.Test;

public class ObjectMtest {

    @Test
    public void test(){


     /*   WebMvcConfig webMvcConfig = new WebMvcConfig();
        ObjectMapper objectMapper = webMvcConfig.objectMapper();
        String date = "{\n" +
                "  \"caseInstanceId\": \"344629878513078272\",\n" +
                "  \"caseName\": \"[FHB]-完美世界\",\n" +
                "  \"caseOrgId\": \"344629878513078272\",\n" +
                "  \"experimentName\": \"test\",\n" +
                "  \"experimentDescr\": \"\",\n" +
                "  \"startTime\": \"2023-06-20T01:12:39.266Z\",\n" +
                "  \"model\": 2,\n" +
                "  \"teachers\": [\n" +
                "    {\n" +
                "      \"id\": \"1612291523374872230\",\n" +
                "      \"accountId\": \"347215824748154880\",\n" +
                "      \"appId\": \"3\",\n" +
                "      \"accountName\": \"rst3\",\n" +
                "      \"accountPwd\": null,\n" +
                "      \"avatar\": null,\n" +
                "      \"principalType\": null,\n" +
                "      \"intro\": null,\n" +
                "      \"tenantId\": null,\n" +
                "      \"source\": null,\n" +
                "      \"ver\": null,\n" +
                "      \"userId\": null,\n" +
                "      \"userName\": \"rst3\",\n" +
                "      \"password\": \"123456\",\n" +
                "      \"gender\": null,\n" +
                "      \"orgId\": \"347215911847071744\",\n" +
                "      \"orgName\": \"rs-复制人物-03\",\n" +
                "      \"groupInfoId\": \"347215911956123648\",\n" +
                "      \"roleId\": null,\n" +
                "      \"roleName\": \"教师\",\n" +
                "      \"status\": 1,\n" +
                "      \"phone\": null,\n" +
                "      \"expdate\": null,\n" +
                "      \"indate\": null\n" +
                "    }\n" +
                "  ],\n" +
                "  \"appId\": \"3\",\n" +
                "  \"experimentSetting\": {\n" +
                "    \"schemeSetting\": {\n" +
                "      \"duration\": 10086,\n" +
                "      \"weight\": 100,\n" +
                "      \"schemeEndTime\": \"2023-06-20T01:12:49.390Z\",\n" +
                "      \"scoreEndTime\": \"2023-06-20T01:12:51.221Z\",\n" +
                "      \"auditEndTime\": \"2023-06-20T01:12:52.974Z\"\n" +
                "    },\n" +
                "    \"sandSetting\": {\n" +
                "      \"periods\": 2,\n" +
                "      \"interval\": 5,\n" +
                "      \"durationMap\": {\n" +
                "        \"1\": 40,\n" +
                "        \"2\": 40\n" +
                "      },\n" +
                "      \"weightMap\": {\n" +
                "        \"1\": 40,\n" +
                "        \"2\": 60\n" +
                "      },\n" +
                "      \"periodMap\": {\n" +
                "        \"1\": 60,\n" +
                "        \"2\": 60\n" +
                "      },\n" +
                "      \"healthIndexWeight\": 40,\n" +
                "      \"knowledgeWeight\": 30,\n" +
                "      \"medicalRatioWeight\": 15,\n" +
                "      \"operateRightWeight\": 15\n" +
                "    }\n" +
                "  }\n" +
                "}";

        try {
            CreateExperimentRequest createExperimentRequest = objectMapper.readValue(date, CreateExperimentRequest.class);
            System.out.printf(JSONUtil.toJsonStr(createExperimentRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
    }
}
