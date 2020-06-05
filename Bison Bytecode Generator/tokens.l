

%{
    #include <iostream>
    #include <string>
  	#include "y.tab.h"

    extern int yylex();
    int32_t lineNum = 1;
%}

%option noyywrap


digit               [0-9]
letter              [a-zA-Z]
digits              {digit}+
string              \"(\\.|[^"\\])*\"
id                  {letter}({letter}|{digit})*
num                 "-"?{digits}
fnum                "-"?{digits}.{digits}("E"{digits}+)?

relop               "=="|"!="|">"|">="|"<"|"<="
operation           "+"|"-"|"*"|"/"|"%"
booloperation       "&&"|"||"
binary              "true"|"false"

%%
"boolean"             {return BOOLEAN_DECL;}
"int"                 {return INTEGER_DECL;}
"float"               {return FLOAT_DECL;}
"if"                  {return IF_TOK;}
"else"                {return ELSE_TOK;}
"while"               {return WHILE_TOK;}
"for"                 {return FOR_TOK;}
{num}	              {yylval.intValue = atoi(yytext); return INTEGER_NUMBER;}
{fnum}	              {yylval.floatValue = atof(yytext); return FLOAT_NUMBER;}
{string}              {yylval.stringValue = strdup(yytext); return STRING; } // nothing fancy used in printing only
{relop}               {yylval.operationValue = strdup(yytext);  return RELOP;}
{operation}           {yylval.operationValue = strdup(yytext);  return OPERATION;}
{booloperation}       {yylval.operationValue = strdup(yytext);  return BOOLOP;}
{binary}              {if(strcmp(yytext,"true") == 0){ yylval.booleanValue = true;} else { yylval.booleanValue = false;} return BOOLEAN;}
{id}	              {yylval.varName = strdup(yytext); return IDENTIFIER;}
"="                   {return ASSIGNMENT_OPERATOR;}
";"                   {return SEMI_COLON;}
"("                   {return LEFT_BRACKET;}
")"                   {return RIGHT_BRACKET;}
"}"                   {return RIGHT_CURLY_BRACKET;}
"{"                   {return LEFT_CURLY_BRACKET;}
","                   {return COMMA_TOK;}
"System.out.println"  {return PRINTLN_TOK;}
[ \t]               ;  // ignore all whitespace
\n                    { ++lineNum; }
%%
// main is defined for the parser
/*
int main(int, char**) {
  
  FILE *file = fopen("input.txt", "r");
  if (!file) {
      std::cout << "Failed to open input.txt" << std::endl;
      return -1;
  }
  yyin = file;
  while (yylex());
}
*/