package com.payeasy.core.base.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The <code>Log4jControlServlet</code> is used to adjust logging levels for
 * packages that utilize Log4j.
 *
 */
@SuppressWarnings("unchecked")
public class Log4jControlServlet extends HttpServlet {

    private static final long serialVersionUID = 322943225947009551L;

    public static final String CONTENT_TYPE = "text/html";
    public static final String ROOT = "Root";
    public static final String CLASS = "CLASS";
    public static final String PRIORITY = "PRIORITY";

    /**
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(CONTENT_TYPE);

        PrintWriter out = response.getWriter();

        // print title and header
        out.println("<html>");
        out.println("<head><title>Log4J Control Console</title></head>");
        out.println("<body>");
        out.println("<H3>Log4J Control Console</H3>");
        out.println("<A href=\"" + request.getRequestURI() + "\">Refresh</A><HR>");
        out.println("<table width=\"50%\" border=\"1\">");
        out.println("<tr BGCOLOR=\"#5991A6\">");
        out.println("<td><FONT  COLOR=\"BLACK\" FACE=\"Helvetica\"><B>Class</B></FONT></td>");
        out.println("<td><FONT  COLOR=\"BLACK\" FACE=\"Helvetica\"><B>Priority</B></FONT></td>");
        out.println("</tr>");

        List<Logger> loggers = this.getSortedLoggers();

        // print the root Logger
        this.displayLogger(out, Logger.getRootLogger(), 0, request);

        // print the rest of the loggers
        for (int i = 0; i < loggers.size(); i++) {
            this.displayLogger(out, loggers.get(i), i + 1, request);
        }

        out.println("</table>");
        out.println("<FONT SIZE=\"-3\" COLOR=\"BLACK\" FACE=\"Helvetica\">* " + "Inherits Priority From Parent.</FONT><BR>");
        out.println("<A href=\"" + request.getRequestURI() + "\">Refresh</A><HR>");

        // print set options
        out.println("<FORM action=\"" + request.getRequestURI() + "\" method=\"post\">");
        out.println("<FONT  SIZE=\"+2\" COLOR=\"BLACK\" FACE=\"Helvetica\"><U>" + "Set Log4J Option</U><BR><BR></FONT>");
        out.println("<FONT COLOR=\"BLACK\" FACE=\"Helvetica\">");
        out.println("<table width=\"50%\" border=\"1\">");
        out.println("<tr BGCOLOR=\"#5991A6\">");
        out.println("<td><FONT COLOR=\"BLACK\" " + "FACE=\"Helvetica\"><B>Class Name:</B></FONT></td>");
        out.println("<td><SELECT name=\"CLASS\">");
        out.println("<OPTION VALUE=\"" + ROOT + "\">" + ROOT + "</OPTION>");

        for (Logger logger : loggers) {
            String loggerName = logger.getName().equals("") ? "Root" : logger.getName();
            out.println("<OPTION VALUE=\"" + loggerName + "\">" + loggerName + "</OPTION>");
        }

        out.println("</SELECT><BR></td></tr>");

        // print logging levels
        out.println("<tr BGCOLOR=\"#5991A6\"><td><FONT COLOR=\"BLACK\" " + "FACE=\"Helvetica\"><B>Priority:</B></FONT></td>");
        out.println("<td><SELECT name=\"PRIORITY\">");
        out.println("<OPTION VALUE=\"" + Level.OFF + "\">" + Level.OFF + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.FATAL + "\">" + Level.FATAL + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.ERROR + "\">" + Level.ERROR + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.WARN + "\">" + Level.WARN + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.INFO + "\">" + Level.INFO + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.DEBUG + "\">" + Level.DEBUG + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.ALL + "\">" + Level.ALL + "</OPTION>");
        out.println("</SELECT><BR></td></tr>");
        out.println("</table></FONT>");
        out.println("<input type=\"submit\" name=\"Submit\" value=\"Set Option\"></FONT>");
        out.println("</FORM>");
        out.println("</body>");
        out.println("</html>");

        out.flush();
        out.close();
    }

    /**
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception java.io.IOException if an error occurs
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String className = request.getParameter(CLASS);
        String priority = request.getParameter(PRIORITY);

        if (className != null) {
            this.setClass(className, priority);
        }

        this.doGet(request, response);
    }

    private void displayLogger(PrintWriter out, Logger logger, int row,
            HttpServletRequest request) {

        String color = (row % 2 == 1) ? "#E1E1E1" : "#FBFBFB";
        String loggerName = logger.getName().equals("") ? ROOT : logger.getName();
        String levelName = logger.getLevel() == null ? logger.getEffectiveLevel().toString() + "*" : logger.getLevel().toString();

        out.println("<tr BGCOLOR=\"" + color + "\">");
        out.println("<td><FONT SIZE=\"-2\" COLOR=\"BLACK\" FACE=\"Helvetica\">" + loggerName + "</FONT></td>");
        out.println("<td><FONT SIZE=\"-2\" COLOR=\"BLACK\" FACE=\"Helvetica\">" + levelName + "</FONT></td>");
        out.println("</tr>");
    }

    private synchronized String setClass(String className, String priority) {
        Logger logger = null;

        try {
            logger = className.equals(ROOT) ? Logger.getRootLogger() : Logger.getLogger(className);
            logger.setLevel(Level.toLevel(priority));
        } catch (Exception e) {
            System.out.println("ERROR Setting LOG4J Logger:" + e);
        }

        return "Message Set For " + (logger.getName().equals("") ? ROOT : logger.getName());
    }

    /**
     * <code>getSortedLoggeregories</code> sorts the log4j loggeregories
     *
     * @return a <code>List</code> value
     */
    private List<Logger> getSortedLoggers() {

        ArrayList<Logger> result = new ArrayList<Logger>();
        Comparator<Logger> comparator = new LoggerComparator();
        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();

        // Need an object that impliments Collection
        while (loggers.hasMoreElements()) {
            result.add(loggers.nextElement());
        }

        Collections.sort(result, comparator);

        return result;
    }

    private class LoggerComparator implements Comparator<Logger> {

        public int compare(Logger logger1, Logger logger2) {

            String logger1Name = null;
            String logger2Name = null;

            if (logger1 != null) {
                logger1Name = logger1.getName().equals("") ? ROOT : logger1.getName();
            }

            if (logger2 != null) {
                logger2Name = logger2.getName().equals("") ? ROOT : logger2.getName();
            }

            return logger1Name.compareTo(logger2Name);
        }

        @Override
        public boolean equals(Object o) {

            if (o instanceof LoggerComparator) {
                return true;
            } else {
                return false;
            }
        }
    }

}
