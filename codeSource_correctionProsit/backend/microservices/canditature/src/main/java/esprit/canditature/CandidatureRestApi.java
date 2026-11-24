package esprit.canditature;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/candidatures")
public class CandidatureRestApi {

    private final DiscoveryClient discoveryClient;

    public CandidatureRestApi(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping ("/hello")
    public String sayHello() {

        return "Hello Im microservice 3!";
    }

    @GetMapping("/instance")
    public List<Map<String, Object>> getInstances() {
        List<ServiceInstance> instances = discoveryClient.getInstances("candidature");
        if (instances.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Aucune instance CANDIDATURE n'est enregistrée dans Eureka");
        }

        return instances.stream()
                .map(instance -> Map.<String, Object>of(
                        "serviceId", instance.getServiceId(),
                        "host", instance.getHost(),
                        "port", instance.getPort(),
                        "uri", instance.getUri().toString()))
                .toList();
    }
}

@RestController
@RequestMapping("/candidature")
class CandidatureInstanceApi {

    private final int port;

    CandidatureInstanceApi(@Value("${server.port:8080}") int port) {
        this.port = port;
    }

    @GetMapping("/instance")
    public String getInstance() {
        return "CANDIDATURE instance on port " + port;
    }
}
