package com.ns.timezone.javatimezone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

/**
 * @author nseo
 */
@Configuration
public class AppRouterSpringConfiguration
{
    @Bean
    public RouterFunction<ServerResponse> getAllTimezoneInfo() {
        return RouterFunctions.route(RequestPredicates.GET("/timezone"), request ->
        {
            Flux<Timezone.TimezoneDisplayInfo> flux = Flux.fromIterable(Timezone.buildTimezoneDisplayInfoList());
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromPublisher(flux, Timezone.TimezoneDisplayInfo.class));
        });
    }
}
