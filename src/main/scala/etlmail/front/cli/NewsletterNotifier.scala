package etlmail.front.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import grizzled.slf4j.Logging

import etlmail.engine.NewsletterNotification;
import etlmail.engine.ToolMailSender;

@Component
class NewsletterNotifier extends Logging {
  import NewsletterNotifier._

  @Autowired private var toolMailSender: ToolMailSender = _

  @Autowired private var newsletterNotification: NewsletterNotification = _

  def run() {
    info("Sending mail...")
    toolMailSender.sendMail(newsletterNotification)
    info("Mail successfully sent")
    exitOnSuccess()
  }
}

object NewsletterNotifier extends App with Logging {
  try {
    info("Initializing mail tool...")
    val ctx = new AnnotationConfigApplicationContext(classOf[CliAppCtx])
    ctx.registerShutdownHook()
    ctx.getBean(classOf[NewsletterNotifier]).run()
  } catch {
    case e: Exception =>
      error("Error, could not send mail", e)
      exitOnError();
  }

  def exitOnError() {
    System.exit(1)
  }

  def exitOnSuccess() {
    System.exit(0)
  }
}
