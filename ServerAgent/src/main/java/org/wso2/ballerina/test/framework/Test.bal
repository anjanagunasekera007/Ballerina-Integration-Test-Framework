import ballerina.net.http;

@http:configuration {
    basePath:"/services/normal_server"
}
service<http> passthrough {

    @Description {value:"Requests which contain any HTTP method will be directed to passthrough resource."}
    @http:resourceConfig {
        methods:["POST"],
        path:"/"
    }
    resource slowResponse (http:Request req, http:Response res) {
        endpoint<http:HttpClient> endPoint {
            create http:HttpClient("http://10.100.5.65:6065", {});


        }
        string method = req.getMethod();
        http:Response clientResponse = {};
        http:HttpConnectorError err;
        clientResponse, err = endPoint.execute(method, "/slow/response", req);
        println("PROXY");
        if (err != null) {
            res.setStatusCode(500);
            res.setStringPayload(err.msg);
            res.send();
        } else {
            res.forward(clientResponse);
        }
    }
}