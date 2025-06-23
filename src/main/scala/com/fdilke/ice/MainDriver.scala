package com.fdilke.ice

import com.fdilke.ice.MainDriver.teaBoys
import com.fdilke.ice.music.domain.{Artist, Release, Song}
import com.fdilke.ice.music.local.LocalMusicStorageService
import com.fdilke.ice.music.{ArtistId, MusicDistributionSystem, MusicStorageService, ReleaseId, SongId}

object MainDriver extends App:
  println("ICE exercise modelling a Music Distribution System (MDS)")
  val storageService: MusicStorageService =
    new LocalMusicStorageService
  val mds: MusicDistributionSystem = 
    new MusicDistributionSystem(storageService)
  println("Instantiated an MDS")

  val teaBoys: ArtistId =
    mds.storeArtist:
      Artist(
        name = "The Clueless Tea Boys",
        categories = Seq("Light Jazz", "Modern", "R&B")
      )

  println(s"added an artist: $teaBoys")

  val oneLump: ReleaseId =
    mds.storeRelease:
      Release(
        name = "One Lump Or Two?",
        artist = teaBoys,
        description = "album"
      )

  println(s"added a release: $oneLump")

  val Seq(teacups, sugarTongs, passTheStrainer): Seq[SongId] =
    Seq("Teacups", "Sugar Tongs", "Pass The Strainer").map: songName =>
      mds.storeSong:
        Song(
          name = songName
        )

  mds.addSongsToRelease(oneLump, teacups, sugarTongs, passTheStrainer)

  println(s"added songs: $teacups, $sugarTongs, $passTheStrainer to the release")