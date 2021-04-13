package pl.touk.nussknacker.prinz

import org.scalatest.{Inside, Inspectors, OptionValues}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

trait UnitTest extends AnyFlatSpecLike
  with should.Matchers
  with OptionValues
  with Inside
  with Inspectors
