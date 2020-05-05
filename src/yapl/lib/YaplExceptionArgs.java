package yapl.lib;

public class YaplExceptionArgs {

    // used for opening and closing stuff
    public boolean isProgram;
    public String startName;
    public String endName;

    // used for symbol usage
    public String name;
    public String kind;

    public YaplExceptionArgs(boolean isProgram, String startName, String endName) {
        this(isProgram, startName, endName, "", "");
    }

    public YaplExceptionArgs(String name, String kind) {
        this(false, "", "", name, kind);
    }

    public YaplExceptionArgs(String name) {
        this(false, "", "", name, "");
    }

    public YaplExceptionArgs(boolean isProgram, String startName, String endName, String name, String kind) {
        this.isProgram = isProgram;
        this.startName = startName;
        this.endName = endName;
        this.name = name;
        this.kind = kind;
    }
}
