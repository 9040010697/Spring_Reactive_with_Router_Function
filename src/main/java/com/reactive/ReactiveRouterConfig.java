package com.reactive;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.handler.EmployeeHandler;

@Configuration
public class ReactiveRouterConfig {

	@Bean
	public RouterFunction<ServerResponse> routerFunction(EmployeeHandler routerHandlers) {
		return RouterFunctions.route(RequestPredicates.GET("/rest/employee/all"), routerHandlers::getAll)
				.andRoute(RequestPredicates.GET("/rest/employee/timeEvent"), routerHandlers::getTimeEvents)
				.andRoute(RequestPredicates.GET("/rest/employee/{id}"), routerHandlers::getId)
				.andRoute(RequestPredicates.GET("/rest/employee/{id}/events"), routerHandlers::getEvents);

	}
}
