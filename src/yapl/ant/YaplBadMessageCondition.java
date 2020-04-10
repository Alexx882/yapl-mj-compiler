package yapl.ant;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class YaplBadMessageCondition implements Condition
{
    private String yaplFile = null;
    private String logFile = null;
    private boolean warnColumn = false;
    private Pattern yaplPattern;
    private Pattern programPattern;
    private Pattern logPattern;
    private boolean debug = false;
    
    private class Message
    {
        String program = null;
        int error = -1;        // 0 means OK
        int line = -1;
        int column = -1;
        
        public boolean equals(Message other)
        {
            if (program == null)
                return false;
            return error == other.error && line == other.line && program.equals(other.program);
        }
        
        public String toString()
        {
            return String.format("(program=%s, error=%d, line=%d, column=%d)", program, error, line, column);
        }
    }
    
    public YaplBadMessageCondition()
    {
        yaplPattern = Pattern.compile("<expected result> (OK|ERROR (\\d+) \\(line (\\d+), column (\\d+)\\))");
        programPattern = Pattern.compile("^Program (\\w+)");
        logPattern = Pattern.compile("YAPL compilation: \\[(\\w+)\\] (OK|ERROR (\\d+) \\(line (\\d+), column (\\d+)\\))");
    }

    public void setYapl(String yaplFile)
    {
        this.yaplFile = yaplFile;
    }
    
    public void setLog(String logFile)
    {
        this.logFile = logFile;
    }
    
    public void setWarnColumn(boolean warnColumn)
    {
        this.warnColumn = warnColumn;
    }
    
    private Message parseYapl() throws BuildException
    {
        Message msg = new Message();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(yaplFile));
            String line = reader.readLine();
            int msgValid = 0;
            while (line != null) {
                Matcher m = yaplPattern.matcher(line);
                if (m.find()) {
                    if ("OK".equals(m.group(1))) {
                        msg.error = 0;
                    } else {
                        msg.error = Integer.parseInt(m.group(2));
                        msg.line = Integer.parseInt(m.group(3));
                        msg.column = Integer.parseInt(m.group(4));
                    }
                    msgValid++;
                }
                m = programPattern.matcher(line);
                if (m.find()) {
                    msg.program = m.group(1);
                    msgValid++;
                    break;
                }
                line = reader.readLine();
            }
            reader.close();
            if (msgValid != 2)
                throw new BuildException("cannot parse " + yaplFile);
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        } catch (IOException e) {
            throw new BuildException(e);
        } catch (NumberFormatException e) {
            // cannot occur
        }
        return msg;
    }
    
    private Message parseLog() throws BuildException
    {
        Message msg = new Message();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line = reader.readLine();
            int msgValid = 0;
            while (line != null) {
                Matcher m = logPattern.matcher(line);
                if (m.find()) {
                    msg.program = m.group(1);
                    if ("OK".equals(m.group(2))) {
                        msg.error = 0;
                    } else {
                        msg.error = Integer.parseInt(m.group(3));
                        msg.line = Integer.parseInt(m.group(4));
                        msg.column = Integer.parseInt(m.group(5));
                    }
                    msgValid++;
                    break;
                }
                line = reader.readLine();
            }
            reader.close();
            if (msgValid != 1)
                throw new BuildException("cannot parse " + yaplFile);
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        } catch (IOException e) {
            throw new BuildException(e);
        } catch (NumberFormatException e) {
            // cannot occur
        }
        return msg;
    }
    
    @Override
    public boolean eval() throws BuildException
    {
        if (yaplFile == null || logFile == null)
            throw new BuildException("<yaplbadmessage>: 'yapl' and 'log' attributes required!");
        Message yapl = parseYapl();
        if (debug)
            System.out.println("parseYapl: " + yapl);
        Message log = parseLog();
        if (debug)
            System.out.println("parseLog: " + log);
        boolean success = yapl.equals(log);
        if (success && warnColumn && yapl.column != log.column) {
            System.err.format("WARNING: incorrect YAPL compiler error: expected column %d, got %d in %s\n",
                    yapl.column, log.column, logFile);
        }
        return !success;
    }

    public static void main(String[] args)
    {
        YaplBadMessageCondition cond = new YaplBadMessageCondition();
        cond.debug = true;
        cond.setYapl(args[0]);
        cond.setLog(args[1]);
        cond.setWarnColumn(true);
        System.out.println("result = " + cond.eval());
    }
}
