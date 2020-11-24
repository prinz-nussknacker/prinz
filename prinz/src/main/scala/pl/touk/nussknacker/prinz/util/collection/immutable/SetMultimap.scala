package pl.touk.nussknacker.prinz.util.collection.immutable

import scala.collection._

class SetMultimap[K, V](private val delegate: Map[K, Set[V]]) {

  protected def createSet(values: V*): Set[V] = Set(values: _*)

  def add(key: K, value: V): SetMultimap[K, V] = {
    get(key) match {
      case None =>
        val newSet = createSet(value)
        SetMultimap(delegate.updated(key, newSet))
      case Some(oldSet) =>
        val newSet = oldSet + value
        SetMultimap(delegate.updated(key, newSet))
    }
  }

  def addAll(elements: Iterable[(K, V)]): SetMultimap[K, V] = {
    val created = new mutable.HashMap[K, Set[V]] ++ delegate
    elements.foreach { case (key, value) =>
      created.get(key) match {
        case None => created(key) = Set(value)
        case Some(set) => created(key) = set + value
      }
    }
    SetMultimap(Map.empty[K, Set[V]] ++ created)
  }

  def remove(key: K, value: V): SetMultimap[K, V] = {
    get(key) match {
      case None => this
      case Some(oldSet) =>
        val newSet = oldSet - value
        if (newSet.nonEmpty)
          SetMultimap(delegate.updated(key, newSet))
        else
          SetMultimap(delegate - key)
    }
  }

  def removeAll(elements: Iterable[(K, V)]): SetMultimap[K, V] = {
    val created = new mutable.HashMap[K, Set[V]] ++ delegate
    elements.foreach { case (key, value) =>
      created.get(key) match {
        case Some(set) => created(key) = set - value
      }
    }
    SetMultimap(Map.empty[K, Set[V]] ++ created)
  }

  def exists(key: K, p: V => Boolean): Boolean = {
    get(key) match {
      case None => false
      case Some(set) => set exists p
    }
  }

  def contains(key: K, value: V): Boolean =
    exists(key, x => x equals value)

  def get(key: K): Option[Set[V]] = delegate.get(key)

  def -(key: K): SetMultimap[K, V] = SetMultimap(delegate - key)

  def values: Iterable[Set[V]] = delegate.values

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
      case m: SetMultimap[K, V] => this.delegate.equals(m.delegate)
      case _ => false
    }
}

object SetMultimap {

  def empty[K, V]: SetMultimap[K, V] =
    new SetMultimap[K,V](Map.empty[K, Set[V]])

  def apply[K, V](elements: (K, V)*): SetMultimap[K, V] =
    buildCollection(elements)

  def apply[K, V](elements: Iterable[(K, V)]): SetMultimap[K, V] =
    buildCollection(elements)

  def apply[K, V](delegate: Map[K, Set[V]]): SetMultimap[K, V] =
    new SetMultimap[K, V](delegate)

  private def buildCollection[K, V](elements: Iterable[(K, V)]): SetMultimap[K, V] = {
    val builder = new SetMultimapBuilder[K, V]
    builder ++= elements
    builder.result()
  }

  private class SetMultimapBuilder[K, V] extends mutable.Builder[(K, V), SetMultimap[K, V]] {
    private val elements = new mutable.HashMap[K, Set[V]]

    override def +=(element: (K, V)): this.type = {
      element match {
        case (key, value) =>
          elements.get(key) match {
            case None => elements(key) = Set(value)
            case Some(set) => elements(key) = set + value
          }
      }
      this
    }

    override def clear(): Unit = elements.clear()

    override def result(): SetMultimap[K, V] =
      new SetMultimap[K, V](Map.empty[K, Set[V]] ++ elements)
  }
}
