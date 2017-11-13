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

package org.apache.synapse.integration.tests;

import io.netty.handler.codec.http.HttpMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.synapse.integration.BaseTest;
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

public class ClientTest extends BaseTest {
    private String path = "/services/normal_server";
    private String responseBody = "{\"glossary\":{\"title\":\"exampleglossary\",\"GlossDiv\":{\"title\":\"S\"," +
            "\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\"," +
            "\"GlossTerm\":\"StandardGeneralizedMarkupLanguage\",\"Acronym\":\"SGML\"," +
            "\"Abbrev\":\"ISO8879:1986\",\"GlossDef\":{\"para\":\"Ameta-markuplanguage," +
            "usedtocreatemarkuplanguagessuchasDocBook.\",\"GlossSeeAlso\":[\"GML\"," +
            "\"XML\"]},\"GlossSee\":\"markup\"}}}}}";
    private String res = "Slowly responding backend";
    private File plainFile = new File("src/test/resources/files/100KB.txt");
    private File largeFile = new File("src/test/resources/files/1MB.txt");
    private String processingPath = "/services/content_type";

    private String xmlBodySmall = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<note>\n" +
            "</note>";

    //===
    ///-----

    @BeforeClass
    public void initParameters() throws Exception {
        System.out.println(" RUNNING BEFORE CLASS");
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distribution/modules/ballerina/target/ballerina-0.94.0-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/ServerAgent/src/main/java/org/wso2/ballerina/test/framework/Test.bal");
//        postMethod.addParameter("Config", "config.xml");
        HttpClient httpClient = new HttpClient();
//        httpClient.executeMethod(postMethod);

        httpClient.executeMethod(postMethod);
    }

        //----
    @Test
    public void testClientLargePayload() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(path)
                                .withMethod(HttpMethod.POST).withBody(xmlBodySmall)

                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), res);
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody);
//        Assert.assertEquals(HttpHeaders.Values.APPLICATION_JSON,
//                response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE));
    }
    //===
//
//    @Test
//    public void testClientProcessingLargePayload() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort()))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(processingPath)
//                                .withMethod(HttpMethod.POST).withBody(largeFile)
//                                .withHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain")
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
////        Assert.assertEquals(HttpHeaders.Values.APPLICATION_JSON,
////                            response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE));
//    }
//
//    @Test
//    public void testClientSlowWriting() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort())).withWritingDelay(3000)
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(path)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
////        Assert.assertEquals(HttpHeaders.Values.APPLICATION_JSON,
////                            response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE));
//    }
//
//
//    @Test
//    public void testClientSlowReading() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort())).withReadingDelay(3000)
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(path)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
////        Assert.assertEquals(HttpHeaders.Values.APPLICATION_JSON,
////                            response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE));
//    }
//
//    @Test
//    public void testClientDisableKeepAlive() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort())).withKeepAlive(false)
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(path)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
////        Assert.assertEquals(HttpHeaders.Values.APPLICATION_JSON,
////                            response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE));
//        Assert.assertNotEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONNECTION),
//                               HttpHeaders.Values.KEEP_ALIVE);
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONNECTION),
//                            HttpHeaders.Values.CLOSE);
//    }
//
//    @Test
//    public void testDisableChunking() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort()))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(path)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
////        Assert.assertEquals(HttpHeaders.Values.APPLICATION_JSON,
////                            response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE));
//    }
//
//    @Test
//    public void testDisconnectPartially() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort()))
//                                .withPartialWriteConnectionDrop()
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(path)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
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
//    @Test
//    public void testBurstRequests() {
//        for (int i = 0; i < 10; i++) {
//            HttpClientOperationBuilderContext httpClientOperationBuilderContext = Emulator.getHttpEmulator()
//                    .client()
//                    .given(
//                            HttpClientConfigBuilderContext.configure()
//                                    .host(getConfig().getSynapseServer().getHostname())
//                                    .port(Integer.parseInt(getConfig().getSynapseServer().getPort()))
//                    )
//                    .when(
//                            HttpClientRequestBuilderContext.request().withPath(path)
//                                    .withMethod(HttpMethod.POST).withBody(plainFile)
//                    )
//                    .then(
//                            HttpClientResponseBuilderContext.response().assertionIgnore()
//                    )
//                    .operation()
//                    .sendAsync();
//            List<RequestResponseCorrelation> responseCorrelations = httpClientOperationBuilderContext.shutdown();
//            Assert.assertEquals(responseBody, responseCorrelations.get(0).getReceivedResponse()
//                    .getReceivedResponseContext()
//                    .getResponseBody());
////            Assert.assertEquals(HttpHeaders.Values.APPLICATION_JSON, responseCorrelations.get(0).getReceivedResponse()
////                    .getReceivedResponse()
////                    .headers().get(HttpHeaders.Names.CONTENT_TYPE));
//        }
//    }
//
//    @Test
//    public void testMalformedPayload() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort()))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(processingPath)
//                                .withMethod(HttpMethod.POST).withXmlPayload("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                                                                              "<note>\n" +
//                                                                              "  <to>Tove<to>\n" +
//                                                                              "  <from>Jani</from>\n" +
//                                                                              "  <heading>Reminder</heading>\n" +
//                                                                              "  <body>Don't forget me this " +
//                                                                              "weekend!</body>\n" +
//                                                                              "</note>")
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody().trim(),
//                            "<Exception>Error in proxy execution</Exception>",
//                            "Did not receive an error message when payload is malformed payload");
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(),
//                            HttpResponseStatus.INTERNAL_SERVER_ERROR,
//                            "Status code should be 500 for malformed payload");
//    }
//
//    /**
//     * Client does not send the Content-Type header
//     */
//    @Test
//    public void testMissingHeaders() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort()))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(processingPath)
//                                .withMethod(HttpMethod.POST).withBody(xmlBody)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertNull(response.getReceivedResponseContext().getResponseBody(),
//                            "Did not receive an error message when payload is malformed payload");
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(),
//                            HttpResponseStatus.INTERNAL_SERVER_ERROR,
//                            "Status code should be 500 for malformed payload");
//    }
//
//    @Test
//    public void testInvalidHeader() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort()))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(processingPath).withMethod(HttpMethod.POST)
//                                .withBody(plainFile)
//                                .withHeader(HttpHeaders.Names.CONTENT_TYPE, "application/xml")
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertNull(response,
//                          "The response " + response.getReceivedResponseContext().getResponseBody()
//                                  + " should not receive");
//    }
//
//    /**
//     * TODO: Fix the emulator client code to get this working
//     */
//    @Test(enabled = false)
//    public void testConnectionDropWhileReading() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host(getConfig().getSynapseServer().getHostname())
//                                .port(Integer.parseInt(getConfig().getSynapseServer().getPort()))
//                                .withEnableReadingConnectionDrop()
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(path)
//                                .withMethod(HttpMethod.POST).withBody(plainFile)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertNull(response);
//    }


    @AfterClass
    public void StopAgent() throws IOException {
        System.out.println(" RUNNING BEFORE CLASS");
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distribution/modules/ballerina/target/ballerina-0.94.0-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/ServerAgent/src/main/java/org/wso2/ballerina/test/framework/Test.bal");
//        postMethod.addParameter("Config", "config.xml");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);    }
}
