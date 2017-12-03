package org.ballerinalang.integration.tests.Clients;/*
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

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import org.testng.Assert;
import org.wso2.carbon.protocol.emulator.dsl.Emulator;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientRequestBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseProcessorContext;

import java.io.File;

public class ClientSlowReading  extends Client implements Runnable {
    private String echoBackServerPath = "/services/client/normal";
    private File plainFile = new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
            "IntegrationTests/src/test/resources/files/100KB.txt");

    private String responseBody = "{\"glossary\":{\"title\":\"exampleglossary\",\"GlossDiv\":{\"title\":\"S\"," +
            "\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\"," +
            "\"GlossTerm\":\"StandardGeneralizedMarkupLanguage\",\"Acronym\":\"SGML\"," +
            "\"Abbrev\":\"ISO8879:1986\",\"GlossDef\":{\"para\":\"Ameta-markuplanguage," +
            "usedtocreatemarkuplanguagessuchasDocBook.\",\"GlossSeeAlso\":[\"GML\"," +
            "\"XML\"]},\"GlossSee\":\"markup\"}}}}}";

    HttpClientResponseProcessorContext rsp;


    public HttpClientResponseProcessorContext getRsp() {
        return rsp;
    }

    @Override
    public void run() {
        System.out.println( "====================== client slow reading ==========================");

        rsp = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090")).withReadingDelay(3000)
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
                                .withMethod(HttpMethod.POST).withBody(responseBody)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
//                "The received response body is not same as the expected");
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                HttpHeaders.Values.APPLICATION_JSON,
//                "The received ContentType header value is different from that expected");

    }
}
