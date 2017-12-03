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

import io.netty.handler.codec.http.HttpMethod;
import org.wso2.carbon.protocol.emulator.dsl.Emulator;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientRequestBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseBuilderContext;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseProcessorContext;

import java.io.File;

public class ClientLargePayload extends Client implements Runnable{

    private String echoBackServerPath = "/services/client/normal";
    private File largeFile = new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
            "IntegrationTests/src/test/resources/files/1MB.txt");

    private String responseBody = "{\"glossary\":{\"title\":\"exampleglossary\",\"GlossDiv\":{\"title\":\"S\"," +
            "\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\"," +
            "\"GlossTerm\":\"StandardGeneralizedMarkupLanguage\",\"Acronym\":\"SGML\"," +
            "\"Abbrev\":\"ISO8879:1986\",\"GlossDef\":{\"para\":\"Ameta-markuplanguage," +
            "usedtocreatemarkuplanguagessuchasDocBook.\",\"GlossSeeAlso\":[\"GML\"," +
            "\"XML\"]},\"GlossSee\":\"markup\"}}}}}";

    public HttpClientResponseProcessorContext getRsp() {
        return rsp;
    }

    HttpClientResponseProcessorContext rsp;
    @Override
    public void run() {
        System.out.println( "====================== client large payload ==========================");

        rsp = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
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

//        System.out.println("===================== ASSERTING ===================");
//        System.out.println(response.getReceivedResponseContext().getResponseBody() + " + " + responseBody );
//        System.out.println("=========== ASSERTED =============");
//        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "looool",
//                "The received response body is not same as the expected");
//        System.out.println(" LOL OL OL OL OLOL OL OL OL ");
//        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
//                HttpHeaders.Values.APPLICATION_JSON,
//                "The received ContentType header value is different from that expected");
////        System.exit(67);


    }


}
