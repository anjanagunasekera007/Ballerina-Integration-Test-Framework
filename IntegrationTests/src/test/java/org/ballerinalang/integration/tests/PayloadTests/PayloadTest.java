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

    private File plainFile = new File("src/test/resources/files/100KB.txt");
    private File largeFile = new File("src/test/resources/files/1MB.txt");

    private String largeFilePath = "/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/EmulatorServer/1MB.txt";
    private String plainFilePath = "/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/IntegrationTests/src/test/resources/files/100KB.txt";


        @BeforeClass
    public void initParameters() throws Exception {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/start2");
        postMethod.addParameter("ballerinaHome", "/home/anjana/work/buildballerina/tools-distribution/modules/ballerina/target/ballerina-0.95.1-SNAPSHOT/");
        postMethod.addParameter("ballerinaFilePath", "/home/anjana/work/Ballerina-Integration-Test-Framework-Bals/Test.bal");
//        postMethod.addParameter("Config", "config.xml");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
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

    @AfterClass
    public void StopAgent() throws IOException {
        PostMethod postMethod = new PostMethod("http://localhost:9001/ballerinaagent/stop");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
    }
}
