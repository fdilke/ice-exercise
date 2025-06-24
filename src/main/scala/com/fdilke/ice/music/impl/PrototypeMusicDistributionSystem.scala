package com.fdilke.ice.music.impl

import com.fdilke.ice.music.api.{MusicDistributionSystem, MusicStorageService}
import com.fdilke.ice.music.domain.{Artist, Id, Release, Song}

import java.time.LocalDate

class PrototypeMusicDistributionSystem(
   storageService: MusicStorageService
) extends MusicDistributionSystem:
  override def storeArtist(artist: Artist): Id[Artist] =
    val id = 
      Id[Artist](storageService.uniqueIdString("artist"))
    storageService.storeArtist(id, artist)
    id

  override def storeRelease(release: Release): Id[Release] =
    val id = 
      Id[Release](storageService.uniqueIdString("release"))
    storageService.storeRelease(id, release)
    id
    
  def storeSong(song: Song): Id[Song] =
    val id = 
      Id[Song](storageService.uniqueIdString("song"))
    storageService.storeSong(id, song)
    id

  override def withRelease[T](
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

  override def addSongsToRelease(
    id: Id[Release], 
    songs: Id[Song]*
  ): Unit  =
    updateRelease(id): release => 
        release.copy(
          songs = release.songs ++ songs
        )

  override def proposeReleaseDate(
    id: Id[Release], 
    date: LocalDate
  ): Unit =
    updateRelease(id): release =>
      release.copy(
        proposedReleaseDate = Some(date)
      )

  override def agreeReleaseDate(id: Id[Release]): Unit =
    updateRelease(id): release =>
      release.copy(
        proposedReleaseDate = None,
        agreedReleaseDate = release.proposedReleaseDate
      )

  override def getSong(id: Id[Song]): Option[Song] =
    storageService.getSong(id)
    
  override def searchSongs(text: String, maxResults: Int): Seq[(Id[Song], Int)] =
    storageService.searchSongs(text, maxResults)
