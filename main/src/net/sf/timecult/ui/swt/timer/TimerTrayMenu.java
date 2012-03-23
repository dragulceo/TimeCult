/*
 * Copyright (c) Rustam Vishnyakov, 2009 (dyadix@gmail.com)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * $Id: TimerTrayMenu.java,v 1.1 2009/02/12 19:12:51 dyadix Exp $
 */
package net.sf.timecult.ui.swt.timer;

import net.sf.timecult.ResourceHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import java.util.HashMap;
import java.util.Map;

public class TimerTrayMenu {

    private SWTTimerWindow timerWindow;
    private Shell          trayShell;
    private Menu           popup;
    private final static Map<SWTTimerWindow, TimerTrayMenu> currentMenus = new HashMap<SWTTimerWindow, TimerTrayMenu>();


    private TimerTrayMenu(SWTTimerWindow timerWindow) {
        this.timerWindow = timerWindow;
    }
    
    
    public void open() {
        Display d = timerWindow.getShell().getDisplay();
        if (trayShell == null || trayShell.isDisposed()) {
            trayShell = new Shell(d, SWT.SINGLE);
            setup(trayShell);
        }
        this.popup.setVisible(true);
        while (trayShell != null && !trayShell.isDisposed()) {
            if (!d.readAndDispatch())
                d.sleep();
        }
    }
    
    private void setup(Shell shell) {
        popup = new Menu(shell);
        MenuItem pauseResumeItem = new MenuItem(popup, SWT.CASCADE);
        pauseResumeItem.setText(ResourceHelper.getString("button.pauseResume.tooltip"));
        pauseResumeItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimerTrayMenu.this.timerWindow.getToolBar().pauseOrResume();               
            }
        });
        MenuItem stopItem = new MenuItem(popup, SWT.CASCADE);
        stopItem.setText(ResourceHelper.getString("button.stop.tooltip"));
        stopItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimerTrayMenu.this.timerWindow.terminate();             
            }
        });
        shell.setMenu(popup);
        shell.pack();
    }
           
    
    public boolean isDisposed() {
        return this.trayShell.isDisposed();
    }

    public void dispose() {
        if (trayShell != null && !trayShell.isDisposed()) {
            trayShell.dispose();
            trayShell = null;
        }
    }

    public static TimerTrayMenu getInstance(SWTTimerWindow timerWindow) {
        if (currentMenus.containsKey(timerWindow)) return currentMenus.get(timerWindow);
        TimerTrayMenu newMenu = new TimerTrayMenu(timerWindow);
        currentMenus.put(timerWindow, newMenu);
        return newMenu;
    }

    public static void disposeAll() {
        for (TimerTrayMenu trayMenu : currentMenus.values()) {
            trayMenu.dispose();
        }
        currentMenus.clear();
    }

}
