package ml.virtualthreads.structuredconcurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/a")
public class ServiceA {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceA.class);

    @GetMapping("/test")
    String runTest() {
        LOG.info("Service A...");
        return "A";
    }
}