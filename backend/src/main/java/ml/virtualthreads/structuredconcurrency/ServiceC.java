package ml.virtualthreads.structuredconcurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/c")
public class ServiceC {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceC.class);

    @GetMapping("/test")
    String runTest(@RequestParam(value = "delay", required = true) int delay) {

        if (delay > 0) {
            LOG.info("Service C, will sleep for {} ms...", delay);
            try {Thread.sleep(delay);} catch (InterruptedException e) {}
            LOG.info("Service C sleep is over");
        } else {
            LOG.info("Service C...");
        }
        return "C";
    }
}