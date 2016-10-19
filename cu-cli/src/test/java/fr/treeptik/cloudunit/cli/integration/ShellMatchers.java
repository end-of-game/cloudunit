package fr.treeptik.cloudunit.cli.integration;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.shell.core.CommandResult;

public class ShellMatchers {
    // This utility class should not be instantiated
    private ShellMatchers() {}
    
    public static Matcher<CommandResult> isSuccessfulCommand() {
        return new CommandStatusMatcher(true);
    }
    
    public static Matcher<CommandResult> isFailedCommand() {
        return new CommandStatusMatcher(false);
    }
    
    private static class CommandStatusMatcher extends TypeSafeMatcher<CommandResult> {
        private final boolean success;
        
        public CommandStatusMatcher(boolean success) {
            super(CommandResult.class);
            this.success = success;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Command to result in ");
            description.appendText(success ? "success" : "failure");
        }
        
        @Override
        protected void describeMismatchSafely(CommandResult item, Description mismatchDescription) {
            mismatchDescription.appendText("was ");
            mismatchDescription.appendText(item.isSuccess() ? "success" : "failure");
            mismatchDescription.appendText(" [");
            mismatchDescription.appendText(item.isSuccess()
                    ? item.getResult().toString()
                    : item.getException().getMessage());
            mismatchDescription.appendText("]");
        }

        @Override
        protected boolean matchesSafely(CommandResult item) {
            return item.isSuccess() == success;
        }
        
    }
}
