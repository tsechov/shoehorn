package filters

import sun.misc.BASE64Decoder
import play.api.mvc._
import scala.concurrent.Future
import play.api.Logger
import play.api.http.HeaderNames._

case class BasicAuthFilter() extends Filter {
  private lazy val ajaxUnauthResult = Results.Unauthorized.withHeaders((AUTHENTICATION_REQUIRED_HEADER,
    "Basic realm=\"shoehorn basic authentication\"")
  )
  private lazy val browserUnauthResult = Results.Unauthorized.withHeaders((WWW_AUTHENTICATE,
    "Basic realm=\"shoehorn basic authentication\""))
  private lazy val passwordRequired = true
  private lazy val username = "admin"
  private lazy val password = "xxxx"
  private lazy val outsidePages = List("assets", "version")
  //need the space at the end
  private lazy val basicSt = "basic "

  //This is needed if you are behind a load balancer or a proxy
  private def getUserIPAddress(request: RequestHeader): String = {
    return request.headers.get(X_FORWARDED_FOR).getOrElse(request.remoteAddress.toString)
  }

  private def logFailedAttempt(requestHeader: RequestHeader) = {
    Logger.warn(s"IP address ${getUserIPAddress(requestHeader)} failed to log in, requested uri: ${requestHeader.uri}")
  }

  private def decodeBasicAuth(auth: String): Option[(String, String)] = {
    if (auth.length() < basicSt.length()) {
      return None
    }
    val basicReqSt = auth.substring(0, basicSt.length())
    if (basicReqSt.toLowerCase() != basicSt) {
      return None
    }
    val basicAuthSt = auth.replaceFirst(basicReqSt, "")
    //BESE64Decoder is not thread safe, don't make it a field of this object
    val decoder = new BASE64Decoder()
    val decodedAuthSt = new String(decoder.decodeBuffer(basicAuthSt), "UTF-8")
    val usernamePassword = decodedAuthSt.split(":")
    if (usernamePassword.length >= 2) {
      //account for ":" in passwords
      Some(usernamePassword(0), usernamePassword.splitAt(1)._2.mkString)
    } else {
      None
    }
  }

  private def isOutsideSecurityRealm(requestHeader: RequestHeader): Boolean = {
    val reqURI = requestHeader.uri
    if (reqURI.length() > 0) {
      //remove the first "/" in the uri
      val taken = reqURI.substring(1).takeWhile(_ != '/')

      outsidePages.contains(taken)

    } else {
      false
    }
  }

  private def fail(requestHeader: RequestHeader) = {
    logFailedAttempt(requestHeader)
    requestHeader.headers.get("X-Requested-With").map(
      _ => Future.successful(ajaxUnauthResult)
    ).getOrElse {
      Future.successful(browserUnauthResult)
    }

  }

  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])(requestHeader: RequestHeader):
  Future[SimpleResult] = {
    if (!passwordRequired || isOutsideSecurityRealm(requestHeader)) {
      nextFilter(requestHeader)
    } else {

      requestHeader.headers.get(AUTHORIZATION).map {
        basicAuth =>
          decodeBasicAuth(basicAuth) match {
            case Some((user, pass)) if (username == user && password == pass) => {
              Logger.debug(s"logging in with $user : ***")
              nextFilter(requestHeader)
            }
            case _ => fail(requestHeader)
          }

      }.getOrElse({
        fail(requestHeader)
      })
    }

  }
}