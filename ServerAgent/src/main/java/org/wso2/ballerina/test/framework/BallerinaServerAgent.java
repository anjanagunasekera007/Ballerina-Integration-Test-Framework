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

import com.sun.deploy.net.HttpResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.msf4j.HttpStreamHandler;
import org.wso2.msf4j.HttpStreamer;
//import org.wso2.synapse.test.framework.ServerLogReader;

import javax.ws.rs.*;
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
@Path("/ballerinaagent")
public class BallerinaServerAgent {

    private static final String DEFAULT_SYNAPSE_HOME_LOCATION = ".";
    public static final String INTEGRATION_SYNAPSE_XML = "integration-synapse.xml";
    public static final String BALLERINA_SERVER_SCRIPT_NAME = "ballerina";

    private ServerLogReader inputStreamHandler;
    private ServerLogReader errorStreamHandler;

    private String serverHome;


    private static final Log log = LogFactory.getLog(org.wso2.ballerina.test.framework.BallerinaServerAgent.class);

    private Process process;

    @POST
    @Path("/start")
    public boolean  startService(@FormParam("ballerinaHome") String home,
                                 @FormParam("ballerinaFilePath") String filePath,
                                 @FormParam("config") String configPath) throws Exception {

        //--------------------------------------------------------o----------------------------------------------

        System.out.println(" BALLERINA SERVER AGENT STARTED ======");
//        String serverHome = "/home/anjana/work/buildballerina/tools-distribution/modules/ballerina/target/ballerina-0.94.0-SNAPSHOT/";
//        String[] args = {"/home/anjana/work/Test-framework/wso2-synapse-engine-test-framework/ServerAgent/src/main/java/org/wso2/ballerina/test/framework/Test.bal"};
//        String scriptName = "ballerina";
        String[] cmdArray;
        System.out.println(" HOME ");
        System.out.println(home);
        System.out.println(" FILE PATH ");
        System.out.println(filePath);
        File commandDir = new File(home);

        String serverHome = home;
        String[] args = {filePath};
        String scriptName = "ballerina";


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
                ProcessBuilder pb = new ProcessBuilder(cmdArgs);
                pb.directory(new File(serverHome));
//                pb.redirectError(err);
//                String error = pb.redirectError().toString();
//                pb.redirectError(error);
                process = pb.inheritIO().start();

//                process = pb.inheritIO().command();
//                process = pb.command(cmdArgs).start();
//                pb.redirectInput(process.INHERIT)



//                System.console().writer().println(error);

//                process = pb.start();
                //---
                return true;
                //---
                process.waitFor();

            }

        } catch (Exception e) {
//            throw new StartFailException("Error while starting Ballerina Service", e);

        }
    }


    @POST
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

    @POST
    @Path("/stopagent")
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
}
