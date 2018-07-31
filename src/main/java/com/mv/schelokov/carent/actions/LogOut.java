package com.mv.schelokov.carent.actions;

import com.mv.schelokov.carent.actions.interfaces.Action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class LogOut implements Action {

    @Override
    public JspForward execute(HttpServletRequest req, HttpServletResponse res) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new JspForward("action/home", true);
    }
    
}
