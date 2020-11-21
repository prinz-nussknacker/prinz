package pl.touk.nussknacker.prinz.util.collection.immutable

import pl.touk.nussknacker.prinz.util.collection.MultiMap
import pl.touk.nussknacker.prinz.util.collection.immutable.MultiTreeMap.collectValuesToList

import scala.collection.immutable.TreeMap

// TODO make it implement some better multimap interface and make it faster by not keeping data in lists

class MultiTreeMap[K, V] private (init: Option[Map[K, List[V]]] = None)(implicit val ordering: Ordering[K])
  extends MultiMap[K, V]
     with Serializable {

  private val delegate: Map[K, List[V]] = init.getOrElse(new TreeMap[K, List[V]]()(ordering))

  def add(key: K, value: V): MultiTreeMap[K, V] = {
    val newElem = delegate.get(key) match {
      case Some(list) => value::list
      case None => List(value)
    }
    MultiTreeMap(delegate + (key -> newElem))
  }

  override def add(key: K, value: Iterable[V]): MultiTreeMap[K, V] = {
    val newElem = delegate.get(key) match {
      case Some(list) => value.toList:::list
      case None => value.toList
    }
    MultiTreeMap(delegate + (key -> newElem))
  }

  def add(keyValue: (K, V)): MultiTreeMap[K, V] = this.add(keyValue._1, keyValue._2)

  def addAll(init: Iterable[(K, V)]): MultiTreeMap[K, V] = {
    val created = MultiTreeMap[K, V](delegate)
    val grouped: Map[K, List[V]] = init
      .groupBy(_._1)
      .mapValues[List[V]](collectValuesToList)
    grouped.foreach { case (k, vs) => created.add(k, vs) }
    created
  }

  def remove(key: K, value: V): MultiTreeMap[K, V] =
    throw new NotImplementedError("Remove operation on MultiMap not implemented. Implement MultiSet first.")

  def size: Int = delegate.
    values.
    map(_.size).
    sum

  override def toString: String = {
    val sb = new StringBuilder(s"${getClass.getName}[\n")
    delegate.map { case (k, vs) => s"\t$k -> $vs\n" }.foreach(sb.append)
    sb.append("]").toString()
  }

  override def hashCode(): Int = this.delegate.hashCode()

  override def equals(obj: Any): Boolean = obj match {
    case m: MultiTreeMap[K, V] => this.delegate.equals(m.delegate)
    case _ => false
  }
}

object MultiTreeMap {

  private def apply[K, V](init: Map[K, List[V]])(implicit ord: Ordering[K]) = new MultiTreeMap[K, V](Some(init))

  def apply[K, V](init: Iterable[(K, V)])(implicit ord: Ordering[K]): MultiTreeMap[K, V] = new MultiTreeMap[K, V]().addAll(init)

  def apply[K, V](init: (K, V)*)(implicit ord: Ordering[K]): MultiTreeMap[K, V] = new MultiTreeMap[K, V]().addAll(init)

  private def collectValuesToList[K, V](value: Iterable[(K, V)]): List[V] =
    value.map(_._2).toList
}
