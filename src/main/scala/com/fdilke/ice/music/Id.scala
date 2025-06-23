package com.fdilke.ice.music

trait Id:
  val key: String

case class ArtistId(key: String) extends Id
case class ReleaseId(key: String) extends Id
case class SongId(key: String) extends Id



