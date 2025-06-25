package com.fdilke.ice.music.impl

import com.fdilke.ice.music.domain.{Artist, Id, Release, Song, Streaming}
import com.fdilke.ice.music.api.MusicStorageService
import com.fdilke.ice.music.utility.Levenshtein

import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable

class LocalMusicStorageService extends MusicStorageService:
  private val sequenceNumber: AtomicLong =
    AtomicLong(0)

  private def blankMap[T]: mutable.Map[Id[T], T] =
    new mutable.HashMap[Id[T], T]()

  private val artists: mutable.Map[Id[Artist], Artist] =
    blankMap[Artist]

  private val releases: mutable.Map[Id[Release], Release] =
    blankMap[Release]
    
  private val songs: mutable.Map[Id[Song], Song] =
    blankMap[Song]

  private val streamings: mutable.Map[Id[Streaming], Streaming] =
    blankMap[Streaming]

  override def uniqueIdString(prefix: String): String =
    s"$prefix${sequenceNumber.getAndIncrement()}"

  override def storeArtist(id: Id[Artist], artist: Artist): Unit =
    artists(id) = artist

  override def storeRelease(id: Id[Release], release: Release): Unit =
    releases(id) = release
    
  override def storeSong(id: Id[Song], song: Song): Unit =
    songs(id) = song

  override def storeStreaming(id: Id[Streaming], streaming: Streaming): Unit =
    streamings(id) = streaming
  
  override def getSong(id: Id[Song]): Option[Song] =
    songs.get(id)

  override def getStreaming(id: Id[Streaming]): Option[Streaming] =
    streamings.get(id)
  
  override def getRelease(id: Id[Release]): Option[Release] =
    releases.get(id)

  override def getArtist(id: Id[Artist]): Option[Artist] =
    artists.get(id)
      
  override def searchReleasedSongs(text: String, maxResults: Int): Seq[(Id[Song], Int)] =
    songs.toSeq.filter: (songId, song) =>
      isSongStreamable(songId)
    .map: (songId, song) =>
      songId -> Levenshtein.distance(text, song.name)
    .sortBy: (songId, distance) =>
      distance
    .take(maxResults)

  def getReleases: Seq[Id[Release]] =
    releases.keys.toSeq

  override def isSongStreamable(songId: Id[Song]): Boolean =
    releases.values.exists: release =>
      release.isStreamable &&
        release.hasSong(songId)

  override def getSongs: Seq[Id[Song]] =
    songs.keys.toSeq

  private def songMatchesArtistInStreamableRelease(
    songId: Id[Song], 
    artistId: Id[Artist]
  ): Boolean =
    releases.values.exists: release =>
      release.hasSong(songId) &&
        release.isStreamable
  
  override def getStreamings(artistId: Id[Artist]): Seq[Id[Streaming]] =
    streamings.toSeq.filter: (idStream, stream) =>
      songMatchesArtistInStreamableRelease(stream.songId, artistId)  
    .map: (idStream, stream) =>
      idStream
