/* DISCLAIMER: Most of bytecode directives follows jasmin2.4 guide */

%{
    enum VARTYPE{TYNONE=0, TYINTEGER, TYFLOAT, TYBOOLEAN};
    enum INSTTYPE{INST_NONE=0, INST_LABEL, INST_NORMAL, INST_GOTO, INST_FUNC, INST_JASMIN}; //need the label type only to concat instructions to labels
    #include <iostream>
    #include <unordered_map>
    #include <vector>
    #include <fstream>
    #include <cstring>
    #include <unordered_set>
    #define INPUT_FILE_NAME "input.txt"
    #define OUTPUT_FILE_NAME "bytecode.j"
    #define OUTPUT_CLASS_NAME "Main"
    #define LIMIT_LOCALS ".limit locals 128"
    #define LIMIT_STACK ".limit stack 128"
    #define SYSO_INT_VARID (1)  //starts from 1 in java
    #define SYSO_FLOAT_VARID (2)
    #define VARID_START (3)
    #define LABEL_OPTIONAL_PREFIX std::string("LABEL")
    #define LABEL(n) (LABEL_OPTIONAL_PREFIX + std::to_string(n))
    // stuff from flex that bison needs to know about:
    extern int yylex();
    extern int yyparse();
    void yyerror(const char *);
    extern FILE *yyin;
    extern int32_t lineNum;
    int32_t varID = VARID_START; 
    int32_t labelCounter = 0;	
    struct instruction{
        std::string code;
        INSTTYPE type;
        instruction(const std::string& instCode,INSTTYPE instType):code(instCode.c_str()),type(instType){
        }
    };
    std::vector<instruction> instructions; // bytecode instructions
    std::string outputfileName; // file name of the output fie
    std::unordered_map<std::string, std::string> opInstructions =
    {
    /* arithmetic operations */
    {"+", "add"},
    {"-", "sub"},
    {"*", "mul"},
    {"/", "div"},
    {"%", "rem"},

    //Bitwise operation
    {"|", "or"},
    {"&", "and"},
    
	/* relational operations */
    {"==",  "\tif_icmpeq"},
    {"<=",  "\tif_icmple"},
    {">=",  "\tif_icmpge"},
    {"!=",  "\tif_icmpne"},
    {">",   "\tif_icmpgt"},
    {"<",   "\tif_icmplt"}
    };
    std::unordered_set<int32_t> prefixedLabels;
    std::unordered_map<std::string, std::pair<int32_t,VARTYPE> > symbolTable;
    std::vector<std::string> temporaryVarNames; //used with multiple declare to temporary hold var names until flush
    
    /* functions for parser processing */
    void addHeader();
    void addFooter();
    void addInstruction(const instruction&);
    std::string getOp(const std::string&);
    void addVariable(const std::string&, VARTYPE);
    void addVarName(const std::string&);
    void flushVarNames(VARTYPE);
    bool checkVariableExists(const std::string&);
    std::string generateLabel();
    /* for the back patching algorithm explained in the dragon book */
    void backpatch(std::vector<int32_t> *, int32_t); 
    std::vector<int32_t> *mergeLists(std::vector<int32_t> *, std::vector<int32_t>*);
    void typeCast(std::string, int32_t);//TODO
    void operationCast(const std::string&, int32_t, int32_t);//TODO
    void outBytecode();
    std::string getLabelString(int32_t n);
%}

//includes needed in union
%code requires {
	#include <vector>
    #include <string>
}
%start method_body
%union {
    /* using different int32_ts for readability */
    int32_t                      intValue;
    float                        floatValue;
    char*                        stringValue;
    bool                         booleanValue;
    char*                        varName;
    char*                        operationValue;
    struct {
        std::vector<int32_t>     *trueList, *falseList;
    } boolExpression;
    struct{
        std::vector<int32_t>     *nextList;
    } stmt;
    int32_t                  primType;
    int32_t                  expressionType;
}
/*
 * By convention, Every non terminal is lower case, and every terminal is upper case
 */
