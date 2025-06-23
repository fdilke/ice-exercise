package com.fdilke.ice.music

import com.fdilke.ice.music.domain.{Artist, Release, Song}

trait MusicStorageService:
  def uniqueIdString(prefix: String): String
  def storeArtist(id: ArtistId, artist: Artist): Unit
  def storeRelease(id: ReleaseId, release: Release): Unit
  def storeSong(id: SongId, song: Song): Unit
  
  def getRelease(id: ReleaseId): Option[Release]

