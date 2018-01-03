package muio.restapi;

import io.vertx.codegen.annotations.VertxGen;
import muio.restapi.annotations.Blocking;
import muio.restapi.annotations.Get;
import muio.restapi.annotations.Path;

@Path("")
public class TestApi {

    @Get("123")
    @Blocking
    public void test(String test, Integer test2) {

    }
}
