package edu.arizona.cs.mwli

class Tokenizer(){
  def tokenize(qString: String): Array[String] = {
      val init = (qString + " +")
      .split("\\)").mkString(" \\) ")
      .split("\\(").mkString(" \\( ")
      .split(" ")
      var res = List[String]()
      for (t <- init){
        if(t != ""){
          res = t::res
        }
      }
      res = res.tail
      res = res.reverse
      res.toArray
    }
}
