package pl.touk.nussknacker.prinz.util.collection.immutable

import scala.collection.mutable

object VectorMultimapUtils {

  implicit class VectorMultimapAsRowset[K, V](val inputsMap: VectorMultimap[K, V]) {
    def forEachRow[T](f: Map[K, V] => T): IndexedSeq[T] = {
      val totalTuples = inputsMap.map(_._2.length).max
      val iteratorsMap = mutable.Map() ++ inputsMap.mapVectors(_.iterator)

      (1 to totalTuples) map (_ => {
        val row = iteratorsMap map {
          case (key, iter) if iter.hasNext => (key, iter.next())
        }
        f(row.toMap)
      })
    }
  }

}