%token INTEGER_DECL FLOAT_DECL BOOLEAN_DECL 
%token IF_TOK ELSE_TOK WHILE_TOK FOR_TOK
%token LEFT_BRACKET RIGHT_BRACKET RIGHT_CURLY_BRACKET  LEFT_CURLY_BRACKET
%token SEMI_COLON
%token COMMA_TOK
%token ASSIGNMENT_OPERATOR
%token PRINTLN_TOK

%token <intValue>           INTEGER_NUMBER
%token <floatValue>         FLOAT_NUMBER
%token <stringValue>        STRING
%token <booleanValue>       BOOLEAN
%token <operationValue>     RELOP BOOLOP OPERATION
%token <varName>            IDENTIFIER

%type  <intValue>           location_marker
%type  <intValue>           goto_operation
%type  <primType>           primitive_type
%type  <expressionType>     expression
%type  <boolExpression>     boolean_expression
%type  <stmt>               statement
%type  <stmt>               statement_list
%type  <stmt>               if
%type  <stmt>               while
%type  <stmt>               for



%%
/* Most of the actions are defined as explained in Lectures and Backpatching algorithm in dragon book */
/* backpatching : https://www.isi.edu/~pedro/Teaching/CSCI565-Spring14/Materials/Backpatching.pdf */
/* Marker location as explained in the dragon book with backpatching algorithm */
location_marker:
    /* empty */
    {$$ = labelCounter;addInstruction({generateLabel() + ":", INSTTYPE::INST_LABEL});}
    ;

/* goto to be used with control structures */
goto_operation:
    /* empty */
    {$$ = instructions.size();addInstruction({"\tgoto ",INSTTYPE::INST_GOTO});} /* goto will be resolved later by backpatching */
    ;
method_body:
    {addHeader();} /* it's the start symbol so we will add the bytecode header first */
    statement_list location_marker
    {backpatch($2.nextList, $3);addFooter();} /* code will end here so add the footer */
    ;

statement_list: 
    statement 
    |
    statement location_marker statement_list
    {backpatch($1.nextList, $2);$$.nextList = $3.nextList;}
    ;

statement:
    declaration
    {std::vector<int32_t> * newList = new std::vector<int32_t>(); $$.nextList = newList;}
    | 
    assignment
    {std::vector<int32_t> * newList = new std::vector<int32_t>(); $$.nextList = newList;}
    |
    print_func 
    {std::vector<int32_t> * newList = new std::vector<int32_t>(); $$.nextList = newList;}
    | 
    if
    {$$.nextList = $1.nextList;}
    | 
    while
    {$$.nextList = $1.nextList;}
    | 
    for
    {$$.nextList = $1.nextList;}
    ;

declaration:
    primitive_type IDENTIFIER declaration_extended SEMI_COLON
     {
        std::string varName($2);
        addVariable(varName, (VARTYPE)$1);
        flushVarNames((VARTYPE)$1);
     }
     ;
declaration_extended:
     COMMA_TOK IDENTIFIER declaration_extended
     {
         std::string varName($2);
         addVarName(varName);
     }
     |
    /* nothing */
    ;

primitive_type:
    INTEGER_DECL
    {$$ = VARTYPE::TYINTEGER;}
    |
    FLOAT_DECL
    {$$ = VARTYPE::TYFLOAT;}
    |
    BOOLEAN_DECL
    {$$ = VARTYPE::TYBOOLEAN;}
    ;
