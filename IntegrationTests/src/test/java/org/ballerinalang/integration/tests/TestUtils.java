/*
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
package org.ballerinalang.integration.tests;

import org.testng.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtils {
    public static String getCurrentDir() {
        return System.getProperty("user.dir") + File.separator;
    }

    /**
     * Get synapse configuration template file as string
     *
     * @param relativePath path relative to src/test/resources/synapseConfigs
     * @return string representation of the synapse config file content
     * @throws IOException if issue in accessing the file
     */
    public static String getSynapseConfig(String relativePath) throws IOException {
        String resourceLocation = getTestResourceLocation();

        String absolutePath = resourceLocation + File.separator + "synapseConfigs" + File.separator + relativePath;
        return getContentAsString(absolutePath);
    }

    /**
     * Get file content as string
     *
     * @param filePath path to file
     * @return string content
     * @throws IOException if issue accessing the file
     */
    public static String getContentAsString(String filePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, Charset.defaultCharset());
    }

    public static String getTestResourceLocation() {
        String resourceFileLocation = System.getProperty("framework.resource.location");

        //noinspection ObviousNullCheck System.getProperty can return null
        Assert.assertNotNull(resourceFileLocation, "framework.resource.location property should be set");

        return resourceFileLocation;
    }

    public static String getFileBody(File filePath) throws IOException {

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            int c;
            StringBuilder stringBuilder = new StringBuilder();
            while ((c = fileInputStream.read()) != -1) {
                stringBuilder.append(c);
            }
            String content = stringBuilder.toString();
            content = content.replace("\n", "").replace("\r", "");

            return content;
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }
}
