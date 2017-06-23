run:
	$(MAKE) -C frontend prod
	$(MAKE) -C backend public run

clean:
	$(MAKE) -C backend  clean
	$(MAKE) -C frontend clean

realclean:
	$(MAKE) -C backend  realclean
	$(MAKE) -C frontend realclean


.PHONY: run clean realclean