print_func:
    PRINTLN_TOK LEFT_BRACKET STRING RIGHT_BRACKET SEMI_COLON
    {
        /* push System.out onto the stack */
        addInstruction({"\tgetstatic java/lang/System/out Ljava/io/PrintStream;",INSTTYPE::INST_NORMAL});
        /* push a string onto the stack */
        addInstruction({"\tldc " + std::string($3), INSTTYPE::INST_NORMAL});
        /* call the PrintStream.println() method. */
        addInstruction({"\tinvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V",INSTTYPE::INST_NORMAL});
    }
    |
 	PRINTLN_TOK LEFT_BRACKET expression RIGHT_BRACKET SEMI_COLON
 	{
        // Expression is on top of stack
        // so just push System.out onto the stack and invoke but we need to store expression first in a temp var so we can load it again before calling
       
        if($3 == VARTYPE::TYINTEGER)
 		{
            std::string tempVarName = std::to_string(SYSO_INT_VARID);
            addInstruction({"\tistore " + tempVarName, INSTTYPE::INST_NORMAL});
            addInstruction({"\tgetstatic java/lang/System/out Ljava/io/PrintStream;", INSTTYPE::INST_NORMAL});
            addInstruction({"\tiload " + tempVarName, INSTTYPE::INST_NORMAL});
            addInstruction({"\tinvokevirtual java/io/PrintStream/println(I)V", INSTTYPE::INST_NORMAL});
        }
        else if($3 == VARTYPE::TYFLOAT)
        {
            std::string tempVarName = std::to_string(SYSO_FLOAT_VARID);
            addInstruction({"\tfstore " + tempVarName, INSTTYPE::INST_NORMAL});
            addInstruction({"\tgetstatic java/lang/System/out Ljava/io/PrintStream;", INSTTYPE::INST_NORMAL});
            addInstruction({"\tfload " + tempVarName, INSTTYPE::INST_NORMAL});
            addInstruction({"\tinvokevirtual java/io/PrintStream/println(F)V", INSTTYPE::INST_NORMAL});
        }
 	}
   
 	;

if:
    IF_TOK LEFT_BRACKET boolean_expression RIGHT_BRACKET LEFT_CURLY_BRACKET location_marker
    statement_list goto_operation
    RIGHT_CURLY_BRACKET
    ELSE_TOK LEFT_CURLY_BRACKET location_marker
    statement_list
    RIGHT_CURLY_BRACKET
    {
        /* fix the 2 goto for location markers by backpatching */
        backpatch($3.trueList, $6);
		backpatch($3.falseList, $12);
        /* fix the next lists for this if */
		$$.nextList = mergeLists($7.nextList, $13.nextList);
		$$.nextList->push_back($8);
    }
    ;

while:
    WHILE_TOK location_marker LEFT_BRACKET boolean_expression RIGHT_BRACKET location_marker LEFT_CURLY_BRACKET 
    statement_list
    RIGHT_CURLY_BRACKET
    {
        backpatch($8.nextList,$2);
        backpatch($4.trueList,$6);
        $$.nextList = $4.falseList;
        addInstruction({"\tgoto " + getLabelString($2), INSTTYPE::INST_GOTO});
    }
    ;

for://TODO
    FOR_TOK LEFT_BRACKET for_assignment SEMI_COLON location_marker boolean_expression SEMI_COLON
    location_marker for_assignment goto_operation
    RIGHT_BRACKET LEFT_CURLY_BRACKET location_marker
	statement_list goto_operation
	RIGHT_CURLY_BRACKET
    {
        backpatch($6.trueList, $13);
		std::vector<int32_t> * newList = new std::vector<int32_t>();
		newList->push_back($10);
		backpatch(newList,$5);
		newList = new std::vector<int32_t>();
		newList->push_back($15);
		backpatch(newList,$8);
		backpatch($14.nextList,$8);
		$$.nextList = $6.falseList;
    }
    ;
for_assignment:
    IDENTIFIER ASSIGNMENT_OPERATOR expression
    {
        /* expression result on top of the stack */
        std::string varName($1);
		if(checkVariableExists(varName))
		{
             std::string varID = std::to_string(symbolTable[varName].first);
			if($3 == symbolTable[varName].second)
			{
                 std::string varID = std::to_string(symbolTable[varName].first);
				if($3 == VARTYPE::TYINTEGER)
				{
					addInstruction({"\tistore " + varID, INSTTYPE::INST_NORMAL});
				}else if ($3 == VARTYPE::TYFLOAT)
				{
					addInstruction({"\tfstore " + varID, INSTTYPE::INST_NORMAL});
				}
			}
			else
			{
				typeCast(varName, $3);
			}
		}
    }
    ;
