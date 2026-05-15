import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.util.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Юнит-тесты плагина {@link ForestGraphChecker} по схеме WiseTask:
 * JSON из {@code src/test/resources} (экспорт графа с сайта / учебные примеры).
 */
class ForestGraphCheckerTest {

    private Graph loadGraph(String fileName) {
        ClassLoader cl = getClass().getClassLoader();
        try (InputStream is = cl.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new IllegalArgumentException("Ресурс не найден: " + fileName);
            }
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseGraph(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Печатает в stdout подпись теста и фактический результат {@code run(Graph)} (видно в ./gradlew test).
     */
    private void assertForestResult(String fileName, String graphDescription, boolean expected) {
        System.out.println();
        System.out.println("── Тест: " + fileName);
        System.out.println("   Граф: " + graphDescription);
        Graph graph = loadGraph(fileName);
        boolean actual = new ForestGraphChecker().run(graph);
        System.out.println("   run(Graph) = " + actual + " (ожидалось " + expected + ")");
        assertThat(actual).isEqualTo(expected);
        System.out.println("   ✓ утверждение выполнено");
    }

    @Test
    @DisplayName("forest_true.json — две компоненты (ребра 1–2 и 3–4), без цикла → лес (true)")
    void shouldReturnTrueForForestGraph() {
        assertForestResult(
                "forest_true.json",
                "Две изолированные пары вершин, лес из двух деревьев.",
                true);
    }

    @Test
    @DisplayName("forest_false_cycle.json — треугольник 1–2–3–1 → не лес (false)")
    void shouldReturnFalseForGraphWithCycle() {
        assertForestResult(
                "forest_false_cycle.json",
                "Простой цикл из трёх вершин.",
                false);
    }

    @Test
    @DisplayName("forest_empty.json — пустой граф → true по договорённости проверки")
    void shouldReturnTrueForEmptyGraph() {
        assertForestResult(
                "forest_empty.json",
                "Нет вершин и рёбер.",
                true);
    }

    @Test
    @DisplayName("site_export_with_cycle.json — выгрузка с WiseTask, цикл → false")
    void shouldReturnFalseForSiteExportedGraphWithCycle() {
        assertForestResult(
                "site_export_with_cycle.json",
                "JSON как с сайта («скачать json»), в графе есть цикл.",
                false);
    }

    @Test
    @DisplayName("forest_single_vertex.json — одна вершина без рёбер → дерево (true)")
    void shouldReturnTrueForSingleVertex() {
        assertForestResult(
                "forest_single_vertex.json",
                "Одиночная вершина id=1, edgeCount=0.",
                true);
    }

    @Test
    @DisplayName("forest_true_chain.json — цепочка 1–2–3–4, одно дерево → лес (true)")
    void shouldReturnTrueForChainTree() {
        assertForestResult(
                "forest_true_chain.json",
                "Путь из четырёх вершин, три ребра, без циклов.",
                true);
    }

    @Test
    @DisplayName("forest_false_selfloop.json — петля (1→1) → не лес (false)")
    void shouldReturnFalseForSelfLoop() {
        assertForestResult(
                "forest_false_selfloop.json",
                "Одна вершина с ребром source=target=1.",
                false);
    }
}
