package semantics.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import semantics.model.Conceptual;
import semantics.model.Individual;
import spark.ResponseTransformer;
import static semantics.Util.concept;
import static semantics.Util.head;
import static semantics.Util.names;
import static semantics.Util.sorted;


public class LambdaModel implements Model knows "wine.rdf" {
  private Gson gson;

  public LambdaModel() {
    JsonSerializer<Individual> serializer = (src, type, context) -> {
      return new JsonPrimitive(src.getName());
    };

    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(Individual.class, serializer)
        .create();
  }


  public ResponseTransformer getResponseTransformer() {
    return gson::toJson;
  }

  private static String prefix(String s) {
    return ":" + s;
  }


  private static List<String> getCriterion(Conceptual concept) {
    return sorted(names(query-for(concept)));
  }

  private static Set<«:Region»> getTopLevelRegions() {
    Set<«:Region»> regions = query-for(":Region");
    regions.removeAll(query-for(∃":locatedIn"·":Region"));
    return regions;
  }

  public Map<String, List<String>> criteria() {
    Map<String, List<String>> criteria = new HashMap<>();

    criteria.put("body",   getCriterion(concept(":WineBody"  )));
    criteria.put("color",  getCriterion(concept(":WineColor" )));
    criteria.put("flavor", getCriterion(concept(":WineFlavor")));
    criteria.put("sugar" , getCriterion(concept(":WineSugar" )));
    criteria.put("maker" , getCriterion(concept(":Winery"    )));
    criteria.put("region", sorted(names(getTopLevelRegions())));

    return criteria;
  }


  public List<«:Wine»> wines(List<WineSearch.Arg> searchArgs) {
    Conceptual dl = ⊤;

    for (WineSearch.Arg arg : searchArgs) {
      String     role  = prefix(arg.role);
      String     first = prefix(arg.values[0]);
      Conceptual union = ∃role·⎨first⎬;

      for (int i = 1; i < arg.values.length; ++i) {
        String part = prefix(arg.values[i]);
        union ⊔= ∃role·⎨part⎬;
      }

      dl ⊓= union;
    }

    return sorted(query-for(":Wine" ⊓ dl));
  }


  public Map<String, ∃⊤·«:Wine»> wine(String name) {
    «:Wine» wine = head(query-for(":Wine" ⊓ ⎨prefix(name)⎬));

    if (wine == null) {
      return null;
    }

    Map<String, ∃⊤·«:Wine»> wineInfo = new HashMap<>();

    wineInfo.put("body",   head(wine.(":hasBody"  )));
    wineInfo.put("color",  head(wine.(":hasColor" )));
    wineInfo.put("flavor", head(wine.(":hasFlavor")));
    wineInfo.put("maker",  head(wine.(":hasMaker" )));
    wineInfo.put("region", head(wine.(":locatedIn")));
    wineInfo.put("sugar",  head(wine.(":hasSugar" )));

    return wineInfo;
  }
}
