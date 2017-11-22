/*
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
package org.ballerinalang.integration.tests.PayloadTests;

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

public class PayloadTest {
    private String pathLargePayload = "/services/normal_server/largepayload";
    private String pathMalformedPayload = "/services/normal_server/malformedpayload";
    private String pathMalformedPayloadProcess = "/services/normal_server/malformedpayloadprocess";

    private String largeFilePath = "/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/" +
            "EmulatorServer/1MB.txt";

        @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/" +
                "tools-distribution/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/" +
                "Ballerina-Integration-Test-Framework-Bals/Test.bal");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }


    @Test
    public void testServerSendingLargePayload() throws IOException {

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
        Assert.assertEquals(getFileBody(new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
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
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(),"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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

    public static String getFileBody(File filePath) throws IOException {

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            int c;
            StringBuilder stringBuilder = new StringBuilder();
            while ((c = fileInputStream.read()) != -1) {
                stringBuilder.append(c);
            }
            String content = stringBuilder.toString();
            content = content.replace("\n", "").replace("\r", "");

            return content;
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    @AfterClass
    public void StopAgent() throws IOException {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }
}