assignment:
    IDENTIFIER ASSIGNMENT_OPERATOR expression SEMI_COLON
    {
        /* expression result on top of the stack */
        std::string varName($1);
		if(checkVariableExists(varName))
		{
             std::string varID = std::to_string(symbolTable[varName].first);
			if($3 == symbolTable[varName].second)
			{
                 std::string varID = std::to_string(symbolTable[varName].first);
				if($3 == VARTYPE::TYINTEGER)
				{
					addInstruction({"\tistore " + varID, INSTTYPE::INST_NORMAL});
				}else if ($3 == VARTYPE::TYFLOAT)
				{
					addInstruction({"\tfstore " + varID, INSTTYPE::INST_NORMAL});
				}
			}
			else
			{
				typeCast(varName, $3);
			}
		}
    }
    ;

expression:
    INTEGER_NUMBER
    {$$ = VARTYPE::TYINTEGER;  addInstruction({"\tldc "+ std::to_string($1), INSTTYPE::INST_NORMAL});} 
    |
    FLOAT_NUMBER
    {$$ = VARTYPE::TYFLOAT;  addInstruction({"\tldc "+ std::to_string($1), INSTTYPE::INST_NORMAL});} 
    |
    expression OPERATION expression
    {operationCast(std::string($2), $1, $3);}
    |
    IDENTIFIER
    {
        /* make sure the id exists first then load it */
		std::string varName($1);
		if(checkVariableExists(varName))
		{
			$$ = symbolTable[varName].second;
            std::string varID = std::to_string(symbolTable[varName].first);
			if(symbolTable[varName].second == VARTYPE::TYINTEGER)
			{
				addInstruction({"\tiload " + varID, INSTTYPE::INST_NORMAL});
			}else if (symbolTable[varName].second == VARTYPE::TYFLOAT)
			{
				addInstruction({"\tfload " + varID, INSTTYPE::INST_NORMAL});
			}
		}
		else
		{
			$$ = VARTYPE::TYNONE;
		}
	}
    |
    LEFT_BRACKET expression RIGHT_BRACKET
    {$$ = $2;}
    ;

boolean_expression:
	BOOLEAN
    {
        $$.trueList = new std::vector<int32_t>();
        $$.falseList = new std::vector<int32_t>();
        if($1 == true) $$.trueList->push_back(instructions.size());
		else $$.falseList->push_back(instructions.size());
        addInstruction({"\tgoto ", INSTTYPE::INST_GOTO});
    }
    |
    expression RELOP expression
    {
		std::string operation($2);
		$$.trueList = new std::vector<int32_t>();
		$$.trueList->push_back(instructions.size());
		$$.falseList = new std::vector<int32_t>();
		$$.falseList->push_back(instructions.size() + 1);
		addInstruction({getOp(operation)+ " ", INSTTYPE::INST_NORMAL});
		addInstruction({"\tgoto ", INSTTYPE::INST_GOTO});
	}
	|
    boolean_expression BOOLOP location_marker boolean_expression
    {
        if(strcmp($2, "&&") == 0)
		{
			backpatch($1.trueList, $3);
			$$.trueList = $4.trueList;
			$$.falseList = mergeLists($1.falseList,$4.falseList);
		}
		else if(strcmp($2, "||") == 0)
		{
			backpatch($1.falseList,$3);
			$$.trueList = mergeLists($1.trueList, $4.trueList);
			$$.falseList = $4.falseList;
		}
    }
	;
%%




main (int argv, char * argc[])
{
	FILE *fileDesc;
	if(argv == 1) 
	{
		fileDesc = fopen(INPUT_FILE_NAME, "r");
		outputfileName = OUTPUT_FILE_NAME;
	}
	else 
	{
		fileDesc = fopen(argc[1], "r");
		outputfileName = std::string(argc[1]);
	}
	if (fileDesc == NULL) {
		std::cout << "Error opening the file" << std::endl;
		return -1;
	}
	yyin = fileDesc;
	yyparse();
	outBytecode();
}

