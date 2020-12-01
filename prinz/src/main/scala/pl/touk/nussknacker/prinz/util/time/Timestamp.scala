package pl.touk.nussknacker.prinz.util.time

import java.time.Instant

object Timestamp {

  def instant(value: String): Instant = Instant.ofEpochMilli(value.toLong)
}
