package semantics.example;

import java.util.List;
import java.util.Map;
import spark.ResponseTransformer;


public interface Model {
  public ResponseTransformer getResponseTransformer();

  public Map<String, ? extends List<?>> criteria();

  public List<?> wines(List<WineSearch.Arg> searchArgs);

  public Map<String, ?> wine(String name);
}
