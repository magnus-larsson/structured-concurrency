package ml.virtualthreads.structuredconcurrency.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;

@RestController
@RequestMapping("/composite")
public class Composite {

    private static final Logger LOG = LoggerFactory.getLogger(Composite.class);
    private final RestClient restClient;
    private final String BASE_URL = "http://localhost:7070";

    public Composite(RestClient.Builder restClientBuilder) {
        restClient = restClientBuilder
            .baseUrl(BASE_URL)
            .requestFactory(getClientHttpRequestFactory())
            .build();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        LOG.info("Configuring a SimpleClientHttpRequestFactory...");
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(Duration.ofMillis(500));
        simpleClientHttpRequestFactory.setReadTimeout(Duration.ofMillis(1000));
        return simpleClientHttpRequestFactory;
    }

    // curl http://localhost:8080/composite/ok
    @GetMapping("/ok")
    void runOk() {
        LOG.info("Calling services A, B and C...");
        try (var scope = new StructuredTaskScope<String>()) {
            var taskA = scope.fork(() -> callRestService("a", 0));
            var taskB = scope.fork(() -> callRestService("b", 0));
            var taskC = scope.fork(() -> callRestService("c", 0));
            scope.join();
            LOG.info("Done calling services A, B and C...");

            inspectTask("taskA", taskA);
            inspectTask("taskB", taskB);
            inspectTask("taskC", taskC);

        } catch (RuntimeException ex) {
            LOG.error(STR."RuntimeException: \{ex}");
        } catch (InterruptedException ex) {
            LOG.error(STR."InterruptedException: \{ex}");
            throw new RuntimeException(ex);
        }
    }

    // curl http://localhost:8080/composite/fail
    @GetMapping("/fail")
    void runFail() {
        LOG.info("Calling services A, B and C with failures...");
        try (var scope = new StructuredTaskScope<Object>()) {
            var taskA = scope.fork(() -> callRestService("a", 0));
            var taskB = scope.fork(() -> callRestService("not-found", 0));
            var taskC = scope.fork(() -> callRestService("c", 2000));
            scope.join();
            LOG.info("Done calling services A, B and C with failures...");

            inspectTask("taskA", taskA);
            inspectTask("taskB", taskB);
            inspectTask("taskC", taskC);

        } catch (Throwable e) {
            LOG.error(STR."Error: \{e}");
        }
    }

    private void inspectTask(String name, StructuredTaskScope.Subtask<String> st) {
        LOG.info(STR."State for \{name}: \{st.state()}");
        if (st.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
            LOG.info(STR."- Result: \{st.get()}");
        }
        if (st.state() == StructuredTaskScope.Subtask.State.FAILED) {
            LOG.info(STR."- Exception: \{st.exception()}");
        }
    }

    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 1000))
    public String callRestService(String serviceName, int responseDelayMS) {

        String url = STR."\{serviceName}/test?delay=\{responseDelayMS}";

        LOG.info(STR."Calling: \{BASE_URL}/\{url}");
        ResponseEntity<String> response = restClient.get()
            .uri(url)
            .retrieve()
            .toEntity(String.class);

        if (response.getStatusCode().value() != 200) {
            String message = STR."Error calling service: \{serviceName}, status code: \{response.getStatusCode().value()}";
            LOG.warn(message);
            throw new RuntimeException(message);
        } else {
            LOG.info(STR."Got a response from service \{serviceName}: \{response.getBody()}");
            return response.getBody();
        }
    }
}

