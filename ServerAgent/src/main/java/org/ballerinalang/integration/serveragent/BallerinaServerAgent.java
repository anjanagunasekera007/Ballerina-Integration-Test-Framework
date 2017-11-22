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

package org.ballerinalang.integration.serveragent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This is a Agent to run in ballerina server machine.
 *
 * @since 1.0-SNAPSHOT
 */
@Path("/ballerinaagent")
public class BallerinaServerAgent {

    private static final Log log = LogFactory.getLog(BallerinaServerAgent.class);

    @POST
    @Path("/start")
    public void startService(@FormParam("ballerinaHome") String home,
                             @FormParam("ballerinaFilePath") String filePath,
                             @FormParam("config") String configPath) throws ServiceStartException {
        String[] args = {filePath};
        String[] cmdArray = {"bash", "bin/ballerina", "run"};
        String[] cmdArgs = Stream.concat(Arrays.stream(cmdArray), Arrays.stream(args))
                .toArray(String[]::new);
        ProcessBuilder pb = new ProcessBuilder(cmdArgs);
        pb.directory(new File(home));
        try {
            pb.inheritIO().start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        } catch (IOException e) {
            throw new ServiceStartException("Service " + filePath + " start failed", e);
        }
    }


    @POST
    @Path("/stop")
    public synchronized void stopService() {
        killProcess("9090");

    }

    @POST
    @Path("/stopagent")
    public synchronized void stopAgent() throws Exception {

        killProcess("9090");
        killProcess("9001");
    }


    public static void killProcess(String port) {
        String cmd = "fuser -k " + port + "/tcp";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            log.error("Unable to shut down server at 9090 ", e);
        }
    }


}
