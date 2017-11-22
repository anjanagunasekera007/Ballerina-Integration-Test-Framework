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

import java.io.*;

public class ConnectionDrop {

    private String pathServerDisconnect = "/services/normal_server/servicedisconnect";

    private String xmlBodySmall = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<note>\n" +
            "</note>";

    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distribu" +
                "tion/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-Te" +
                "st-Framework-Bals/Test.bal");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }

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

    @AfterClass
    public void stopAgent() throws IOException {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }
}