void yyerror(const char * errString)
{
	printf("Error at Line %d: %s\n", lineNum, errString);
}
/*------------------------------------------------------------------------
 * addHeader  - adds the default header bytecode for any java compiled program
 *------------------------------------------------------------------------
 */
void addHeader(){
    addInstruction({".source " + std::string(OUTPUT_FILE_NAME), INSTTYPE::INST_JASMIN});
	addInstruction({".class public " + std::string(OUTPUT_CLASS_NAME), INSTTYPE::INST_JASMIN});
    addInstruction({".super  java/lang/Object", INSTTYPE::INST_JASMIN});
	addInstruction({".method public <init>()V", INSTTYPE::INST_JASMIN});
	addInstruction({"aload_0", INSTTYPE::INST_NORMAL});
	addInstruction({"invokenonvirtual java/lang/Object/<init>()V", INSTTYPE::INST_NORMAL});
	addInstruction({"return", INSTTYPE::INST_NORMAL});
	addInstruction({".end method", INSTTYPE::INST_NORMAL});
	addInstruction({".method public static main([Ljava/lang/String;)V", INSTTYPE::INST_NORMAL});
    addInstruction({LIMIT_LOCALS, INSTTYPE::INST_NORMAL});
    addInstruction({LIMIT_STACK, INSTTYPE::INST_NORMAL});
}
/*------------------------------------------------------------------------
 * addFooter  -   adds the default footer bytecode for any java compiled program
 *------------------------------------------------------------------------
 */
void addFooter(){

	addInstruction({"return", INSTTYPE::INST_NORMAL});
	addInstruction({".end method", INSTTYPE::INST_NORMAL});

}

/*------------------------------------------------------------------------
 * addInstruction  -  adds instruction to the generated bytecode
 *------------------------------------------------------------------------
 */
void addInstruction(const instruction& instr)
{
    // if top is label so i can add code at that label
    // if(instructions.empty() == false && instructions.back().type == INSTTYPE::INST_LABEL){ 
    //     if(instr.type != INSTTYPE::INST_LABEL){
    //         instructions.back().code = instructions.back().code + instr.code;
    //         instructions.back().type = INSTTYPE::INST_NONE;
    //     }else{
    //         prefixedLabels.insert(atoi(instructions.back().code.c_str()));
    //         instructions.back().code = LABEL_OPTIONAL_PREFIX + instructions.back().code + instr.code;
    //         instructions.back().type = INSTTYPE::INST_NONE;
    //     }
    // }else{
        instructions.push_back(instr);
    //}
	
}

/*------------------------------------------------------------------------
 * getOp  - 
 *------------------------------------------------------------------------
 */
std::string getOp(const std::string& op){
    auto ite = opInstructions.find(op);
    if(ite != opInstructions.end())
	{
		return ite->second;
	}
	return "";

}
/*------------------------------------------------------------------------
 * addVariable  - adds a variable to the symbol table
 *------------------------------------------------------------------------
 */
void addVariable(const std::string& name,VARTYPE type){

    if(symbolTable.find(name) != symbolTable.end())
	{
		std::string err = name +" was declared before.";
		yyerror(err.c_str());
	}else
	{
        std::string currVariableID = std::to_string(varID); // get new id
		if(type == VARTYPE::TYINTEGER)
		{
			addInstruction({"\ticonst_0", INSTTYPE::INST_NORMAL});
            addInstruction({"\tistore " + currVariableID, INSTTYPE::INST_NORMAL});
		}
		else if ( type == VARTYPE::TYFLOAT)
		{
			addInstruction({"\tfconst_0" + currVariableID, INSTTYPE::INST_NORMAL});
            addInstruction({"\tfstore " + currVariableID, INSTTYPE::INST_NORMAL});
		}
		symbolTable[name] = std::make_pair(varID,type);
        varID++;
	}

}
/*------------------------------------------------------------------------
 * checkVariableExists  - check if variable exists and print error if not
 *------------------------------------------------------------------------
 */
 bool checkVariableExists(const std::string& varName){
		if(symbolTable.find(varName) != symbolTable.end())
		{
			return true;
		}
		else
		{
			std::string err = varName +" wasn't declared.";
			yyerror(err.c_str());
            return false;
		}
 }
