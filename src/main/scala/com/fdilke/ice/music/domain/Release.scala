package com.fdilke.ice.music.domain

import com.fdilke.ice.music.{ArtistId, SongId}

import java.time.LocalDate
import java.util.Date

case class Release(
  name: String,
  description: String,
  artist: ArtistId,
  proposedReleaseDate: Option[LocalDate] = None,
  confirmedReleaseDate: Option[LocalDate] = None,
  songs: Seq[SongId] = Seq()
)
