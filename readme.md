# Boolean query, w/ inverted index

## Features
- Supports complex boolean queries with AND, OR and parentheses

## How to run/test
The easiest way is to run with sbt command:

```sbt "run <docFileName.txt> <query string...>"```

for example

```sbt "run doc2.txt a AND (b OR c)"```
Note the double quotation marks. 

## Details on query format
The priority of operations follows a common practice:

(...) > AND > OR

For example,
```a OR b AND c```
is equivalent to
```a OR (b AND c)```,
but they are both different from
```(a OR b) AND c```


Operators with the same priority are computed from left to right.

Also, spaces between token words and operators are necessary. Spaces between a parenthesis and its neighbors do _not_ matter. For example,

```a AND(b OR c)```,
```a AND ( b OR c )```, etc, 

are valid, while

```aAND ( b OR c )``` or
```a AND ( bORc )```

are not.


## Limitations
- Tokens in the documents are indexed by its original form (i.e. no normalization)
- Query words are not normalized either
- Basic items in a query must be single words without space. Also, parenthesis, "AND" and "OR" are reserved words so they are not searchable.
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

