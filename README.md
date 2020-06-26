# Compiler Generator
<p> 
A compiler translates and/or compiles a program written in a suitable source language into an equivalent target language without changing the meaning of the program through a number of stages.<br>
</p>

## 📝 Table of Contents
- [About](#about)
- [Project flow](#project_flow)
- [Prerequisites](#prerequisites)
- [Features](#features)
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
The java bytecode generator must follows bytecode instructions defined in the [Java virtual machine specifications](https://docs.oracle.com/javase/specs/)
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

## Features <a name = "features"></a>
  * Code imporing/exporting + editable area in the GUI.
  * Left Factoring Enabled.
  * Left Recursion Elimination (both direct & indirect).
  * Ability to view:
    - NFA , DFA (for debugging).
    - Tranisition table.
    - Lexemes table.
    - First & Follow Sets.
    - Parsing table.
    - Parse tree ( for debugging).
  * Third phase is implemented by using GNU Bison.

## Sample Runs <a name = "sample_runs"></a>
  * Simple editor to lay rules/code in and a simple code analyzer UI to check:
    - Tokens (Lexemes)
    - Transition table
    - First and follow sets
    

<p align="center"> 
<img src="https://user-images.githubusercontent.com/48100957/85892081-92bb3d80-b7f0-11ea-9c57-83993b690eb0.png">
<img src="https://user-images.githubusercontent.com/48100957/85892150-b2eafc80-b7f0-11ea-8fa3-d21027b01933.png">
<img src="https://user-images.githubusercontent.com/48100957/85892186-c302dc00-b7f0-11ea-989a-1cb3f44fcd90.png">
<img src="https://user-images.githubusercontent.com/48100957/85892233-d746d900-b7f0-11ea-90b8-7522845051a6.png">
<img src="https://user-images.githubusercontent.com/48100957/85892308-fc3b4c00-b7f0-11ea-9b2c-d27149b14f46.png">
</p>
