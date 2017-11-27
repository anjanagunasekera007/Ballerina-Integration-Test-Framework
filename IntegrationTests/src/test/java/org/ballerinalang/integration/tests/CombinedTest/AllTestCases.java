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

package org.ballerinalang.integration.tests.CombinedTest;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.ballerinalang.integration.tests.TestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.protocol.emulator.dsl.Emulator;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientOperationBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientRequestBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseProcessorContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.RequestResponseCorrelation;


import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class contains normal test cases for Client
 */
public class AllTestCases {
    private String path = "/services/servers/normal";
    private String pathSlowReading = "/services/servers/slowreading";
    private String pathSlowWriting = "/services/servers/slowriting";

    private String pathLargePayload = "/services/servers/largepayload";
    private String pathMalformedPayload = "/services/servers/malformedpayload";
    private String pathMalformedPayloadProcess = "/services/servers/malformedpayloadprocess";

    private String pathServerDisconnect = "/services/servers/servicedisconnect";

    private String echoBackServerPath = "/services/servers/singleclient";
//    private File largeFile = new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
//            "IntegrationTests/src/test/resources/files/1MB.txt");
    private String largePayloadProcess ="/services/client/process";

//    private File plainFile = new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
//            "IntegrationTests/src/test/resources/files/100KB.txt");

//    private String responseBody = "{\"glossary\":{\"title\":\"exampleglossary\",\"GlossDiv\":{\"title\":\"S\"," +
//            "\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\"," +
//            "\"GlossTerm\":\"StandardGeneralizedMarkupLanguage\",\"Acronym\":\"SGML\"," +
//            "\"Abbrev\":\"ISO8879:1986\",\"GlossDef\":{\"para\":\"Ameta-markuplanguage," +
//            "usedtocreatemarkuplanguagessuchasDocBook.\",\"GlossSeeAlso\":[\"GML\"," +
//            "\"XML\"]},\"GlossSee\":\"markup\"}}}}}";

//    private String xmlBodySmall = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//            "<note>\n" +
//            "</note>";

    private String responseBody = "{\"glossary\":{\"title\":\"exampleglossary\",\"GlossDiv\":{\"title\":\"S\"," +
            "\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\"," +
            "\"GlossTerm\":\"StandardGeneralizedMarkupLanguage\",\"Acronym\":\"SGML\"," +
            "\"Abbrev\":\"ISO8879:1986\",\"GlossDef\":{\"para\":\"Ameta-markuplanguage," +
            "usedtocreatemarkuplanguagessuchasDocBook.\",\"GlossSeeAlso\":[\"GML\"," +
            "\"XML\"]},\"GlossSee\":\"markup\"}}}}}";

    private String xmlBodySmall = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<note>\n" +
            "</note>";

