package ru.leti.wise.task.graph.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.leti.wise.task.graph.model.Graph;

import java.io.IOException;


public class JsonParser {

    /** Экспорт с сайта WiseTask содержит поля вне модели {@link Graph}; не считаем их ошибкой. */
    private static final ObjectMapper objectMapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static Graph parseGraph(String graph) {
        try {
            return objectMapper.readValue(graph, Graph.class);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось разобрать JSON графа: " + e.getMessage(), e);
        }

    }
}
