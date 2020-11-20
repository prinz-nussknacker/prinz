package pl.touk.nussknacker.prinz.util.collection.immutable

import scala.collection._

object SetMultiMap {

  def empty[K, V](): SetMultiMap[K, V] =
    new SetMultiMap[K,V](Map.empty[K, Set[V]])

  def apply[K,V](elements: (K, V)*): SetMultiMap[K, V] = {
    val builder = new SetMultiMapBuilder[K, V]()
    builder ++= elements
    builder.result()
  }

  def apply[K,V](elements: Iterable[(K, V)]): SetMultiMap[K, V] = {
    val builder = new SetMultiMapBuilder[K, V]()
    builder ++= elements
    builder.result()
  }

  def apply[K,V](delegate: Map[K, Set[V]]): SetMultiMap[K, V] =
    new SetMultiMap[K, V](delegate)

  class SetMultiMapBuilder[K, V]() extends mutable.Builder[(K,V), SetMultiMap[K,V]] {
    private val elements = new mutable.HashMap[K, Set[V]]

    override def +=(element: (K, V)): SetMultiMapBuilder.this.type = {
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

    override def result(): SetMultiMap[K, V] = {
      new SetMultiMap[K,V](Map.empty[K, Set[V]] ++ elements)
    }
  }

}

class SetMultiMap[K,V](private val delegate: Map[K, Set[V]]) {

  protected def createSet(values: V*): Set[V] = Set(values: _*)

  def add(key: K, value: V): SetMultiMap[K, V] = {
    get(key) match {
      case None =>
        val newSet = createSet(value)
        new SetMultiMap(delegate.updated(key, newSet))
      case Some(oldSet) =>
        val newSet = oldSet + value
        new SetMultiMap(delegate.updated(key, newSet))
    }
  }

  def addAll(elements: Iterable[(K, V)]): SetMultiMap[K, V] = {
    val result = new SetMultiMap(delegate)
    elements.foreach( tuple => result.add(tuple._1, tuple._2))
    result
  }

  def remove(key: K, value: V): SetMultiMap[K, V] = {
    get(key) match {
      case None => this
      case Some(oldSet) =>
        val newSet = oldSet - value
        if (newSet.nonEmpty)
          new SetMultiMap(delegate.updated(key, newSet))
        else
          new SetMultiMap(delegate - key)
    }
  }

  def removeAll(elements: Iterable[(K, V)]): SetMultiMap[K, V] = {
    val result = new SetMultiMap(delegate)
    elements.foreach( tuple => result.remove(tuple._1, tuple._2))
    result
  }

  def exists(key: K, p: V => Boolean): Boolean = {
      get(key) match {
        case None => false
        case Some(set) => set exists p
      }
    }

  def get(key: K): Option[Set[V]] = delegate.get(key)

  def +(kv: (K, Set[V])): SetMultiMap[K, V] = new SetMultiMap(delegate + kv)

  def ++(xs: GenTraversableOnce[(K, Set[V])]): SetMultiMap[K, V] =
    new SetMultiMap(delegate ++ xs)

  def -(key: K): SetMultiMap[K, V] = new SetMultiMap(delegate - key)

  def --(xs: GenTraversableOnce[K]): SetMultiMap[K, V] =
    new SetMultiMap(delegate -- xs)

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

  override def equals(obj: Any): Boolean = {
    obj match {
    case m: SetMultiMap[K, V] => this.delegate.equals(m.delegate)
    case _ => false
    }
  }
}