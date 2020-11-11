package pl.touk.nussknacker.prinz

import org.scalatest.{GivenWhenThen, Inside, Inspectors, OptionValues}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

abstract class UnitTest extends AnyFlatSpec with should.Matchers with
  OptionValues with Inside with Inspectors
