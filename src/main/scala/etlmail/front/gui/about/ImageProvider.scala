package etlmail.front.gui.about

import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStream
import java.util.List
import java.util.concurrent.ExecutionException

import javax.imageio.ImageIO
import javax.swing._

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.stereotype.Component

import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io._
import grizzled.slf4j.Logging

@Component
class ImageProvider {
  lazy val madame: JLabel = {
    val result = new JLabel
    new FetchImageWorker(result, "http://feeds.feedburner.com/BonjourMadame?format=xml").execute()
    result
  }
}

class FetchImageWorker(label: JLabel, feedUrl: String) extends SwingWorker[BufferedImage, Void] with Logging {
  private val httpClient = new DefaultHttpClient

  protected def doInBackground(): BufferedImage = {
    val entries = getFeedEntries(feedUrl)
    val url = getUrlDerniereImage(entries)
    return getImage(url)
  }

  override protected def done() {
    try {
      label.setIcon(new ImageIcon(get()))
    } catch {
      case _: InterruptedException =>
        Thread.interrupted()
      case e: ExecutionException =>
        error("Could not set image", e)
    }
  }

  private def getFeedEntries(url: String): List[SyndEntry] = {
    val input = new SyndFeedInput
    val bonjourStream = get(url)
    val feed = input.build(new XmlReader(bonjourStream))
    bonjourStream.close()
    return feed.getEntries.asInstanceOf[List[SyndEntry]]
  }

  private def getUrlDerniereImage(entries: List[SyndEntry]): String = {
    val derniereEntree = entries.get(0).getDescription.getValue
    val doc = Jsoup.parse(derniereEntree)
    val image = doc.getElementsByTag("img")
    return image.attr("src")
  }

  private def getImage(url: String): BufferedImage = {
    val imageStream = get(url)
    val image = ImageIO.read(imageStream)
    imageStream.close()
    return image
  }

  private def get(url: String): InputStream = {
    val response = httpClient.execute(new HttpGet(url))
    val entity = response.getEntity
    return entity.getContent
  }
}
