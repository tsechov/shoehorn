package controllers

import scala.util.{Failure, Success, Try}
import reactivemongo.core.commands.LastError
import reactivemongo.core.errors.ReactiveMongoException



class Option2TryWrapper[A](option: Option[A])  {
   def orFailWithMessage(msg: String): Try[A] = {
    option match {
      case Some(a) => Success(a)
      case None => Failure(new IllegalArgumentException(msg))
    }
  }
}


object OptionWrapperImplicits {
  implicit def Option2Wrapper[A](option: Option[A]) = new Option2TryWrapper[A](option)
}


class LastError2TryWrapper(lastError: LastError)  {
   def orFail: Try[LastError] = {
    if (lastError.ok) {
      Success(lastError)
    } else {
      Failure(ReactiveMongoException(lastError.stringify))
    }
  }
}


object LastErrorWrapperImplicits {
  implicit def LastError2TryWrapper(lastError: LastError) = new LastError2TryWrapper(lastError)
}