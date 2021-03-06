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

package org.ballerinalang.integration.tests;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
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

import java.io.*;

public class ClientTest {
    private String path = "/services/normal_server/normal";
    private String pathLargePayload = "/services/normal_server/largepayload";
    private String pathSlowReading = "/services/normal_server/slowreading";
    private String pathSlowWriting = "/services/normal_server/slowriting";
    private String pathChunkingDisabled = "/services/normal_server/chunkingdisabled";
    private String pathServerDisconnect = "/services/normal_server/servicedisconnect";
    private String pathMalformedPayload = "/services/normal_server/malformedpayload";


    private String responseBody = "{\"glossary\":{\"title\":\"exampleglossary\",\"GlossDiv\":{\"title\":\"S\"," +
            "\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\"," +
            "\"GlossTerm\":\"StandardGeneralizedMarkupLanguage\",\"Acronym\":\"SGML\"," +
            "\"Abbrev\":\"ISO8879:1986\",\"GlossDef\":{\"para\":\"Ameta-markuplanguage," +
            "usedtocreatemarkuplanguagessuchasDocBook.\",\"GlossSeeAlso\":[\"GML\"," +
            "\"XML\"]},\"GlossSee\":\"markup\"}}}}}";
    private String res = "Slowly responding backend";
    private File plainFile = new File("src/test/resources/files/100KB.txt");
    private File largeFile = new File("src/test/resources/files/1MB.txt");

    private String largeFilePath = "/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/EmulatorServer/1MB.txt";
    private String plainFilePath = "/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/IntegrationTests/src/test/resources/files/100KB.txt";


    private String processingPath = "/services/content_type";

    private String xmlBodySmall = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<note>\n" +
            "</note>";

    private String malformedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<note>\n" +
            "  <to>Tove</to>\n" +
            "  <from>Jani</from>\n" +
            "  <heading>Reminder</heading>\n" +
            "  <body>Don't forget me this weekend!</body>\n" +
            "</note>";

    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start2");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distribution/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-Test-Framework-Bals/TestX.bal");
//        postMethod.addParameter("Config", "config.xml");
        HttpClient httpClient = new HttpClient();

        httpClient.executeMethod(postMethod);
    }


    @Test
    public void testNormalServer() {
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
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), xmlBodySmall);

    }

    @Test
    public void testServerSendingLargePayload() {

        String s = readFile(largeFilePath);

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
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody().toString(), s);

    }

    @Test
    public void testServerSlowReading() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(pathSlowReading)
                                .withMethod(HttpMethod.POST).withBody(responseBody)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());

    }

    @Test
    public void testServerSlowWriting() {

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(pathSlowWriting)
                                .withMethod(HttpMethod.POST).withBody(responseBody)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());

    }

//    @Test
//    public void testDisableChunking() {
//        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
//                .client()
//                .given(
//                        HttpClientConfigBuilderContext.configure()
//                                .host("127.0.0.1")
//                                .port(Integer.parseInt("9090"))
//                )
//                .when(
//                        HttpClientRequestBuilderContext.request().withPath(pathChunkingDisabled)
//                                .withMethod(HttpMethod.POST).withBody(responseBody)
//                )
//                .then(
//                        HttpClientResponseBuilderContext.response().assertionIgnore()
//                )
//                .operation()
//                .send();
//        Assert.assertEquals(responseBody, response.getReceivedResponseContext().getResponseBody());
//    }


    @Test
    public void testServerDisconnectPartially() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                                .withPartialWriteConnectionDrop()
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(pathServerDisconnect)
                                .withMethod(HttpMethod.POST).withBody(xmlBodySmall)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();

        Assert.assertNull(response);
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
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(),"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<note>\n" +
                "  <to>Tove</to>\n" +
                "  <from>Jani</from>\n" +
                "  <heading>Reminder</heading>\n" +
                "  <body>Don't forget me this weekend!</body>\n" +
                "</note>");
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(),
//                HttpResponseStatus.INTERNAL_SERVER_ERROR,
//                "Status code should be 500 for malformed payload");
    }


      public String readFile(String filePath) {
        String line = null;
        String st = "";
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                st = st + line;
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + filePath + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + filePath + "'");
        }
        return st;
    }




    @AfterClass
    public void StopAgent() throws IOException {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stopagent");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }
}
