# Compiler Generator
<p> 
A compiler translates and/or compiles a program written in a suitable source language into an equivalent target language without changing the meaning of the program through a number of stages.<br>
</p>

## 📝 Table of Contents
- [About](#about)
- [Project flow](#project_flow)
- [Prerequisites](#prerequisites)
- [Sample runs](#sample_runs)

---

## About <a name = "about"></a>
<p align="center"> 
<img src="https://user-images.githubusercontent.com/48100957/85887004-4f100600-b7e7-11ea-8ef8-2d7962f1453e.png">
</p>

An implementation of a front-end compiler that is divided into three phases:
  - Lexical analyzer generator
  - Parser generator
  - Java bytecode generation

### Lexical analyzer generator
The lexical analyzer generator is required to automatically construct a lexical analyzer from a regular expression description of a set of tokens. The tool is required to construct a nondeterministic finite automata (NFA) for the given regular expressions, combine these NFAs together with a new starting state, convert the resulting NFA to a DFA, minimize it and emit the transition table for the reduced DFA together with a lexical analyzer program that simulates the resulting DFA machine.
<br><br>
The generated lexical analyzer has to read its input one character at a time, until it finds the longest prefix of the input, which matches one of the given regular expressions. It should create a symbol table and insert each identifier in the table. If more than one regular expression matches some longest prefix of the input, the lexical analyzer should break the tie in favor of the regular expression listed first in the regular specifications. <br>

### Parser generator
The parser generator expects an LL (1) grammar as input, it should compute the first and follow and uses them to construct the predictive parsing table. <br>
The table is used to derive a predictive top-down parser. If the input grammar is not LL (1), an appropriate error message should be produced. <br>
If an error is encountered, a panic-mode error recovery routine is to be called to print an error message and to resume parsing. <br>

### Java bytecode generation
The java bytecode generator must follows bytecode instructions defined in the ![Java virtual machine specifications](https://docs.oracle.com/javase/specs/)
<br>
Grammar covers the following:
  * Primitive types (int, float) with operations on them (+, -, *, /)
  * Boolean Expressions
  * Arithmetic Expressions
  * Assignment statements
  * If-else statements
  * for loops
  * while loops
  
---
  
## Project_flow <a name = "project_flow"></a>
<p align="center"> 
<img src="https://user-images.githubusercontent.com/48100957/85888110-3c96cc00-b7e9-11ea-9754-cb42c36a30c3.png">
</p>

---

## Prerequisites <a name = "prerequisites"></a>
#### You need to have the following installed:
- Java
- GNU Bison
- GNU Flex

## Sample Runs <a name = "sample_runs"></a>
  * Simple editor to lay rules/code in and a simple code analyzer UI to check:
    - Tokens (Lexemes)
    - Transition table
    - First and follow sets
    
<p align="center"> 
<img src="https://user-images.githubusercontent.com/48100957/85889297-7c5eb300-b7eb-11ea-8aa0-f28095fd0f88.png">
<img src="https://user-images.githubusercontent.com/48100957/85889338-913b4680-b7eb-11ea-82b7-c9b69e85ae03.png">
<img src="https://user-images.githubusercontent.com/48100957/85889390-a87a3400-b7eb-11ea-8599-aca5ff62dfae.png">
</p>
