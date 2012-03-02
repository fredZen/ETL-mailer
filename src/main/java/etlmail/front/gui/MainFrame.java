package etlmail.front.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import etlmail.front.gui.application.ShutdownEvent;
import etlmail.front.gui.application.WindowJanitor;
import etlmail.front.gui.helper.InvokeAndWait;
import etlmail.front.gui.preferences.SwingServerConfiguration;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private @Autowired ApplicationEventPublisher eventPublisher;
    private @Autowired SwingServerConfiguration serverConfiguration;
    private @Autowired MailDefinitionPane pane;

    @PostConstruct
    @InvokeAndWait
    public void init() {
	pane.makeLayout(getContentPane(), serverConfiguration.getPasswordDocument());
	pane.addButtonActions(this);
	setTitle("Mailer GUI");
	setResizable(false);
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent arg0) {
		eventPublisher.publishEvent(new ShutdownEvent(arg0));
	    }
	});
    }

    @Autowired
    public void register(WindowJanitor janitor) {
	janitor.register(this);
    }
}