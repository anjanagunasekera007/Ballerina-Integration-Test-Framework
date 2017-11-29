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

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WaitUtil {

    private Semaphore waitSem;

    public WaitUtil(int permits) {
        waitSem = new Semaphore(permits);
    }

    public boolean waitTillResponse(long timeout) {
        try {
            return waitSem.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void releaseSem() {
        waitSem.release();
    }
}
