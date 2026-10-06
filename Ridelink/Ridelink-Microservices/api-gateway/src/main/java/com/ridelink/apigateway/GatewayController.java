package com.ridelink.apigateway;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class GatewayController {

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final String accountServiceUrl;
    private final String driverServiceUrl;
    private final String rideServiceUrl;
    private final String paymentServiceUrl;

    public GatewayController(
            @Value("${services.account.url}") String accountServiceUrl,
            @Value("${services.driver.url}") String driverServiceUrl,
            @Value("${services.ride.url}") String rideServiceUrl,
            @Value("${services.payment.url}") String paymentServiceUrl) {
        this.accountServiceUrl = accountServiceUrl;
        this.driverServiceUrl = driverServiceUrl;
        this.rideServiceUrl = rideServiceUrl;
        this.paymentServiceUrl = paymentServiceUrl;
    }

    @RequestMapping({
            "/api/v1/auth/**", "/api/v1/accounts/**", "/api/v1/admin/accounts/**",
            "/api/v1/drivers/**", "/api/v1/vehicles/**",
            "/api/v1/rides/**", "/api/v1/payments/**"
    })
    public void proxy(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        String path = request.getRequestURI();
        String baseUrl = resolveService(path);
        String query = request.getQueryString();
        URI uri = URI.create(baseUrl + path + (query == null ? "" : "?" + query));

        byte[] body = request.getInputStream().readAllBytes();
        HttpRequest.BodyPublisher publisher = body.length == 0
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(body);

        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .method(request.getMethod(), publisher);

        Collections.list(request.getHeaderNames()).forEach(name -> {
            if (!isHopByHopHeader(name)) {
                Collections.list(request.getHeaders(name)).forEach(value -> builder.header(name, value));
            }
        });

        HttpResponse<byte[]> downstream = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        response.setStatus(downstream.statusCode());
        downstream.headers().map().forEach((name, values) -> {
            if (!name.equalsIgnoreCase("transfer-encoding") && !name.equalsIgnoreCase("content-length")) {
                values.forEach(value -> response.addHeader(name, value));
            }
        });
        response.getOutputStream().write(downstream.body());
    }

    private String resolveService(String path) {
        if (path.startsWith("/api/v1/auth") || path.startsWith("/api/v1/accounts") || path.startsWith("/api/v1/admin/accounts")) {
            return accountServiceUrl;
        }
        if (path.startsWith("/api/v1/drivers") || path.startsWith("/api/v1/vehicles")) {
            return driverServiceUrl;
        }
        if (path.startsWith("/api/v1/rides")) {
            return rideServiceUrl;
        }
        if (path.startsWith("/api/v1/payments")) {
            return paymentServiceUrl;
        }
        throw new IllegalArgumentException("No route configured for " + path);
    }

    private boolean isHopByHopHeader(String name) {
        return name.equalsIgnoreCase("host")
                || name.equalsIgnoreCase("content-length")
                || name.equalsIgnoreCase("connection")
                || name.equalsIgnoreCase("transfer-encoding")
                || name.equalsIgnoreCase("upgrade")
                || name.equalsIgnoreCase("keep-alive")
                || name.equalsIgnoreCase("proxy-authenticate")
                || name.equalsIgnoreCase("proxy-authorization")
                || name.equalsIgnoreCase("te")
                || name.equalsIgnoreCase("trailer");
    }
}
