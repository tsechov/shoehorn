package filters

import sun.misc.BASE64Decoder
import play.api.mvc._
import scala.concurrent.Future
import play.api.Logger
import play.api.http.HeaderNames._
import services.production

object BasicAuthFilter extends Filter {

  lazy val userService = production userService

  private lazy val ajaxUnauthResult = Results.Unauthorized.withHeaders((AUTHENTICATION_REQUIRED_HEADER,
    """Basic realm="shoehorn basic authentication"""")
  )
  private lazy val browserUnauthResult = Results.Unauthorized.withHeaders((WWW_AUTHENTICATE,
    """Basic realm="shoehorn basic authentication""""))

  private lazy val outsidePages = List("assets", "version")
  //need the space at the end
  private lazy val basicSt = "basic "

  //This is needed if you are behind a load balancer or a proxy
  private def getUserIPAddress(request: RequestHeader): String = {
    return request.headers.get(X_FORWARDED_FOR).getOrElse(request.remoteAddress.toString)
  }

  private def logFailedAttempt(requestHeader: RequestHeader, username: Option[String]) = {
    val user = username.getOrElse("not known")
    Logger.warn(s"IP address [${getUserIPAddress(requestHeader)}], user: [$user] failed to log in, requested uri: ${requestHeader.uri}")
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

  private def fail(requestHeader: RequestHeader, username: Option[String] = None) = {
    logFailedAttempt(requestHeader, username)
    Future.successful(if (requestHeader.headers.keys.contains("X-Requested-With")) ajaxUnauthResult else browserUnauthResult)
  }

  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])(requestHeader: RequestHeader) = {
    if (isOutsideSecurityRealm(requestHeader)) {
      nextFilter(requestHeader)
    } else {

      requestHeader.headers.get(AUTHORIZATION).map {
        basicAuth =>
          decodeBasicAuth(basicAuth) match {
            case Some((user, pass)) => {
              if (userService.authenticate(user, pass))

                nextFilter(requestHeader.copy(tags = requestHeader.tags + (AUTHENTICATED_USER -> user)))
              else
                fail(requestHeader, Some(user))
            }

            case _ => fail(requestHeader)
          }

      }.getOrElse {
        fail(requestHeader)
      }
    }

  }
}