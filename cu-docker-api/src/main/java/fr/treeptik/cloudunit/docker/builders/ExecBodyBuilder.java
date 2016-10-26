package fr.treeptik.cloudunit.docker.builders;

import fr.treeptik.cloudunit.docker.model.ExecBody;

import java.util.List;

/**
 * Created by guillaume on 22/10/15.
 */
public class ExecBodyBuilder {
    private Boolean attachStdin;
    private Boolean attachStdout;
    private Boolean attachStderr;
    private Boolean tty;
    private List<String> cmd;

    private ExecBodyBuilder() {
    }

    public static ExecBodyBuilder anExecBody() {
        return new ExecBodyBuilder();
    }

    public ExecBodyBuilder withAttachStdin(Boolean attachStdin) {
        this.attachStdin = attachStdin;
        return this;
    }

    public ExecBodyBuilder withAttachStdout(Boolean attachStdout) {
        this.attachStdout = attachStdout;
        return this;
    }

    public ExecBodyBuilder withAttachStderr(Boolean attachStderr) {
        this.attachStderr = attachStderr;
        return this;
    }

    public ExecBodyBuilder withTty(Boolean tty) {
        this.tty = tty;
        return this;
    }

    public ExecBodyBuilder withCmd(List<String> cmd) {
        this.cmd = cmd;
        return this;
    }

    public ExecBodyBuilder but() {
        return anExecBody().withAttachStdin(attachStdin).withAttachStdout(attachStdout).withAttachStderr(attachStderr).withTty(tty).withCmd(cmd);
    }

    public ExecBody build() {
        ExecBody execBody = new ExecBody();
        execBody.setAttachStdin(attachStdin);
        execBody.setAttachStdout(attachStdout);
        execBody.setAttachStderr(attachStderr);
        execBody.setTty(tty);
        execBody.setCmd(cmd);
        return execBody;
    }
}
