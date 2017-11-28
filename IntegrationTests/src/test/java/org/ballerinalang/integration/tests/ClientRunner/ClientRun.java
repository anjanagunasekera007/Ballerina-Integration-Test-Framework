package org.ballerinalang.integration.tests.ClientRunner;/*
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ballerinalang.integration.tests.Clients.ClientSlowReading;
import org.ballerinalang.integration.tests.Clients.ClientLargePayload;
import org.ballerinalang.integration.tests.Clients.ClientSlowWriting;
import org.testng.annotations.Test;


public class ClientRun {

    @Test
    public void testcaseX() throws ExecutionException, InterruptedException {


        Future taskTwo = null;
        Future taskThree = null;
        Future taskFour = null;
        ExecutorService executor = Executors.newFixedThreadPool(2);


        for (int i=0 ;i < 100;i++)
        {
            if ((taskTwo == null) || (taskTwo.isDone()) || (taskTwo.isCancelled())) {
                taskTwo = executor.submit(new ClientLargePayload());
            }
            if ((taskThree == null) || (taskThree.isDone()) || (taskThree.isCancelled())) {
                taskTwo = executor.submit(new ClientSlowWriting());
            }
            if ((taskFour == null) || (taskFour.isDone()) || (taskFour.isCancelled())) {
                taskTwo = executor.submit(new ClientSlowReading());
            }

        }

        executor.shutdown();
        System.out.println("-----------------------");
        executor.awaitTermination(1, TimeUnit.SECONDS);

    }
    }

