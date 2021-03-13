package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.Future

trait ParameterProvider {

  //   tę metodę wywołujemy na etapie przygotowywania sygnatury.
  //   Alternatywą byłoby także aby ta metoda zwracała po prostu
  //   ModelSignature - i wtedy cały trait byłby czymś w stylu "ModelTransformer"?
  def signatureChange(modelSignature: ModelSignature): Future[SignatureChange]

  def provideParameters(originalParameters: InputData): Future[InputData]
  //tutaj na wejściu może tylko extraFields z SignatureChange? a jeśli na
  // wejściu/wyjściu są wszystkie parametry, to można to wręcz nazwać signatureChange...

  type InputData = VectorMultimap[String, AnyRef] // a może tutaj coś ładniejszego?}
}

case class SignatureChange(providedParameters: List[SignatureField], extraFields: List[SignatureField])
