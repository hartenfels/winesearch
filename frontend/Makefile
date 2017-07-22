YARN = yarn
BIN  = node_modules/.bin
AU   = $(BIN)/au

SRC  = $(shell find src -type f)
NOW := $(shell date +%s)


dev: aurelia_project/aurelia.json yarn.lock node_modules
	$(AU) run --watch
	rm -f prod

prod: aurelia_project/aurelia.json yarn.lock node_modules
	$(AU) build --env prod
	touch prod

yarn.lock node_modules: package.json
	$(YARN) install
	touch $@


aurelia_project/aurelia.json: aurelia_project/aurelia.tpl.json index.tpl.html $(SRC)
	$(MAKE) clean
	sed 's/{{OUTPUT}}/scripts$(NOW)/g' <$< >$@
	sed 's/{{OUTPUT}}/scripts$(NOW)/g' <index.tpl.html >index.html


clean:
	rm -rf scripts*

realclean: clean


.PHONY: dev project clean realclean
