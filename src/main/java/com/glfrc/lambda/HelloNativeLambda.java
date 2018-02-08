package com.glfrc.lambda;

import com.glfrc.jni.JavaHello;
import com.glfrc.lambda.Request;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HelloNativeLambda implements RequestHandler<Request, Response> {

    public JavaHello hi = new JavaHello();

    public Response handleRequest(Request request, Context context) {
        System.out.println("name = " + request.getName());

        return new Response(hi.sayHello(request.getName()));
    }
}
