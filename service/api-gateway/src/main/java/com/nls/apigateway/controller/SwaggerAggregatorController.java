package com.nls.apigateway.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nls.apigateway.config.SwaggerServiceConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api-docs/aggregate")
@RequiredArgsConstructor
public class SwaggerAggregatorController {

    private final SwaggerServiceConfig swaggerConfig;
    private final WebClient webClient = WebClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping
    public Mono<ResponseEntity<String>> aggregate() {
        List<Mono<JsonNode>> docs = swaggerConfig.services().stream()
                .map(service -> webClient.get()
                        .uri(service.url())
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(this::toJsonNode))
                .collect(Collectors.toList());

        return Mono.zip(docs, this::mergeAll)
                .map(json -> ResponseEntity.ok(json.toPrettyString()));
    }

    private JsonNode toJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON from service", e);
        }
    }

    private JsonNode mergeAll(Object[] jsonNodes) {
        ObjectNode merged = mapper.createObjectNode();
        ObjectNode paths = mapper.createObjectNode();
        ObjectNode components = mapper.createObjectNode();

        for (Object nodeObj : jsonNodes) {
            ObjectNode root = (ObjectNode) nodeObj;
            // Merge paths
            JsonNode pathNode = root.path("paths");
            pathNode.fields().forEachRemaining(entry -> paths.set(entry.getKey(), entry.getValue()));

            // Merge components
            JsonNode compNode = root.path("components");
            if (compNode.isObject()) {
                compNode.fields().forEachRemaining(entry -> {
                    ObjectNode target = (ObjectNode) components.path(entry.getKey());
                    ObjectNode source = (ObjectNode) entry.getValue();
                    if (target.isMissingNode()) {
                        components.set(entry.getKey(), source);
                    } else {
                        source.fields().forEachRemaining(e -> ((ObjectNode) target).set(e.getKey(), e.getValue()));
                    }
                });
            }
        }

        merged.put("openapi", "3.0.1");
        merged.set("info", mapper.createObjectNode()
                .put("title", "API Gateway Aggregated Docs")
                .put("version", "1.0.0"));
        merged.set("paths", paths);
        merged.set("components", components);

        return merged;
    }
}
