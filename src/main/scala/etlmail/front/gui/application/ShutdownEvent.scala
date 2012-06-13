package etlmail.front.gui.application

import org.springframework.context.ApplicationEvent

class ShutdownEvent(source: Any) extends ApplicationEvent(source)