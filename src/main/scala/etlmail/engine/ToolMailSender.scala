package etlmail.engine

import java.io._

import javax.mail.internet.MimeMessage

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.exception.VelocityException
import org.apache.velocity.tools.ToolContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.javamail._

import etlmail.engine.css.CssInliner

import scala.collection.mutable.{ HashSet, Set }
import scala.collection.JavaConversions._
import grizzled.slf4j.Logging

abstract class ToolMailSender extends Logging {

  @Autowired var javaMailSender: JavaMailSender = _

  @Autowired var cssInliner: CssInliner = _

  @Autowired var toolContext: ToolContext = _

  @throws(classOf[VelocityException])
  @throws(classOf[IOException])
  protected def velocityEngine(resourcesDirectory: String): VelocityEngine

  def sendMail(notification: NewsletterNotification) {
    javaMailSender.send(new MimeMessagePreparator() {
      @Override
      def prepare(mimeMessage: MimeMessage) {
        val message = new MimeMessageHelper(mimeMessage, true)
        message.setTo(getAddressesFromString(notification.to))
        message.setCc(getAddressesFromString(notification.cc))
        message.setFrom(notification.from)
        message.setSubject(notification.subject)

        val text = render(notification)

        val doc = Jsoup.parse(text)

        val imageNames = convertImagesToCid(doc)
        cssInliner.inlineStyles(doc)

        message.setText(doc.outerHtml, true)

        val resources = new File(notification.resourcesPath)
        // Adding Inline Resources
        for (imageName <- imageNames) {
          val image = new FileSystemResource(new File(resources, imageName))
          message.addInline(imageName, image)
        }
      }
    })
  }

  @throws(classOf[IOException])
  def render(notification: NewsletterNotification): String = {
    val velocityEngine = this.velocityEngine(notification.resourcesPath)
    val result = new StringWriter
    val velocityContext = new VelocityContext(notification.variables, toolContext)
    velocityEngine.mergeTemplate(notification.template, "UTF-8", velocityContext, result)
    return result.toString
  }

  def getAddressesFromString(addresses: String): Array[String] =
    for {
      adresse <- addresses.split(",")
      adressePure = adresse.trim
      if (!adressePure.isEmpty)
    } yield adressePure.toLowerCase

  def convertImagesToCid(doc: Document): Collection[String] = {
    val imageNames: Set[String] = new HashSet[String]
    for (image <- doc.select("img")) {
      val source = image.attr("src")
      if (source != null && !source.startsWith("http://")) {
        imageNames.add(source)
        image.attr("src", "cid:" + source)
        debug("Convert image to cid: " + source)
      }
    }
    return imageNames
  }
}
