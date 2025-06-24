package com.fdilke.ice.music.domain

import java.time.LocalDate

case class Streaming(
  songId: Id[Song],
  lengthSeconds: Int,
  streamTime: LocalDate
):
  def isMonetizable: Boolean =
    lengthSeconds > 30
