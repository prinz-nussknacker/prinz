package pl.touk.nussknacker.prinz.pmml.model

import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

object VectorMultimapUtils {
  implicit class VectorMultimapAsRowset[K, V](val inputMap: VectorMultimap[K, V]) {
    def forEachRow[T](f: Map[K, V] => T) : IndexedSeq[T] = {
      val totalTuples = inputMap.map(_._2.length).max
      val iteratorMap = inputMap.mapVectors(_.iterator)

      (1 to totalTuples) map (_ => {
        val row = iteratorMap map {
          case (key, iter) if iter.hasNext => (key, iter.next())
        }
        f(row.toMap)
      })
    }
  }
}
