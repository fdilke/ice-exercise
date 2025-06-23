package com.fdilke.ice.music

import com.fdilke.ice.music.domain.{Artist, Release, Song}

import java.time.LocalDate

class MusicDistributionSystem(
   storageService: MusicStorageService
):
  def storeArtist(artist: Artist): Id[Artist] =
    val id = 
      Id[Artist](storageService.uniqueIdString("artist"))
    storageService.storeArtist(id, artist)
    id
    
  def storeRelease(release: Release): Id[Release] =
    val id = 
      Id[Release](storageService.uniqueIdString("release"))
    storageService.storeRelease(id, release)
    id
    
  def storeSong(song: Song): Id[Song] =
    val id = 
      Id[Song](storageService.uniqueIdString("song"))
    storageService.storeSong(id, song)
    id

  private def withRelease[T](
    id: Id[Release]
  )(
    block: Release => T
  ): T =
    storageService.getRelease(id) match
      case Some(release) =>
        block(release)
      case _ =>
        throw IllegalArgumentException(s"unknown release id $id")

  private def updateRelease[T](
    id: Id[Release]
  )(
    updateFn: Release => Release
  ): Unit =
    withRelease(id): release =>
      storageService.storeRelease(
        id,
        updateFn(release)
      )

  def addSongsToRelease(
    id: Id[Release], 
    songs: Id[Song]*
  ): Unit  =
    updateRelease(id): release => 
        release.copy(
          songs = release.songs ++ songs
        )

  def proposeReleaseDate(
    id: Id[Release], 
    date: LocalDate
  ): Unit =
    updateRelease(id): release =>
      release.copy(
        proposedReleaseDate = Some(date)
      )
    