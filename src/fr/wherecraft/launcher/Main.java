package fr.wherecraft.launcher;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fr.wherecraft.launcher.controller.MainController;
import fr.wherecraft.launcher.view.MainView;

public class Main
{
    public static void main(final String[] args)
    {
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    final MainController main = new MainController();
                    main.preInit();
                    Thread graphics = new Thread("Graphics") {
                        @Override
                        public void run() {
                            final MainView view = new MainView(main);
                            main.addView(view);
                            view.setVisible(true);
                        }
                        
                    };
                    graphics.start();
                    
                }
                
            });
        } catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
}
}