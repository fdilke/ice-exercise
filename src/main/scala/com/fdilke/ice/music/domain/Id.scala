package com.fdilke.ice.music.domain

import scala.reflect.ClassTag

case class Id[T: ClassTag](key: String):
  override def toString: String =
    val tag = summon[ClassTag[T]]
    s"Id[${tag.runtimeClass.getSimpleName}](\"$key\")"
