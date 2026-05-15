import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/// Проверяет граф на признаки леса.
public class ForestGraphChecker implements GraphProperty
{

    @Override
    public boolean run(Graph graph)
    {
        List<Vertex> vertices = graph.getVertexList();
        if (vertices.isEmpty())
            return true;
        // Строим список смежности по id
        Map<Integer, List<Integer>> adjacency = new HashMap<>();
        for (Vertex v : vertices)
            adjacency.put(v.getId(), new ArrayList<>());
        for (Edge edge : graph.getEdgeList())
        {
            int source = edge.getSource();
            int target = edge.getTarget();
            adjacency.get(source).add(target);
            adjacency.get(target).add(source);
        }
        Set<Integer> visited = new HashSet<>();
        for (Vertex vertex : vertices)
        {
            int id = vertex.getId();
            // Лес - это совокупность деревьев (т.е. может быть несколько компонент связности). Поэтому нужно
            // проверить все вершины на ацикличность, если они не посещены.
            if (!visited.contains(id))
            {
                if (HasCycle(id, -1, adjacency, visited))
                    return false;
            }
        }
        return true;
    }

    /// Проверяет дерево на цикл, начиная с вершины current. По сути реализует обычный обход в глубину с
    /// дополнительной проверкой.
    private boolean HasCycle(int current, int parent,
                             Map<Integer, List<Integer>> adjacency,
                             Set<Integer> visited)
    {
        visited.add(current);
        for (int neighbor : adjacency.get(current))
        {
            if (!visited.contains(neighbor))
            {
                if (HasCycle(neighbor, current, adjacency, visited))
                    return true;
            }
            // Если вершина уже посещена, но по списку смежности это не родитель, значит, цикл найден.
            else if (neighbor != parent)
                return true;
        }
        return false;
    }
}
