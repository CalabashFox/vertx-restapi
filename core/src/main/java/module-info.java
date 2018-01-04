module io.calabash.vertx {
    exports io.calabash.vertx.annotation;
    exports io.calabash.vertx.param.annotation;

    requires java.compiler;
    requires vertx.core;
    requires vertx.web;
}

