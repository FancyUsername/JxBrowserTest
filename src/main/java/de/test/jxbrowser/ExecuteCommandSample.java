package de.test.jxbrowser;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

/**
 * This sample demonstrates how to execute Browser commands such as Cut, Copy,
 * Paste, Undo, SelectAll, InsertText etc.
 */
public class ExecuteCommandSample {
	private JavaObject javaObject;
	private Browser browser;

	public class JavaObject {
	    public void println(String message) {
	        System.out.println(message);
	    }

		public void setValues() {
			JSValue window = browser.executeJavaScriptAndReturnValue("window");
            window.asObject().setProperty("values", new Random().doubles(10).boxed().collect(Collectors.toList()));
		}
	}
	
    public static void main(String[] args) {
    	new ExecuteCommandSample().run();
    }

	private void run() {
        browser = new Browser();
        javaObject = new JavaObject();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    Browser browser = event.getBrowser();
                    javaObject.setValues();
                    System.out.println(browser.executeJavaScriptAndReturnValue("fromJava();"));
                }
            }
		});
        
        browser.addScriptContextListener(new ScriptContextAdapter() {
            @Override
            public void onScriptContextCreated(ScriptContextEvent event) {
                Browser browser = event.getBrowser();
                JSValue window = browser.executeJavaScriptAndReturnValue("window");
                window.asObject().setProperty("java", new JavaObject());
            }
        });
        
        browser.loadURL(ExecuteCommandSample.class.getResource("demo.html").toString());
	}
}
