package edu.arizona.cs.mwli
import edu.arizona.cs.mwli

object MyApp extends App {
  val docFn = args(0)
  val db = new InvertedIndex(docFn)
  val queryString = args(1)
  

  printf("QUERY: %s\n", queryString)

  println("TOKENS:")
  val tokens = db.tokenize(queryString)
  for((t, i) <- tokens.zipWithIndex){
    println(s"$i| '$t'")
  }

  var docIdList = db.query(queryString)
  var docNames = docIdList.map(db.id2doc)
  println(s"docIdList = $docIdList")
  println(s"docNames = $docNames")
}
