package com.fdilke.ice.music.app

import com.fdilke.ice.music.api.{MusicDistributionSystem, MusicStorageService}
import com.fdilke.ice.music.app.MainDriver.{afternoonPicnic, teaBoys}
import com.fdilke.ice.music.domain.{Artist, Id, Release, Song}
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

  val Seq(teacups, sugarTongs, passTheStrainer): Seq[Id[Song]] =
    Seq(
      Song("Teacups", 120),
      Song("Sugar Tongs", 240),
      Song("Pass The Strainer", 300)
    ).map:
      mds.storeSong

  mds.addSongsToRelease(oneLump, teacups, sugarTongs, passTheStrainer)

  println(s"added songs: $teacups, $sugarTongs, $passTheStrainer to the release")

  val proposedReleaseDate: LocalDate =
    LocalDate.of(2025, 8, 2)
  mds.proposeReleaseDate(oneLump, proposedReleaseDate)

  println(s"Proposed a release date for $oneLump of $proposedReleaseDate")

  {
    mds.withRelease(oneLump): r =>
      assert:
        r.proposedReleaseDate.isDefined
      assert:
        r.proposedReleaseDate.get == proposedReleaseDate
    mds.agreeReleaseDate(oneLump)
    println(s"Agreed release date for $oneLump")
  }

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

  println(s"searching on \"Crumpets\", results:")
  for
    (songId, distance) <- mds.searchSongs("Crumpets", 3)
  do
    val song: Song =
      mds.getSong(songId).getOrElse:
        throw IllegalArgumentException("song not found")
    println(
      s"- \"${song.name}\"".padTo(20, ' ') +
      s"\t${song.lengthSeconds.toString.padTo(10, ' ')}" +
      s"$distance")
