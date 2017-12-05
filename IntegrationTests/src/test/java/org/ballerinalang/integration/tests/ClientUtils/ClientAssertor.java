package org.ballerinalang.integration.tests.ClientUtils;/*
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

import org.ballerinalang.integration.tests.TestUtils;
import org.testng.Assert;
import org.wso2.carbon.protocol.emulator.http.client.contexts.HttpClientResponseProcessorContext;

import java.io.File;
import java.io.IOException;

public class ClientAssertor {
    private String responseBody = "{\"glossary\":{\"title\":\"exampleglossary\",\"GlossDiv\":{\"title\":\"S\"," +
            "\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\"," +
            "\"GlossTerm\":\"StandardGeneralizedMarkupLanguage\",\"Acronym\":\"SGML\"," +
            "\"Abbrev\":\"ISO8879:1986\",\"GlossDef\":{\"para\":\"Ameta-markuplanguage," +
            "usedtocreatemarkuplanguagessuchasDocBook.\",\"GlossSeeAlso\":[\"GML\"," +
            "\"XML\"]},\"GlossSee\":\"markup\"}}}}}";

    private File largeFile = new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
            "IntegrationTests/src/test/resources/files/1MB.txt");


    public void clientAssert(HttpClientResponseProcessorContext res, String assertString, String name) {
        System.out.println(" ASSERTING");
        System.out.println("**** " + res.getReceivedResponseContext().getResponseBody() + " \n ((( WITH ))) \n " + responseBody );
        Assert.assertEquals(res.getReceivedResponseContext().getResponseBody(), responseBody);
        System.out.println(name);
        System.out.println();
        System.out.println();
    }

    public void clientAssertFile(HttpClientResponseProcessorContext res, String name) throws IOException {
        Assert.assertEquals(res.getReceivedResponseContext().getResponseBody(), TestUtils.getFileBody(largeFile));
        System.out.println(name);
        System.out.println();
        System.out.println();
    }
}
