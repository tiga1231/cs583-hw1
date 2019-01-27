package edu.arizona.cs.mwli
// import scala.util.control.Breaks._


import scala.io.Source
import scala.collection.mutable

/** stores indexed terms given in documents
 *
 *  @constructor create the index.
 *  @param filename of text file that contains all the documents
 */
class InvertedIndex(filename: String){

  val doc2id = mutable.Map[String, Int]()
  val id2doc = mutable.Map[Int, String]()
  val db = mkDb(filename)
  var isDebug = false
  var tokenizer = new Tokenizer()

  /** stores indexed terms given in documents
   *
   *  @param q the query, support operators AND, OR and (),  e.g. "a AND (b or c)"
   */
  def query(q: String):List[Int] = {
    val tokens = tokenizer.tokenize(q)
    var res = parse(tokens)
    res
  }
  
  def tokenize(q: String):Array[String] = {
    val tokens = tokenizer.tokenize(q)
    tokens
  }

  
  //--------parser---------
  var parserProgress = 0
  val reserved = Set("AND", "OR", "(", ")")
  //parser inspired by examples in the book:
  //programming principles and practice using c++, 2nd edition, (p.199-203)
  //by Bjarne Stroustrup 
  def parse(tokens: Array[String]):List[Int] = {
    parserProgress = 0
    disjunction(tokens)
  }
  //grammar:
  //disjunction:
  //  conjunction
  //  conjunction "OR" disjunction
  //conjunction:
  //  block
  //  block "AND" conjunction
  //block:
  //  word
  //  "(" disjunction ")"
  var indentLevel = 0
  def disjunction(tokens: Array[String]):List[Int] = {
    if(isDebug) println(" "*2*indentLevel + "disjunction={")
    indentLevel += 1
    
    Predef.assert(parserProgress < tokens.length, "called disjunction() but query has reach the end, wrong syntax?")
    var left = conjunction(tokens)

    if(parserProgress < tokens.length){
      var t = tokens(parserProgress)
      while(t=="OR"){
        if(isDebug) printf(" "*2*indentLevel+"\"%s\" @%d\n", t, parserProgress)

        parserProgress+=1
        val right = conjunction(tokens)
        left = union(left, right)
        if(parserProgress < tokens.length){
          t = tokens(parserProgress)
        }else{
          t = "break"
        }
      }
    }
    indentLevel-=1
    if(isDebug) println(" "*2*indentLevel + "}")
    left
  }


  def conjunction(tokens: Array[String]):List[Int] = {
    if(isDebug) println(" "*2*indentLevel + "conjunction={")
    indentLevel += 1
    var left = List[Int]()

    Predef.assert(parserProgress < tokens.length, "called conjunction() but query has reach the end, wrong syntax?")
    left = block(tokens)
    
    if(parserProgress < tokens.length){
      var t = tokens(parserProgress)
      while(t=="AND"){
        if(isDebug) printf(" "*2*indentLevel+"\"%s\" @%d\n", t, parserProgress)

        parserProgress+=1
        val right = block(tokens)
        left = intersection(left, right)
        if(parserProgress < tokens.length){
          t = tokens(parserProgress)
        }else{
          t = "break"
        }
      }
    }
    indentLevel -= 1
    if(isDebug) println(" "*2*indentLevel + "}")
    left
  }


  def block(tokens: Array[String]):List[Int] = {
    if(isDebug) println(" "*2*indentLevel + "block={")
    indentLevel += 1

    var left = List[Int]()
    var t = ""

    Predef.assert(parserProgress < tokens.length, "called block() but query has reach the end, wrong syntax?")
    t = tokens(parserProgress)
    if(isDebug) printf(" "*2*indentLevel+"\"%s\" @%d\n", t, parserProgress)

    parserProgress+=1
    if(t == "\\("){
      left = disjunction(tokens)
      Predef.assert(parserProgress < tokens.length, "parenthesis not closed")
      t = tokens(parserProgress)
      parserProgress+=1
      Predef.assert(t == "\\)", "parenthesis not closed")
      left
    }else if(!isOperator(t)){
      left = getPostingList(t)
    }else{
      Predef.assert(false, "encounter un-handled token")
    }
    
    indentLevel -= 1
    if(isDebug) println(" "*2*indentLevel + "}")
    left
  }



  //--------list operations---------
  def getPostingList(term: String):List[Int] = {
    if (db.contains(term)){
      db(term)
    }else{
      List[Int]()
    }
  }


  def intersection(l1:List[Int], l2:List[Int]) :List[Int] = {
    var res = List[Int]()
    var i = 0
    var j = 0
    while ( i < l1.length && j < l2.length){
      if( l1(i) == l2(j) ){
        res = l1(i) :: res
        i+=1
        j+=1
      }else if( l1(i) < l2(j) ){
        i+=1
      }else if( l1(i) > l2(j) ){
        j+=1
      }
    }
    res.reverse
  }


  def union(l1:List[Int], l2:List[Int]) :List[Int] = {
    var res = List[Int]()
    var i = 0
    var j = 0
    while ( i < l1.length && j < l2.length){
      if( l1(i) == l2(j) ){
        res = l1(i) :: res
        i+=1
        j+=1
      }else if( l1(i) < l2(j) ){
        res = l1(i) :: res
        i+=1
      }else if( l1(i) > l2(j) ){
        res = l2(j) :: res
        j+=1
      }
    }
    for(x <- i to l1.length-1){
      res = l1(x) :: res
    }
    for(y <- j to l2.length-1){
      res = l2(y) :: res
    }
    res.reverse
  }



  //--------utils---------
  def isOperator(s:String) = {
    reserved.contains(s)
  }


  def mkDb(filename: String) = {
    var term_docId_pairs: List[Tuple2[String, Int]] = Nil
    var i = 0
    for (line <- Source.fromFile(filename).getLines() ){
      val array = line.split(" ")
      val (doc, tokens) = (array.head, array.tail)
      if(!doc2id.contains(doc)){
        doc2id(doc) = i
        id2doc(i) = doc
        i += 1
      }
      val docId = doc2id(doc)
      for (t <- tokens){
        term_docId_pairs = (t, docId) :: term_docId_pairs
      }
    }
    term_docId_pairs = term_docId_pairs.reverse
    term_docId_pairs = term_docId_pairs.sorted
    val db = pairs2invertedIndex( term_docId_pairs )
    // println(db)
    db
  }


  def pairs2invertedIndex(
    term_docId_pairs: List[Tuple2[String, Int]]
  ):mutable.Map[String, List[Int]] = {
    //build inverted index from term_docId_pairs
    val invertedIndex = mutable.Map[String, List[Int]]()
    var lastTerm = "※\\(^o^)/※"
    var lastDocId = -99
    for((term, docId) <- term_docId_pairs){
      if(term != lastTerm){ //new term
        invertedIndex(term) = List(docId)
      }else if(docId != lastDocId){ //new doc
        invertedIndex(term) = docId :: invertedIndex(term)
      }
      lastTerm = term
      lastDocId = docId
    }
    for ((term, docIdList) <- invertedIndex){
      invertedIndex(term) = docIdList.reverse
    }
    invertedIndex
  }
}




