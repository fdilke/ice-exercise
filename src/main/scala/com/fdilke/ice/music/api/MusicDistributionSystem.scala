package com.fdilke.ice.music.api

import com.fdilke.ice.music.domain.{Artist, Id, Release, Song}

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
