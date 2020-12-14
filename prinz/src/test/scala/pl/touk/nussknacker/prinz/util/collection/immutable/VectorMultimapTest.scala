package pl.touk.nussknacker.prinz.util.collection.immutable

import pl.touk.nussknacker.prinz.UnitTest

class VectorMultimapTest extends UnitTest {

  "VectorMultimap" should "be initialized as an empty structure" in {
    val emptyMap = VectorMultimap.empty[Int, Int]
    val noArgsMap = VectorMultimap[Int, Int]()

    emptyMap.size should equal (0)
    emptyMap.isEmpty shouldBe true
    noArgsMap.size should equal (0)
    noArgsMap.isEmpty shouldBe true
  }

  it should "allow adding multiple elements to a single key" in {
    val map = VectorMultimap[Int, Int]()
      .add(1, 1)
      .add(1, 2)
      .add(2, 1)
      .add(2, 2)

    map.size should equal (4)
    map.get(1) should equal (Some(Vector(1, 2)))
    map.get(2) should equal (Some(Vector(1, 2)))
  }

  it should "allow adding all elements from an iterable" in {
    val map = VectorMultimap[Int, Int]((1, 1), (1, 2))
      .addAll(List((1, 3), (1, 4)))
      .addAll(List((2, 1), (2, 2)))

    map.size should equal (6)
    map.get(1) should equal (Some(Vector(1, 2, 3, 4)))
    map.get(2) should equal (Some(Vector(1, 2)))
  }


  it should "return number of key-value pairs in size() method" in {
    val map = VectorMultimap[Int, Int]((1, 1), (1, 2))
    val extendedMap = map
      .add(1, 3)
      .add(2, 1)
    val shrunkMap = extendedMap - 1

    map.size should equal (2)
    extendedMap.size should equal (4)
    shrunkMap.size should equal (1)
  }
}