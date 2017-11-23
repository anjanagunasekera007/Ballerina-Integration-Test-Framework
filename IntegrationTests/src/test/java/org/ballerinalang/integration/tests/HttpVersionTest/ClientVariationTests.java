/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.integration.tests.HttpVersionTest;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.protocol.emulator.dsl.Emulator;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientRequestBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseProcessorContext;

import java.io.IOException;

public class ClientVariationTests {

    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-" +
                "distribution/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-" +
                "Test-Framework-Bals/Test.bal");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }

    /**
     * The client will use HTTP 1.0 while backend use HTTP 1.1 to communicate with Ballerina server.
     */
    @Test
    public void testHttp10ToHttp11() {
        HttpClientConfigBuilderContext clientConfig = HttpClientConfigBuilderContext
                .configure()
                .host("127.0.0.1")
                .port(Integer.parseInt("9090"))
                .withWritingDelay(3000);

        HttpClientRequestBuilderContext requestConfig = HttpClientRequestBuilderContext
                .request()
                .withPath("/services/normal_server/normal")
                .withMethod(HttpMethod.POST)
                .withHttpVersion(HttpVersion.HTTP_1_0)
                .withXmlPayload(getXmlBody());

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client().given(clientConfig)
                .when(requestConfig)
                .then(HttpClientResponseBuilderContext.response().assertionIgnore())
                .operation().send();

        Assert.assertEquals(getXmlBody(), response.getReceivedResponseContext().getResponseBody());
    }


    /**
     * The client and backend will use HTTP 1.0 to communicate with Ballerina server.
     */
    @Test
    public void testHttp10ToHttp10() {
        HttpClientConfigBuilderContext clientConfig = HttpClientConfigBuilderContext
                .configure()
                .host("127.0.0.1")
                .port(Integer.parseInt("9090"))
                .withWritingDelay(3000);

        HttpClientRequestBuilderContext requestConfig = HttpClientRequestBuilderContext
                .request()
                .withPath("/services/normal_server/http10_only")
                .withMethod(HttpMethod.POST)
                .withHttpVersion(HttpVersion.HTTP_1_0)
                .withXmlPayload(getXmlBody());

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client().given(clientConfig)
                .when(requestConfig)
                .then(HttpClientResponseBuilderContext.response().assertionIgnore())
                .operation().send();

        Assert.assertEquals("HTTP version 1.0 only", response.getReceivedResponseContext().getResponseBody().trim());
    }

    /**
     * The client will use HTTP 1.1 while the backend use HTTP 1.0 to communicate with Ballerina server.
     */
    @Test
    public void testHttp11ToHttp10() {
        HttpClientConfigBuilderContext clientConfig = HttpClientConfigBuilderContext
                .configure()
                .host("127.0.0.1")
                .port(Integer.parseInt("9090"))
                .withWritingDelay(3000);

        HttpClientRequestBuilderContext requestConfig = HttpClientRequestBuilderContext
                .request()
                .withPath("/services/normal_server/http10_only")
                .withMethod(HttpMethod.POST)
                .withXmlPayload(getXmlBody());

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client().given(clientConfig)
                .when(requestConfig)
                .then(HttpClientResponseBuilderContext.response().assertionIgnore())
                .operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody().trim(), "The HTTP/1.1 is not " +
                "supported because of the configurations");

    }

    private String getXmlBody() {
        return "<request>Http10ClientTest</request>";
    }

    @AfterClass
    public void stopAgent() throws IOException {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }

 }
