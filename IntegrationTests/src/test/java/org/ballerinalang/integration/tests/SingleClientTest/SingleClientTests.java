package org.ballerinalang.integration.tests.SingleClientTest;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
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

public class SingleClientTests {

    private String echoBackServerPath = "/services/client/normal";
    private File largeFile = new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
            "IntegrationTests/src/test/resources/files/1MB.txt");

    private File plainFile = new File("/home/anjana/work/Ballerina-Integration-Test-Framework/" +
            "IntegrationTests/src/test/resources/files/100KB.txt");

    private String responseBody = "{\"glossary\":{\"title\":\"exampleglossary\",\"GlossDiv\":{\"title\":\"S\"," +
            "\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\"," +
            "\"GlossTerm\":\"StandardGeneralizedMarkupLanguage\",\"Acronym\":\"SGML\"," +
            "\"Abbrev\":\"ISO8879:1986\",\"GlossDef\":{\"para\":\"Ameta-markuplanguage," +
            "usedtocreatemarkuplanguagessuchasDocBook.\",\"GlossSeeAlso\":[\"GML\"," +
            "\"XML\"]},\"GlossSee\":\"markup\"}}}}}";

    private String xmlBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<note>\n" +
            "  <to>Tove</to>\n" +
            "  <from>Jani</from>\n" +
            "  <heading>Reminder</heading>\n" +
            "  <body>Don't forget me this weekend!</body>\n" +
            "</note>";

    @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/" +
                "tools-distribution/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/" +
                "Ballerina-Integration-Test-Framework-Bals/SingleClientTest.bal");
//        postMethod.addParameter("Config", "config.xml");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }


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
                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
                                .withMethod(HttpMethod.POST).withBody(largeFile)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
                "The received response body is not same as the expected");
        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
                HttpHeaders.Values.APPLICATION_JSON,
                "The received ContentType header value is different from that expected");
    }

    @Test
    public void testClientProcessingLargePayload() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
                                .withMethod(HttpMethod.POST).withBody(largeFile)
                                .withHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain")
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
                "The received response body is not same as the expected");
        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
                HttpHeaders.Values.APPLICATION_JSON,
                "The received ContentType header value is different from that expected");
    }

    @Test
    public void testClientSlowWriting() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090")).withWritingDelay(3000)
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
                                .withMethod(HttpMethod.POST).withBody(plainFile)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
                "The received response body is not same as the expected");
        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
                HttpHeaders.Values.APPLICATION_JSON,
                "The received ContentType header value is different from that expected");
    }

    @Test
    public void testClientSlowReading() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090")).withReadingDelay(3000)
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
                                .withMethod(HttpMethod.POST).withBody(plainFile)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
                "The received response body is not same as the expected");
        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
                HttpHeaders.Values.APPLICATION_JSON,
                "The received ContentType header value is different from that expected");
    }

    @Test
    public void testClientDisableKeepAlive() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090")).withKeepAlive(false)
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
                                .withMethod(HttpMethod.POST).withBody(plainFile)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
                "The received response body is not same as the expected");
        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
                HttpHeaders.Values.APPLICATION_JSON,
                "The received ContentType header value is different from that expected");
        Assert.assertNotEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONNECTION),
                HttpHeaders.Values.KEEP_ALIVE,
                "The received Keep-alive header value is different from that expected");

    }

    @Test
    public void testDisableChunking() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator()
                .client()
                .given(
                        HttpClientConfigBuilderContext.configure()
                                .host("127.0.0.1")
                                .port(Integer.parseInt("9090"))
                )
                .when(
                        HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
                                .withMethod(HttpMethod.POST).withBody(plainFile)
                )
                .then(
                        HttpClientResponseBuilderContext.response().assertionIgnore()
                )
                .operation()
                .send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), responseBody,
                "The received response body is not same as the expected");
        Assert.assertEquals(response.getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
                HttpHeaders.Values.APPLICATION_JSON,
                "The received ContentType header value is different from that expected");
    }

    @Test
    public void testBurstRequests() {
        for (int i = 0; i < 10; i++) {
            HttpClientOperationBuilderContext httpClientOperationBuilderContext = Emulator.getHttpEmulator()
                    .client()
                    .given(
                            HttpClientConfigBuilderContext.configure()
                                    .host("127.0.0.1")
                                    .port(Integer.parseInt("9090"))
                    )
                    .when(
                            HttpClientRequestBuilderContext.request().withPath(echoBackServerPath)
                                    .withMethod(HttpMethod.POST).withBody(plainFile)
                    )
                    .then(
                            HttpClientResponseBuilderContext.response().assertionIgnore()
                    )
                    .operation()
                    .sendAsync();
            List<RequestResponseCorrelation> responseCorrelations = httpClientOperationBuilderContext.shutdown();
            Assert.assertEquals(responseCorrelations.get(0).getReceivedResponse().getReceivedResponseContext()
                            .getResponseBody(), responseBody,
                    "The received response body is not same as the expected");
            Assert.assertEquals(responseCorrelations.get(0).getReceivedResponse()
                            .getReceivedResponse().headers().get(HttpHeaders.Names.CONTENT_TYPE),
                    HttpHeaders.Values.APPLICATION_JSON,
                    "The received ContentType header value is different from that expected");
        }
    }


    @AfterClass
    public void stopAgent() throws IOException {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }

}
