package com.fdilke.ice.music.local

import com.fdilke.ice.music.domain.{Artist, Release, Song}
import com.fdilke.ice.music.{ArtistId, MusicStorageService, ReleaseId, SongId}

import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable

class LocalMusicStorageService extends MusicStorageService:
  private val sequenceNumber: AtomicLong =
    AtomicLong(0)

  private val artists: mutable.Map[ArtistId, Artist] =
    new mutable.HashMap[ArtistId, Artist]()

  private val releases: mutable.Map[ReleaseId, Release] =
    new mutable.HashMap[ReleaseId, Release]()
    
  private val songs: mutable.Map[SongId, Song] =
    new mutable.HashMap[SongId, Song]()

  override def uniqueIdString(prefix: String): String =
    s"$prefix${sequenceNumber.getAndIncrement()}"

  override def storeArtist(id: ArtistId, artist: Artist): Unit =
    artists(id) = artist

  override def storeRelease(id: ReleaseId, release: Release): Unit =
    releases(id) = release
    
  override def storeSong(id: SongId, song: Song): Unit =
    songs(id) = song

  override def getRelease(id: ReleaseId): Option[Release] =
    releases.get(id)