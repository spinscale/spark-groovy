package de.spinscale.spark

import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.template.FileTemplateLoader
import de.neuland.jade4j.template.JadeTemplate
import groovy.json.JsonBuilder
import spark.Filter
import spark.Request
import spark.Response
import spark.Route

class SparkGroovy {

    static JadeConfiguration config = new JadeConfiguration()
    static {
        config.setTemplateLoader(new FileTemplateLoader("src/main/resources/views/", "UTF-8"))
    }

    def before(final Closure closure) {
        spark.Spark.before(new Filter() {
            void handle(Request request, Response response) {
                closure.delegate = this
                closure(request, response)
            }
        })
    }

    def after(Closure closure) {
        spark.Spark.after(new Filter() {
            void handle(Request request, Response response) {
                closure.delegate = this
                closure(request, response)
            }
        })
    }

    private Route createClosureBasedRouteForPath(String path, Closure ... closures) {
        new Route(path) {
            def handle(Request request, Response response) {
                closures*.delegate = this
                return closures*.call(request, response).findAll { it }.join()
            }
        }
    }

    def get(String path, Closure ... closures) {
        spark.Spark.get(createClosureBasedRouteForPath(path, closures))
    }

    def post(String path, Closure ... closures) {
        spark.Spark.post(createClosureBasedRouteForPath(path, closures))
    }

    def put(String path, Closure ... closures) {
        spark.Spark.put(createClosureBasedRouteForPath(path, closures))
    }

    def delete(String path, Closure ... closures) {
        spark.Spark.delete(createClosureBasedRouteForPath(path, closures))
    }

    def head(String path, Closure ... closures) {
        spark.Spark.head(createClosureBasedRouteForPath(path, closures))
    }

    def trace(String path, Closure ... closures) {
        spark.Spark.trace(createClosureBasedRouteForPath(path, closures))
    }

    def connect(String path, Closure ... closures) {
        spark.Spark.connect(createClosureBasedRouteForPath(path, closures))
    }

    def options(String path, Closure ... closures) {
        spark.Spark.options(createClosureBasedRouteForPath(path, closures))
    }

    /* renderers */
    def json(Object obj) {
        return new JsonBuilder(obj)
    }

    def jade(String template, Object obj) {
        JadeTemplate jadeTemplate = config.getTemplate(template)
        return config.renderTemplate(jadeTemplate, obj)
    }

}
