package io.mu.restapi;

import io.mu.restapi.annotations.Blocking;
import io.mu.restapi.annotations.Get;
import io.mu.restapi.annotations.Path;

@Path("")
public class TestApi {

    @Get("123")
    @Blocking
    public void test(String test, Integer test2) {

    }
}
