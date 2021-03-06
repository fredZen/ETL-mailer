package etlmail.front.cli

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation._

import etlmail.engine.NewsletterNotification

@Configuration
@PropertySource(Array("classpath:mailTool.properties"))
@ComponentScan(basePackageClasses = Array(
  classOf[etlmail.context.ComponentScanMarker],
  classOf[etlmail.front.cli.ComponentScanMarker]))
class CliAppCtx {
  @Bean
  def notification(
    @Value("${mail.resources.directory}") resourcesDirectory: String,
    @Value("${mail.from}") from: String,
    @Value("${mail.to}") to: String,
    @Value("${mail.cc}") cc: String,
    @Value("${mail.subject}") subject: String,
    @Value("${mail.template}") template: String): NewsletterNotification =
    new NewsletterNotification(subject, template, resourcesDirectory, from, to, cc, Map[String, Any]())
}
