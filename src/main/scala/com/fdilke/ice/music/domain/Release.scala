package com.fdilke.ice.music.domain

import java.time.LocalDate
import java.util.Date

case class Release(
  name: String,
  description: String,
  artist: Id[Artist],
  inDistribution: Boolean = true,
  proposedReleaseDate: Option[LocalDate] = None,
  agreedReleaseDate: Option[LocalDate] = None,
  songs: Seq[Id[Song]] = Seq()
):
  def isStreamable: Boolean =
    inDistribution &&
      agreedReleaseDate.exists:
        LocalDate.now().isAfter

  def hasSong(songId: Id[Song]): Boolean =
    songs.contains(songId)