package pl.touk.nussknacker.prinz.util.collection.immutable

import pl.touk.nussknacker.prinz.UnitTest

class SetMultiMapTest extends UnitTest {

  // To write test matcher when good MultiTreeMap implemented see to https://www.scalatest.org/user_guide/using_matchers

  "MultiTreeMap" should "be initialized as empty structure" in {
    val map = SetMultiMap[Int, Int]()

    map.size should equal (0)
  }

  it should "allow to add many elements to single key" in {
    val map = SetMultiMap[Int, Int]()
      .add(1, 1)
      .add(1, 2)
      .add(1, 3)
      .add(1, 4)

    map.size should equal (4)
    // TODO check elements for keys when get implemented
  }

  it should "keep single object reference under single key" in {

  }

  it should "return number of key-value pairs in size() method" in {

  }

  it should "remove values for specified key with removeAll(key) method" in {

  }
}
