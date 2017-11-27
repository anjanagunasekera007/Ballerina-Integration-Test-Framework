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
package org.ballerinalang.integration.tests.NetworkTest;

import io.netty.handler.codec.http.HttpMethod;
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

/**
 * This class contains Test cases for connection drop tests.
 */
public class ConnectionTests {

    private String pathServerDisconnect = "/services/servers/servicedisconnect";

    private String xmlBodySmall = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<note>\n" +
            "</note>";

//    @BeforeClass
//    public void initParameters() throws Exception {
//        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
//        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distribu" +
//                "tion/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
//        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-Te" +
//                "st-Framework-Bals/ConnectionTest.bal");
//        HttpClient httpClient = new HttpClient();
//        httpClient.executeMethod(postMethod);
//    }

    @Test(invocationCount = 10)
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

//    @AfterClass
//    public void stopAgent() throws IOException {
//        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
//        HttpClient httpClient = new HttpClient();
//        httpClient.executeMethod(postMethod);
//    }
}
