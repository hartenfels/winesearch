package semantics.example;

import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import spark.ResponseTransformer;


public class SparqlModel implements Model {
  private static final String VIN =
    "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";

  private static final String PREFIXES =
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
    "PREFIX    : <" + VIN + ">\n";


  private Connection conn;
  private Gson       gson;

  public SparqlModel() {
    conn = ConnectionConfiguration
      .to("wine")
      .server("snarl://localhost")
      .credentials("admin", "admin")
      .reasoning(true)
      .connect();

    JsonSerializer<IRI> serializer = (src, type, context) -> {
      return new JsonPrimitive(src.getLocalName());
    };

    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(IRI.class, serializer)
        .create();
  }


  public ResponseTransformer getResponseTransformer() {
    return gson::toJson;
  }


  private SelectQuery query(String... lines) {
    return conn
      .select(PREFIXES + String.join("\n", lines))
      .reasoning(false);
  }

  private SelectQuery reason(String... lines) {
    return conn
      .select(PREFIXES + String.join("\n", lines))
      .reasoning(true)
      .limit(20);
  }

  private Stream<Map<String, Value>> execute(SelectQuery sq) {
    Stream.Builder<Map<String, Value>> bindings = Stream.builder();

    int offset =  0;
    int found  = 20;

    while (found >= 20) {
      sq.offset(offset);
      offset += 20;
      found   = 0;

      try (TupleQueryResult rs = sq.execute()) {
        while (rs.hasNext()) {
          Map<String, Value> map = new HashMap<>();

          for (Binding b : rs.next()) {
            map.put(b.getName(), b.getValue());
          }

          bindings.accept(map);
          ++found;
        }
      }
    }

    return bindings.build();
  }


  private static int compareValues(Value a, Value b) {
    return a.stringValue().compareTo(b.stringValue());
  }


  private List<Value> getCriterion(String concept) {
    SelectQuery sq = query(
      "SELECT ?criterion WHERE {",
      "  ?criterion rdf:type ?concept .",
      "}"
    );

    sq.parameter("concept", Values.iri(VIN, concept));

    return execute(sq)
      .map(m -> m.get("criterion"))
      .sorted(SparqlModel::compareValues)
      .distinct()
      .collect(Collectors.toList());
  }

  public Map<String, List<Value>> criteria() {
    Map<String, List<Value>> criteria = new HashMap<>();

    criteria.put("body",   getCriterion("WineBody"  ));
    criteria.put("color",  getCriterion("WineColor" ));
    criteria.put("flavor", getCriterion("WineFlavor"));
    criteria.put("sugar" , getCriterion("WineSugar" ));
    criteria.put("maker" , getCriterion("Winery"    ));

    return criteria;
  }


  public List<Value> wines(List<WineSearch.Arg> searchArgs) {
    List<String> lines  = new ArrayList<>();
    lines.add("SELECT ?wine WHERE {");
    lines.add("  ?wine rdf:type :Wine .");

    List<String> params = new ArrayList<>();

    for (WineSearch.Arg arg : searchArgs) {
      String union = Arrays
        .stream(arg.values)
        .map(v -> {
          int index = params.size();
          params.add(arg.role);
          params.add(v);
          return String.format("?wine ?param%d ?param%d", index, index + 1);
        })
        .collect(Collectors.joining(" UNION "));

      lines.add(String.format("  { %s } .", union));
    }

    lines.add("}");

    SelectQuery sq = reason(String.join("\n", lines));

    for (int i = 0; i < params.size(); ++i) {
      sq.parameter("param" + i, Values.iri(VIN, params.get(i)));
    }

    return execute(sq)
      .map(m -> m.get("wine"))
      .sorted(SparqlModel::compareValues)
      .distinct()
      .collect(Collectors.toList());
  }


  private Value project(String subject, String predicate) {
    SelectQuery sq = reason("SELECT ?o WHERE { ?s ?p ?o }").limit(1);
    sq.parameter("s", Values.iri(VIN, subject));
    sq.parameter("p", Values.iri(VIN, predicate));

    Map<String, Value> first = execute(sq).findAny().orElse(null);
    return first == null ? null : first.get("o");
  }

  public Map<String, Value> wine(String name) {
    Map<String, Value> wineInfo = new HashMap<>();

    wineInfo.put("body",   project(name, "hasBody"  ));
    wineInfo.put("color",  project(name, "hasColor" ));
    wineInfo.put("flavor", project(name, "hasFlavor"));
    wineInfo.put("maker",  project(name, "hasMaker" ));
    wineInfo.put("region", project(name, "locatedIn"));
    wineInfo.put("sugar",  project(name, "hasSugar" ));

    return wineInfo;
  }


  private Value getSingleOfType(String subject, String object) {
    SelectQuery sq = reason("SELECT ?s WHERE { ?s rdf:type ?o }");
    sq.parameter("s", Values.iri(VIN, subject));
    sq.parameter("o", Values.iri(VIN, object));
    return single(sq, "s");
  }

  public Value region(String name) {
    return getSingleOfType(name, "Region");
  }

  public Value winery(String name) {
    return getSingleOfType(name, "Winery");
  }
}
