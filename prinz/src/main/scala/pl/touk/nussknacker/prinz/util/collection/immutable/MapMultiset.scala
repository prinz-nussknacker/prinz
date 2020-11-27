package pl.touk.nussknacker.prinz.util.collection.immutable

import scala.collection.mutable

class MapMultiset[T](private val delegate: Map[T, Int]) {

  def count(element: T): Int = if (contains(element)) {
      delegate(element)
    }
    else {
      0
    }

  def contains(element: T): Boolean = delegate.contains(element)

  def iterator: Iterator[T] =
    delegate.iterator.flatMap {
      case (k, n) =>
        currentIterator(k, n)
    }

  private def currentIterator(k: T, n: Int): Iterator[T] =
    new Iterator[T] {
      var remaining: Int = n

      def hasNext: Boolean = remaining > 0

      def next(): T = {
        remaining -= 1; k
      }
    }

  def add(element: T, count: Int = 1): MapMultiset[T] =
    if (count > 0) {
      MapMultiset(delegate.updated(element, delegate(element) + count))
    }
    else {
      this
    }

  def addAll(elements: Iterable[T]): MapMultiset[T] = {
    val created = new mutable.HashMap[T, Int].withDefaultValue(0) ++ delegate
    elements.foreach { element => {
      val count = created(element)
      created.update(element, count + 1)
      }
    }
    MapMultiset(Map.empty[T, Int].withDefaultValue(0) ++ created)
  }

  def remove(element: T, count: Int = 1): MapMultiset[T] =
    if (contains(element) && count > 0) {
      val newCount = delegate(element) - count

      if (newCount > 0) {
        MapMultiset(delegate.updated(element, newCount))
      }
      else {
        MapMultiset(delegate - element)
      }
    }
    else {
      this
    }

  def removeAll(elements: Iterable[T]): MapMultiset[T] = {
    val created = new mutable.HashMap[T, Int].withDefaultValue(0) ++ delegate
    elements.foreach { element => {
      val count = created(element)
      if (count > 0) {
        created.update(element, count - 1)
      }
      else {
        created.remove(element)
      }
      }
    }
    MapMultiset(Map.empty[T, Int].withDefaultValue(0) ++ created)
  }

  def -(key: T): MapMultiset[T] = MapMultiset(delegate - key)

  def keys: Iterable[T] = delegate.keys

  def isEmpty: Boolean = delegate.isEmpty

  def nonEmpty: Boolean = delegate.nonEmpty

  def size: Int = delegate.values.sum

  override def toString: String = {
    val sb = new StringBuilder(s"${getClass.getName}[\n")
    this.iterator.map{ el => s"\t$el" }.foreach(sb.append)
    sb.append("]").toString()
  }

  override def hashCode(): Int = delegate.hashCode()

  override def equals(obj: Any): Boolean =
    obj match {
      case m: MapMultiset[T] => this.delegate.equals(m.delegate)
      case _ => false
    }
}

object MapMultiset {

  def empty[T]: MapMultiset[T] =
    new MapMultiset[T](Map.empty[T, Int].withDefaultValue(0))

  def apply[T](elements: T*): MapMultiset[T] =
    buildCollection[T](elements)

  def apply[T](elements: Iterable[T]): MapMultiset[T] =
    buildCollection[T](elements)

  def apply[T](delegate: Map[T, Int]): MapMultiset[T] =
    new MapMultiset[T](delegate.filter{ case (_, count) => count > 0 })

  private def buildCollection[T](elements: Iterable[T]): MapMultiset[T] = {
    val builder = new MapMultisetBuilder[T]
    builder ++= elements
    builder.result()
  }

  private class MapMultisetBuilder[T] extends mutable.Builder[T, MapMultiset[T]] {
    private val elements = new mutable.HashMap[T, Int].withDefaultValue(0)

    override def +=(element: T): this.type = {
      val count = elements(element)
      elements.update(element, count + 1)
      this
    }

    override def clear(): Unit = elements.clear()

    override def result(): MapMultiset[T] =
      new MapMultiset[T](Map.empty[T, Int].withDefaultValue(0) ++ elements)
  }
}
