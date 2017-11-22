package org.ballerinalang.integration.tests.CommonTest;/*
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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.protocol.emulator.dsl.Emulator;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientRequestBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseProcessorContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CommonTests {
    private File largeFile = new File("src/test/resources/files/1MB.txt");
    private File plainFile = new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
            "IntegrationTests/src/test/resources/files/100KB.txt");

    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distribu" +
                "tion/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-Te" +
                "st-Framework-Bals/CommonTest.bal");
//        postMethod.addParameter("Config", "config.xml");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }

    @Test
    public void testLargePayloadClientServer() throws IOException {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath("/services/common/largepayload")
                                .withMethod(HttpMethod.POST).withBody(largeFile)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(getFileBody(largeFile), response.getReceivedResponseContext().getResponseBody(),
                "The received response body is not same as the expected");
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
}
