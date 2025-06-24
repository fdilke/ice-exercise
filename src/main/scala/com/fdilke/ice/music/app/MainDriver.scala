package com.fdilke.ice.music.app

import com.fdilke.ice.music.api.{MusicDistributionSystem, MusicStorageService}
import com.fdilke.ice.music.app.MainDriver.{afternoonPicnic, teaBoys}
import com.fdilke.ice.music.domain.{Artist, Id, Release, Song, Streaming}
import com.fdilke.ice.music.impl.{LocalMusicStorageService, PrototypeMusicDistributionSystem}

import java.time.LocalDate

object MainDriver extends App:
  println("ICE exercise modelling a Music Distribution System (MDS)")
  val storageService: MusicStorageService =
    new LocalMusicStorageService
  val mds: MusicDistributionSystem = 
    new PrototypeMusicDistributionSystem(storageService)
  println("Instantiated an MDS")

  val teaBoys: Id[Artist] =
    mds.storeArtist:
      Artist(
        name = "The Clueless Tea Boys",
        categories = Seq("Light Jazz", "Modern", "R&B")
      )

  println(s"added an artist: $teaBoys")

  val oneLump: Id[Release] =
    mds.storeRelease:
      Release(
        name = "One Lump Or Two?",
        artist = teaBoys,
        description = "album"
      )

  println(s"added a release: $oneLump")

  val Seq(teacups, sugarTongs, passTheStrainer, biscuits, madeira, oneLumpSong, madAbout): Seq[Id[Song]] =
    Seq(
      Song("Teacups", 120),
      Song("Sugar Tongs", 240),
      Song("Pass The Strainer", 300),
      Song("Bath Oliver Biscuits", 360),
      Song("Madeira Cake", 600),
      Song("One Lump Or Two?", 420),
      Song("Mad About the Teapot", 280)
    ).map:
      mds.storeSong

  mds.addSongsToRelease(oneLump, teacups, sugarTongs, passTheStrainer, biscuits, madeira, oneLumpSong, madAbout)

  println(s"added songs: $teacups, $sugarTongs, $passTheStrainer to the release")

  val proposedReleaseDate: LocalDate =
    LocalDate.of(2024, 8, 2)
  mds.proposeReleaseDate(oneLump, proposedReleaseDate)

  println(s"Proposed a release date for $oneLump of $proposedReleaseDate")

  mds.withRelease(oneLump): r =>
    assert:
      r.proposedReleaseDate.isDefined
    assert:
      r.proposedReleaseDate.get == proposedReleaseDate
  mds.agreeReleaseDate(oneLump)
  println(s"Agreed release date for $oneLump")

  val afternoonPicnic: Id[Release] =
    mds.storeRelease:
      Release(
        name = "Afternoon Picnic",
        artist = teaBoys,
        description = "EP"
      )

  println(s"added a release: $afternoonPicnic")

  val Seq(crumpets, crumpetsDisco): Seq[Id[Song]] =
    Seq(
      Song("Crumpets", 160),
      Song("Crumpets (Disco Remix)", 220)
    ).map:
      mds.storeSong

  mds.addSongsToRelease(oneLump, teacups, sugarTongs, passTheStrainer)

  println(s"added songs $crumpets, $crumpetsDisco to $afternoonPicnic")

  println(s"list of releases, streamable or not:")
  for
    releaseId <- mds.getReleases
  do
    mds.withRelease(releaseId): release =>
      println(
        s"- \"${release.name}\"".padTo(30, ' ') +
        (
          if release.isStreamable then "YES" else "NO"
        )
      )

  println(s"list of songs/lengths, streamable or not:")
  for
    songId <- mds.getSongs
  do
    mds.withSong(songId): song =>
      println(
        s"- \"${song.name}\"".padTo(30, ' ') +
        s"\t${song.lengthSeconds.toString.padTo(10, ' ')}" +
          (
            if mds.isSongStreamable(songId) then "YES" else "NO"
          )
      )

  val searchTerm: String =
    "Madeira"
  println(s"searching on \"$searchTerm\", results (name/length/distance):")
  for
    (songId, distance) <- mds.searchReleasedSongs(searchTerm, 3)
  do
    val song: Song =
      mds.getSong(songId).getOrElse:
        throw IllegalArgumentException("song not found")
    println(
      s"- \"${song.name}\"".padTo(20, ' ') +
      s"\t${song.lengthSeconds.toString.padTo(10, ' ')}" +
      s"$distance"
    )

  println("Recording some streaming of released songs")
  val storedStreams: Seq[Id[Streaming]] =
    Seq(
      Streaming(teacups, 3, LocalDate.of(2024, 8, 3)),
      Streaming(passTheStrainer, 70, LocalDate.of(2024, 8, 4)),
      Streaming(passTheStrainer, 7, LocalDate.of(2024, 8, 5)),
      Streaming(madeira, 27, LocalDate.of(2024, 8, 6)),
      Streaming(oneLumpSong, 120, LocalDate.of(2024, 8, 7)),
      Streaming(passTheStrainer, 75, LocalDate.of(2024, 8, 8))
    ).map:
      mds.storeStreaming
  println(s"Recorded streams: ${storedStreams.mkString(",")}")

  println("Streamed songs report:\t(name/date/length/monetizable)\n" +
    mds.streamedSongsReport(teaBoys)
  )
  
  
