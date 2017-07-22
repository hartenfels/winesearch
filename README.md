# NAME

wineseλrch — sample semantic data application for the [Software Languages Team](http://softlang.wikidot.com/) and [Institute for Web Science](https://west.uni-koblenz.de/lambda-dl) of the [University of Koblenz-Landau](https://www.uni-koblenz-landau.de/en/university-of-koblenz-landau)


# SYNOPSIS

For building, you'll need `make`, [Gradle](https://gradle.org/) and
[yarn](https://yarnpkg.com/en/).

1. Have [Semserv](https://github.com/hartenfels/Semserv).

2. Build [Semantics4J](https://github.com/hartenfels/Semantics4J) and copy or
   link the resulting `semantics.jar`.

3. Run `make` and let the application build and start the server.

4. Visit <http://localhost:3000/>.


# DESCRIPTION

This is a single-page web application. It uses
[Semantics4J](https://github.com/hartenfels/Semantics4J) and
[Spark](http://sparkjava.com/) on the [backend](backend) and
[Aurelia](http://aurelia.io/) on the [frontend](frontend).

The application provides the following features, each of them utilizing a
different kind of data source.

## Search

The primary feature of wineseλrch is searching wines of course. It uses the
[Wine Ontology](https://www.w3.org/TR/owl-guide/wine.rdf) as its data source
and [Semantics4J](https://github.com/hartenfels/Semantics4J) for its primary
implementation.

The user can specify a set of criteria to search by and receives a filtered set
of results. The query simply builds a union out of each specified category,
such as body or color, and then intersects all those unions.

By clicking on a wine in the result list, additional details can be viewed,
such as maker and region of origin.

## External Topic Information

For regions and wineries, additional information can be obtained by clicking on
its name in the detail view of a wine. The information will be shown in a modal
window.

The source for this is the REST API of [Wikipedia](https://en.wikipedia.org/).
The application performs a search for the given topic and shows a summary of
the best-matching result. While this is just a best-effort guess, if there
exists a Wikipedia article for the given topic, it will usually be chosen
correctly.

## Ratings and Reviews

Wines can be given ratings and reviews. Users can “log in” (really just enter a
name, actual logins are elided as this is just an example) and give each wine a
rating and review. Ratings are based on stars, with 1 star being the wost and 5
stars being the best. The average star rating, rounded to the nearest half, is
displayed in the detail view of each wine.

Ratings use a local [SQLite](https://www.sqlite.org/) relational database to
store and query ratings and reviews.


# DEVELOPMENT

This application is separated into independent [backend](backend) and
[frontend](frontend) parts. The backend is the web application server and the
frontend is the web browser client, which communicate via JSON over a REST
interface.

By default, running `make` will build the production version of the frontend
and set it up to be served by the backend server. The folder where the
JavaScript files are served from is timestamped for the sake of cache busting.

During development, it may be easier to run the two pieces separately, as the
frontend takes a relatively long time to build. For that, run `make` in the
[backend](backend) and [frontend](frontend) directories *separately* and visit
<http://localhost:9000/> instead. This page will automatically reload when
changes are made to the frontend. The backend server still needs to be
recompiled manually when something is modified, but it builds much more
quickly.


# LICENSE

[Apache License, Version 2](LICENSE)


# SEE ALSO

* [Semantics4J](https://github.com/hartenfels/Semantics4J)

* [Semserv](https://github.com/hartenfels/Semserv)

* [The Wine Ontology](https://www.w3.org/TR/owl-guide/wine.rdf)

* [λ-DL](https://west.uni-koblenz.de/lambda-dl)

* [Aurelia](http://aurelia.io/)
