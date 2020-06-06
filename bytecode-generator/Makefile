filename = bytecode.j
jasminout = Main.class
generated = lex.yy.c y.tab.c y.tab.h codegenerator $(filename) $(jasminout)
all: 
	flex tokens.l
	bison -dy parser.y
	g++ -std=c++11 lex.yy.c y.tab.c -o codegenerator
clean:
	rm -f $(generated)
test: clean all
	./codegenerator
run:clean all
	./codegenerator
	java -jar jasmin.jar $(filename)
	java $(filename)

