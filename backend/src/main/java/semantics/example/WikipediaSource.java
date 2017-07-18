package semantics.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.Unirest;


public class WikipediaSource {
  private static final String API_URL = "https://en.wikipedia.org/w/api.php";


  private static String normalizeName(String name) {
    return name.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2").toLowerCase().trim();
  }

  private static String normalizeRegionName(String name) {
    return normalizeName(name).replaceAll("\\s*region$", "");
  }


  private static String findClosestPage(String search) throws UnirestException {
    String res = Unirest.get(API_URL)
      .queryString("srsearch", search)
      .queryString("srlimit",  1)
      .queryString("action",   "query")
      .queryString("list",     "search")
      .queryString("format",   "json")
      .asString()
      .getBody();

    System.out.println(res);

    return new JsonParser().parse(res)
      .getAsJsonObject()
      .getAsJsonObject("query")
      .getAsJsonArray("search")
      .get(0)
      .getAsJsonObject()
      .get("title")
      .getAsString();
  }

  private static JsonElement fetchPage(String title) throws UnirestException {
    String res = Unirest.get(API_URL)
      .queryString("titles",      title)
      .queryString("action",      "query")
      .queryString("format",      "json")
      .queryString("prop",        "extracts")
      .queryString("exintro",     true)
      .queryString("explaintext", true)
      .asString()
      .getBody();

    return new JsonParser().parse(res)
      .getAsJsonObject()
      .getAsJsonObject("query")
      .getAsJsonObject("pages")
      .entrySet()
      .iterator()
      .next()
      .getValue();
  }

  private static JsonElement fetch(String name) {
    try {
      return fetchPage(findClosestPage(name));
    }
    catch (UnirestException e) {
      throw new RuntimeException(e);
    }
  }


  public JsonElement getRegionInfo(String name) {
    return fetch(normalizeRegionName(name) + " wine");
  }

  public JsonElement getWineryInfo(String name) {
    return fetch(normalizeName(name) + " wine producer");
  }
}
