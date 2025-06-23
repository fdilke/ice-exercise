package com.fdilke.ice.music

import com.fdilke.ice.music.domain.{Artist, Release, Song}

class MusicDistributionSystem(
   storageService: MusicStorageService
):
  def storeArtist(artist: Artist): ArtistId =
    val id = 
      ArtistId(storageService.uniqueIdString("artist"))
    storageService.storeArtist(id, artist)
    id
    
  def storeRelease(release: Release): ReleaseId =
    val id = 
      ReleaseId(storageService.uniqueIdString("release"))
    storageService.storeRelease(id, release)
    id
    
  def storeSong(song: Song): SongId =
    val id = 
      SongId(storageService.uniqueIdString("song"))
    storageService.storeSong(id, song)
    id
    
  def addSongsToRelease(id: ReleaseId, songs: SongId*): Unit  =
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