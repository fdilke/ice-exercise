package com.fdilke.ice.music

import com.fdilke.ice.music.domain.{Artist, Release, Song}

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
    
  def addSongsToRelease(id: Id[Release], songs: Id[Song]*): Unit  =
    storageService.getRelease(id) match 
      case Some(release) =>
        storageService.storeRelease(
          id,
          release.copy(
            songs = release.songs ++ songs
          )
        )
      case _ =>
        throw IllegalArgumentException(s"unknown release id $id")