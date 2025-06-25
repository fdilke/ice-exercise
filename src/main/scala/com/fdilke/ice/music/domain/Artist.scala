package com.fdilke.ice.music.domain

import java.time.LocalDate

case class Artist(
  name: String,
  categories: Seq[String],
  paymentRequestDates: Seq[LocalDate] = 
    Seq.empty,
  paymentDates: Seq[LocalDate] = 
    Seq.empty
)

