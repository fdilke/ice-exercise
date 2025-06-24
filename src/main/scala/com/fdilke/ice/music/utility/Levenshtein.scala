package com.fdilke.ice.music.utility

object Levenshtein:
  // initial algorithm, too slow
  def distanceOld(x: String, y: String): Int =
    if x.isEmpty then
      y.length
    else if y.isEmpty then
      x.length
    else
      val substitution: Int =
        distanceOld(x.substring(1), y.substring(1)) + (
          if x.charAt(0) == y.charAt(0) then
            0
          else 1
        )
      val insertion: Int =
        distanceOld(x, y.substring(1)) + 1
      val deletion =
        distanceOld(x.substring(1), y) + 1
      Math.min(substitution, Math.min(insertion, deletion))

  def distance(string1: String, string2: String): Int =
    val s1: Array[Char] = string1.toCharArray
    val s2: Array[Char] = string2.toCharArray
    var prev = new Array[Int](s2.length + 1)
    for
      j <- 0 until s2.length + 1
    do
      prev(j) = j
    for
      i <- 1 until s1.length + 1
    do
      val curr = new Array[Int](s2.length + 1)
      curr(0) = i
      for (j <- 1 until s2.length + 1) {
        val d1 = prev(j) + 1
        val d2 = curr(j - 1) + 1
        var d3 = prev(j - 1)
        if (s1(i - 1) != s2(j - 1)) d3 += 1
        curr(j) = Math.min(Math.min(d1, d2), d3)
      }
      prev = curr
    prev(s2.length)

// test harness to check the distance calculation
//object LevensteinDemo extends App:
//  for
//    s1 <- Seq("pig", "wig", "together", "simulate")
//    s2 <- Seq("pug", "rather", "stimulate", "wiz")
//  do
//    val dist = Levenshtein.distance(s1, s2)
//    println(s"$s1\t$s2\t$dist")
