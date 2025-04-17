package ml.virtualthreads.structuredconcurrency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

// import static java.lang.StringTemplate.STR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

@RestController
@RequestMapping("/httpbin")
public class HttpBinController {

    private static final Logger LOG = LoggerFactory.getLogger(HttpBinController.class);
    private final RestClient restClient;

    // Try out RestClient.builder().requestFactory(new HttpComponentsClientHttpRequestFactory())
    //
    // https://stackoverflow.com/questions/77785181/the-new-restclient-in-spring-boot-3-2-how-to-log-the-outgoing-requests-and-resp
    // https://github.com/spring-projects/spring-boot/issues/38832
    public HttpBinController(RestClient.Builder restClientBuilder) {
        restClient = restClientBuilder.baseUrl("https://httpbin.org/").build();
    }

    @GetMapping("/test")
    void runTest() {
        LOG.info("Running HttpBin test");
		LOG.info("\n\r-- BEGIN Sync");
		try {
			sync();
		} catch (Exception e) {
			System.err.println("Error: " + e);
		}
		System.out.println("\n\r-- BEGIN Structured Concurrency");
		try {
			sc();
		} catch (Exception e) {
			System.err.println("Error: " + e);
		}
		System.out.println("\n\r-- BEGIN failFast");
		try {
			failFast();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
		System.out.println("\n\r-- BEGIN succeedFast");
		try {
			succeedFast();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
    }

    public String getWithRestClient(int id) {

        Article article = new Article(id, "ML TITLE");
        // System.out.println(STR."REQUEST body to httpbin: \{article.articleId} & \{article.title}");
        System.out.println("REQUEST body to httpbin: " + article.articleId + " & " + article.title);

        if (id < 0) {
            ResponseEntity<String> response = restClient.get()
                    .uri("https://httpbin.org/status/409")
                    .retrieve()
                    .toEntity(String.class);

            // Check the response status code
            if (response.getStatusCode().value() != 200) {
                //System.err.println("Error fetching planet information for ID: " + planetId);
                throw new RuntimeException("Error fetching planet information for ID: " + id);
            } else {
                // Parse the JSON response and extract planet information
                System.out.println("Got a Planet: " + response.getBody());
            }
        }

        ResponseEntity<String> response = restClient.post()
                .uri("https://httpbin.org/anything")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(article)
                .retrieve()
                .toEntity(String.class);

        JsonNode responseJson = getJsonNode(response);
        JsonNode data = responseJson.findPath("data");
        String dataText = data.asText();

        Article responseArticle = getArticle(dataText);

        // System.out.println(STR."RESPONSE from httpbin: \{response.getStatusCode()}: \{responseArticle.articleId} & \{responseArticle.title} } on thread class [\{Thread.currentThread().getClass().getName()}]");
        System.out.println("RESPONSE from httpbin: " + response.getStatusCode() + " : " + responseArticle.articleId + " & " + responseArticle.title + " on thread class [" + Thread.currentThread().getClass().getName() + "]");
        return dataText;
    }

    private Article getArticle(String dataText) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(dataText, Article.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode getJsonNode(ResponseEntity<String> response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Test methods
     */

	void failFast() throws ExecutionException, InterruptedException {
		int[] planetIds = { 1, 2, 3, -1, 4 };
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			for (int planetId : planetIds) {
				scope.fork(() -> getWithRestClient(planetId));
			}
			scope.join();
		}
	}

	void succeedFast() throws ExecutionException, InterruptedException {
		int[] planetIds = { 1, 2 };
		try (var scope = new StructuredTaskScope.ShutdownOnSuccess()) {
			for (int planetId : planetIds) {
				scope.fork(() -> getWithRestClient(planetId));
			}
			scope.join();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}

	}

	void sync() throws Exception {
		int[] planetIds = { 1, 2, 3, 4, 5 };
		for (int planetId : planetIds) {
			getWithRestClient(planetId);
		}
	}

	void sc() throws Exception {
		int[] planetIds = { 1, 2, 3, 4, 5 };
		try (var scope = new StructuredTaskScope<Object>()) {
			for (int planetId : planetIds) {
				Subtask<Object> st = scope.fork(() -> getWithRestClient(planetId));
			}
			scope.join();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}

	}

}
