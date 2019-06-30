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
    private final TimezoneService timezoneService;

    public AppRouterSpringConfiguration(TimezoneService timezoneService)
    {
        this.timezoneService = timezoneService;
    }

    @Bean
    public RouterFunction<ServerResponse> getAllTimezoneInfo() {
            return RouterFunctions.route(RequestPredicates.GET("/timezone"), request ->
            {
                Flux<TimezoneService.TimezoneDisplayInfo> flux = timezoneService.getAllTimezoneDisplay();
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromPublisher(flux, TimezoneService.TimezoneDisplayInfo.class));
            })
            .andRoute(RequestPredicates.GET("/timezoneIds"), request ->
            {
                Flux<TimezoneService.TimezoneId> flux = timezoneService.getAllTimezoneIds().map(TimezoneService.TimezoneId::new);
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromPublisher(flux, TimezoneService.TimezoneId.class));
            });
    }
}
