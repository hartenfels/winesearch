package semantics.example;

import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
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
  private final JsonSchema      ratingsSchema;

  public WineSearch() {
    port(3000);
    staticFiles.externalLocation("public");

    model     = new SemanticsModel();
    wikipedia = new WikipediaSource();

    try {
      ratingsSchema = JsonSchemaFactory.byDefault()
        .getJsonSchema("resource:/ratings.schema.json");
    }
    catch (ProcessingException e) {
      throw new RuntimeException(e);
    }

    ResponseTransformer rt = model.getResponseTransformer();
    get("/criteria",         this::criteria, rt);
    get("/wines",            this::wines,    rt);
    get("/wines/:wine",      this::wine,     rt);
    get("/regions/:region",  this::region,   rt);
    get("/wineries/:winery", this::winery,   rt);

    post("/wines/:wine", this::rateWine, rt);

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
    Object wine = model.wine(req.params(":wine"));

    if (wine == null) {
      res.status(404);
    }

    return wine;
  }

  private Object rateWine(Request req, Response res) {
    try {
      ProcessingReport report =
          ratingsSchema.validate(JsonLoader.fromString(req.body()));

      if (!report.isSuccess()) {
        throw new RuntimeException(report.toString());
      }
    }
    catch (Exception e) {
      res.status(400);
      return e;
    }

    JsonObject  obj    = new JsonParser().parse(req.body()).getAsJsonObject();
    JsonElement review = obj.get("review");
    model.rateWine(req.params(":wine"),
                   obj.get("author").getAsString(),
                   obj.get("rating").getAsInt(),
                   review == null ? null : review.getAsString().trim());

    return wine(req, res);
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
