package com.fdilke.ice.music.api

import com.fdilke.ice.music.domain.{Artist, Id, Release, Song}

trait MusicStorageService:
  def uniqueIdString(prefix: String): String
  def storeArtist(id: Id[Artist], artist: Artist): Unit
  def storeRelease(id: Id[Release], release: Release): Unit
  def storeSong(id: Id[Song], song: Song): Unit
  
  def getRelease(id: Id[Release]): Option[Release]

