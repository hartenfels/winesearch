GRADLE ?= gradle
JAVA   ?= java


run: WineSearch.jar
	$(JAVA) -jar $< || echo

WineSearch.jar: src/main/java/semantics/example/*.java semantics.jar build.gradle
	$(GRADLE) jar
	touch $@


public: ../frontend/prod
	rm -rf $@
	mkdir $@
	cp -r ../frontend/index.html ../frontend/scripts* $@


semantics.jar:
	@echo
	@echo -e "\e[31m*** Error: semantics.jar not found! ***\e[0m"
	@echo
	@echo 'You need to build it and copy or link it into this directory:'
	@echo "$$(pwd)"
	@echo
	@echo 'See the README.md for details on how to do that.'
	@echo


clean:
	$(GRADLE) clean

realclean: clean
	rm -rf public WineSearch.jar


.PHONY: run clean realclean
