package pl.touk.nussknacker.prinz.util.collection.immutable

import scala.collection.mutable

class VectorMultimap[K, V](private val delegate: mutable.LinkedHashMap[K, Vector[V]]) {

  def add(key: K, value: V): VectorMultimap[K, V] = get(key) match {
    case None =>
      val newVector = Vector(value)
      val newMap = delegate.clone()
      newMap.update(key, newVector)
      VectorMultimap(newMap)
    case Some(oldVector) =>
      val newVector = oldVector :+ value
      val newMap = delegate.clone()
      newMap.update(key, newVector)
      VectorMultimap(newMap)
  }

  def addAll(elements: Iterable[(K, V)]): VectorMultimap[K, V] = {
    val created = delegate.clone()
    elements.foreach { case (key, value) =>
      created.get(key) match {
        case None => created(key) = Vector(value)
        case Some(vector) => created(key) = vector :+ value
      }
    }
    VectorMultimap(created)
  }

  def exists(key: K, p: V => Boolean): Boolean = get(key) match {
    case None => false
    case Some(vector) => vector exists p
  }

  def mapValues[B](f: V => B): VectorMultimap[K, B] = {
    val mapped = for (key <- keys.toList; value <- get(key).get)
      yield (key, f(value))
    VectorMultimap(mapped)
  }

  def contains(key: K, value: V): Boolean =
    exists(key, x => x equals value)

  def get(key: K): Option[Vector[V]] = delegate.get(key)

  def -(key: K): VectorMultimap[K, V] = VectorMultimap(delegate - key)

  def values: Iterable[Vector[V]] = delegate.values

  def keys: Iterable[K] = delegate.keys

  def isEmpty: Boolean = delegate.isEmpty

  def nonEmpty: Boolean = delegate.nonEmpty

  def size: Int = delegate.values.map(_.size).sum

  override def toString: String = {
    val sb = new StringBuilder(s"${getClass.getName}[\n")
    delegate.map { case (k, vs) => s"\t$k -> $vs\n" }.foreach(sb.append)
    sb.append("]").toString()
  }

  override def hashCode(): Int = delegate.hashCode()

  override def equals(obj: Any): Boolean =
    obj match {
      case m: VectorMultimap[K, V] => this.delegate.equals(m.delegate)
      case _ => false
    }
}

object VectorMultimap {

  def empty[K, V]: VectorMultimap[K, V] =
    new VectorMultimap[K,V](mutable.LinkedHashMap.empty[K, Vector[V]])

  def apply[K, V](elements: (K, V)*): VectorMultimap[K, V] =
    buildCollection(elements)

  def apply[K, V](elements: Iterable[(K, V)]): VectorMultimap[K, V] =
    buildCollection(elements)

  def apply[K, V](delegate: mutable.LinkedHashMap[K, Vector[V]]): VectorMultimap[K, V] =
    new VectorMultimap[K, V](delegate)

  private def buildCollection[K, V](elements: Iterable[(K, V)]): VectorMultimap[K, V] = {
    val builder = new VectorMultimapBuilder[K, V]
    builder ++= elements
    builder.result()
  }

  private class VectorMultimapBuilder[K, V] extends mutable.Builder[(K, V), VectorMultimap[K, V]] {
    private val elements = new mutable.LinkedHashMap[K, Vector[V]]

    override def +=(element: (K, V)): this.type = {
      element match {
        case (key, value) =>
          elements.get(key) match {
            case None => elements(key) = Vector(value)
            case Some(vector) => elements(key) = vector :+ value
          }
      }
      this
    }

    override def clear(): Unit = elements.clear()

    override def result(): VectorMultimap[K, V] =
      new VectorMultimap[K, V](elements)
  }
}
