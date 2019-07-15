package com.svlugovoy.demoasyncapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;

@RestController
public class DemoController {

    @Autowired
    private RestTemplate restTemplate;

    HttpClient client = HttpClient.newHttpClient();
    WebClient helloClient = WebClient.create("http://localhost:8082");
    WebClient worldClient = WebClient.create("http://localhost:8081");

    @GetMapping("/sync")
    public String demo1() {

        long startTime = System.currentTimeMillis();

        ResponseEntity<String> msg1 = restTemplate.getForEntity("http://localhost:8082/hello", String.class);
        ResponseEntity<String> msg2 = restTemplate.getForEntity("http://localhost:8081/world", String.class);
        String result = msg1.getBody() + ", " + msg2.getBody() + "!!!";

        long endTime = System.currentTimeMillis();

        return result + " Total execution time: " + (endTime - startTime) + "ms";
    }

    @GetMapping("/future")
    public String demo2() throws ExecutionException, InterruptedException {

        long startTime = System.currentTimeMillis();

        ExecutorService service = Executors.newCachedThreadPool();
        Future<ResponseEntity<String>> future = service.submit(
                () -> restTemplate.getForEntity("http://localhost:8081/world", String.class)
        );

        ResponseEntity<String> msg1 = restTemplate.getForEntity("http://localhost:8082/hello", String.class);
        ResponseEntity<String> msg2 = future.get();

        String result = msg1.getBody() + ", " + msg2.getBody() + "!!!";

        long endTime = System.currentTimeMillis();

        return result + " Total execution time: " + (endTime - startTime) + "ms";
    }

    @GetMapping("/httpclient")
    public String demo3() throws URISyntaxException, ExecutionException, InterruptedException {

        long startTime = System.currentTimeMillis();

        HttpRequest helloRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8082/hello")).GET().build();

        HttpRequest worldRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/world")).GET().build();

        CompletableFuture<String> helloCompletableFuture = client
                .sendAsync(helloRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        CompletableFuture<String> worldCompletableFuture = client
                .sendAsync(worldRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        String result = helloCompletableFuture.join() + ", " + worldCompletableFuture.get() + "!!!";

        long endTime = System.currentTimeMillis();

        return result + " Total execution time: " + (endTime - startTime) + "ms";
    }

    @GetMapping("/httpclient2")
    public String demo4() throws URISyntaxException, ExecutionException, InterruptedException {

        long startTime = System.currentTimeMillis();

        HttpRequest helloRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8082/hello")).GET().build();

        HttpRequest worldRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/world")).GET().build();

        CompletableFuture<String> helloCompletableFuture = client
                .sendAsync(helloRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        CompletableFuture<String> worldCompletableFuture = client
                .sendAsync(worldRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        String result = helloCompletableFuture
                .thenCombine(worldCompletableFuture, (h, w) -> h + ", " + w + "!!!").get();

        long endTime = System.currentTimeMillis();

        return result + " Total execution time: " + (endTime - startTime) + "ms";
    }

    @GetMapping("/reactor")
    public String demo5() {

        long startTime = System.currentTimeMillis();

        Mono<String> helloResp = helloClient.get()
                .uri("/hello")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> worldResp = worldClient.get()
                .uri("/world")
                .retrieve()
                .bodyToMono(String.class);

        String result = helloResp.zipWith(worldResp, (h, w) -> h + ", " + w + "!!!").block();

        long endTime = System.currentTimeMillis();

        return result + " Total execution time: " + (endTime - startTime) + "ms";
    }

}
