package com.fdilke.ice.music.impl

import com.fdilke.ice.music.api.{MusicDistributionSystem, MusicStorageService}
import com.fdilke.ice.music.domain.{Artist, Id, Release, Song, Streaming}

import java.time.LocalDate

class PrototypeMusicDistributionSystem(
   storageService: MusicStorageService
) extends MusicDistributionSystem:
  override def storeArtist(artist: Artist): Id[Artist] =
    val id: Id[Artist] = 
      Id[Artist](storageService.uniqueIdString("artist"))
    storageService.storeArtist(id, artist)
    id

  override def storeRelease(release: Release): Id[Release] =
    val id: Id[Release] = 
      Id[Release](storageService.uniqueIdString("release"))
    storageService.storeRelease(id, release)
    id
    
  def storeSong(song: Song): Id[Song] =
    val id: Id[Song] = 
      Id[Song](storageService.uniqueIdString("song"))
    storageService.storeSong(id, song)
    id

  override def storeStreaming(streaming: Streaming): Id[Streaming] =
    val id: Id[Streaming] =
      Id[Streaming](storageService.uniqueIdString("streaming"))
    storageService.storeStreaming(id, streaming)
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

  override def withSong[T](
   id: Id[Song]
  )(
    block: Song => T
  ): T =
    storageService.getSong(id) match
      case Some(song) =>
        block(song)
      case _ =>
        throw IllegalArgumentException(s"unknown song id $id")

  override def withArtist[T](
   id: Id[Artist]
  )(
    block: Artist => T
  ): T =
    storageService.getArtist(id) match
      case Some(artist) =>
        block(artist)
      case _ =>
        throw IllegalArgumentException(s"unknown artist id $id")

  override def withStreaming[T](
    id: Id[Streaming]
  )(
    block: Streaming => T
  ): T =
    storageService.getStreaming(id) match
      case Some(streaming) =>
        block(streaming)
      case _ =>
        throw IllegalArgumentException(s"unknown streaming id $id")

  private def updateArtist[T](
    id: Id[Artist]
  )(
    updateFn: Artist => Artist
  ): Unit =
    withArtist(id): artist =>
      storageService.storeArtist(
        id,
        updateFn(artist)
      )

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

  override def searchReleasedSongs(text: String, maxResults: Int): Seq[(Id[Song], Int)] =
    storageService.searchReleasedSongs(text, maxResults)

  override def getReleases: Seq[Id[Release]] =
    storageService.getReleases

  override def isSongStreamable(songId: Id[Song]): Boolean =
    storageService.isSongStreamable(songId)

  override def getSongs: Seq[Id[Song]] =
    storageService.getSongs

  override def streamedSongsReport(
    artistId: Id[Artist]
  ): String =
    storageService.getStreamings(artistId).map: streamingId =>
      val streaming: Streaming =
        storageService.getStreaming(streamingId).getOrElse:
          throw IllegalArgumentException(s"streaming not found for id $streamingId")
      val song: Song =
        storageService.getSong(streaming.songId).getOrElse:
          throw IllegalArgumentException(s"song not found for id ${streaming.songId}")
      s"\"${song.name}\"".padTo(30, ' ') +
        s"${streaming.streamTime}".padTo(12, ' ') +
        s"${streaming.lengthSeconds}".padTo(5, ' ') +
        (
          if streaming.isMonetizable then "£" else "-"
        )
    .mkString("\n")

  def requestArtistPayment(artistId: Id[Artist], date: LocalDate): Unit =
    updateArtist(artistId): artist =>
      artist.copy(
        paymentRequestDates = artist.paymentRequestDates :+ date
      )

  def recordArtistPayment(artistId: Id[Artist], date: LocalDate): Unit =
    updateArtist(artistId): artist =>
      artist.copy(
        paymentDates = artist.paymentDates :+ date
      )
  def takeReleaseOutOfDistribution(id: Id[Release]): Unit =
    updateRelease(id): release =>
      release.copy(
        inDistribution = false
      )