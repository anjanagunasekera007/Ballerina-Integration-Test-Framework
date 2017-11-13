import ballerina.net.http;

service<http> passthrough {

    @Description {value:"Requests which contain any HTTP method will be directed to passthrough resource."}
    @http:resourceConfig {
        path:"/"
    }
    resource passthrough (http:Request req, http:Response res) {

        println("Service invoked");
        res.setStringPayload("Hello, World!");
        res.send();

    }
}