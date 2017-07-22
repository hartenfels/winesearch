# NAME

frontend – the web browser frontend for wineseλrch


# SYNOPSIS

Run `make` to run the frontend in continuous build and visit
<http://localhost:9000/> in your browser. The site will automatically reload
whenever you modify the code.

This project uses [yarn](https://yarnpkg.com/en/) for dependency management,
but you may use raw npm as well. For that, run `npm install` manually and then
`touch yarn.lock` so that make doesn't try to run it again.


# DESCRIPTION

The [src](src) directory contains everything interesting about the application:
HTML templates, JavaScript view models, a style sheet and some value
converters.

The project uses ES6 via [Babel](https://babeljs.io/), see the
[.babelrc](.babelrc) file for its exact configuration and plugins.

Browsers often have issues of caching the generated Aurelia JavaScript files
too much, so that they end up not reloading them when the source code changes.
Because of this, each time the application is built, it does a little dance to
timestamp the scripts folder for cache busting. This is what the template files
[index.tpl.html](index.tpl.html) and
[aurelia.tpl.json](aurelia_project/aurelia.tpl.json) are for.


# LICENSE

Copyright 2017 Carsten Hartenfels.

Licensed under the [Apache License, Version 2](../LICENSE).


# SEE ALSO

* [The wineseλrch README](../README.md)
