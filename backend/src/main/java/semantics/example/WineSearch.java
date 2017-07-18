package semantics.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import static spark.Spark.*;


public class WineSearch {
  public static class Arg {
    public final String   key;
    public final String   role;
    public final String[] values;

    public Arg(String key, String role, String[] values) {
      this.key    = key;
      this.role   = role;
      this.values = values;
    }
  }

  private static final String[][] SEARCH_KEYS = {
    {"body",   "hasBody"  },
    {"color",  "hasColor" },
    {"flavor", "hasFlavor"},
    {"maker",  "hasMaker" },
    {"region", "locatedIn"},
    {"sugar",  "hasSugar" },
  };


  private final Model           model;
  private final WikipediaSource wikipedia;

  public WineSearch() {
    port(3000);
    staticFiles.externalLocation("public");

    model     = new SparqlModel();
    wikipedia = new WikipediaSource();

    ResponseTransformer rt = model.getResponseTransformer();
    get("/criteria",         this::criteria, rt);
    get("/wines",            this::wines,    rt);
    get("/wines/:wine",      this::wine,     rt);
    get("/regions/:region",  this::region,   rt);
    get("/wineries/:winery", this::winery,   rt);

    after((req, res) -> {
      res.type("application/json");
      res.header("Access-Control-Allow-Origin", "*");
      res.header("Access-Control-Allow-Methods", "GET, OPTIONS, POST");
    });
  }


  private Object criteria(Request req, Response res) {
    return model.criteria();
  }


  private Object wines(Request req, Response res) {
    List<Arg> searchArgs = new ArrayList<>();

    for (String[] pair : SEARCH_KEYS) {
      String key   = pair[0];
      String param = req.queryParams(key);

      if (param != null) {
        searchArgs.add(new Arg(key, pair[1], param.split(",")));
      }
    }

    return model.wines(searchArgs);
  }


  private Object wine(Request req, Response res) {
    try {
      Object wine = model.wine(req.params(":wine"));

      if (wine == null) {
        res.status(404);
      }

      return wine;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }


  private Object region(Request req, Response res) {
    String name = req.params(":region");

    if (model.region(name) == null) {
      res.status(404);
      return null;
    }

    return wikipedia.getRegionInfo(name);
  }

  private Object winery(Request req, Response res) {
    String name = req.params(":winery");

    if (model.winery(name) == null) {
      res.status(404);
      return null;
    }

    return wikipedia.getWineryInfo(name);
  }


  public static void main(String[] args) {
    new WineSearch();
  }
}
