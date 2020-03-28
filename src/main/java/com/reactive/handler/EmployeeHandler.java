package com.reactive.handler;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.model.Employee;
import com.reactive.model.EmployeeEvent;
import com.reactive.repository.EmployeeRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
public class EmployeeHandler {

	@Autowired
	private EmployeeRepository employeeRepository;

	public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
		return ServerResponse
				.ok()
				.body(employeeRepository.findAll(), Employee.class);
	}

	public Mono<ServerResponse> getId(ServerRequest serverRequest) {

		String empId = serverRequest.pathVariable("id");
		return ServerResponse
				.ok()
				.body(employeeRepository.findById(empId), Employee.class);
	}

	public Mono<ServerResponse> getEvents(ServerRequest serverRequest) {
		String empId = serverRequest.pathVariable("id");

		return ServerResponse.ok()
				.contentType(MediaType.TEXT_EVENT_STREAM)
				.body(getEmpEvents(empId),
				EmployeeEvent.class);
	}

	public Mono<ServerResponse> getTimeEvents(ServerRequest serverRequest) {
		return ServerResponse.ok()
				.contentType(MediaType.TEXT_EVENT_STREAM)
				.body(getTimeEvents(), Date.class);
	}

	private Flux<Date> getTimeEvents() {
		// Sleep for 2 second
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));
		Flux<Date> dateEvent = Flux.fromStream(Stream.generate(Date::new));
	
		// Combining results
		return Flux.zip(interval, dateEvent).map(Tuple2::getT2);
	}
	
	private Flux<EmployeeEvent> getEmpEvents(String empId) {
		return employeeRepository.findById(empId).flatMapMany(employee -> {

			// Sleep for 2 second
			Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));

			Flux<EmployeeEvent> employeeEventFlux = Flux
					.fromStream(Stream.generate(() -> new EmployeeEvent(employee, new Date())));

			// Combine both and return only employeeEvent Data
			return Flux.zip(interval, employeeEventFlux).map(Tuple2::getT2);
		});
	}

}
