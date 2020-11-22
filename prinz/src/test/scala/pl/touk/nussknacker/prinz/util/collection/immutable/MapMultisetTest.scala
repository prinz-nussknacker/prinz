package pl.touk.nussknacker.prinz.util.collection.immutable

import pl.touk.nussknacker.prinz.UnitTest

import scala.collection.mutable.ListBuffer

class MapMultisetTest extends UnitTest {

  "MapMultiSet" should "be initialized as an empty structure" in {
    val emptySet = MapMultiset.empty[Int]
    val noArgsSet = MapMultiset[Int]()

    emptySet.size should equal (0)
    emptySet.isEmpty shouldBe true
    noArgsSet.size should equal (0)
    noArgsSet.isEmpty shouldBe true
  }

  it should "allow adding multiple elements with the same value" in {
    val set = MapMultiset[Int]()
      .add(1)
      .add(1)
      .add(1)
      .add(1)

    set.size should equal (4)
    set.count(1) should equal (4)
  }

  it should "allow adding all elements from an iterable" in {
    val set = MapMultiset[Int](1, 2)
      .addAll(List(1, 2))
      .addAll(List(3, 4))

    set.size should equal (6)
    set.count(1) should equal (2)
    set.count(2) should equal (2)
    set.count(3) should equal (1)
    set.count(4) should equal (1)
  }

  it should "allow removing single elements" in {
    val set = MapMultiset[Int](1, 2, 3, 4)
      .remove(1)
      .remove(3)

    set.size should equal (2)
    set.count(1) should equal (0)
    set.count(2) should equal (1)
    set.count(3) should equal (0)
    set.count(4) should equal (1)
  }

  it should "allow removing all elements from an iterable" in {
    val set = MapMultiset[Int](1, 2, 3, 4)
      .removeAll(List(1, 3))

    set.size should equal (2)
    set.count(1) should equal (0)
    set.count(2) should equal (1)
    set.count(3) should equal (0)
    set.count(4) should equal (1)
  }

  it should "return number of elements with repetitions in size() method" in {
    val set = MapMultiset[Int](1, 2)
    val extendedSet = set
      .add(1)
      .add(2)
    val shrunkSet = extendedSet - 1

    set.size should equal (2)
    extendedSet.size should equal (4)
    shrunkSet.size should equal (2)
  }

  it should "allow iterating with repeated elements" in {
    val set = MapMultiset[Int](1, 1, 1, 2, 2, 3)
    val it = set.iterator
    val elements = new ListBuffer[Int]()
    it.foreach(elem => elements += elem)

    set.size should equal(6)
    set.count(1) should equal(3)
    set.count(2) should equal(2)
    set.count(3) should equal(1)
    elements.toList.sorted should equal(List(1, 1, 1, 2, 2, 3))
  }
}