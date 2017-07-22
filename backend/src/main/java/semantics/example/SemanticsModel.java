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


public class SemanticsModel implements Model knows "wine.rdf" {
  private Gson     gson;
  private Database db;

  public SemanticsModel() {
    JsonSerializer<Individual> serializer = (src, type, context) -> {
      return new JsonPrimitive(src.getName());
    };

    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(Individual.class, serializer)
        .create();

    db = new Database();
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
      Conceptual union = ⎨prefix(arg.values[0])⎬;

      for (int i = 1; i < arg.values.length; ++i) {
        union ⊔= ⎨prefix(arg.values[i])⎬;
      }

      dl ⊓= ∃prefix(arg.role)·union;
    }

    return sorted(query-for(":Wine" ⊓ dl));
  }


  private «:Wine» findWine(String name) {
    return head(query-for(":Wine" ⊓ ⎨prefix(name)⎬));
  }

  public Map<String, Object> wine(String name) {
    «:Wine» wine = findWine(name);

    if (wine == null) {
      return null;
    }

    Map<String, Object> wineInfo = new HashMap<>();

    wineInfo.put("body",   head(sorted(wine.(":hasBody"  ))));
    wineInfo.put("color",  head(sorted(wine.(":hasColor" ))));
    wineInfo.put("flavor", head(sorted(wine.(":hasFlavor"))));
    wineInfo.put("maker",  head(sorted(wine.(":hasMaker" ))));
    wineInfo.put("region", head(sorted(wine.(":locatedIn"))));
    wineInfo.put("sugar",  head(sorted(wine.(":hasSugar" ))));

    wineInfo.put("ratings", db.getRatingsFor(wine));

    return wineInfo;
  }

  public void rateWine(String name, String author, int rating, String review) {
    db.rate(findWine(name), author, rating, review);
  }


  public «:Region» region(String name) {
    return head(query-for(":Region" ⊓ ⎨prefix(name)⎬));
  }

  public «:Winery» winery(String name) {
    return head(query-for(":Winery" ⊓ ⎨prefix(name)⎬));
  }
}
