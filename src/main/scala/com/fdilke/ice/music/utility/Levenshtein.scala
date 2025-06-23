package com.fdilke.ice.music.utility

object Levenshtein:
   def distance(x: String, y: String): Int =
      if x.isEmpty then
        y.length
      else if y.isEmpty then
        x.length
      else
        val substitution: Int =
          distance(x.substring(1), y.substring(1)) + (
            if x.charAt(0) == y.charAt(0) then
              0
            else 1
          )
        val insertion: Int =
          distance(x, y.substring(1)) + 1
        val deletion =
          distance(x.substring(1), y) + 1
        Math.min(substitution, Math.min(insertion, deletion))

//object LevensteinDemo extends App:
//  for
//    s1 <- Seq("pig", "wig", "together", "simulate")
//    s2 <- Seq("pug", "rather", "stimulate", "wiz")
//  do
//    val dist = Levenshtein.distance(s1, s2)
//    println(s"$s1\t$s2\t$dist")
