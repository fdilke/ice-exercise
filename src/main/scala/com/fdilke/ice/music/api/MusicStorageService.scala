package com.fdilke.ice.music.api

import com.fdilke.ice.music.domain.{Artist, Id, Release, Song, Streaming}

import java.time.LocalDate

trait MusicStorageService:
  def uniqueIdString(prefix: String): String
  def storeArtist(id: Id[Artist], artist: Artist): Unit
  def storeRelease(id: Id[Release], release: Release): Unit
  def storeSong(id: Id[Song], song: Song): Unit
  def storeStreaming(id: Id[Streaming], streaming: Streaming): Unit
  
  def getRelease(id: Id[Release]): Option[Release]
  def getSong(id: Id[Song]): Option[Song]
  def getArtist(id: Id[Artist]): Option[Artist]
  def getStreaming(id: Id[Streaming]): Option[Streaming]
  def searchReleasedSongs(text: String, maxResults: Int): Seq[(Id[Song], Int)]
  def getReleases: Seq[Id[Release]]
  def isSongStreamable(songId: Id[Song]): Boolean
  def getSongs: Seq[Id[Song]]
  def getStreamings(id: Id[Artist]): Seq[Id[Streaming]]
