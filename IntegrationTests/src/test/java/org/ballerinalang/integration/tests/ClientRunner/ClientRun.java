package org.ballerinalang.integration.tests.ClientRunner;/*
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.ballerinalang.integration.tests.ClientUtils.ClientAssertor;
import org.ballerinalang.integration.tests.Clients.ClientSlowReading;
import org.ballerinalang.integration.tests.Clients.ClientLargePayload;
import org.ballerinalang.integration.tests.Clients.ClientSlowWriting;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ClientRun {

    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distri" +
                "bution/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-T" +
                "est-Framework-Bals/normalservice.bal");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }

    @Test
    public void AsyncTest() throws ExecutionException, InterruptedException, IOException {
        List<Future> futuresList = new ArrayList<>();

        List<ClientLargePayload> clientsL = new ArrayList<>();
        List<ClientSlowReading> clientsSR = new ArrayList<>();
        List<ClientSlowWriting> clientsSW = new ArrayList<>();

        ClientAssertor assertex = new ClientAssertor();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            ClientLargePayload c1 = new ClientLargePayload();
            clientsL.add(c1);
            futuresList.add(executor.submit(c1));

            ClientSlowReading c2 = new ClientSlowReading();
            clientsSR.add(c2);
            futuresList.add(executor.submit(c2));

            ClientSlowWriting c3 = new ClientSlowWriting();
            clientsSW.add(c3);
            futuresList.add(executor.submit(c3));
        }

        executor.shutdown();
        executor.awaitTermination(500, TimeUnit.SECONDS);

        for (ClientLargePayload c : clientsL) {
            assertex.clientAssert(c.getRsp(), "largepayload", "");
        }
        for (ClientSlowWriting csw : clientsSW) {
            assertex.clientAssert(csw.getRsp(), "slowwriting", "");

        }
        for (ClientSlowReading csr : clientsSR) {
            assertex.clientAssert(csr.getRsp(), "slowreading", "");

        }
        for (Future s : futuresList) {
            System.out.println(s.isDone());
        }

    }
}

