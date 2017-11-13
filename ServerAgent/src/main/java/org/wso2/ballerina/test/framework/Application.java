package org.wso2.ballerina.test.framework;

//import org.wso2.msf4j.MicroservicesRunner;

import org.wso2.msf4j.MicroservicesRunner;

public class Application {

    public static void main(String[] args) {
//        new MicroservicesRunner()
//                .deploy(new ServerAgent())
//                .start();


        System.out.println("Starting ms in  8081");
        new MicroservicesRunner().deploy(new BallerinaServerAgent()).start();
//        String[] ar = new String[0];
//        BallerinaServerAgent bs = new BallerinaServerAgent();
//        bs.startService(ar);
    }


}
