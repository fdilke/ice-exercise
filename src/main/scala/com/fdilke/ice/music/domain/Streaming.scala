package com.fdilke.ice.music.domain

import java.time.LocalDate

case class Streaming(
  id: Id[Song],
  lengthSeconds: Int,
  streamTime: LocalDate
)
