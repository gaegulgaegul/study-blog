package me.gaegul.ch20

object Tuples {
  def main(args: Array[String]) {
    val book = (2018, "Modern Java in Action");
    val numbers = (42, 1337, 0, 3, 7);
    println(book)
    println(numbers._4)
  }
}
