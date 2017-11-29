package org.ballerinalang.integration.tests.AsyncTests;/*
* Copyright (c) $today.year, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.ballerinalang.integration.tests.ClientRunner.ClientAssert;
import org.ballerinalang.integration.tests.ClientRunner.WaitUtil;
import org.ballerinalang.integration.tests.Clients.ClientLargePayload;
import org.ballerinalang.integration.tests.Clients.ClientSlowReading;
import org.ballerinalang.integration.tests.Clients.ClientSlowWriting;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncTests {

    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distri" +
                "bution/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-T" +
                "est-Framework-Bals/AllinoneTests.bal");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }

    @Test
    public void asyncClientLargePayload() throws InterruptedException {

//        Future taskTwo = null;


//        ExecutorService executor = Executors.newFixedThreadPool(2);


        WaitUtil waitUtil = new WaitUtil(-2);

        ClientLargePayload clientLarge = new ClientLargePayload(waitUtil);
        ClientSlowReading clientslowread = new ClientSlowReading(waitUtil);

//        taskTwo = executor.submit(clientLarge);

        boolean ret = waitUtil.waitTillResponse(50000);

        if (!ret) {
            //assertfail
        }else {
            System.out.println(clientLarge.getResponse().toString());

        }




    }


}

