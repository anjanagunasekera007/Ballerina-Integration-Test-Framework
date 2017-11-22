package org.ballerinalang.integration.tests.ConcurrentTest;/*
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

import io.netty.handler.codec.http.HttpMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.ballerinalang.integration.tests.TestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.protocol.emulator.dsl.Emulator;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientRequestBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseProcessorContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentTests {
    private File largeFile = new File("src/test/resources/files/1MB.txt");


    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distribu" +
                "tion/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-Te" +
                "st-Framework-Bals/ConcurrentTest.bal");
//        postMethod.addParameter("Config", "config.xml");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }

    @Test(threadPoolSize = 6, invocationCount = 6, timeOut = 1000)
    public void testConcurrentNormalServer() throws IOException {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath("/services/concurrent/largefile")
                                .withMethod(HttpMethod.POST).withBody(largeFile)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(TestUtils.getFileBody(largeFile), response.getReceivedResponseContext().getResponseBody(),
                "The received response body is not same as the expected");
    }

//    @Test
//    public void test() throws Exception {
//        ExecutorService threads = Executors.newFixedThreadPool(6);
//        List<Callable<Boolean>> torun = new ArrayList<>(6);
//        for (int i = 0; i < 6; i++) {
//            torun.add(new Callable<Integer>() {
//                public Boolean call() throws B2BException {
//                    b2b.execute(request);
//                    return Boolean.TRUE;
//                }
//            });
//        }
//
//        // all tasks executed in different threads, at 'once'.
//        List<Future<Boolean>> futures = threads.invokeAll(torun);
//
//        // no more need for the threadpool
//        threads.shutdown();
//        // check the results of the tasks...throwing the first exception, if any.
//        for (Future<Boolean> fut : futures) {
//            fut.get();
//        }
//
//        //check the threadpool is now in fact complete
////        if (!threads.isShutDown()) {
////            // something went wrong... our accounting is off...
////        }
//
//    }


    @AfterClass
    public void stopAgent() throws IOException {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }
}
