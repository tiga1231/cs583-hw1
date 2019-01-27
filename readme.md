# Boolean Retrieval with an Inverted Index

## Features
- Supports complex boolean queries with AND, OR and parentheses

## How to run it
The easiest way is to run with sbt command:

```sbt "run <docFileName.txt> <query string...>"```

for example

```sbt "run doc2.txt a AND (b OR c)"```

Note the double quotation marks. 
## Sample input and output
doc1.txt in this repo contains the sample document stated in the problem:

```
Doc1 breakthrough drug for schizophrenia
Doc2 new approach for treatment of schizophrenia 
Doc3 new hopes for schizophrenia patients
Doc4 new schizophrenia drug
```
The command

```
sbt "run doc1.txt (drug OR treatment) AND schizophrenia"
```
will yield an output as:

```
[info] Loading project definition from ...
[info] Loading settings for project root from build.sbt ...
...
(sbt info omitted)
...
QUERY: (drug OR treatment) AND schizophrenia
TOKENS:
0| '\('
1| 'drug'
2| 'OR'
3| 'treatment'
4| '\)'
5| 'AND'
6| 'schizophrenia'
docIdList = List(0, 1, 3)
docNames = List(Doc1, Doc2, Doc4)
[success] Total time: 1 s, completed Jan 27, 2019 10:50:57 AM
```
It will print out the original query, tokens in it, 
the answer to the query in a list that contains internal document ids, in this case List(0, 1, 3), 
and also the same answer, but as a list that contains original document names, i.e. List(Doc1, Doc2, Doc4).

## Details on query format
The priority of operations follows a common practice:

(...) > AND > OR

Where > mean higher priority.
For example,
```a OR b AND c```
is equivalent to
```a OR (b AND c)```,
but they are both different from
```(a OR b) AND c```.


Operators with the same priority are computed from left to right, without query optimization

Also, spaces between token words and operators are necessary. Spaces between a parenthesis and its direct neighbors do _not_ matter. For example,

```a AND(b OR c)```,
```a AND ( b OR c )```, etc, 

are valid, while

```aAND ( b OR c )``` or
```a AND ( bORc )```

are not.


## Limitations
- Tokens in the documents are indexed by its original form (i.e. no normalization)
- Query words are not normalized either
- Basic items in a query must be single words without space. Also, parenthesis, "AND" and "OR" are reserved keywords so they are not searchable.
- Indexed terms are static, i.e. update of documents is not supported.

## Assumptions
### Document Files
- documents are assumed to be stored in a _single_ file. Each line in the file records a document identifier (single word without any space) followed by a list of tokens:
```
<document_name> <token1> <token2> ...
```

For example, the demo file ```doc1.txt``` contains:

```
Doc1 breakthrough drug for schizophrenia
Doc2 new approach for treatment of schizophrenia
Doc3 new hopes for schizophrenia patients
Doc4 new schizophrenia drug
```

## API Refernce
### InvertedIndex.scala

