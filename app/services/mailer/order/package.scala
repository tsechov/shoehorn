package services.mailer.order


package object support {

  case class OrderMailBody(customerName: String, link: String, orderNumber: String)

  case class Mail(to: List[String],
                  cc: Seq[String] = Seq.empty,
                  bcc: Seq[String] = Seq.empty,
                  subject: String,
                  message: String,
                  richMessage: Option[String] = None,
                  attachment: Option[java.io.File] = None)

  trait MailText {
    def asText(params: OrderMailBody): String

    def asHtml(params: OrderMailBody): String
  }

  object create extends MailText {
    def asText(params: OrderMailBody) = {
      s"""
        |Kedves ${params.customerName}!
        |
        |Rendszerünkben az alábbi linken található, ${params.orderNumber} számú, Ön által leadott megrendelés került rögzítésre a 2015. Tavasz-Nyár szezonra.
        |
        |${params.link}
        |
        |Amennyiben bármilyen eltérést tapasztal a rögzített adatokban az Ön által leadott rendeléshez képest, úgy azt kérem minél előbb jelezze üzletkötőjének.
        |
        |/Tájékoztatásként:
        |A szandálok talpának egységesítése miatt előfordulhat, hogy a papír alapon felvett rendelésében eltér a modellszám utolsó 3 számjegye a jelen levélhez csatolt elektronikusan rögzített rendeléséhez képest. Ez nem jelent eltérést a kiválasztott modellt illetően./
        |Az ár tájékoztató jellegű, nem tartalmazza az esetlegesen fennálló kedvezményeket.
        |
        |Megrendelését köszönjük, örülünk, hogy Vevőink között tudhatjuk Önt!
        |
        |Üdvözlettel:
        |Szamos Kft.
        |
        |Ez egy automatikusan generált e-mail, kérem ne válaszoljon rá.
      """.stripMargin
    }

    def asHtml(params: OrderMailBody) = {
      s"""
<p>Kedves ${params.customerName}!</p>

<p>Rendszer&uuml;nkben az al&aacute;bbi&nbsp;linken&nbsp;tal&aacute;lhat&oacute;, ${params.orderNumber} sz&aacute;m&uacute;, &Ouml;n &aacute;ltal leadott megrendel&eacute;s ker&uuml;lt r&ouml;gz&iacute;t&eacute;sre a 2015. Tavasz-Ny&aacute;r szezonra.</p>

<p><a href="${params.link}">${params.orderNumber}</a></p>

<p>Amennyiben b&aacute;rmilyen elt&eacute;r&eacute;st tapasztal a r&ouml;gz&iacute;tett adatokban az &Ouml;n &aacute;ltal leadott rendel&eacute;shez k&eacute;pest, &uacute;gy azt k&eacute;rem min&eacute;l előbb jelezze &uuml;zletk&ouml;tőj&eacute;nek.</p>

<p>/T&aacute;j&eacute;koztat&aacute;sk&eacute;nt:<br />
A szand&aacute;lok talp&aacute;nak egys&eacute;ges&iacute;t&eacute;se miatt előfordulhat, hogy a pap&iacute;r alapon felvett rendel&eacute;s&eacute;ben elt&eacute;r a modellsz&aacute;m utols&oacute; 3 sz&aacute;mjegye a jelen lev&eacute;lhez csatolt elektronikusan r&ouml;gz&iacute;tett rendel&eacute;s&eacute;hez k&eacute;pest. Ez nem jelent elt&eacute;r&eacute;st a kiv&aacute;lasztott modellt illetően./<br/>
Az &aacute;r t&aacute;j&eacute;koztat&oacute; jellegű, nem tartalmazza az esetlegesen fenn&aacute;ll&oacute; kedvezm&eacute;nyeket.</p>
<p>Megrendel&eacute;s&eacute;t k&ouml;sz&ouml;nj&uuml;k, &ouml;r&uuml;l&uuml;nk, hogy Vevőink k&ouml;z&ouml;tt tudhatjuk &Ouml;nt!</p>

<p>&Uuml;dv&ouml;zlettel:<br />
Szamos Kft.</p>

<p>Ez egy automatikusan gener&aacute;lt e-mail, k&eacute;rem ne v&aacute;laszoljon r&aacute;.</p>
     """.stripMargin
    }
  }

  object update extends MailText {

    def asText(params: OrderMailBody) = {
      s"""
        |Kedves ${params.customerName}!
        |
        |Üzletkötőnk a mai napon az Ön kérésének megfelelően módosította a 2015. Tavasz-Nyár szezonra leadott, ${params.orderNumber} számú megrendelését. A módosított adatokat tartalmazó megrendelését az alábbi hivatkozáson keresztül tekintheti meg.
        |        |
        |${params.link}
        |
        |Amennyiben bármilyen eltérést tapasztal a rögzített adatokban, úgy azt minél előbb jelezze üzletkötőjének.
        |
        |Megrendelését köszönjük, örülünk, hogy Vevőink között tudhatjuk Önt!
        |
        |Üdvözlettel:
        |Szamos Kft.
        |
        |Ez egy automatikusan generált e-mail, kérem ne válaszoljon rá.
      """.stripMargin
    }


    def asHtml(params: OrderMailBody) = {
      s"""
<p>Kedves ${params.customerName}!</p>

<p>&Uuml;zletk&ouml;tőnk a mai napon az &Ouml;n k&eacute;r&eacute;s&eacute;nek megfelelően m&oacute;dos&iacute;totta a 2015. Tavasz-Ny&aacute;r szezonra leadott, ${params.orderNumber} sz&aacute;m&uacute; megrendel&eacute;s&eacute;t. A m&oacute;dos&iacute;tott adatokat tartalmaz&oacute; megrendel&eacute;s&eacute;t az al&aacute;bbi hivatkoz&aacute;son kereszt&uuml;l tekintheti meg.</p>

<p><a href="${params.link}">${params.orderNumber}</a></p>

<p>Amennyiben b&aacute;rmilyen elt&eacute;r&eacute;st tapasztal a r&ouml;gz&iacute;tett adatokban, &uacute;gy azt min&eacute;l előbb jelezze &uuml;zletk&ouml;tőj&eacute;nek.</p>

<p>Megrendel&eacute;s&eacute;t k&ouml;sz&ouml;nj&uuml;k, &ouml;r&uuml;l&uuml;nk, hogy Vevőink k&ouml;z&ouml;tt tudhatjuk &Ouml;nt!</p>

<p>&Uuml;dv&ouml;zlettel:<br />
Szamos Kft.</p>

<p>Ez egy automatikusan gener&aacute;lt e-mail, k&eacute;rem ne v&aacute;laszoljon r&aacute;.</p>
     """.stripMargin
    }
  }

}
