package edu.arizona.cs
import org.scalatest._

import edu.arizona.cs.mwli.InvertedIndex

class TestHw1 extends FlatSpec with Matchers {

  var docFn = "doc2.txt"
  var db = new InvertedIndex(docFn)
  db.isDebug = false
  print("\n"*50)


  "Inverted index" should "generate correct posting list" in {
    var list = db.getPostingList("a")
    list should be (List[Int](0, 1))

    list = db.getPostingList("0")
    list should be (List[Int](0, 1, 9))

    list = db.getPostingList("e")
    list should be (List[Int](0, 2))

    list = db.getPostingList("apple")
    list should be (List[Int](5, 6, 10))

    list = db.getPostingList("x")
    list should be (List[Int](3, 4, 8))
  }


	"AND operator" should "support simple intersection" in {
		var answer = db.query("0 AND 1")
		answer should be (List[Int](0, 1, 9))

    answer = db.query("x AND y")
    answer should be (List[Int](3))

    answer = db.query("apple AND orange")
    answer should be (List[Int](6, 10))

    answer = db.query("x AND x")
    answer should be (List[Int](3,4,8))

	}


	it should "support multiple intersection" in {
		var answer = db.query("1 AND 2 AND 3 AND 4")
    answer should be (List[Int](9))

    answer = db.query("0 AND 1 AND 2 AND 3")
    answer should be (List[Int](0, 1, 9))

    answer = db.query("apple AND orange AND juice")
    answer should be (List[Int](6, 10))

	}

  it should "support parenthesis" in {
    var answer = db.query("1 AND (2 AND orange) and 3")
    answer should be (List[Int]())
  }




  "OR operator" should "support simple intersection" in {
    var answer = db.query("z OR 10")
    answer should be (List[Int]())

    answer = db.query("x OR 9-year-old")
    answer should be (List[Int](3, 4, 7, 8))

    answer = db.query("0 OR 1")
    answer should be (List[Int](0, 1, 9))

  }

  it should "support multiple intersection" in {
    var answer = db.query("a OR b OR 1 OR 2 OR ssr")
    answer should be (List[Int](0, 1, 9, 11, 12))
  }



  "Compound expression" should "compute AND before OR" in {
    var answer = db.query("1 AND 2 OR 4")
    answer should be (List[Int](0,1,9))

    answer = db.query("4 OR 1 AND 2")
    answer should be (List[Int](0,1,9))

    answer = db.query("5 AND 2 OR 1")
    answer should be (List[Int](0, 1, 9, 12))
  }

  it should "support parenthesis" in {
    var answer = db.query("5 AND (2 OR 8)")
    answer should be (List[Int](12, 13))
  }

  it should "support very long and complex query" in {
    var answer = db.query("e OR x AND (y OR m)")
    answer should be (List[Int](0, 2, 3, 4))

    answer = db.query("e OR (x AND (y OR m))")
    answer should be (List[Int](0, 2, 3, 4))

    answer = db.query("(e OR x) AND (y OR m)")
    answer should be (List[Int](3, 4))
  }

  "Parenthesis" should "allow crazy nesting" in {
    var answer = db.query("(a OR d) AND ((b) OR c)")
    answer should be (List[Int](0,1))
    //TODO
    // answer = db.query("4 OR 1 AND 2")
    // answer should be (List[Int](0,1,9))

  }
  


}