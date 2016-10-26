package fr.treeptik.cloudunit.docker.builders;

import fr.treeptik.cloudunit.docker.model.ExecStartBody;

/**
 * Created by guillaume on 22/10/15.
 */
public class ExecStartBodyBuilder {
    private Boolean detach;
    private Boolean tty;

    private ExecStartBodyBuilder() {
    }

    public static ExecStartBodyBuilder anExecStartBody() {
        return new ExecStartBodyBuilder();
    }

    public ExecStartBodyBuilder withDetach(Boolean detach) {
        this.detach = detach;
        return this;
    }

    public ExecStartBodyBuilder withTty(Boolean tty) {
        this.tty = tty;
        return this;
    }

    public ExecStartBodyBuilder but() {
        return anExecStartBody().withDetach(detach).withTty(tty);
    }

    public ExecStartBody build() {
        ExecStartBody execStartBody = new ExecStartBody();
        execStartBody.setDetach(detach);
        execStartBody.setTty(tty);
        return execStartBody;
    }
}
