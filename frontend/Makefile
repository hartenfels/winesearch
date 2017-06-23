YARN = yarn
BIN  = node_modules/.bin
AU   = $(BIN)/au

SRC  = $(shell find src -type f)
NOW := $(shell date +%s)


dev: aurelia_project/aurelia.json yarn.lock
	$(AU) run --watch
	rm -f prod

prod: aurelia_project/aurelia.json yarn.lock
	$(AU) build --env prod
	touch prod

yarn.lock: package.json
	$(YARN) install
	touch $@


aurelia_project/aurelia.json: aurelia_project/aurelia.tpl.json index.tpl.html $(SRC)
	$(MAKE) clean
	sed 's/{{OUTPUT}}/scripts$(NOW)/g' <$< >$@
	sed 's/{{OUTPUT}}/scripts$(NOW)/g' <index.tpl.html >index.html


clean:
	rm -rf scripts*


.PHONY: dev project clean
