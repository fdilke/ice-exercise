package com.fdilke.ice

import com.fdilke.ice.music.MusicDistributionSystem

object MainDriver extends App:
  println("ICE exercise modelling a Music Distribution System (MDS)")
  
  val mds: MusicDistributionSystem = 
    new MusicDistributionSystem
  println("Instantiated an MDS")
  

