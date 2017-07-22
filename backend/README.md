# NAME

backend – the web application server for wineseλrch


# SYNOPSIS

Run `make` to build and run the server. You'll need to have
[Semserv](https://github.com/hartenfels/Semserv) running for this to work.


# DESCRIPTION

The application is structured as follows:

* [src/main/java/semantics/example](src/main/java/semantics/example) contains
  the Java source code.

  * [WineSearch.java](src/main/java/semantics/example/WineSearch.java) is the
    main application file, defining the routes and controller methods.

  * [Model.java](src/main/java/semantics/example/Model.java) defines the
    interface for the data model.

  * [SemanticsModel.java](src/main/java/semantics/example/SemanticsModel.java)
    implements said interface with
    [Semantics4J](https://github.com/hartenfels/Semantics4J).

  * [WikipediaSource.java](src/main/java/semantics/example/WikipediaSource.java)
    is the interface to the [Wikipedia](https://en.wikipedia.org/) REST API.

  * [Database.java](src/main/java/semantics/example/Database.java) is the
    interface to the [SQLite](https://www.sqlite.org/) database for ratings and
    reviews.

* [src/main/resources/ratings.schema.json](src/main/resources/ratings.schema.json)
  contains the [JSON Schema](http://json-schema.org/) for posting ratings and
  reviews. It is used to validate these POST requests.


# LICENSE

Copyright 2017 Carsten Hartenfels.

Licensed under the [Apache License, Version 2](../LICENSE).


# SEE ALSO

* [The wineseλrch README](../README.md)
