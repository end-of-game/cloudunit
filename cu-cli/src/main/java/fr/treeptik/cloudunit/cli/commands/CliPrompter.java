package fr.treeptik.cloudunit.cli.commands;

import java.io.IOException;
import java.text.MessageFormat;

import fr.treeptik.cloudunit.cli.CloudUnitCliException;
import fr.treeptik.cloudunit.cli.utils.Prompter;
import jline.console.ConsoleReader;

public class CliPrompter implements Prompter {
    public String prompt(String prompt) {
        return prompt(prompt, null);
    }
    
    public String prompt(String prompt, Character mask) {
        System.out.println(prompt);
        try {
            String line = new ConsoleReader().readLine(mask);

            return line;
        } catch (IOException e) {
            throw new CloudUnitCliException("Could not read input", e);
        }
    }

    @Override
    public boolean promptConfirmation(String question) {
        String choice = promptChoice(question, "y", "n");
        
        switch (choice) {
        case "yes":
        case "y":
            return true;
        default:
            return false;
        }
    }

    @Override
    public String promptChoice(String question, String... choices) {
        String prompt = MessageFormat.format("{0} [{1}]", question, String.join("/", choices));
        
        return prompt(prompt);
    }
    
    @Override
    public String promptPassword(String question) {
        return prompt(question, '*');
    }
}
