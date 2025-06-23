package com.fdilke.ice.music.domain

import com.fdilke.ice.music.Id

import java.time.LocalDate
import java.util.Date

case class Release(
  name: String,
  description: String,
  artist: Id[Artist],
  proposedReleaseDate: Option[LocalDate] = None,
  confirmedReleaseDate: Option[LocalDate] = None,
  songs: Seq[Id[Song]] = Seq()
)
