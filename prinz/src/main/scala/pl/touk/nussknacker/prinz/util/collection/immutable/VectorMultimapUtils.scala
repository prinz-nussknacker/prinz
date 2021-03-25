package pl.touk.nussknacker.prinz.util.collection.immutable

import scala.collection.mutable

object VectorMultimapUtils {

  implicit class VectorMultimapAsRowset[K, V](val inputsMap: VectorMultimap[K, V]) {

    private def toMutableMap[T](immutable: collection.Map[K, T]): mutable.Map[K, T] = mutable.Map() ++ immutable

    def mapRows[T](f: Map[K, V] => T): IndexedSeq[T] = {
      val totalTuples = inputsMap.map(_._2.length).max
      val iteratorsMap = toMutableMap(inputsMap.mapVectors(_.iterator))

      (1 to totalTuples) map (_ => {
        val row = iteratorsMap map {
          case (key, iter) if iter.hasNext => (key, iter.next())
        }
        f(row.toMap)
      })
    }
  }

}
