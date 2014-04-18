// Copyright (C) 2014 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package reactivemongo.extensions.dao

import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.time.SpanSugar._
import reactivemongo.bson._
import reactivemongo.bson.Macros.Options.Verbose
import reactivemongo.extensions.model.DummyModel
import scala.concurrent.ExecutionContext.Implicits.global

class BsonDaoSpec extends FlatSpec with Matchers with ScalaFutures {

  override implicit def patienceConfig = PatienceConfig(timeout = 10 seconds, interval = 1 seconds)

  "A BsonDao" should "insert bson document" in {
    val dummyModel = DummyModel(ad = "haydar", soyad = "cabbar")

    val futureResult = for {
      insertResult <- DummyBsonDao.insert(dummyModel)
      maybeDummyModel <- DummyBsonDao.findById(dummyModel.id)
      count <- DummyBsonDao.count(Some(BSONDocument("id" -> dummyModel.id)))
    } yield (maybeDummyModel, count)

    whenReady(futureResult) {
      case (maybeDummyModel, count) =>
        maybeDummyModel should be('defined)
        maybeDummyModel.get.id should be(dummyModel.id)
        count should be(1)
    }
  }

}
