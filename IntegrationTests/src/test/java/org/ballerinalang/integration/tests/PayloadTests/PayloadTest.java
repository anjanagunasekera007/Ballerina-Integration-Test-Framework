/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.integration.tests.PayloadTests;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
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

/**
 * This class contains Test cases for payload testing malformed and processing.
 */
public class PayloadTest {
    private String pathLargePayload = "/services/normal_server/largepayload";
    private String pathMalformedPayload = "/services/normal_server/malformedpayload";
    private String pathMalformedPayloadProcess = "/services/normal_server/malformedpayloadprocess";

    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/" +
                "tools-distribution/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/" +
                "Ballerina-Integration-Test-Framework-Bals/PayloadTest.bal");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }


    @Test
    public void testServerSendingLargePayload() throws IOException {

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(pathLargePayload)
                                .withMethod(HttpMethod.POST).withBody("Recieve Large Payload")

                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(TestUtils.getFileBody(new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
                        "IntegrationTests/src/test/resources/files/1MB.txt")),
                response.getReceivedResponseContext().getResponseBody(),
                "The received response body is not same as the expected");

    }

    @Test
    public void testMalformedPayload() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(pathMalformedPayload)
                                .withMethod(HttpMethod.POST).withXmlPayload("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<note>\n" +
                                "  <to>Tove</to>\n" +
                                "  <from>Jani</from>\n" +
                                "  <heading>Reminder</heading>\n" +
                                "  <body>Don't forget me this weekend!</body>\n" +
                                "</note>")
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<note>\n" +
                "  <to>Tove</to>\n" +
                "  <from>Jani</from>\n" +
                "  <heading>Reminder</heading>\n" +
                "  <body>Don't forget me this weekend!</body>\n" +
                "</note>");
    }

    @Test
    public void testMalformedPayloadProcess() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(pathMalformedPayloadProcess)
                                .withMethod(HttpMethod.POST).withXmlPayload("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<note>\n" +
                                "  <to>Tove</to>\n" +
                                "  <from>Jani</from>\n" +
                                "  <heading>Reminder</heading>\n" +
                                "  <body>Don't forget me this weekend!</body>\n")
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(),
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                "Status code should be 500 for malformed payload");
    }

    @AfterClass
    public void stopAgent() throws IOException {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }
}
