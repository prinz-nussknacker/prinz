package pl.touk.nussknacker.prinz.util.collection

trait MultiMap[K, V] {

  def add(key: K, value: Iterable[V]): MultiMap[K, V]
}
