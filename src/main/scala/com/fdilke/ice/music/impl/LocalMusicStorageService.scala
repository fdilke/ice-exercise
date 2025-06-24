package com.fdilke.ice.music.impl

import com.fdilke.ice.music.domain.{Artist, Id, Release, Song}
import com.fdilke.ice.music.api.MusicStorageService
import com.fdilke.ice.music.utility.Levenshtein

import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable

class LocalMusicStorageService extends MusicStorageService:
  private val sequenceNumber: AtomicLong =
    AtomicLong(0)

  private val artists: mutable.Map[Id[Artist], Artist] =
    new mutable.HashMap[Id[Artist], Artist]()

  private val releases: mutable.Map[Id[Release], Release] =
    new mutable.HashMap[Id[Release], Release]()
    
  private val songs: mutable.Map[Id[Song], Song] =
    new mutable.HashMap[Id[Song], Song]()

  override def uniqueIdString(prefix: String): String =
    s"$prefix${sequenceNumber.getAndIncrement()}"

  override def storeArtist(id: Id[Artist], artist: Artist): Unit =
    artists(id) = artist

  override def storeRelease(id: Id[Release], release: Release): Unit =
    releases(id) = release
    
  override def storeSong(id: Id[Song], song: Song): Unit =
    songs(id) = song

  override def getSong(id: Id[Song]): Option[Song] =
    songs.get(id)

  override def getRelease(id: Id[Release]): Option[Release] =
    releases.get(id)

  override def searchSongs(text: String, maxResults: Int): Seq[(Id[Song], Int)] =
    songs.toSeq.map: (songId, song) =>
      songId -> Levenshtein.distance(text, song.name)
    .sortBy: (songId, distance) =>
      distance
    .take(maxResults)

  def getReleases: Seq[Id[Release]] =
    releases.keys.toSeq