/*------------------------------------------------------------------------
 * generateLabel  - generates a new label for the code
 *------------------------------------------------------------------------
 */
std::string generateLabel(){
    return LABEL(labelCounter++);
}
/*------------------------------------------------------------------------
 * backpatch  - adds jump location for a goto [as explained in the dragon book]
 *------------------------------------------------------------------------
 */
void backpatch(std::vector<int32_t> *list, int32_t jmploc){
    if(list != nullptr){
        for(int32_t codeLoc :*list)
	    {
		    instructions[codeLoc].code = instructions[codeLoc].code + getLabelString(jmploc);
	    }
    }
}
/*------------------------------------------------------------------------
 * merge  - merge two lists contents together
 *------------------------------------------------------------------------
 */
std::vector<int32_t> *mergeLists(std::vector<int32_t> *list1, std::vector<int32_t> *list2){
    if(list1 != nullptr && list2 != nullptr){
		std::vector<int32_t> *outList = new std::vector<int32_t> (*list1);
		outList->insert(outList->end(), list2->begin(),list2->end());
		return outList;
	}else if(list1 != nullptr)
	{
		return list1;
	}else if (list2 != nullptr)
	{
		return list2;
	}
	return new std::vector<int32_t>();
}
/*------------------------------------------------------------------------
 * typeCast  -  writes the output bytecode to a file
 *------------------------------------------------------------------------
 */
void typeCast(std::string id, int32_t newType){
        yyerror("casting not implemented yet");//TODO
 }
 /*------------------------------------------------------------------------
 * operationCast  -  check if 2 variables are equal type otherwise not handled ?
 *------------------------------------------------------------------------
 */
void operationCast(const std::string& operation,int32_t varType1, int32_t varType2){
    if(varType1 == varType2)
	{
		if(varType1 == VARTYPE::TYINTEGER)
		{
			addInstruction({"\ti" + getOp(operation), INSTTYPE::INST_NORMAL});
		}else if (varType1 == VARTYPE::TYFLOAT)
		{
			addInstruction({"\tf" + getOp(operation), INSTTYPE::INST_NORMAL});
		}
	}
	else
	{
		yyerror("cast not implemented yet");//TODO
	}
}

/*------------------------------------------------------------------------
 * addVarName  -  temporarily store var name to a buffer
 *------------------------------------------------------------------------
 */
void addVarName(const std::string& varName){
    temporaryVarNames.push_back(varName);
}

/*------------------------------------------------------------------------
 * flushVarNames  -  flush the buffer content to the symbol table with given type
 *------------------------------------------------------------------------
 */
void flushVarNames(VARTYPE type){
    for(std::string varName : temporaryVarNames){
        addVariable(varName, type);
    }
    temporaryVarNames.clear();
}
/*------------------------------------------------------------------------
 * getLabelString  -  check if label needs a prefix and return label accordingly
 *------------------------------------------------------------------------
 */

std::string getLabelString(int32_t n){
    // if(prefixedLabels.find(n) != prefixedLabels.end()){
    //     return LABEL_OPTIONAL_PREFIX + LABEL(n);
    // }
    return LABEL(n);
}

/*------------------------------------------------------------------------
 * outBytecode  -  writes the output bytecode to a file
 *------------------------------------------------------------------------
 */
void outBytecode(){
    std::ofstream fout(outputfileName);
    if(fout.is_open()){
        for (const instruction& instr : instructions)
	    {
		    fout << instr.code << std::endl;
	    }
    }else{
        std::cout << "Error opening the file !" << std::endl;
    }
    fout.close();
   
}  