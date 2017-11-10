package org.wso2.ballerina.test.framework;
/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.msf4j.HttpStreamHandler;
import org.wso2.msf4j.HttpStreamer;
//import org.wso2.synapse.test.framework.ServerLogReader;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.msf4j.HttpStreamHandler;
import org.wso2.msf4j.HttpStreamer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * This is a Agent to run in synapse server machine
 *
 * @since 1.0-SNAPSHOT
 */
@Path("/synapseAgent")
public class BallerinaServerAgent {

    private static final String DEFAULT_SYNAPSE_HOME_LOCATION = ".";
    public static final String INTEGRATION_SYNAPSE_XML = "integration-synapse.xml";
    public static final String BALLERINA_SERVER_SCRIPT_NAME = "ballerina";

    private ServerLogReader inputStreamHandler;
    private ServerLogReader errorStreamHandler;

    private String serverHome;


    private static final Log log = LogFactory.getLog(org.wso2.ballerina.test.framework.BallerinaServerAgent.class);

    private Process process;

    @GET
    @Path("/start")
//    public synchronized void  startServer(String[] args) {
    public synchronized void  startService(String[] ar) {

        //--------------------------------------------------------o----------------------------------------------

        String serverHome = "/home/anjana/work/buildballerina/tools-distribution/modules/ballerina/target/ballerina-0.94.0-SNAPSHOT/";
//        String[] args = {"/home/anjana/JavaP/src/runBallerina/Test.bal"};
        String[] args = {"/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/ServerAgent/src/main/java/org/wso2/ballerina/test/framework/Test.bal"};
        String scriptName = "ballerina";
        String[] cmdArray;
        File commandDir = new File(serverHome);
        File err = new File("/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/ServerAgent/src/main/java/org/wso2/ballerina/test/framework/Errors.txt");

        Process process;

        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                commandDir = new File(serverHome + File.separator + "bin");
                cmdArray = new String[]{"cmd.exe", "/c", scriptName + ".bat", "run"};
                String[] cmdArgs = Stream.concat(Arrays.stream(cmdArray), Arrays.stream(args))
                       .toArray(String[]::new);
                process = Runtime.getRuntime().exec(cmdArgs, null, commandDir);

            } else {
                System.out.println();
                cmdArray = new String[]{"bash", "bin/" + scriptName, "run"};
                String[] cmdArgs = Stream.concat(Arrays.stream(cmdArray), Arrays.stream(args))
                        .toArray(String[]::new);
                System.out.println(Arrays.toString(args));
                System.out.println(Arrays.toString(cmdArgs));
//                process = Runtime.getRuntime().exec(cmdArgs, null, commandDir);

                ProcessBuilder pb = new ProcessBuilder(cmdArgs);
                pb.directory(new File(serverHome));
                pb.redirectError(err);

                process = pb.start();
                process.waitFor();

            }

        } catch (InterruptedException e) {
            System.out.println("ERROR 1");
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("ERROR 2");
            e.printStackTrace();
        }
//        long pid = ProcessHandle.current().getPid();


    }


    @GET
    @Path("/stop")
    public synchronized void stopService() {
        if (process != null) {
            try {
//                String synapseKillCommand = getSynapseHome() + File.separator + "bin" + File.separator + "synapse-stop.sh";
//                Runtime.getRuntime().exec(synapseKillCommand);
            } catch (Exception e) {
                log.error("Error while stopping Ballerina Service", e);
            }
            inputStreamHandler.stop();
            errorStreamHandler.stop();
            process = null;
        }
    }

    @GET
    @Path("/stop")
    public synchronized void stopAgent() {
        if (process != null) {
            try {
//                String synapseKillCommand = getSynapseHome() + File.separator + "bin" + File.separator + "synapse-stop.sh";
//                Runtime.getRuntime().exec(synapseKillCommand);
            } catch (Exception e) {
                log.error("Error while stopping synapse server", e);
            }
            inputStreamHandler.stop();
            errorStreamHandler.stop();
            process = null;
        }
    }



//    private String getSynapseHome() {
//        return System.getProperty("synapse.home", DEFAULT_SYNAPSE_HOME_LOCATION);
//    }

//    @GET
//    @Path("/stop")
//    public synchronized void stopServer() {
//        if (process != null) {
//            try {
//                String synapseKillCommand = getSynapseHome() + File.separator + "bin" + File.separator + "synapse-stop.sh";
//                Runtime.getRuntime().exec(synapseKillCommand);
//            } catch (IOException e) {
//                log.error("Error while stopping synapse server", e);
//            }
//            inputStreamHandler.stop();
//            errorStreamHandler.stop();
//            process = null;
//        }
//    }
//
//    /**
//     * Upload a file with streaming.
//     *
//     * @param httpStreamer Handle for setting the {@link HttpStreamHandler}callback for streaming.
//     * @throws IOException
//     */
//    @POST
//    @Path("/upload-config")
//    public void postFile(@Context HttpStreamer httpStreamer) throws IOException {
//        httpStreamer.callback(new org.wso2.synapse.test.framework.ServerAgent.HttpStreamHandlerImpl(
//                getSynapseHome() + File.separator + "repository" + File.separator + "conf" + File.separator
//                        + INTEGRATION_SYNAPSE_XML));
//    }
//
//    private static class HttpStreamHandlerImpl implements HttpStreamHandler {
//        private static final String SYNAPSE_SAMPLE_DIR = DEFAULT_SYNAPSE_HOME_LOCATION + File.separator + "repository"
//                + File.separator + "conf";
//        private FileChannel fileChannel = null;
//        private org.wso2.msf4j.Response response;
//
//        HttpStreamHandlerImpl(String fileName) throws FileNotFoundException {
//            File file = Paths.get(fileName).toFile();
//            if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
//                fileChannel = new FileOutputStream(file).getChannel();
//            }
//        }
//
//        @Override
//        public void init(org.wso2.msf4j.Response response) {
//            this.response = response;
//        }
//
//        @Override
//        public void end() throws Exception {
//            fileChannel.close();
//            response.setStatus(Response.Status.ACCEPTED.getStatusCode());
//            response.send();
//        }
//
//        @Override
//        public void chunk(ByteBuffer content) throws Exception {
//            if (fileChannel == null) {
//                throw new IOException("Unable to write file");
//            }
//            fileChannel.write(content);
//        }
//
//        @Override
//        public void error(Throwable cause) {
//            try {
//                if (fileChannel != null) {
//                    fileChannel.close();
//                }
//            } catch (IOException e) {
//                // Log if unable to close the output stream
//                log.error("Unable to close file output stream", e);
//            }
//        }
//    }
}
