package com.fdilke.ice.music.api

import com.fdilke.ice.music.domain._

import java.time.LocalDate

trait MusicDistributionSystem:
  def storeArtist(artist: Artist): Id[Artist]
  def storeRelease(release: Release): Id[Release]
  def storeSong(song: Song): Id[Song]
  def withRelease[T](
    id: Id[Release]
  )(
    block: Release => T
  ): T
  def withSong[T](
    id: Id[Song]
  )(
    block: Song => T
  ): T
  def withArtist[T](
   id: Id[Artist]
  )(
   block: Artist => T
  ): T  
  def withStreaming[T](
   id: Id[Streaming]
  )(
   block: Streaming => T
  ): T  
  def addSongsToRelease(
    id: Id[Release], 
    songs: Id[Song]*
  ): Unit
  def proposeReleaseDate(
    id: Id[Release], 
    date: LocalDate
  ): Unit
  def agreeReleaseDate(id: Id[Release]): Unit
  def searchReleasedSongs(text: String, maxResults: Int): Seq[(Id[Song], Int)]
  def getSong(id: Id[Song]): Option[Song]
  def getReleases: Seq[Id[Release]]
  def isSongStreamable(songId: Id[Song]): Boolean      
  def getSongs: Seq[Id[Song]]
  def storeStreaming(streaming: Streaming): Id[Streaming]
  def streamedSongsReport(id: Id[Artist]): String
  def requestArtistPayment(artistId: Id[Artist], date: LocalDate): Unit
  def recordArtistPayment(artistId: Id[Artist], date: LocalDate): Unit
  def takeReleaseOutOfDistribution(id: Id[Release]): Unit   