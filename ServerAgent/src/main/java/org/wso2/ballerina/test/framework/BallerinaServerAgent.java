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
import javax.ws.rs.*;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
/**
 * This is a Agent to run in ballerina server machine
 *
 * @since 1.0-SNAPSHOT
 */
@Path("/ballerinaagent")
public class BallerinaServerAgent {

    private int httpServerPort = 9099;


    private static final Log log = LogFactory.getLog(org.wso2.ballerina.test.framework.BallerinaServerAgent.class);

    private Process process;


    @POST
    @Path("/start2")
    public void startService2(@FormParam("ballerinaHome") String home,
                              @FormParam("ballerinaFilePath") String filePath,
                              @FormParam("config") String configPath) {

        String[] cmdArray;
        System.out.println(home);
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

                process = pb.inheritIO().start();

                Thread.sleep(3000);

            }

        } catch (Exception e) {
            log.error("Error while starting ballerina service", e);

        }
    }


    @POST
    @Path("/stop")
    public synchronized void stopService() {
        if (process != null) {
            try {
                stopServer();
            } catch (Exception e) {
                log.error("Error while stopping Ballerina Service", e);
            }
            process = null;
        }
    }

    @POST
    @Path("/stopagent")
    public synchronized void stopAgent() throws Exception {
        if (process != null) {
                String pid;
                try {
                    pid = getServerPID();
                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                        Process killServer = Runtime.getRuntime().exec("TASKKILL -F /PID " + pid);
                        log.info(readProcessInputStream(killServer.getInputStream()));
                        killServer.waitFor(15, TimeUnit.SECONDS);
                        killServer.destroy();
                    } else {
                        Process killServer = Runtime.getRuntime().exec("kill -9 " + pid);
                        killServer.waitFor(15, TimeUnit.SECONDS);
                        killServer.destroy();
                    }
                } catch (IOException e) {
                    log.error("Error getting process id for the server in port - " + httpServerPort
                            + " error - " + e.getMessage(), e);
                    throw new Exception("Error while getting the server process id", e);
                } catch (Exception e) {
                    log.error("Error while stopping Ballerina server", e);
                }

                process = null;

        }

    }

    public void stopServer() throws Exception {
        log.info("Stopping server..");
        if (process != null) {
            String pid;
            try {
                pid = getServerPID();
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    Process killServer = Runtime.getRuntime().exec("TASKKILL -F /PID " + pid);
                    log.info(readProcessInputStream(killServer.getInputStream()));
                    killServer.waitFor(15, TimeUnit.SECONDS);
                    killServer.destroy();
                } else {
                    Process killServer = Runtime.getRuntime().exec("kill -9 " + pid);
                    killServer.waitFor(15, TimeUnit.SECONDS);
                    killServer.destroy();
                }
            } catch (IOException e) {
                log.error("Error getting process id for the server in port - " + httpServerPort
                        + " error - " + e.getMessage(), e);
                throw new Exception("Error while getting the server process id", e);
            } catch (InterruptedException e) {
                log.error("Error stopping the server in port - " + httpServerPort + " error - " + e.getMessage(), e);
                throw new Exception("Error waiting for services to stop", e);
            }
            process.destroy();
            process = null;
            //wait until port to close
            //TODO Fix port close
//            Utils.waitForPortToClosed(httpServerPort, 30000);
            log.info("Server Stopped Successfully");

//            deleteWorkDir();
        }
    }

    private String getServerPID() throws Exception {
        String pid = null;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            //reading the process id from netstat
            Process tmp;
            try {
                tmp = Runtime.getRuntime().exec("netstat -a -n -o");
            } catch (IOException e) {
                throw new Exception("Error retrieving netstat data", e);
            }

            String outPut = readProcessInputStream(tmp.getInputStream());
            String[] lines = outPut.split("\r\n");
            for (String line : lines) {
                String[] column = line.trim().split("\\s+");
                if (column.length < 5) {
                    continue;
                }
                if (column[1].contains(":" + httpServerPort) && column[3].contains("LISTENING")) {
                    log.info(line);
                    pid = column[4];
                    break;
                }
            }
            tmp.destroy();
        } else {

            //reading the process id from ss
            Process tmp = null;
            try {
                String[] cmd = { "bash", "-c",
                        "ss -ltnp \'sport = :" + httpServerPort + "\' | grep LISTEN | awk \'{print $6}\'" };
                tmp = Runtime.getRuntime().exec(cmd);
                String outPut = readProcessInputStream(tmp.getInputStream());
                log.info("Output of the PID extraction command : " + outPut);
                /* The output of ss command is "users:(("java",pid=24522,fd=161))" in latest ss versions
                 But in older versions the output is users:(("java",23165,116))
                 TODO : Improve this OS dependent logic */
                if (outPut.contains("pid=")) {
                    pid = outPut.split("pid=")[1].split(",")[0];
                } else {
                    pid = outPut.split(",")[1];
                }

            } catch (Exception e) {
                log.warn("Error occurred while extracting the PID with ss " + e.getMessage());
                // If ss command fails trying with lsof. MacOS doesn't have ss by default
                pid = getPidWithLsof(httpServerPort);
            } finally {
                if (tmp != null) {
                    tmp.destroy();
                }
            }
        }
        log.info("Server process id in " + System.getProperty("os.name").toLowerCase() + " : " + pid);

            return pid;
    }


    private String getPidWithLsof(int httpServerPort) throws Exception {
        String pid ="";
        Process tmp = null;
        try {
            String[] cmd = { "bash", "-c", "lsof -Pi tcp:" + httpServerPort + " | grep LISTEN | awk \'{print $2}\'" };
            tmp = Runtime.getRuntime().exec(cmd);
            pid = readProcessInputStream(tmp.getInputStream());

        } catch (Exception err) {
//            throw Exception("Error retrieving the PID : ", err);
            log.error("Error retrieving the PID : ", err);
        } finally {
            if (tmp != null) {
                tmp.destroy();
            }
        }
        return pid;
    }

    private String readProcessInputStream(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStreamReader = new InputStreamReader(inputStream, Charset.defaultCharset());
            bufferedReader = new BufferedReader(inputStreamReader);
            int x;
            while ((x = bufferedReader.read()) != -1) {
                stringBuilder.append((char) x);
            }
        } catch (Exception ex) {
            log.error("Error reading process id", ex);
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStream.close();
                    inputStreamReader.close();
                } catch (IOException e) {
                    log.error("Error occurred while closing stream: " + e.getMessage(), e);
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("Error occurred while closing stream: " + e.getMessage(), e);
                }
            }
        }
        return stringBuilder.toString();
    }

}
