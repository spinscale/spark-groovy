package de.spinscale.spark

import spark.Request
import spark.Response

class WebServer extends SparkGroovy{

    public static void main(String[] argv) {
        init()
    }

    // workaround needed for my jrebel class reloader
    public static void init() {
        new WebServer().start()
    }


    public void start() {
        def authCheck = { Request request, Response response ->
            if (request.session().attribute('userLogin') != request.params(":name")) {
                halt(401, "No permissions for helloing ${request.params(":name")}\n")
            }
        }

        post "/login/:name", { Request request, Response response ->
            request.session().attribute("userLogin", request.params(":name"))
            response.status(204)
        }

        // only works if you already logged in, due to authCheck
        get "/greet/:name", authCheck, { Request request, Response response ->
            return "Hello " + request.params('name') + "\n"
        }

        // simple post, no frills
        post "/hello", { Request request, Response response ->
            return "hello " + request.requestMethod() + "\n"
        }

        // json renderer
        get "/json", { Request request, Response response ->
            json([foo: 'bar'])
        }

        // jade renderer
        get "/jade", { Request request, Response response ->
            jade "test.jade", [foo:  'barz', pageName: 'My page']
        }


        // json and xml closures depending on request header
        def jsonClosure = { Request request, Response response ->
            if (request.contentType() == 'application/json')
                json([name: request.attribute('name')])
        }

        def xmlClosure = { Request request, Response response ->
            if (request.contentType() == 'application/xml')
                '<name>' + request.attribute('name') + '</name>\n'
        }

        // returns nothing if no content-type header is set
        get "/format", { Request request, Response response ->
            request.attribute("name", "some cool name")
        }, jsonClosure, xmlClosure
    }
}