    private File largeFile = new File("src/test/resources/files/1MB.txt");
    private File plainFile = new File("src/test/resources/files/100KB.txt");

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


//    @Test(invocationCount = 10)
//    public void testNormalServer() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(path)
//                                .withMethod(HttpMethod.POST).withBody(xmlBodySmall)
//
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), xmlBodySmall);
//
//    }
//
//    @Test(invocationCount = 10)
//    public void testServerSlowReading() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(pathSlowReading)
//                                .withMethod(HttpMethod.POST).withBody(responseBody)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
//
//    }
//
//    @Test(invocationCount = 10)
//    public void testServerSlowWriting() {
//
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(pathSlowWriting)
//                                .withMethod(HttpMethod.POST).withBody(responseBody)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
//
//    }
//
//    @Test(invocationCount = 10)
//    public void testLargePayloadClientServer() throws IOException {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath("/services/servers/largepayload")
//                                .withMethod(HttpMethod.POST).withBody(largeFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(TestUtils.getFileBody(largeFile), response.getReceivedResponseContext().getResponseBody(),
//                "The received response body is not same as the expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testBackendLargeSlowClient() throws IOException {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090")).withReadingDelay(3000)
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath("/services/servers/slowclient")
//                                .withMethod(HttpMethod.POST).withBody(largeFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(TestUtils.getFileBody(largeFile), response.getReceivedResponseContext().getResponseBody(),
//                "The received response body is not same as the expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testLargeFileClientSlowReadingBackend() throws Exception {
//        String payload = TestUtils.getContentAsString("src/test/resources/files/1MB.txt");
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath("/services/servers/readingdelay")
//                                .withMethod(HttpMethod.POST).withBody("Slowly reading backend")
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(),
//                "Slowly reading backend",
//                "Slowly reading backend response did not receive correctly");
//    }
//
//    @Test(invocationCount = 10)
//    public void testSlowWritingLargeResponseBackend() throws IOException {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath("/services/servers/slowwritinglargeresponse")
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(TestUtils.getFileBody(largeFile), response.getReceivedResponseContext().getResponseBody(),
//                "The received response body is not same as the expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testHttp10ToHttp11() {
//        HttpClientConfigBuilderContext clientConfig = HttpClientConfigBuilderContext
//                .configure()
//                .host("127.0.0.1")
//                .port(Integer.parseInt("9090"))
//                .withWritingDelay(3000);
//
//        HttpClientRequestBuilderContext requestConfig = HttpClientRequestBuilderContext
//                .request()
//                .withPath("/services/servers/normal")
//                .withMethod(HttpMethod.POST)
//                .withHttpVersion(HttpVersion.HTTP_1_0)
//                .withXmlPayload(getXmlBody());
//
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client().given(clientConfig)
//                .when(requestConfig)
//                .then(HttpClientResponseBuilderContext.response().assertionIgnore())
//                .operation().send();
//
//        Assert.assertEquals(getXmlBody(), response.getReceivedResponseContext().getResponseBody());
//    }
//
//
//    /**
//     * The client and backend will use HTTP 1.0 to communicate with Ballerina server.
//     */
//    @Test(invocationCount = 10)
//    public void testHttp10ToHttp10() {
//        HttpClientConfigBuilderContext clientConfig = HttpClientConfigBuilderContext
//                .configure()
//                .host("127.0.0.1")
//                .port(Integer.parseInt("9090"))
//                .withWritingDelay(3000);
//
//        HttpClientRequestBuilderContext requestConfig = HttpClientRequestBuilderContext
//                .request()
//                .withPath("/services/servers/http10_only")
//                .withMethod(HttpMethod.POST)
//                .withHttpVersion(HttpVersion.HTTP_1_0)
//                .withXmlPayload(getXmlBody());
//
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client().given(clientConfig)
//                .when(requestConfig)
//                .then(HttpClientResponseBuilderContext.response().assertionIgnore())
//                .operation().send();
//
//        Assert.assertEquals("HTTP version 1.0 only", response.getReceivedResponseContext().getResponseBody().trim());
//    }
//
//    /**
//     * The client will use HTTP 1.1 while the backend use HTTP 1.0 to communicate with Ballerina server.
//     */
//    @Test(invocationCount = 10)
//    public void testHttp11ToHttp10() {
//        HttpClientConfigBuilderContext clientConfig = HttpClientConfigBuilderContext
//                .configure()
//                .host("127.0.0.1")
//                .port(Integer.parseInt("9090"))
//                .withWritingDelay(3000);
//
//        HttpClientRequestBuilderContext requestConfig = HttpClientRequestBuilderContext
//                .request()
//                .withPath("/services/servers/http10_only")
//                .withMethod(HttpMethod.POST)
//                .withXmlPayload(getXmlBody());
//
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client().given(clientConfig)
//                .when(requestConfig)
//                .then(HttpClientResponseBuilderContext.response().assertionIgnore())
//                .operation().send();
//
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody().trim(), "The HTTP/1.1 is not " +
//                "supported because of the configurations");
//
//    }
//
//    @Test(invocationCount = 10)
//    public void testServerDisconnectPartially() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                                .withPartialWriteConnectionDrop()
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(pathServerDisconnect)
//                                .withMethod(HttpMethod.POST).withBody(xmlBodySmall)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//
//        Assert.assertNull(response);
//    }
//
//    @Test(invocationCount = 10)
//    public void testServerSendingLargePayload() throws IOException {
//
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(pathLargePayload)
//                                .withMethod(HttpMethod.POST).withBody("Recieve Large Payload")
//
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(TestUtils.getFileBody(new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
//                        "IntegrationTests/src/test/resources/files/1MB.txt")),
//                response.getReceivedResponseContext().getResponseBody(),
//                "The received response body is not same as the expected");
//
//    }

    @Test(invocationCount = 10)
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
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(),"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<note>\n" +
                "  <to>Tove</to>\n" +
                "  <from>Jani</from>\n" +
                "  <heading>Reminder</heading>\n" +
                "  <body>Don't forget me this weekend!</body>\n" +
                "</note>");
    }

    @Test(invocationCount = 10)
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

//    @Test(invocationCount = 10)
//    public void testClientLargePayload() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
//                                .withMethod(HttpMethod.POST).withBody(largeFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
//                "The received response body is not same as the expected");
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                HttpHeaders.Values.APPLICATION_JSON,
//                "The received ContentType header value is different from that expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testClientProcessingLargePayload() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
//                                .withMethod(HttpMethod.POST).withBody(largeFile)
//                                .withHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain")
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
//                "The received response body is not same as the expected");
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                HttpHeaders.Values.APPLICATION_JSON,
//                "The received ContentType header value is different from that expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testClientSlowWriting() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090")).withWritingDelay(3000)
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
//                "The received response body is not same as the expected");
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                HttpHeaders.Values.APPLICATION_JSON,
//                "The received ContentType header value is different from that expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testClientSlowReading() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090")).withReadingDelay(3000)
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
//                "The received response body is not same as the expected");
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                HttpHeaders.Values.APPLICATION_JSON,
//                "The received ContentType header value is different from that expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testClientDisableKeepAlive() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090")).withKeepAlive(false)
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
//                "The received response body is not same as the expected");
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                HttpHeaders.Values.APPLICATION_JSON,
//                "The received ContentType header value is different from that expected");
//        Assert.assertNotEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONNECTION),
//                HttpHeaders.Values.KEEP_ALIVE,
//                "The received Keep-alive header value is different from that expected");
//
//    }
//
//    @Test(invocationCount = 10)
//    public void testDisableChunking() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
//                "The received response body is not same as the expected");
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                HttpHeaders.Values.APPLICATION_JSON,
//                "The received ContentType header value is different from that expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testBurstRequests() {
//        for (int i = 0; i < 10; i++) {
//            HttpClientOperationBuilderContext httpClientOperationBuilderContext = Emulator.getHttpEmulator()
//                    .client()
//                    .given(
//                            HttpClientConfigBuilderContext.configure()
//                                    .host("127.0.0.1")
//                                    .port(Integer.parseInt("9090"))
//                    )
//                    .when(
//                            HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
//                                    .withMethod(HttpMethod.POST).withBody(plainFile)
//                    )
//                    .then(
//                            HttpClientResponseBuilderContext.response().assertionIgnore()
//                    )
//                    .operation()
//                    .sendAsync();
//            List<RequestResponseCorrelation> responseCorrelations = httpClientOperationBuilderContext.shutdown();
//            Assert.assertEquals(responseCorrelations.get(0).getReceivedResponse().getReceivedResponseContext()
//                            .getResponseBody(), responseBody,
//                    "The received response body is not same as the expected");
//            Assert.assertEquals(responseCorrelations.get(0).getReceivedResponse()
//                            .getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                    HttpHeaders.Values.APPLICATION_JSON,
//                    "The received ContentType header value is different from that expected");
//        }
//    }
//
//    @Test(invocationCount = 10)
//    public void testDisconnectPartially() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                                .withPartialWriteConnectionDrop()
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//
//        Assert.assertNull(response, "The response received is not null");
//    }
//
//
//    @Test(invocationCount = 10)
//    public void testLargePayload() throws IOException {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath("/services/servers/largepayload")
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(TestUtils.getFileBody(new File("/home/anjana/work/" +
//                        "Ballerina-Integration-Test-Framework/IntegrationTests/src/test/resources/files/1MB.txt")),
//                response.getReceivedResponseContext().getResponseBody(),
//                "The received response body is not same as the expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testSlowResponse() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath("/services/servers/slowresponse")
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals("Slowly responding backend", response.getReceivedResponseContext().getResponseBody(),
//                "The received response body is not same as the expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testWritingDelay() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath("/services/servers/writingdelay")
//                                .withMethod(HttpMethod.POST).withBody("Slowly writing backend")
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals("Slowly writing backend", response.getReceivedResponseContext().getResponseBody(),
//                "The received response body is not same as the expected");
//    }
//
//    @Test(invocationCount = 10)
//    public void testKeepAlive() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath("/services/servers/keepalive")
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals("Keep alive", response.getReceivedResponseContext().getResponseBody(),
//                "The received response body is not same as the expected");
//    }









//    @AfterClass
//    public void stopAgent() throws IOException {
//        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
//        HttpClient httpClient = new HttpClient();
//        httpClient.executeMethod(postMethod);
//    }
}
